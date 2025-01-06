package pp.muza.monopoly.model.pieces.lands;

import lombok.Getter;

/**
 * @author dmytromuza
 */

@Getter
public enum LandType {
    /**
     * Chance card.
     */
    CHANCE(ChanceLand.class),
    /**
     * The GOTO_JAIL is a land that sends the player to jail.
     */
    GOTO_JAIL(GotoJail.class),
    /**
     * The JAIL is a land that the player is sent when he/she is caught by the
     * police.
     */
    JAIL(Jail.class),
    /**
     * The parking is the place where the players can do nothing.
     */
    PARKING(Parking.class),
    /**
     * The property is a land that can be bought or sold.
     */
    PROPERTY(BaseProperty.class),
    /**
     * start is a land used to start the game.
     */
    START(Start.class);

    private final Class<? extends BaseLand> aClass;

    LandType(Class<? extends BaseLand> aClass) {
        this.aClass = aClass;
    }
}
