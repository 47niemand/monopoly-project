package pp.muza.monopoly.model;

public enum PlayerStatus {
    // status of a player
    IN_GAME(false),
    IN_JAIL(false),
    OUT_OF_GAME(true);

    /**
     * true if a player is out of game
     */
    private final boolean isFinished;

    PlayerStatus(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
