package pp.muza.monopoly.model.actions;


/**
 * this interface is used to represent an action card.
 */
public interface ActionCard {

    int HIGH_PRIORITY = 0;
    int NEW_TURN_PRIORITY = 100;
    int DEFAULT_PRIORITY = 1000;
    int LOW_PRIORITY = 10000;


    /**
     * returns the name of the card. i.e. "Roll Dice",  "End Turn" etc.
     *
     * @return the name of the card.
     */
    String getName();

    /**
     * the speciality of the action card. it is ENUM that represents the purpose of the card.
     *
     * @return the speciality of the action card.
     */
    Action getAction();

    /**
     * the type of the action card. it represents the behavior of the card.
     *
     * @return returns the type of the action card.
     */
    Type getType();

    /**
     * It is a priority of the action card. It is used to determine the order of the cards.
     *
     * @return the priority of the card.
     */
    int getPriority();

    enum Type {
        // The action card is a chance card. Chance card, player must use it, this type of card can be used only once
        CHANCE(true),
        // Using the contract card to sell a property is optional and up to the player.
        CONTRACT(false),
        // keepable card, player can keep it and use it later
        KEEPABLE(false),
        // obligation card, player must use it
        OBLIGATION(true),
        // optional card, player can choose to use it
        OPTIONAL(false);

        /**
         * true if the card is obligatory, false otherwise
         */
        private final boolean mandatory;

        Type(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public boolean isMandatory() {
            return mandatory;
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
        // pay tax or fine to bank
        TAX,
        // contract
        CONTRACT,
        // go to jail
        GO_TO_JAIL,
        // get chance card, this is a special card that stores the chance pile of the
        // game. it should be returned to the game when the card is used.
        CHANCE,
        // get income
        INCOME,
        // end turn
        END_TURN,
        // get gift
        GIFT
    }
}
