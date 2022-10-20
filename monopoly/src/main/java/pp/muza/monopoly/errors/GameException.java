package pp.muza.monopoly.errors;

/**
 * @author dmytromuza
 */
public final class GameException extends Exception {

    final GameError error;

    public GameException(GameError error) {
        super(error.getMessage());
        this.error = error;
    }


}
