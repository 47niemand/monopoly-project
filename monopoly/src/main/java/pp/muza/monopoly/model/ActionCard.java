package pp.muza.monopoly.model;

import lombok.Getter;

public interface ActionCard {

    int HIGH_PRIORITY = 0;
    int NEW_TURN_PRIORITY = 100;
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
        // The CHOOSE  is a special type of cards. Players must use it. This type of
        // card can be used only once.
        CHOOSE(true),
        // Using the contract card to sell a property is optional and up to the player.
        CONTRACT(false),
        // Keepable card, player can keep it and use it later
        KEEPABLE(false),
        // Obligation card, player must use it
        OBLIGATION(true),
        // Optional card, player can choose to use it
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
        // New turn
        NEW_TURN,
        // roll dice get random number
        ROLL_DICE,
        // move to next land
        MOVE,
        // player arrives to a land
        ARRIVAL,
        // buy property
        BUY,
        // pay to player (rent, gift, etc.)
        PAY,
        // pay tax or fine to the bank
        TAX,
        // contract
        CONTRACT,
        // go to jail
        GO_TO_JAIL,
        // chance; this is a special card that stores the chance pile of the
        // game. it should be returned to the game when the card is used.
        // A Card with this type must be an instance of {@link FortuneCard} class.
        CHANCE,
        // get income
        INCOME,
        // end turn
        END_TURN,
        // get a gift
        GIFT
    }
}
