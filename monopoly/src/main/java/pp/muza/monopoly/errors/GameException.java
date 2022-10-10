package pp.muza.monopoly.errors;

public final class GameException extends Exception {

    public GameException(GameError error) {
        super(error.getMessage());
    }

    public GameException(Exception e) {
        super(e.getMessage(), e);
    }

}
