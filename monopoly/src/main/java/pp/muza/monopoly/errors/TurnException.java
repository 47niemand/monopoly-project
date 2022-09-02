package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to do something that is not allowed during his turn.
 */
public class TurnException extends BaseGameException {

    public TurnException(String message) {
        super(message);
    }
}
