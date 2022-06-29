package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Property extends Land {

    private final BigDecimal price;
    private final Color color;

    public Property(String name, BigDecimal price, Color color) {
        super(name, Land.Type.PROPERTY);
        this.price = price;
        this.color = color;
    }

    public enum Color {
        RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET, RAINBOW
    }

    public BigDecimal getRent() {
        // multiply(BigDecimal.valueOf(1.1));
        return price;
    }
}
