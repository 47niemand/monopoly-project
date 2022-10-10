package pp.muza.monopoly.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.cli.*;
import pp.muza.monopoly.app.stats.Statistics;
import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.game.Monopoly;
import pp.muza.monopoly.model.pieces.actions.*;
import pp.muza.monopoly.model.pieces.lands.LandType;
import pp.muza.monopoly.strategy.DefaultStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a simple Monopoly game simulator
 */
public class App {

    static final String OPT_VERBOSE = "verbose";
    static final String OPT_PLAYERS = "players";
    static int playerNumbers = Meta.DEFAULT_PLAYERS;

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
                if (playerNumbers < Meta.MIN_PLAYERS || playerNumbers > Meta.MAX_PLAYERS) {
                    throw new IllegalArgumentException(String.format("Number of players must be between %s and %s", Meta.MIN_PLAYERS, Meta.MAX_PLAYERS));
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

    static void printResults(PlayGame game) {

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

    static String cardInfo(String playerName, Board board, ActionCard card) {
        if (card instanceof PayRent) {
            PayRent rent = (PayRent) card;
            return AppTexts.formatMessage(AppTexts.RENT, Map.of(
                    "amount", rent.getValue(),
                    "land", board.getLand(rent.getPosition()).getName(),
                    "recipient", rent.getRecipient().getName(),
                    "player", playerName
            ));

        } else if (card instanceof Buy) {
            Buy buy = (Buy) card;
            return AppTexts.formatMessage(AppTexts.BUY, Map.of(
                    "land", board.getLand(buy.getPosition()).getName(),
                    "player", playerName,
                    "amount", ((Property) board.getLand(buy.getPosition())).getPrice()
            ));

        } else if (card instanceof Fortune) {
            Fortune fortune = (Fortune) card;
            return String.format("Player got a chance: %s. %s", fortune.getName(), fortune.getDescription());
        } else {
            return card.getName();
        }
    }

    static void printTurnInfo(Board board, TurnInfo turnInfo) {
        // turn number
        int turnNumber = turnInfo.getTurnNumber();
        // player
        String playerName = turnInfo.getPlayerInfo().getPlayer().getName();
        // current location
        int positionNumber = turnInfo.getPlayerInfo().getPosition();
        Land land = board.getLand(positionNumber);
        // cards played
        List<ActionCard> cards = turnInfo.getUsedCards();
        String cardsPlayed = cards.size() > 0 ? cards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")) : "none";
        List<Buy> buyCards = cards.stream().filter(x -> x.getAction() == Action.BUY).map(x -> (Buy) x).collect(Collectors.toList());
        List<Fortune> fortuneCards = cards.stream().filter(x -> x.getAction() == Action.CHANCE).map(x -> (Fortune) x).collect(Collectors.toList());
        List<Contract> contractCards = cards.stream().filter(x -> x.getAction() == Action.CONTRACT).map(x -> (Contract) x).collect(Collectors.toList());
        List<Income> incomeCards = cards.stream().filter(x -> x.getAction() == Action.INCOME).map(x -> (Income) x).collect(Collectors.toList());
        List<Debt> paymentCards = cards.stream().filter(x -> x.getAction() == Action.DEBT).map(x -> (Debt) x).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();

        playedTurnMsg(turnNumber, playerName, turnInfo.getPlayerInfo(), sb);
        if (incomeCards.size() > 0) {
            incomeMsg(playerName, incomeCards, sb);
        }
        playerLocationMsg(playerName, positionNumber, land, sb);
        if (buyCards.size() > 0) {
            newOwnsMsg(board, playerName, buyCards, sb);
        }
        if (paymentCards.size() > 0) {
            paymentMsg(playerName, board, paymentCards, sb);
        }
        if (contractCards.size() > 0) {
            lostMsg(board, playerName, contractCards, sb);
        }
        if (fortuneCards.size() > 0) {
            fortuneMsg(playerName, board, fortuneCards, sb);
        }
        playedCardsMsg(playerName, cardsPlayed, sb);
        if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.OUT_OF_GAME) {
            String obligatoryCards = turnInfo.getActiveCards().stream().map(actionCard -> cardInfo(playerName, board, actionCard)).collect(Collectors.joining(", "));
            sb.append(obligatoryCards).append("\n");
            sb.append(AppTexts.formatMessage(AppTexts.OUT_OF_GAME_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        } else if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.IN_JAIL) {
            sb.append(AppTexts.formatMessage(AppTexts.IN_JAIL_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        }
        System.out.println(sb);
    }

    private static void fortuneMsg(String playerName, Board board, List<Fortune> fortuneCards, StringBuilder sb) {
        for (Fortune x : fortuneCards) {
            sb.append(cardInfo(playerName, board, x)).append("\n");
        }
    }

    private static void paymentMsg(String playerName, Board board, List<Debt> paymentCards, StringBuilder sb) {
        Integer spending = paymentCards.stream().map(Debt::getValue).reduce(0, Integer::sum);
        PayRent r = paymentCards.stream().filter(x -> x instanceof PayRent).map(x -> (PayRent) x).findFirst().orElse(null);
        if (r != null) {
            sb.append(AppTexts.formatMessage(AppTexts.RENT_PAYMENT_MSG, Map.of(
                    "player", playerName,
                    "amount", spending,
                    "land", board.getLand(r.getPosition()).getName(),
                    "owner", r.getRecipient().getName()
            )));
            if (spending - r.getValue() > 0) {
                sb.append(AppTexts.formatMessage(AppTexts.AND_OTHER_DEBTS, Map.of(
                        "player", playerName,
                        "amount", spending - r.getValue()
                )));
            }
        } else {
            sb.append(AppTexts.formatMessage(AppTexts.PLAYER_PAYS_DEBTS, Map.of(
                    "player", playerName,
                    "amount", spending
            )));
        }
        sb.append("\n");
    }

    private static void incomeMsg(String playerName, List<Income> incomeCards, StringBuilder sb) {
        List<RentRevenue> r = incomeCards.stream().filter(x -> x instanceof RentRevenue).map(x -> (RentRevenue) x).collect(Collectors.toList());
        Integer rent = 0;
        if (r.size() > 0) {
            List<Player> players = r.stream().map(RentRevenue::getSender).collect(Collectors.toList());
            String playersStr = players.stream().map(Player::getName).collect(Collectors.joining(", "));
            rent = r.stream().map(RentRevenue::getValue).reduce(0, Integer::sum);
            sb.append(AppTexts.formatMessage(AppTexts.RENT_INCOME_MSG, Map.of(
                    "player", playerName,
                    "sender", playersStr,
                    "amount", rent)));

        }
        Integer incoming = incomeCards.stream().map(Income::getValue).reduce(0, Integer::sum);
        if (incoming - rent > 0) {
            if (r.size() > 0) {
                sb.append(AppTexts.formatMessage(AppTexts.OTHER_INCOME, Map.of(
                        "player", playerName,
                        "amount", incoming - rent
                )));
            } else {
                sb.append(AppTexts.formatMessage(AppTexts.INCOME, Map.of(
                        "player", playerName,
                        "amount", incoming
                )));
            }
        }
        sb.append("\n");
    }

    private static void lostMsg(Board board, String playerName, List<Contract> contractCards, StringBuilder sb) {
        List<Property> lostProperties = contractCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String lostPropertiesStr = lostProperties.stream().map(Property::getName).collect(Collectors.joining(", "));
        if (lostProperties.size() == 1) {
            sb.append(AppTexts.formatMessage(AppTexts.LOST_PROPERTY_MSG, Map.of(
                    "player", playerName,
                    "land", lostPropertiesStr)
            )).append("\n");
        } else {
            sb.append(AppTexts.formatMessage(AppTexts.LOST_PROPERTY_PLURAL_MSG, Map.of(
                    "player", playerName,
                    "lands", lostPropertiesStr)
            )).append("\n");
        }
    }

    private static void newOwnsMsg(Board board, String playerName, List<Buy> buyCards, StringBuilder sb) {
        List<Property> ownedProperties = buyCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String ownedPropertiesStr = ownedProperties.stream().map(Property::getName).collect(Collectors.joining(", "));
        sb.append(AppTexts.formatMessage(AppTexts.NEW_OWN_MSG, Map.of(
                "player", playerName,
                "land", ownedPropertiesStr)
        )).append("\n");
    }

    private static void playedCardsMsg(String playerName, String cardsPlayed, StringBuilder sb) {
        sb.append(AppTexts.formatMessage(AppTexts.PLAYED_CARDS_MSG, Map.of(
                "player", playerName,
                "cards", cardsPlayed)
        )).append("\n");
    }

    private static void playerLocationMsg(String playerName, int positionNumber, Land land, StringBuilder sb) {
        sb.append(AppTexts.formatMessage(AppTexts.PLAYED_TURN_LOCATION_MSG, Map.of(
                        "land", land.getName(),
                        "position", positionNumber,
                        "player", playerName)
                )
        ).append("\n");
    }

    private static void playedTurnMsg(int turnNumber, String playerName, PlayerInfo playerInfo, StringBuilder sb) {
        sb.append(AppTexts.formatMessage(AppTexts.PLAYED_TURN_MSG, Map.of(
                "number", turnNumber,
                "player", playerName)
        )).append(" ").append(AppTexts.formatMessage(AppTexts.PLAYER_INFO, Map.of(
                "player", playerName,
                "amount", playerInfo.getCoins(),
                "lands_count", playerInfo.getBelongings().size())
        )).append("\n");
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
                printTurnInfo(game.getBoard(), turnInfo);
            }
            if (game.getTurnNumber() > Meta.DEFAULT_MAX_TURNS) {
                break;
            }
        }
        System.out.println("Game finished");
        printResults(game);
    }

    public String getGreeting() {
        return "This is a game of Monopoly!";
    }
}
