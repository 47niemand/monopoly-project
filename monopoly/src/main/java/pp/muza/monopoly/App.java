/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package pp.muza.monopoly;

import org.apache.commons.cli.*;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.strategy.DefaultStrategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a simple Monopoly game simulator
 */
public class App {

    public static final int DEFAULT_PLAYERS = 2;

    static final String OPT_PLAYERS = "players";

    private static int players = DEFAULT_PLAYERS;

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
                players = Integer.parseInt(cmd.getOptionValue("players"));
                if (players < 1 || players > 4) {
                    throw new IllegalArgumentException("Number of players must be between 1 and 4");
                }
                System.out.println("Number of players: " + players);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("java -jar <jar-file>", options);
            System.exit(1);
        }
        game();
    }

    static void game() {
        List<Player> p = IntStream.range(0, players).mapToObj(i -> new Player("@Player" + (i + 1)))
                .collect(Collectors.toList());
        Game game = new Game(p, DefaultStrategy.STRATEGY);
        game.gameLoop();
    }

    public String getGreeting() {
        return "This is a game of Monopoly!";
    }
}
