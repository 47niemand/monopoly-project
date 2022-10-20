package pp.muza.monopoly.model;

/**
 * Bid for a property.
 *
 * @author dmytromuza
 */
public interface Biding extends ActionCard {

    /**
     * Gets the player who made the bid.
     *
     * @return the player.
     */
    Player getBidder();

    /**
     * Gets the position of the property.
     *
     * @return the position.
     */
    int getPosition();

    /**
     * Gets the bid amount.
     *
     * @return the bid amount.
     */
    int getPrice();

}
