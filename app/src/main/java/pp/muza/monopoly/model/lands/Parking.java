package pp.muza.monopoly.model.lands;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class Parking extends Land {

    public Parking() {
        super("Parking", Land.Type.PARKING);
    }

}
