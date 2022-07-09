package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Property extends BaseLand {

    private final BigDecimal price;
    private final Color color;

    public Property(String name, BigDecimal price, Color color) {
        super(name, BaseLand.Type.PROPERTY);
        this.price = price;
        this.color = color;
    }

    public enum Color {
        // colors of the properties
        BLUE, GREEN, INDIGO, ORANGE, RAINBOW, RED, VIOLET, YELLOW
    }

    public BigDecimal getPrice() {
        return price;
    }
}
