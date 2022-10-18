package pp.muza.monopoly.model;

/**
 * Auction action card interface.
 *
 * @author dmytromuza
 */
public interface BidingAction extends ActionCard {

    int getPosition();

    int getPrice();

    /**
     * The bid.
     *
     * @param price the price that the player is willing to buy
     * @return the bid
     **/
    BidingAction bid(int price);
}
