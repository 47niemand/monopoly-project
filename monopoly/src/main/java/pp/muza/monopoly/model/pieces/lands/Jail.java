package pp.muza.monopoly.model.pieces.lands;



import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public final class Jail extends BaseLand {

    private final Integer fine;

    public Jail(Integer fine) {
        super("Jail", LandType.JAIL);
        this.fine = fine;
    }
}
