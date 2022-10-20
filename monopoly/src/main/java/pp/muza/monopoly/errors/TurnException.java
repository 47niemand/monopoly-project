package pp.muza.monopoly.errors;

/**
 * This exception is thrown when a player tries to do something that is not allowed during his turn.
 *
 * @author dmytromuza
 */
public final class TurnException extends Exception {

    GameError error;

    public TurnException(GameError e) {
        super(e.getMessage());
        this.error = e;
    }

    public TurnException(Exception e) {
        super(e.getMessage());
        if (e instanceof TurnException) {
            this.error = ((TurnException) e).error;
        } else if (e instanceof GameException) {
            this.error = ((GameException) e).error;
        } else {
            this.error = GameError.UNKNOWN;
        }
    }
}
