package pp.muza.monopoly.model.game;

public enum PlayerStatus {

    IN_GAME(false),
    IN_JAIL(false),
    OUT_OF_GAME(true),
    WINNER(true);

    private final boolean isFinal;

    PlayerStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
