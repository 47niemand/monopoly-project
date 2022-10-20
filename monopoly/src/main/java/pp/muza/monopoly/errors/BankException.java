package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to withdraw more coins than they have
 * or when unhallowed bank action is performed.
 *
 * @author dmytromuza
 */
public final class BankException extends Exception {

    GameError error;

    public BankException(GameError error) {
        super(error.getMessage());
        this.error = error;
    }

}
