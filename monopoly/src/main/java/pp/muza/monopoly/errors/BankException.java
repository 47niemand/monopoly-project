package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to withdraw more coins than they have
 * or when unhallowed bank action is performed.
 */
public class BankException extends BaseGameException {

    public BankException(String message) {
        super(message);
    }
}
