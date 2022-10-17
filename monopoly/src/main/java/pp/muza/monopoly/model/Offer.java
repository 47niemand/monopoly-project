package pp.muza.monopoly.model;

/**
 * Offer to buy a property.
 *
 * @author dmytromuza
 */
public interface Offer extends ActionCard {

    /**
     * Returns a position of the property.
     *
     * @return the position of the property
     */
    int getPosition();

    /**
     * Returns the starting price.
     *
     * @return the starting price
     */
    int getPrice();

    /**
     * The starting price.
     *
     * @param price the price that the player is willing to sale
     * @return the bid card
     */
    Offer openingBid(int price);
}
