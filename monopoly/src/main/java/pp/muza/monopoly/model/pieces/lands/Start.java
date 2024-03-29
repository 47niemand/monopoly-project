package pp.muza.monopoly.model.pieces.lands;

import lombok.Getter;

/**
 * @author dmytromuza
 */
@Getter
public final class Start extends BaseLand {

    private final int incomeTax;

    public Start(int incomeTax) {
        super(Start.class.getSimpleName(), LandType.START);
        this.incomeTax = incomeTax;
    }
}
