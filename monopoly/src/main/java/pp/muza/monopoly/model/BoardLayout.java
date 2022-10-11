package pp.muza.monopoly.model;

import static pp.muza.monopoly.model.Asset.AQUARIUM;
import static pp.muza.monopoly.model.Asset.BAKERY;
import static pp.muza.monopoly.model.Asset.BURGER_JOINT;
import static pp.muza.monopoly.model.Asset.CINEMA;
import static pp.muza.monopoly.model.Asset.COFFEE_SHOP;
import static pp.muza.monopoly.model.Asset.DONUT_SHOP;
import static pp.muza.monopoly.model.Asset.GO_KARTS;
import static pp.muza.monopoly.model.Asset.LIBRARY;
import static pp.muza.monopoly.model.Asset.MAYFAIR;
import static pp.muza.monopoly.model.Asset.MUSEUM;
import static pp.muza.monopoly.model.Asset.PARK_LANE;
import static pp.muza.monopoly.model.Asset.PET_SHOP;
import static pp.muza.monopoly.model.Asset.SWIMMING_POOL;
import static pp.muza.monopoly.model.Asset.THEATRE;
import static pp.muza.monopoly.model.Asset.THE_ZOO;
import static pp.muza.monopoly.model.Asset.TOY_SHOP;

import java.util.ArrayList;
import java.util.List;

import pp.muza.monopoly.model.board.BoardImpl;
import pp.muza.monopoly.model.pieces.lands.BaseProperty;
import pp.muza.monopoly.model.pieces.lands.ChanceLand;
import pp.muza.monopoly.model.pieces.lands.GotoJail;
import pp.muza.monopoly.model.pieces.lands.Jail;
import pp.muza.monopoly.model.pieces.lands.Parking;
import pp.muza.monopoly.model.pieces.lands.Start;

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
        lands.add(new Start(2));
        lands.add(propertyOf(COFFEE_SHOP));
        lands.add(propertyOf(DONUT_SHOP));
        lands.add(new ChanceLand());
        lands.add(propertyOf(BAKERY));
        lands.add(propertyOf(BURGER_JOINT));
        lands.add(new Jail(1));
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
