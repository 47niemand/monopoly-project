package pp.muza.monopoly.model;

import lombok.Getter;

/**
 * The ActionType enum.
 *
 * @author dmytromuza
 */

public enum ActionType {
    /**
     * The CHOOSE is type of cards can be used only once.
     **/
    CHOOSE(true),
    /**
     * Players can keep the card, and use it as they want.
     */
    KEEPABLE(false),
    /**
     * The obligation is a card that must be used.
     */
    OBLIGATION(true),
    /**
     * This is a profit card. It could be used before beginning a new turn.
     */
    PROFIT(true);

    /**
     * true if the card is obligatory, false otherwise
     */
    @Getter
    private final boolean mandatory;

    ActionType(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
