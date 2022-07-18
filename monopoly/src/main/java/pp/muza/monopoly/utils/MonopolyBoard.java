package pp.muza.monopoly.utils;

import static pp.muza.monopoly.model.Property.Asset.AQUARIUM;
import static pp.muza.monopoly.model.Property.Asset.BAKERY;
import static pp.muza.monopoly.model.Property.Asset.BURGER_JOINT;
import static pp.muza.monopoly.model.Property.Asset.CINEMA;
import static pp.muza.monopoly.model.Property.Asset.COFFEE_SHOP;
import static pp.muza.monopoly.model.Property.Asset.DONUT_SHOP;
import static pp.muza.monopoly.model.Property.Asset.GO_KARTS;
import static pp.muza.monopoly.model.Property.Asset.LIBRARY;
import static pp.muza.monopoly.model.Property.Asset.MAYFAIR;
import static pp.muza.monopoly.model.Property.Asset.MUSEUM;
import static pp.muza.monopoly.model.Property.Asset.PARK_LANE;
import static pp.muza.monopoly.model.Property.Asset.PET_SHOP;
import static pp.muza.monopoly.model.Property.Asset.SWIMMING_POOL;
import static pp.muza.monopoly.model.Property.Asset.THEATRE;
import static pp.muza.monopoly.model.Property.Asset.THE_ZOO;
import static pp.muza.monopoly.model.Property.Asset.TOY_SHOP;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.board.BoardImpl;
import pp.muza.monopoly.model.pieces.lands.*;

public class MonopolyBoard {

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

    private static Property propertyOf(Property.Asset asset) {
        return new BaseProperty(asset);
    }

    public static Board defaultBoard() {
        List<Land> lands = new ArrayList<>();
        // the Start field should be the first one in the list
        lands.add(new Start(BigDecimal.valueOf(2)));
        lands.add(propertyOf(COFFEE_SHOP));
        lands.add(propertyOf(DONUT_SHOP));
        lands.add(new ChanceLand());
        lands.add(propertyOf(BAKERY));
        lands.add(propertyOf(BURGER_JOINT));
        lands.add(new Jail(BigDecimal.valueOf(1)));
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
