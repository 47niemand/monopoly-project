package pp.muza.monopoly.app.print;

import static pp.muza.monopoly.app.I18n.resourceBundle;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.PlayGame;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.pieces.actions.*;
import pp.muza.monopoly.model.pieces.lands.LandType;

/**
 * @author dmytromuza
 */
public class Printer {

    static final String[] PLAYER_INFO = {
            resourceBundle.getString("PLAYER_INFO_1"),
            resourceBundle.getString("PLAYER_INFO_2")
    };
    static final String[] PLAYED_TURN_MSG = new String[]{
            resourceBundle.getString("PLAYED_TURN_MSG_1"),
            resourceBundle.getString("PLAYED_TURN_MSG_2"),
            resourceBundle.getString("PLAYED_TURN_MSG_3"),
            resourceBundle.getString("PLAYED_TURN_MSG_4")};
    static final String[] PLAYED_CARDS_MSG = new String[]{
            resourceBundle.getString("PLAYED_CARDS_MSG_1"),
            resourceBundle.getString("PLAYED_CARDS_MSG_2"),
            resourceBundle.getString("PLAYED_CARDS_MSG_3")};
    static final String[] PLAYED_TURN_LOCATION_MSG = new String[]{
            resourceBundle.getString("PLAYED_TURN_LOCATION_MSG_1")};
    static final String[] VISITED_LANDS_MSG = new String[]{
            resourceBundle.getString("VISITED_LANDS_MSG_1")};
    static final String[] NEW_OWN_MSG = new String[]{
            resourceBundle.getString("NEW_OWN_MSG_1"),
            resourceBundle.getString("NEW_OWN_MSG_2"),
            resourceBundle.getString("NEW_OWN_MSG_3")};
    static final String[] LOST_PROPERTY_MSG = new String[]{
            resourceBundle.getString("LOST_PROPERTY_MSG_1"),
            resourceBundle.getString("LOST_PROPERTY_MSG_2"),
    };
    static final String[] LOST_PROPERTY_PLURAL_MSG = new String[]{
            resourceBundle.getString("LOST_PROPERTY_PLURAL_MSG_1"),
            resourceBundle.getString("LOST_PROPERTY_PLURAL_MSG_2"),
    };
    static final String[] RENT_PAYMENT_MSG = new String[]{
            resourceBundle.getString("RENT_PAYMENT_MSG_1")
    };
    static final String[] RENT_INCOME_MSG = new String[]{
            resourceBundle.getString("RENT_INCOME_MSG_1"),
            resourceBundle.getString("RENT_INCOME_MSG_2")
    };
    static final String[] RENT_MSG = new String[]{
            resourceBundle.getString("RENT_1"),
            resourceBundle.getString("RENT_2"),
            resourceBundle.getString("RENT_3")
    };
    static final String[] OUT_OF_GAME_MSG = new String[]{
            resourceBundle.getString("OUT_OF_GAME_MSG")
    };
    static final String[] IN_JAIL_MSG = new String[]{
            resourceBundle.getString("IN_JAIL_MSG")
    };
    static final String[] PLAYER_PAYS_DEBTS_MSG = new String[]{
            resourceBundle.getString("PLAYER_PAYS_DEBTS_MSG")
    };
    static final String[] AND_OTHER_DEBTS_MSG = new String[]{
            resourceBundle.getString("AND_OTHER_DEBTS_MSG")
    };
    static final String[] INCOME_MSG = new String[]{
            resourceBundle.getString("INCOME_MSG")
    };
    static final String[] OTHER_INCOME_MSG = new String[]{
            resourceBundle.getString("OTHER_INCOME_MSG")
    };
    static final String[] BUY_MSG = new String[]{
            resourceBundle.getString("BUY_MSG")
    };

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void printResults(PlayGame game) {
        List<String> freeProperties = IntStream.range(0, game.getBoard().getLands().size())
                .filter(x -> game.getBoard().getLand(x).getType() == LandType.PROPERTY)
                .filter(x -> game.getPropertyOwners().get(x) == null)
                .mapToObj(x -> game.getBoard().getLand(x))
                .map(Land::getName)
                .map(x -> resourceBundle.getString(x))
                .collect(Collectors.toList());
        System.out.println(MessageFormat.format(resourceBundle.getString("FREE_PROPERTIES_MSG"), freeProperties));
        // get player with maximum coins
        Player winner = game.getPlayers().stream()
                .filter(x -> !game.getPlayerStatus(x).isFinal())
                .max(Comparator.comparing(game::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        System.out.println("Winner: " + winner.getName());
        // print results
        game.getPlayers().forEach(x -> System.out.printf("%s - %s\n", x.getName(), game.getPlayerInfo(x)));
    }

    public static void printTurnInfo(Board board, TurnInfo turnInfo) {
        // turn number
        int turnNumber = turnInfo.getTurnNumber();
        // player
        String playerName = turnInfo.getPlayerInfo().getPlayer().getName();
        // current location
        int positionNumber = turnInfo.getPlayerInfo().getPosition();
        Land land = board.getLand(positionNumber);
        // cards played
        List<ActionCard> cards = turnInfo.getUsedCards();
        List<BaseBuy> buyCards = cards.stream().filter(x -> x.getAction() == Action.BUY).map(x -> (BaseBuy) x).collect(Collectors.toList());
        List<Fortune> fortuneCards = cards.stream().filter(x -> x.getAction() == Action.CHANCE).map(x -> (Fortune) x).collect(Collectors.toList());
        List<Contract> contractCards = cards.stream().filter(x -> x.getAction() == Action.CONTRACT).map(x -> (Contract) x).collect(Collectors.toList());
        List<Income> incomeCards = cards.stream().filter(x -> x.getAction() == Action.INCOME).map(x -> (Income) x).collect(Collectors.toList());
        List<BaseDebt> paymentCards = cards.stream().filter(x -> x.getAction() == Action.DEBT).map(x -> (BaseDebt) x).collect(Collectors.toList());
        List<Arrival> arrivalCards = cards.stream().filter(x -> x.getAction() == Action.ARRIVAL).map(x -> (Arrival) x).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();

        playedTurnMsg(turnNumber, playerName, turnInfo.getPlayerInfo(), sb);
        if (incomeCards.size() > 0) {
            incomeMsg(playerName, incomeCards, sb);
        }
        playerLocationMsg(playerName, positionNumber, board, land, arrivalCards, sb);
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
        playedCardsMsg(playerName, cards, sb);
        playerStatus(board, turnInfo, playerName, sb);
        System.out.println(sb);
    }

    static String cardInfo(String playerName, Board board, ActionCard card) {
        if (card instanceof PayRent) {
            PayRent rent = (PayRent) card;
            return format(RENT_MSG, Map.of(
                    "amount", rent.getValue(),
                    "land", resourceBundle.getString(board.getLand(rent.getPosition()).getName()),
                    "recipient", rent.getRecipient().getName(),
                    "player", playerName
            ));

        } else if (card instanceof BaseBuy) {
            BaseBuy buy = (BaseBuy) card;
            return format(BUY_MSG, Map.of(
                    "land", resourceBundle.getString(board.getLand(buy.getPosition()).getName()),
                    "player", playerName,
                    "amount", ((Property) board.getLand(buy.getPosition())).getPrice()
            ));

        } else if (card instanceof Fortune) {
            Fortune fortune = (Fortune) card;
            return String.format("Player got a chance: %s", resourceBundle.getString(fortune.getChance().name()));
        } else {
            return card.getName();
        }
    }


    static void playerStatus(Board board, TurnInfo turnInfo, String playerName, StringBuilder sb) {
        if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.OUT_OF_GAME) {
            String obligatoryCards = turnInfo.getActiveCards().stream().map(actionCard -> cardInfo(playerName, board, actionCard)).collect(Collectors.joining(", "));
            sb.append(obligatoryCards).append("\n");
            sb.append(format(OUT_OF_GAME_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        } else if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.IN_JAIL) {
            sb.append(format(IN_JAIL_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        }
    }

    static Function<String, String> i18n() {
        return x -> resourceBundle.getString(x);
    }

    static void fortuneMsg(String playerName, Board board, List<Fortune> fortuneCards, StringBuilder sb) {
        for (Fortune x : fortuneCards) {
            sb.append(cardInfo(playerName, board, x)).append("\n");
        }
    }

    static void paymentMsg(String playerName, Board board, List<BaseDebt> paymentCards, StringBuilder sb) {
        Integer spending = paymentCards.stream().map(BaseDebt::getValue).reduce(0, Integer::sum);
        PayRent r = paymentCards.stream().filter(x -> x instanceof PayRent).map(x -> (PayRent) x).findFirst().orElse(null);
        if (r != null) {
            sb.append(format(RENT_PAYMENT_MSG, Map.of(
                    "player", playerName,
                    "amount", spending,
                    "land", resourceBundle.getString(board.getLand(r.getPosition()).getName()),
                    "owner", r.getRecipient().getName()
            )));
            if (spending - r.getValue() > 0) {
                sb.append(format(AND_OTHER_DEBTS_MSG, Map.of(
                        "player", playerName,
                        "amount", spending - r.getValue()
                )));
            }
        } else {
            sb.append(format(PLAYER_PAYS_DEBTS_MSG, Map.of(
                    "player", playerName,
                    "amount", spending
            )));
        }
        sb.append("\n");
    }

    static void incomeMsg(String playerName, List<Income> incomeCards, StringBuilder sb) {
        List<RentRevenue> r = incomeCards.stream().filter(x -> x instanceof RentRevenue).map(x -> (RentRevenue) x).collect(Collectors.toList());
        Integer rent = 0;
        if (r.size() > 0) {
            String sender = r.stream().map(RentRevenue::getSender).map(Player::getName).collect(Collectors.joining(", "));
            rent = r.stream().map(RentRevenue::getValue).reduce(0, Integer::sum);
            sb.append(format(RENT_INCOME_MSG, Map.of(
                    "player", playerName,
                    "sender", sender,
                    "amount", rent)));

        }
        Integer incoming = incomeCards.stream().map(Income::getValue).reduce(0, Integer::sum);
        if (incoming - rent > 0) {
            if (r.size() > 0) {
                sb.append(format(OTHER_INCOME_MSG, Map.of(
                        "player", playerName,
                        "amount", incoming - rent
                )));
            } else {
                sb.append(format(INCOME_MSG, Map.of(
                        "player", playerName,
                        "amount", incoming
                )));
            }
        }
        sb.append("\n");
    }

    static void lostMsg(Board board, String playerName, List<Contract> contractCards, StringBuilder sb) {
        List<Property> lostProperties = contractCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String s = lostProperties.stream().map(Property::getName).map(i18n()).collect(Collectors.joining(", "));
        if (lostProperties.size() == 1) {
            sb.append(format(LOST_PROPERTY_MSG, Map.of(
                    "player", playerName,
                    "land", s)
            )).append("\n");
        } else {
            sb.append(format(LOST_PROPERTY_PLURAL_MSG, Map.of(
                    "player", playerName,
                    "lands", s)
            )).append("\n");
        }
    }

    static void newOwnsMsg(Board board, String playerName, List<BaseBuy> buyCards, StringBuilder sb) {
        List<Property> ownedProperties = buyCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String s = ownedProperties.stream().map(Property::getName).map(i18n()).collect(Collectors.joining(", "));
        sb.append(format(NEW_OWN_MSG, Map.of(
                "player", playerName,
                "land", s)
        )).append("\n");
    }

    static void playedCardsMsg(String playerName, List<ActionCard> cards, StringBuilder sb) {
        String cardsPlayed = cards.size() > 0 ? cards.stream().map(ActionCard::getName).map(i18n()).collect(Collectors.joining(", ")) : "none";
        sb.append(format(PLAYED_CARDS_MSG, Map.of(
                "player", playerName,
                "cards", cardsPlayed)
        )).append("\n");
    }

    static void playerLocationMsg(String playerName, int positionNumber, Board board, Land land, List<Arrival> arrivalCards, StringBuilder sb) {
        sb.append(format(PLAYED_TURN_LOCATION_MSG, Map.of(
                "land", resourceBundle.getString(land.getName()),
                "position", positionNumber,
                "player", playerName)
        ));
        if (arrivalCards.size() > 1) {
            List<Land> landsExcludingLast = arrivalCards.stream().map(Arrival::getPosition).filter(x -> x != positionNumber).map(board::getLand).collect(Collectors.toList());
            String s = landsExcludingLast.stream().map(Land::getName).map(i18n()).collect(Collectors.joining(", "));
            sb.append(" ");
            sb.append(format(VISITED_LANDS_MSG, Map.of(
                    "player", playerName,
                    "lands_excluding_last", s)
            ));
        }
        sb.append("\n");
    }

    static void playedTurnMsg(int turnNumber, String playerName, PlayerInfo playerInfo, StringBuilder sb) {
        sb.append(format(PLAYED_TURN_MSG, Map.of(
                "number", turnNumber,
                "player", playerName)
        )).append(" ").append(format(PLAYER_INFO, Map.of(
                "player", playerName,
                "amount", playerInfo.getCoins(),
                "lands_count", playerInfo.getBelongings().size())
        )).append("\n");
    }

    public static String format(String[] array, Map<String, Object> params) {
        String text = array[(int) (Math.random() * array.length)];
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "null" : entry.getValue().toString());
        }
        return text;
    }
}
