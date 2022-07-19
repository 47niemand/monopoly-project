package pp.muza.monopoly.model;

import lombok.Getter;

/**
 * ActionCard is a card that can be used by a player.
 * The action card has a name, an action  {@link ActionCard.Action} (like buying a property, paying rent, etc.),
 * a type of card {@link ActionCard.Type} (like optional, obligation, keepable etc.),
 * and a priority.
 */
public interface ActionCard {

    int HIGHEST_PRIORITY = 0;
    int NEW_TURN_PRIORITY = 100;
    int HIGH_PRIORITY = 200;
    int DEFAULT_PRIORITY = 1000;
    int LOW_PRIORITY = 10000;

    /**
     * Returns a name of the action card.
     *
     * @return the name of the action card
     */
    String getName();

    /**
     * A card takes some action, and this value indicates the card's action.
     *
     * @return the action of the card
     */
    Action getAction();

    /**
     * Returns type of the action card.
     *
     * @return the type of the action card
     */
    Type getType();

    /**
     * Returns priority of the action card.
     *
     * @return the priority of the action card
     */
    int getPriority();

    enum Type {
        /**
         * The CHOOSE is a special type of cards. Players must use it. This type of card can be used only once.
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

        Type(boolean mandatory) {
            this.mandatory = mandatory;
        }
    }

    enum Action {
        /**
         * New turn, the player starts a new turn with this card.
         */
        NEW_TURN,
        /**
         * roll dice get random number.
         */
        ROLL_DICE,
        /**
         * Move to the next land.
         */
        MOVE,
        /**
         * Player arrives to a land, and should use this card.
         */
        ARRIVAL,
        /**
         * Buy property.
         */
        BUY,
        /**
         * Pay to other player (rent, gift, etc.).
         */
        PAY,
        /**
         * Pay tax or fine to the bank, or gift to other players.
         */
        TAX,
        /**
         * Contract, any property-related activity (sale to the bank or other player).
         */
        CONTRACT,
        /**
         * Go to jail.
         */
        GO_TO_JAIL,
        /**
         * chance; this is a special card that stores the chance pile of the
         * game. It should be returned to the game when the card is used.
         * A Card with this type must implement {@link Fortune} interface.
         */
        CHANCE,
        /**
         * Get income.
         */
        INCOME,
        /**
         * End turn.
         */
        END_TURN,
        /**
         * Get a gift.
         */
        GIFT
    }
}
