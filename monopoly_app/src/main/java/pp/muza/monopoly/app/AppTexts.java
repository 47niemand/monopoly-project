package pp.muza.monopoly.app;

import java.util.Map;
import java.util.ResourceBundle;

public class AppTexts {

    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", I18nOptions.currentLocale);

    public static final String[] PLAYER_INFO = {
            resourceBundle.getString("PLAYER_INFO_1"),
            resourceBundle.getString("PLAYER_INFO_2")
    };
    public static final String[] PLAYED_TURN_MSG = new String[]{
            resourceBundle.getString("PLAYED_TURN_MSG_1"),
            resourceBundle.getString("PLAYED_TURN_MSG_2"),
            resourceBundle.getString("PLAYED_TURN_MSG_3"),
            resourceBundle.getString("PLAYED_TURN_MSG_4")};
    public static final String[] PLAYED_CARDS_MSG = new String[]{
            resourceBundle.getString("PLAYED_CARDS_MSG_1"),
            resourceBundle.getString("PLAYED_CARDS_MSG_2"),
            resourceBundle.getString("PLAYED_CARDS_MSG_3")};
    public static final String[] PLAYED_TURN_LOCATION_MSG = new String[]{
            resourceBundle.getString("PLAYED_TURN_LOCATION_MSG_1")};
    public static final String[] NEW_OWN_MSG = new String[]{
            resourceBundle.getString("NEW_OWN_MSG_1"),
            resourceBundle.getString("NEW_OWN_MSG_2"),
            resourceBundle.getString("NEW_OWN_MSG_3")};
    public static final String[] LOST_PROPERTY_MSG = new String[]{
            resourceBundle.getString("LOST_PROPERTY_MSG_1"),
            resourceBundle.getString("LOST_PROPERTY_MSG_2"),
    };
    public static final String[] LOST_PROPERTY_PLURAL_MSG = new String[]{
            resourceBundle.getString("LOST_PROPERTY_PLURAL_MSG_1"),
            resourceBundle.getString("LOST_PROPERTY_PLURAL_MSG_2"),
    };
    public static final String[] RENT_PAYMENT_MSG = new String[]{
            resourceBundle.getString("RENT_PAYMENT_MSG_1")
    };
    public static final String[] RENT_INCOME_MSG = new String[]{
            resourceBundle.getString("RENT_INCOME_MSG_1"),
            resourceBundle.getString("RENT_INCOME_MSG_2")
    };
    public static final String[] RENT_MSG = new String[]{
            resourceBundle.getString("RENT_1"),
            resourceBundle.getString("RENT_2"),
            resourceBundle.getString("RENT_3")
    };
    public static final String[] OUT_OF_GAME_MSG = new String[]{resourceBundle.getString("OUT_OF_GAME_MSG")};
    public static final String[] IN_JAIL_MSG = new String[]{resourceBundle.getString("IN_JAIL_MSG")};
    public static final String[] PLAYER_PAYS_DEBTS_MSG = new String[]{resourceBundle.getString("PLAYER_PAYS_DEBTS_MSG")};
    public static final String[] AND_OTHER_DEBTS_MSG = new String[]{resourceBundle.getString("AND_OTHER_DEBTS_MSG")};
    public static final String[] INCOME_MSG = new String[]{resourceBundle.getString("INCOME_MSG")};
    public static final String[] OTHER_INCOME_MSG = new String[]{resourceBundle.getString("OTHER_INCOME_MSG")};
    public static final String[] BUY_MSG = new String[]{resourceBundle.getString("BUY_MSG")};

    public static String format(String[] array, Map<String, Object> params) {
        String text = array[(int) (Math.random() * array.length)];
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "null" : entry.getValue().toString());
        }
        return text;
    }
}
