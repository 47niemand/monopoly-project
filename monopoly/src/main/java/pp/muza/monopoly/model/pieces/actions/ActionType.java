package pp.muza.monopoly.model.pieces.actions;

import lombok.Getter;

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
     * Optional card, player can choose to use it
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
