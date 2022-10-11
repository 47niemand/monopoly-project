package pp.muza.monopoly.model.pieces.lands;

import lombok.Getter;
import lombok.ToString;

/**
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
public final class Jail extends BaseLand {

    private final int fine;

    public Jail(int fine) {
        super("Jail", LandType.JAIL);
        this.fine = fine;
    }
}
