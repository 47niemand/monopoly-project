package pp.muza.monopoly.model.lands;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
public final class Start extends Land {

    private final BigDecimal incomeTax;

    public Start(BigDecimal incomeTax) {
        super("Start", Land.Type.START);
        this.incomeTax = incomeTax;
    }
}
