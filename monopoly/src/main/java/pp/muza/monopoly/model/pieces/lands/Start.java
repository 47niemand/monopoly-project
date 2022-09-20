package pp.muza.monopoly.model.pieces.lands;



import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Start extends BaseLand {

    private final int incomeTax;

    public Start(int incomeTax) {
        super("Start", LandType.START);
        this.incomeTax = incomeTax;
    }
}
