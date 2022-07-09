package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Jail extends BaseLand {

    private final BigDecimal fine;

    public Jail(BigDecimal fine) {
        super("Jail", BaseLand.Type.JAIL);
        this.fine = fine;
    }
}
