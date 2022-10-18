package pp.muza.monopoly.model;

import pp.muza.monopoly.consts.Constants;
import pp.muza.monopoly.model.board.BoardImpl;
import pp.muza.monopoly.model.pieces.lands.*;

import java.util.ArrayList;
import java.util.List;

import static pp.muza.monopoly.model.Asset.*;

/**
 * The Board layout.
 *
 * @author dmytromuza
 */
public final class BoardLayout {

    /*@formatter:off
     *              |          BLUE         |         |           INDIGO        |           |
     * PARKING      | CINEMA    | THEATRE   | CHANCE  | PET_SHOP   | TOY_SHOP   |GO_TO_JAIL |
     * GO_KARTS     | GREEN |                                         | VIOLET  |AQUARIUM   |
     * SWIMMING_POOL| GREEN |                                         | VIOLET  |THE_ZOO    |
     * CHANCE       |                                                           | CHANCE    |
     * MUSEUM       | YELLOW |                                        | RAINBOW |PARK_LANE  |
     *LIBRARY       | YELLOW |                                        | RAINBOW |MAYFAIR    |
     *              |           ORANGE      |        |           RED            |           |
     * JAIL         |  BURGER_JOINT| BAKERY | CHANCE | DONUT_SHOP | COFFEE_SHOP | START     |
     *                                                                      <<--- Direction |
     @formatter:on*/

    private static Property propertyOf(Asset asset) {
        return new BaseProperty(asset);
    }

    public static Board defaultBoard() {
        List<Land> lands = new ArrayList<>();
        // the Start field should be the first one in the list
        lands.add(new Start(Constants.START_BONUS));
        lands.add(propertyOf(COFFEE_SHOP));
        lands.add(propertyOf(DONUT_SHOP));
        lands.add(new ChanceLand());
        lands.add(propertyOf(BAKERY));
        lands.add(propertyOf(BURGER_JOINT));
        lands.add(new Jail(Constants.JAIL_FINE));
        lands.add(propertyOf(LIBRARY));
        lands.add(propertyOf(MUSEUM));
        lands.add(new ChanceLand());
        lands.add(propertyOf(SWIMMING_POOL));
        lands.add(propertyOf(GO_KARTS));
        lands.add(new Parking());
        lands.add(propertyOf(CINEMA));
        lands.add(propertyOf(THEATRE));
        lands.add(new ChanceLand());
        lands.add(propertyOf(PET_SHOP));
        lands.add(propertyOf(TOY_SHOP));
        lands.add(new GotoJail());
        lands.add(propertyOf(AQUARIUM));
        lands.add(propertyOf(THE_ZOO));
        lands.add(new ChanceLand());
        lands.add(propertyOf(PARK_LANE));
        lands.add(propertyOf(MAYFAIR));
        return new BoardImpl(lands);
    }
}
