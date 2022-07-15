package pp.muza.monopoly.model.pieces.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.Land;

@Getter
@ToString(callSuper = true)
public final class Jail extends BaseLand {

    private final BigDecimal fine;

    public Jail(BigDecimal fine) {
        super("Jail", Land.Type.JAIL);
        this.fine = fine;
    }
}
