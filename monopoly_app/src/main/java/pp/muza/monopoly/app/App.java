package pp.muza.monopoly.app;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import pp.muza.monopoly.app.stats.Statistics;
import pp.muza.monopoly.consts.Constants;
import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.game.Monopoly;
import pp.muza.monopoly.strategy.DefaultStrategy;

/**
 * This is a simple Monopoly game simulator
 *
 * @author dmytromuza
 */
public class App {

    static final String OPT_VERBOSE = "verbose";
    static final String OPT_PLAYERS = "players";
    static ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", I18nOptions.currentLocale);
    static int playerNumbers = Constants.DEFAULT_PLAYERS;

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());

        Options options = new Options();
        options.addOption(Option.builder("p").longOpt(OPT_PLAYERS).hasArg().desc("define number of players").type(Integer.class).build());
        options.addOption(Option.builder("v").longOpt(OPT_VERBOSE).optionalArg(true).desc("verbose mode").type(Integer.class).build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(OPT_PLAYERS)) {
                playerNumbers = Integer.parseInt(cmd.getOptionValue("players"));
                if (playerNumbers < Constants.MIN_PLAYERS || playerNumbers > Constants.MAX_PLAYERS) {
                    throw new IllegalArgumentException(String.format("Number of players must be between %s and %s", Constants.MIN_PLAYERS, Constants.MAX_PLAYERS));
                }
                System.out.println("Number of players: " + playerNumbers);
            }
            if (cmd.hasOption(OPT_VERBOSE)) {
                Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                int level = Integer.parseInt(cmd.getOptionValue(OPT_VERBOSE, "0"));
                switch (level) {
                    case 0:
                        logger.setLevel(Level.INFO);
                        break;
                    case 1:
                        logger.setLevel(Level.DEBUG);
                        break;
                    case 2:
                        logger.setLevel(Level.TRACE);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid verbose level");
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("java -jar <jar-file>", options);
            System.exit(1);
        }

        try {
            gameLoop();
        } catch (TurnException | GameException e) {
            throw new RuntimeException(e);
        }

    }

    static void gameLoop() throws TurnException, GameException {

        Statistics statistics = new Statistics();

        List<Player> players = IntStream.range(0, playerNumbers).mapToObj(i -> new Player("Player" + (i + 1)))
                .collect(Collectors.toList());
        PlayGame game = new Monopoly(players);
        game.start();
        while (game.isGameInProgress()) {
            PlayTurn turn = game.getTurn();
            ActionCard card = DefaultStrategy.getInstance().playTurn(game.getBoard(), game.getPlayers(), turn.getTurnInfo());
            if (card != null) {
                turn.playCard(card);
            } else {
                turn.endTurn();
            }
            if (turn.isFinished()) {
                TurnInfo turnInfo = turn.getTurnInfo();
                statistics.addTurnInfo(turnInfo);
                Printer.printTurnInfo(game.getBoard(), turnInfo);
            }
            if (game.getTurnNumber() > Constants.DEFAULT_MAX_TURNS) {
                break;
            }
        }
        System.out.println(resourceBundle.getString("GAME_FINISHED"));
        Printer.printResults(game);
    }

    public String getGreeting() {
        return resourceBundle.getString("GREETING_MSG");
    }
}
