package pp.muza.monopoly.app;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.game.Monopoly;
import pp.muza.monopoly.model.pieces.lands.LandType;
import pp.muza.monopoly.strategy.DefaultStrategy;

/**
 * This is a simple Monopoly game simulator
 */
public class App {

    static final String OPT_PLAYERS = "players";
    private static int playerNumbers = Meta.DEFAULT_PLAYERS;

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());

        Options options = new Options();
        options.addOption(Option.builder("p").longOpt(OPT_PLAYERS).hasArg().desc("define number of players")
                .type(Integer.class).build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(OPT_PLAYERS)) {
                playerNumbers = Integer.parseInt(cmd.getOptionValue("players"));
                if (playerNumbers < Meta.MIN_PLAYERS || playerNumbers > Meta.MAX_PLAYERS) {
                    throw new IllegalArgumentException(String.format("Number of players must be between %s and %s", Meta.MIN_PLAYERS, Meta.MAX_PLAYERS));
                }
                System.out.println("Number of players: " + playerNumbers);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("java -jar <jar-file>", options);
            System.exit(1);
        }

        try {
            game();
        } catch (TurnException | GameException e) {
            throw new RuntimeException(e);
        }

    }

    private static void printResults(PlayGame game) {

        List<String> freeProperties = IntStream.range(0, game.getBoard().getLands().size())
                .filter(x -> game.getBoard().getLand(x).getType() == LandType.PROPERTY)
                .filter(x -> game.getPropertyOwners().get(x) == null)
                .mapToObj(x -> game.getBoard().getLand(x).getName())
                .collect(Collectors.toList());
        System.out.println("Free properties on the board: " + freeProperties);
        // get player with maximum coins
        Player winner = game.getPlayers().stream()
                .filter(x -> !game.getPlayerStatus(x).isFinal())
                .max(Comparator.comparing(game::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        System.out.println("Winner: " + winner.getName());
        // print results
        game.getPlayers().forEach(x -> System.out.printf("%s - %s\n", x.getName(), game.getPlayerInfo(x)));
    }


    static void game() throws TurnException, GameException {
        List<Player> players = IntStream.range(0, playerNumbers).mapToObj(i -> new Player("@Player" + (i + 1)))
                .collect(Collectors.toList());
        PlayGame game = new Monopoly(players);
        game.start();
        while (game.isGameInProgress()) {
            PlayTurn turn = game.getTurn();
            ActionCard card = DefaultStrategy.getInstance().playTurn(turn.getTurnInfo());
            if (card != null) {
                turn.playCard(card);
            } else {
                turn.endTurn();
            }
            if (game.getTurnNumber() > Meta.DEFAULT_MAX_TURNS) {
                break;
            }
        }
        System.out.println("Game finished");
        printResults(game);
    }

    public String getGreeting() {
        return "This is a game of Game!";
    }
}
