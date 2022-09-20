package pp.muza.monopoly.model.pieces.lands;



import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.Property;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public final class BaseProperty extends BaseLand implements Property {

    private final int price;
    private final PropertyColor color;
    private final Asset asset;

    public BaseProperty(Asset asset) {
        super(asset.getName(), LandType.PROPERTY);
        this.price = asset.getPrice();
        this.color = asset.getColor();
        this.asset = asset;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
