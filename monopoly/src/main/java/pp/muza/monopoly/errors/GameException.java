package pp.muza.monopoly.errors;

public final class GameException extends Exception {

    public GameException(String message) {
        super(message);
    }

    public GameException(Exception e) {
        super(e.getMessage());
    }
}
