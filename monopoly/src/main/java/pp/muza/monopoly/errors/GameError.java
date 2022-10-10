package pp.muza.monopoly.errors;

public enum GameError {
    // TODO: add java doc
    GAME_ALREADY_STARTED("Game already started"),
    GAME_NOT_STARTED("Game is not started"),
    LAND_IS_ALREADY_OWNED("Land is already owned"),
    LAND_IS_NOT_OWNED_BY_PLAYER("Land is not owned by player"),
    LAND_IS_NOT_OWNED_BY_SELLER("Land is not owned by seller"),
    NOT_ENOUGH_PLAYERS("Not enough players"),
    NO_MORE_PLAYERS_IN_GAME("No more players in game"),
    NO_TURN_IN_PROGRESS("No turn in progress"),
    PLAYER_IS_NOT_IN_GAME("Player is not in game"),
    PLAYER_IS_NOT_IN_JAIL("Player is not in jail"),
    TOO_MANY_PLAYERS("Too many players"),
    TURN_NOT_FINISHED("Current turn is not finished"),
    WRONG_TURN("Wrong turn"),
    YOU_CAN_T_TRADE_WITH_YOURSELF("You can't trade with yourself");

    private final String message;

    GameError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
