package pp.muza.monopoly.app;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author dmytromuza
 */
public class I18n {

    public static final String MESSAGES_BUNDLE = "MessagesBundle";
    public static String language = "en";
    public static String country = "US";
    public static Locale currentLocale = new Locale(language, country);
    public static ResourceBundle resourceBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, currentLocale);

}
