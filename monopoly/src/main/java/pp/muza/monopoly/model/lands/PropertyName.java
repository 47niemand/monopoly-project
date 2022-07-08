package pp.muza.monopoly.model.lands;

import lombok.Getter;

import java.math.BigDecimal;

public enum PropertyName {
    // properties for a game board
    COFFEE_SHOP("Coffee Shop", BigDecimal.valueOf(1), Property.Color.RED),
    DONUT_SHOP("Donut Shop", BigDecimal.valueOf(1), Property.Color.RED),
    BAKERY("Bakery", BigDecimal.valueOf(1), Property.Color.ORANGE),
    BURGER_JOINT("Burger Joint", BigDecimal.valueOf(1), Property.Color.ORANGE),
    LIBRARY("Library", BigDecimal.valueOf(2), Property.Color.YELLOW),
    MUSEUM("Museum", BigDecimal.valueOf(2), Property.Color.YELLOW),
    SWIMMING_POOL("Swimming pool", BigDecimal.valueOf(2), Property.Color.GREEN),
    GO_KARTS("Go-Karts", BigDecimal.valueOf(2), Property.Color.GREEN),
    CINEMA("Cinema", BigDecimal.valueOf(3), Property.Color.BLUE),
    THEATRE("Theatre", BigDecimal.valueOf(3), Property.Color.BLUE),
    PET_SHOP("Pet Shop", BigDecimal.valueOf(3), Property.Color.INDIGO),
    TOY_SHOP("Toy Shop", BigDecimal.valueOf(3), Property.Color.INDIGO),
    AQUARIUM("Aquarium", BigDecimal.valueOf(4), Property.Color.VIOLET),
    THE_ZOO("The ZOO", BigDecimal.valueOf(4), Property.Color.VIOLET),
    PARK_LANE("Park lane", BigDecimal.valueOf(5), Property.Color.RAINBOW),
    MAYFAIR("Mayfair", BigDecimal.valueOf(5), Property.Color.RAINBOW);

    @Getter
    private final String name;
    @Getter
    private final BigDecimal price;
    @Getter
    private final Property.Color color;

    PropertyName(String name, BigDecimal price, Property.Color color) {
        this.name = name;
        this.price = price;
        this.color = color;
    }
}
