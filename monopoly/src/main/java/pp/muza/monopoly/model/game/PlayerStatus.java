package pp.muza.monopoly.model.game;

public enum PlayerStatus {
    // status of a player
    IN_GAME(false),
    IN_JAIL(false),
    OUT_OF_GAME(true);

    /**
     * true if the player is out of game
     */
    private final boolean isFinal;

    PlayerStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
