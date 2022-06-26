package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Start extends Land {

    private final BigDecimal incomeTax;

    public Start(BigDecimal incomeTax) {
        super("Start", Land.Type.START);
        this.incomeTax = incomeTax;
    }
}
