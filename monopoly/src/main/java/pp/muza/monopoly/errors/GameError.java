package pp.muza.monopoly.errors;

/**
 * @author dmytromuza
 */

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
    YOU_CAN_T_TRADE_WITH_YOURSELF("You can't trade with yourself"),
    /**
     * Land is not owned
     */
    LAND_IS_NOT_OWNED("Land is not owned"),
    /**
     * Seller can't bid
     */
    SELLER_CANT_BID("Seller can't bid"),
    /**
     * A Bid must be greater than the current price
     */
    BID_MUST_BE_GREATER_THAN_THE_CURRENT_PRICE("Bid must be greater than the current price"),
    /**
     * Property position doesn't match the offer
     */
    POSITION_DOESNT_MATCH_THE_OFFER("Property position doesn't match the offer"),
    /**
     * Auction is not in progress
     */
    AUCTION_IS_NOT_IN_PROGRESS("Auction is not in progress"),
    /**
     * Only seller can end auction
     */
    ONLY_SELLER_CAN_END_AUCTION("Only seller can end auction"),
    /**
     * Auction is already in progress
     */
    AUCTION_IS_ALREADY_IN_PROGRESS("Auction is already in progress"),
    /**
     * Land is not property
     */
    LAND_IS_NOT_PROPERTY("Land is not property");

    private final String message;

    GameError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
