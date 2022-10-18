package pp.muza.monopoly.model;

/**
 * Bid for a property.
 *
 * @author dmytromuza
 */
public interface Biding {

    int getPosition();

    int getPrice();

    Player getBidder();
}
