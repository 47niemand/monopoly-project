package pp.muza.monopoly.model.lands;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class Chance extends Land {

    // TODO: implement chance functionality
    public Chance() {
        super("Chance", Land.Type.CHANCE);
    }
}
