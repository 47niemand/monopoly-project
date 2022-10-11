package pp.muza.monopoly.model;

import lombok.Getter;
import pp.muza.monopoly.consts.Constants;

/**
 * properties for a game board
 *
 * @author dmytromuza
 */
public enum Asset {
    /**
     * Coffee Shop
     */
    COFFEE_SHOP(Constants.PRICE_1, PropertyColor.RED),
    /**
     * Donut Shop
     */
    DONUT_SHOP(Constants.PRICE_1, PropertyColor.RED),
    /**
     * Bakery
     */
    BAKERY(Constants.PRICE_1, PropertyColor.ORANGE),
    /**
     * Burger Joint
     */
    BURGER_JOINT(Constants.PRICE_1, PropertyColor.ORANGE),
    /**
     * Library
     */
    LIBRARY(Constants.PRICE_2, PropertyColor.YELLOW),
    /**
     * Museum
     */
    MUSEUM(Constants.PRICE_2, PropertyColor.YELLOW),
    /**
     * Swimming Pool
     */
    SWIMMING_POOL(Constants.PRICE_2, PropertyColor.GREEN),
    /**
     * Go-Karts Track
     */
    GO_KARTS(Constants.PRICE_2, PropertyColor.GREEN),
    /**
     * Movie Theater
     */
    CINEMA(Constants.PRICE_3, PropertyColor.BLUE),
    /**
     * Concert Hall
     */
    THEATRE(Constants.PRICE_3, PropertyColor.BLUE),
    /**
     * Pet Shop
     */
    PET_SHOP(Constants.PRICE_3, PropertyColor.INDIGO),
    /**
     * Toy Store
     */
    TOY_SHOP(Constants.PRICE_3, PropertyColor.INDIGO),
    /**
     * Aquarium
     */
    AQUARIUM(Constants.PRICE_4, PropertyColor.VIOLET),
    /**
     * Zoo
     */
    THE_ZOO(Constants.PRICE_4, PropertyColor.VIOLET),
    /**
     * Park
     */
    PARK_LANE(Constants.PRICE_5, PropertyColor.RAINBOW),
    /**
     * Mayfair
     */
    MAYFAIR(Constants.PRICE_5, PropertyColor.RAINBOW);

    @Getter
    private final int price;
    @Getter
    private final PropertyColor color;

    Asset(int price, PropertyColor color) {
        this.price = price;
        this.color = color;
    }
}
