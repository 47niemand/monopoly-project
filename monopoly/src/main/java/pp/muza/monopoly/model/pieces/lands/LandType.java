package pp.muza.monopoly.model.pieces.lands;

import lombok.Getter;

public enum LandType {
    /**
     * type of land
     */
    START(Start.class),
    PROPERTY(BaseProperty.class),
    JAIL(Jail.class),
    PARKING(Parking.class),
    GOTO_JAIL(GotoJail.class),
    CHANCE(ChanceLand.class);

    @Getter
    private final Class<? extends BaseLand> aClass;

    LandType(Class<? extends BaseLand> aClass) {
        this.aClass = aClass;
    }
}
