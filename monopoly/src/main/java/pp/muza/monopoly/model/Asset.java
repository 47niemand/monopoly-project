package pp.muza.monopoly.model;

import lombok.Getter;

/**
 * properties for a game board
 *
 * @author dmytromuza
 */
public enum Asset {
    /**
     * Coffee Shop
     */
    COFFEE_SHOP(1, PropertyColor.RED),
    /**
     * Donut Shop
     */
    DONUT_SHOP(1, PropertyColor.RED),
    /**
     * Bakery
     */
    BAKERY(1, PropertyColor.ORANGE),
    /**
     * Burger Joint
     */
    BURGER_JOINT(1, PropertyColor.ORANGE),
    /**
     * Library
     */
    LIBRARY(2, PropertyColor.YELLOW),
    /**
     * Museum
     */
    MUSEUM(2, PropertyColor.YELLOW),
    /**
     * Swimming Pool
     */
    SWIMMING_POOL(2, PropertyColor.GREEN),
    /**
     * Go-Karts Track
     */
    GO_KARTS(2, PropertyColor.GREEN),
    /**
     * Movie Theater
     */
    CINEMA(3, PropertyColor.BLUE),
    /**
     * Concert Hall
     */
    THEATRE(3, PropertyColor.BLUE),
    /**
     * Pet Shop
     */
    PET_SHOP(3, PropertyColor.INDIGO),
    /**
     * Toy Store
     */
    TOY_SHOP(3, PropertyColor.INDIGO),
    /**
     * Aquarium
     */
    AQUARIUM(4, PropertyColor.VIOLET),
    /**
     * Zoo
     */
    THE_ZOO(4, PropertyColor.VIOLET),
    /**
     * Park
     */
    PARK_LANE(5, PropertyColor.RAINBOW),
    /**
     * Mayfair
     */
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
