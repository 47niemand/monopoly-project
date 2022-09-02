package pp.muza.monopoly.model;

/**
 * Players can be in one of these states.
 */
public enum PlayerStatus {
    /**
     * The player is in the game.
     */
    IN_GAME(false),
    /**
     * The player is in jail.
     */
    IN_JAIL(false),
    /**
     * The player is out of the game.
     */
    OUT_OF_GAME(true);

    /**
     * No action can be taken if the status is final.
     */
    private final boolean isFinal;

    PlayerStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinished() {
        return isFinal;
    }
}
