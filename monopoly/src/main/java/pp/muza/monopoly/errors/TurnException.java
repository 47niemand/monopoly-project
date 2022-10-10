package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to do something that is not allowed during his turn.
 */
public final class TurnException extends Exception {

    public TurnException(TurnError error) {
        super(error.getMessage());
    }

    public TurnException(Exception e) {
        super(e.getMessage(), e);
    }

}
