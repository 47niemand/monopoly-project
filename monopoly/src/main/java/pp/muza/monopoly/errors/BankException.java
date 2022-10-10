package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to withdraw more coins than they have
 * or when unhallowed bank action is performed.
 */
public final class BankException extends Exception {

    public BankException(BankError error) {
        super(error.getMessage());
    }

}
