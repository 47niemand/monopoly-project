package pp.muza.monopoly.app;

import java.util.Map;

public class AppTexts {

    public static final String[] PLAYER_INFO = {
            "{player} has ${amount} and {lands_count} plots of land.",
            "{player} has a total of {lands_count} territories and ${amount}."
    };
    public static final String[] PLAYED_TURN_MSG = new String[]{"It was the turn number {number} by {player}.",
            "{player} made turn {number}.",
            "Number {number} was the {player}'s turn.",
            "On its {number} turn, {player} played..."};
    public static final String[] PLAYED_CARDS_MSG = new String[]{
            "{player}'s moves were: {cards}.",
            "The cards listed below were played by {player}: {cards}.",
            "The cards played by {player} were: {cards}."};
    public static final String[] PLAYED_TURN_LOCATION_MSG = new String[]{
            "{player} is now located at {land} in coordinates {position}."};
    public static final String[] NEW_OWN_MSG = new String[]{
            "{player} has bought {land}.",
            "{player} has acquired {land}.",
            "{land} has been purchased by {player}.."};
    public static final String[] LOST_PROPERTY_MSG = new String[]{
            "{player} has lost {land}.",
            "{land} has been lost by {player}..",
    };
    public static final String[] LOST_PROPERTY_PLURAL_MSG = new String[]{
            "{player} has lost {lands}.",
            "{lands} have been lost by {player}..",
    };
    public static final String[] RENT_PAYMENT_MSG = new String[]{
            "{player} paid ${amount} for {land} to {owner}."
    };
    public static final String[] RENT_INCOME_MSG = new String[]{
            "To {player}, {sender} transferred ${amount}.",
            "{player} obtained ${amount} from {sender}."
    };
    public static final String[] RENT = new String[]{
            "{player} must pay {recipient} the ${amount} for visiting {land}.",
            "After {player}'s visit to {land}, it have to pay {recipient} ${amount}.",
            "{player} owes {recipient} ${amount} after its trip to {land}."
    };
    public static final String[] OUT_OF_GAME_MSG = new String[]{"It's game over for player because player has failed to fulfill its obligations."};
    public static final String[] IN_JAIL_MSG = new String[]{"Player is in jail and cannot move until the fine is paid."};
    public static final String[] PLAYER_PAYS_DEBTS = new String[]{"Player pays debts: ${amount}."};
    public static final String[] AND_OTHER_DEBTS = new String[]{" and other debts: ${amount}."};
    public static final String[] INCOME = new String[]{"Income: ${amount}."};
    public static final String[] OTHER_INCOME = new String[]{" Other income: ${amount}."};
    public static final String[] BUY = new String[]{"Player has to buy {land} for ${amount}."};

    public static String formatMessage(String[] array, Map<String, Object> params) {
        String text = array[(int) (Math.random() * array.length)];
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "null" : entry.getValue().toString());
        }
        return text;
    }
}
