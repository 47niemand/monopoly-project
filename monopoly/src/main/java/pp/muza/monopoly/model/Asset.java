package pp.muza.monopoly.model;

import lombok.Getter;

public enum Asset {
    /**
     * properties for a game board
     */
    COFFEE_SHOP("Coffee Shop", 1, PropertyColor.RED),
    DONUT_SHOP("Donut Shop", 1, PropertyColor.RED),
    BAKERY("Bakery", 1, PropertyColor.ORANGE),
    BURGER_JOINT("Burger Joint", 1, PropertyColor.ORANGE),
    LIBRARY("Library", 2, PropertyColor.YELLOW),
    MUSEUM("Museum", 2, PropertyColor.YELLOW),
    SWIMMING_POOL("Swimming Pool", 2, PropertyColor.GREEN),
    GO_KARTS("Go-Karts", 2, PropertyColor.GREEN),
    CINEMA("Cinema", 3, PropertyColor.BLUE),
    THEATRE("Theatre", 3, PropertyColor.BLUE),
    PET_SHOP("Pet Shop", 3, PropertyColor.INDIGO),
    TOY_SHOP("Toy Shop", 3, PropertyColor.INDIGO),
    AQUARIUM("Aquarium", 4, PropertyColor.VIOLET),
    THE_ZOO("The ZOO", 4, PropertyColor.VIOLET),
    PARK_LANE("Park Lane", 5, PropertyColor.RAINBOW),
    MAYFAIR("Mayfair", 5, PropertyColor.RAINBOW);


    @Getter
    private final String name;
    @Getter
    private final int price;
    @Getter
    private final PropertyColor color;

    Asset(String name, int price, PropertyColor color) {

        this.name = name;
        this.price = price;
        this.color = color;
    }
}
