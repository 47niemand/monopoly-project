package pp.muza.monopoly.errors;

public enum GameError {
    /**
     * Game already started
     */
    GAME_ALREADY_STARTED("Game already started"),
    /**
     * Game did not start
     */
    GAME_NOT_STARTED("Game is not started"),
    /**
     * Land is already owned
     */
    LAND_IS_ALREADY_OWNED("Land is already owned"),
    /**
     * Land is not owned by player
     */
    LAND_IS_NOT_OWNED_BY_PLAYER("Land is not owned by player"),
    /**
     * Land is not owned by seller
     */
    LAND_IS_NOT_OWNED_BY_SELLER("Land is not owned by seller"),
    /**
     * Not enough players
     */
    NOT_ENOUGH_PLAYERS("Not enough players"),
    /**
     * No more players in game
     */
    NO_MORE_PLAYERS_IN_GAME("No more players in game"),
    /**
     * No turn in progress
     */
    NO_TURN_IN_PROGRESS("No turn in progress"),
    /**
     * Player is not in game
     */
    PLAYER_IS_NOT_IN_GAME("Player is not in game"),
    /**
     * "Player is not in jail
     */
    PLAYER_IS_NOT_IN_JAIL("Player is not in jail"),
    /**
     * Too many players
     */
    TOO_MANY_PLAYERS("Too many players"),
    /**
     * Current turn is not finished
     */
    TURN_NOT_FINISHED("Current turn is not finished"),
    /**
     * Wrong turn
     */
    WRONG_TURN("Wrong turn"),
    /**
     * You can't trade with yourself
     */
    YOU_CAN_T_TRADE_WITH_YOURSELF("You can't trade with yourself");

    private final String message;

    GameError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
