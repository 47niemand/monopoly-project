package pp.muza.monopoly.model;

import lombok.Getter;

/**
 * properties for a game board
 */
public enum Asset {
    // TODO: add java doc
    COFFEE_SHOP(1, PropertyColor.RED),
    DONUT_SHOP(1, PropertyColor.RED),
    BAKERY(1, PropertyColor.ORANGE),
    BURGER_JOINT(1, PropertyColor.ORANGE),
    LIBRARY(2, PropertyColor.YELLOW),
    MUSEUM(2, PropertyColor.YELLOW),
    SWIMMING_POOL(2, PropertyColor.GREEN),
    GO_KARTS(2, PropertyColor.GREEN),
    CINEMA(3, PropertyColor.BLUE),
    THEATRE(3, PropertyColor.BLUE),
    PET_SHOP(3, PropertyColor.INDIGO),
    TOY_SHOP(3, PropertyColor.INDIGO),
    AQUARIUM(4, PropertyColor.VIOLET),
    THE_ZOO(4, PropertyColor.VIOLET),
    PARK_LANE(5, PropertyColor.RAINBOW),
    MAYFAIR(5, PropertyColor.RAINBOW);


    @Getter
    private final int price;
    @Getter
    private final PropertyColor color;

    Asset(int price, PropertyColor color) {
        this.price = price;
        this.color = color;
    }
}
