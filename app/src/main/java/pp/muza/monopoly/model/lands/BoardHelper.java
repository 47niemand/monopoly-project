package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pp.muza.monopoly.model.game.Board;

public class BoardHelper {

    public static Board<Land> defaultBoard() {
        List<Land> lands = new ArrayList<>();
        lands.add(new Start(BigDecimal.valueOf(2)));
        lands.add(new Property("Coffee Shop", BigDecimal.valueOf(1)));
        lands.add(new Property("Donut Shop", BigDecimal.valueOf(1)));
        lands.add(new Chance());
        lands.add(new Property("Bakery", BigDecimal.valueOf(2)));
        lands.add(new Property("Burger Joint", BigDecimal.valueOf(2)));
        lands.add(new Jail(BigDecimal.valueOf(1)));
        lands.add(new Property("Library", BigDecimal.valueOf(2)));
        lands.add(new Property("Museum", BigDecimal.valueOf(2)));
        lands.add(new Chance());
        lands.add(new Property("Go-Karts", BigDecimal.valueOf(3)));
        lands.add(new Property("Swimming pool", BigDecimal.valueOf(3)));
        lands.add(new Parking());
        lands.add(new Property("Cinema", BigDecimal.valueOf(3)));
        lands.add(new Property("Theater", BigDecimal.valueOf(3)));
        lands.add(new Chance());
        lands.add(new Property("Pet Shop", BigDecimal.valueOf(4)));
        lands.add(new Property("Toy Shop", BigDecimal.valueOf(4)));
        lands.add(new GotoJail());
        lands.add(new Property("Aquarium", BigDecimal.valueOf(4)));
        lands.add(new Property("The ZOO", BigDecimal.valueOf(4)));
        lands.add(new Chance());
        lands.add(new Property("Park lane", BigDecimal.valueOf(5)));
        lands.add(new Property("Mayfair", BigDecimal.valueOf(5)));
        return new Board<>(lands);
    }
}
