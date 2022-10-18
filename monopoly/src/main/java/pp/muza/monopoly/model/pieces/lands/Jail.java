package pp.muza.monopoly.model.pieces.lands;

import lombok.Getter;

/**
 * @author dmytromuza
 */
@Getter
public final class Jail extends BaseLand {

    private final int fine;

    public Jail(int fine) {
        super(Jail.class.getSimpleName(), LandType.JAIL);
        this.fine = fine;
    }
}
