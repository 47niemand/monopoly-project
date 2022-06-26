package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Property extends Land {

    private final BigDecimal price;

    public Property(String name, BigDecimal price) {
        super(name, Land.Type.PROPERTY);
        this.price = price;
    }

    public BigDecimal getRent() {
        // multiply(BigDecimal.valueOf(1.1));
        return price;
    }
}
