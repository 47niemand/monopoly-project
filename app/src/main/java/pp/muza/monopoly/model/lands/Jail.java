package pp.muza.monopoly.model.lands;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Jail extends Land {

    private final BigDecimal fine;

    public Jail(BigDecimal fine) {
        super("Jail", Land.Type.JAIL);
        this.fine = fine;
    }
}
