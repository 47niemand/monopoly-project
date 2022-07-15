package pp.muza.monopoly.model.pieces.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.Land;

@Getter
@ToString(callSuper = true)
public final class Start extends BaseLand {

    private final BigDecimal incomeTax;

    public Start(BigDecimal incomeTax) {
        super("Start", Land.Type.START);
        this.incomeTax = incomeTax;
    }
}
