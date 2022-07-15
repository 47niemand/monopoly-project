package pp.muza.monopoly.model;

import lombok.Getter;

import java.math.BigDecimal;

public interface Property extends Land {

    /**
     * Returns the price of the property.
     *
     * @return the price of the property
     */
    BigDecimal getPrice();

    /**
     * Returns the color of the property.
     *
     * @return the color of the property
     */
    Color getColor();

    /**
     * Returns the asset of the property.
     *
     * @return the asset of the property
     */
    Asset getAsset();

    enum Color {
        // colors of the properties
        BLUE, GREEN, INDIGO, ORANGE, RAINBOW, RED, VIOLET, YELLOW
    }

    enum Asset {
        // properties for a game board
        COFFEE_SHOP("Coffee Shop", BigDecimal.valueOf(1), Color.RED),
        DONUT_SHOP("Donut Shop", BigDecimal.valueOf(1), Color.RED),
        BAKERY("Bakery", BigDecimal.valueOf(1), Color.ORANGE),
        BURGER_JOINT("Burger Joint", BigDecimal.valueOf(1), Color.ORANGE),
        LIBRARY("Library", BigDecimal.valueOf(2), Color.YELLOW),
        MUSEUM("Museum", BigDecimal.valueOf(2), Color.YELLOW),
        SWIMMING_POOL("Swimming pool", BigDecimal.valueOf(2), Color.GREEN),
        GO_KARTS("Go-Karts", BigDecimal.valueOf(2), Color.GREEN),
        CINEMA("Cinema", BigDecimal.valueOf(3), Color.BLUE),
        THEATRE("Theatre", BigDecimal.valueOf(3), Color.BLUE),
        PET_SHOP("Pet Shop", BigDecimal.valueOf(3), Color.INDIGO),
        TOY_SHOP("Toy Shop", BigDecimal.valueOf(3), Color.INDIGO),
        AQUARIUM("Aquarium", BigDecimal.valueOf(4), Color.VIOLET),
        THE_ZOO("The ZOO", BigDecimal.valueOf(4), Color.VIOLET),
        PARK_LANE("Park lane", BigDecimal.valueOf(5), Color.RAINBOW),
        MAYFAIR("Mayfair", BigDecimal.valueOf(5), Color.RAINBOW);

        @Override
        public String toString() {
            return name();
        }

        @Getter
        private final String name;
        @Getter
        private final BigDecimal price;
        @Getter
        private final Color color;

        Asset(String name, BigDecimal price, Color color) {
            this.name = name;
            this.price = price;
            this.color = color;
        }
    }
}
