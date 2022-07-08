package pp.muza.monopoly.model.lands;

import static pp.muza.monopoly.model.lands.PropertyName.AQUARIUM;
import static pp.muza.monopoly.model.lands.PropertyName.BAKERY;
import static pp.muza.monopoly.model.lands.PropertyName.BURGER_JOINT;
import static pp.muza.monopoly.model.lands.PropertyName.CINEMA;
import static pp.muza.monopoly.model.lands.PropertyName.COFFEE_SHOP;
import static pp.muza.monopoly.model.lands.PropertyName.DONUT_SHOP;
import static pp.muza.monopoly.model.lands.PropertyName.GO_KARTS;
import static pp.muza.monopoly.model.lands.PropertyName.LIBRARY;
import static pp.muza.monopoly.model.lands.PropertyName.MAYFAIR;
import static pp.muza.monopoly.model.lands.PropertyName.MUSEUM;
import static pp.muza.monopoly.model.lands.PropertyName.PARK_LANE;
import static pp.muza.monopoly.model.lands.PropertyName.PET_SHOP;
import static pp.muza.monopoly.model.lands.PropertyName.SWIMMING_POOL;
import static pp.muza.monopoly.model.lands.PropertyName.THEATRE;
import static pp.muza.monopoly.model.lands.PropertyName.THE_ZOO;
import static pp.muza.monopoly.model.lands.PropertyName.TOY_SHOP;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    private static Property propertyOf(PropertyName name) {
        return new Property(name.getName(), name.getPrice(), name.getColor());
    }

    public static Board<Land> defaultBoard() {
        List<Land> lands = new ArrayList<>();
        lands.add(new Start(BigDecimal.valueOf(2)));
        lands.add(propertyOf(COFFEE_SHOP));
        lands.add(propertyOf(DONUT_SHOP));
        lands.add(new Chance());
        lands.add(propertyOf(BAKERY));
        lands.add(propertyOf(BURGER_JOINT));
        lands.add(new Jail(BigDecimal.valueOf(1)));
        lands.add(propertyOf(LIBRARY));
        lands.add(propertyOf(MUSEUM));
        lands.add(new Chance());
        lands.add(propertyOf(SWIMMING_POOL));
        lands.add(propertyOf(GO_KARTS));
        lands.add(new Parking());
        lands.add(propertyOf(CINEMA));
        lands.add(propertyOf(THEATRE));
        lands.add(new Chance());
        lands.add(propertyOf(PET_SHOP));
        lands.add(propertyOf(TOY_SHOP));
        lands.add(new GotoJail());
        lands.add(propertyOf(AQUARIUM));
        lands.add(propertyOf(THE_ZOO));
        lands.add(new Chance());
        lands.add(propertyOf(PARK_LANE));
        lands.add(propertyOf(MAYFAIR));
        return new Board<>(lands);
    }
}
