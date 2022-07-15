package pp.muza.monopoly.model;

public enum PlayerStatus {
    // status of a player
    IN_GAME(false),
    IN_JAIL(false),
    OUT_OF_GAME(true);

    /**
     * true if a player is out of game
     */
    private final boolean isFinal;

    PlayerStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
