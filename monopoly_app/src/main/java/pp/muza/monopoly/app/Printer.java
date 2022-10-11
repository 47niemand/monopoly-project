package pp.muza.monopoly.app;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.Buy;
import pp.muza.monopoly.model.pieces.actions.Contract;
import pp.muza.monopoly.model.pieces.actions.Debt;
import pp.muza.monopoly.model.pieces.actions.Income;
import pp.muza.monopoly.model.pieces.actions.PayRent;
import pp.muza.monopoly.model.pieces.actions.RentRevenue;
import pp.muza.monopoly.model.pieces.lands.LandType;

/**
 * @author dmytromuza
 */
public class Printer {
    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", I18nOptions.currentLocale);


    static void printResults(PlayGame game) {
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

    static String cardInfo(String playerName, Board board, ActionCard card) {
        if (card instanceof PayRent) {
            PayRent rent = (PayRent) card;
            return AppTexts.format(AppTexts.RENT_MSG, Map.of(
                    "amount", rent.getValue(),
                    "land", resourceBundle.getString(board.getLand(rent.getPosition()).getName()),
                    "recipient", rent.getRecipient().getName(),
                    "player", playerName
            ));

        } else if (card instanceof Buy) {
            Buy buy = (Buy) card;
            return AppTexts.format(AppTexts.BUY_MSG, Map.of(
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
        playedCardsMsg(playerName, cards, sb);
        playerStatus(board, turnInfo, playerName, sb);
        System.out.println(sb);
    }

    private static void playerStatus(Board board, TurnInfo turnInfo, String playerName, StringBuilder sb) {
        if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.OUT_OF_GAME) {
            String obligatoryCards = turnInfo.getActiveCards().stream().map(actionCard -> cardInfo(playerName, board, actionCard)).collect(Collectors.joining(", "));
            sb.append(obligatoryCards).append("\n");
            sb.append(AppTexts.format(AppTexts.OUT_OF_GAME_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        } else if (turnInfo.getPlayerInfo().getStatus() == PlayerStatus.IN_JAIL) {
            sb.append(AppTexts.format(AppTexts.IN_JAIL_MSG, Map.of(
                    "player", playerName
            ))).append("\n");
        }
    }

    private static Function<String, String> i18n() {
        return x -> resourceBundle.getString(x);
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
            sb.append(AppTexts.format(AppTexts.RENT_PAYMENT_MSG, Map.of(
                    "player", playerName,
                    "amount", spending,
                    "land", resourceBundle.getString(board.getLand(r.getPosition()).getName()),
                    "owner", r.getRecipient().getName()
            )));
            if (spending - r.getValue() > 0) {
                sb.append(AppTexts.format(AppTexts.AND_OTHER_DEBTS_MSG, Map.of(
                        "player", playerName,
                        "amount", spending - r.getValue()
                )));
            }
        } else {
            sb.append(AppTexts.format(AppTexts.PLAYER_PAYS_DEBTS_MSG, Map.of(
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
            String sender = r.stream().map(RentRevenue::getSender).map(Player::getName).collect(Collectors.joining(", "));
            rent = r.stream().map(RentRevenue::getValue).reduce(0, Integer::sum);
            sb.append(AppTexts.format(AppTexts.RENT_INCOME_MSG, Map.of(
                    "player", playerName,
                    "sender", sender,
                    "amount", rent)));

        }
        Integer incoming = incomeCards.stream().map(Income::getValue).reduce(0, Integer::sum);
        if (incoming - rent > 0) {
            if (r.size() > 0) {
                sb.append(AppTexts.format(AppTexts.OTHER_INCOME_MSG, Map.of(
                        "player", playerName,
                        "amount", incoming - rent
                )));
            } else {
                sb.append(AppTexts.format(AppTexts.INCOME_MSG, Map.of(
                        "player", playerName,
                        "amount", incoming
                )));
            }
        }
        sb.append("\n");
    }

    private static void lostMsg(Board board, String playerName, List<Contract> contractCards, StringBuilder sb) {
        List<Property> lostProperties = contractCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String s = lostProperties.stream().map(Property::getName).map(i18n()).collect(Collectors.joining(", "));
        if (lostProperties.size() == 1) {
            sb.append(AppTexts.format(AppTexts.LOST_PROPERTY_MSG, Map.of(
                    "player", playerName,
                    "land", s)
            )).append("\n");
        } else {
            sb.append(AppTexts.format(AppTexts.LOST_PROPERTY_PLURAL_MSG, Map.of(
                    "player", playerName,
                    "lands", s)
            )).append("\n");
        }
    }

    private static void newOwnsMsg(Board board, String playerName, List<Buy> buyCards, StringBuilder sb) {
        List<Property> ownedProperties = buyCards.stream().map(actionCard -> board.getLand(actionCard.getPosition())).map(x -> (Property) x).collect(Collectors.toList());
        String s = ownedProperties.stream().map(Property::getName).map(i18n()).collect(Collectors.joining(", "));
        sb.append(AppTexts.format(AppTexts.NEW_OWN_MSG, Map.of(
                "player", playerName,
                "land", s)
        )).append("\n");
    }

    private static void playedCardsMsg(String playerName, List<ActionCard> cards, StringBuilder sb) {
        String cardsPlayed = cards.size() > 0 ? cards.stream().map(ActionCard::getName).map(i18n()).collect(Collectors.joining(", ")) : "none";
        sb.append(AppTexts.format(AppTexts.PLAYED_CARDS_MSG, Map.of(
                "player", playerName,
                "cards", cardsPlayed)
        )).append("\n");
    }

    private static void playerLocationMsg(String playerName, int positionNumber, Land land, StringBuilder sb) {
        sb.append(AppTexts.format(AppTexts.PLAYED_TURN_LOCATION_MSG, Map.of(
                        "land", resourceBundle.getString(land.getName()),
                        "position", positionNumber,
                        "player", playerName)
                )
        ).append("\n");
    }

    private static void playedTurnMsg(int turnNumber, String playerName, PlayerInfo playerInfo, StringBuilder sb) {
        sb.append(AppTexts.format(AppTexts.PLAYED_TURN_MSG, Map.of(
                "number", turnNumber,
                "player", playerName)
        )).append(" ").append(AppTexts.format(AppTexts.PLAYER_INFO, Map.of(
                "player", playerName,
                "amount", playerInfo.getCoins(),
                "lands_count", playerInfo.getBelongings().size())
        )).append("\n");
    }
}
