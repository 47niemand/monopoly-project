package pp.muza.monopoly.model;

import lombok.Getter;

/**
 * The ActionType enum.
 *
 * @author dmytromuza
 */

public enum ActionType {
    /**
     * Only one action with this type and priority can be executed in the same turn.
     **/
    CHOICE(true),
    /**
     * Players can keep the card, and use it as they want.
     */
    KEEPABLE(false),
    /**
     * The obligation is a card that must be used.
     */
    OBLIGATION(true),
    /**
     * This is a profit card. It can be played only once.
     */
    PROFIT(true),
    /**
     * This card is optional to play.
     */
    OPTIONAL(false);

    /**
     * true if the card is obligatory, false otherwise
     */
    @Getter
    private final boolean mandatory;

    ActionType(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
