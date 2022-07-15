package pp.muza.monopoly.model.pieces.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Property;

@Getter
@ToString(callSuper = true)
public final class BaseProperty extends BaseLand implements Property {

    private final BigDecimal price;
    private final Color color;
    private final Asset asset;

    public BaseProperty(Asset asset) {
        super(asset.getName(), Land.Type.PROPERTY);
        this.price = asset.getPrice();
        this.color = asset.getColor();
        this.asset = asset;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }
}
