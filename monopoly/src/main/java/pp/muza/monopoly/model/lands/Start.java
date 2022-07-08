package pp.muza.monopoly.model.lands;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
public final class Start extends AbstractLand {

    private final BigDecimal incomeTax;

    public Start(BigDecimal incomeTax) {
        super("Start", AbstractLand.Type.START);
        this.incomeTax = incomeTax;
    }
}
