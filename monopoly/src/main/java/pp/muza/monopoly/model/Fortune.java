package pp.muza.monopoly.model;

import lombok.Getter;

public interface Fortune extends ActionCard {

    /**
     * Return the chance card's details
     *
     * @return the chance card's details
     */
    Chance getChance();

    /**
     * There are chances cards in the game.
     */
    enum Chance {
        // all available chance cards
        ADVANCE_TO_MAYFAIR("Advance to Mayfair. If one is available get it for free, otherwise pay rent to the owner.", false),
        ADVANCE_TO_YELLOW_OR_RAINBOW("Advance to Yellow or Rainbow. If one is available get it for free, otherwise pay rent to the owner.", false),
        ADVANCE_TO_GREEN_OR_VIOLET("Advance to Green or Violet. If one is available get it for free, otherwise pay rent to the owner.", false),
        ADVANCE_TO_BLUE_OR_ORANGE("Advance to Blue or Orange. If one is available get it for free, otherwise pay rent to the owner.", false),
        ADVANCE_TO_INDIGO_OR_RED("Advance to Indigo or Red. If one is available get it for free, otherwise pay rent to the owner.", false),
        ADVANCE_TO_GO_KARTS("Advance to Go Kart. If one is available get it for free, otherwise pay rent to the owner.", false),
        INCOME("Income", false),
        BIRTHDAY("Happy Birthday! Everyone gives you a present.", false),
        LUXURY_TAX("Luxury JailFine", false),
        ADVANCE_TO_GO("Advance to Go", false),
        MOVE_FORWARD_ONE_SPACE("Move forward one space or take another chance card.", false),
        MOVE_FORWARD_UP_TO_5_SPACES("Move forward up to 5 spaces", false),
        GET_OUT_OF_JAIL_FREE("Get out of Jail Free", true),
        GIVE_THIS_CARD_TO_A_PLAYER_1("Give this card to a player 1. On your next turn, go forward to any free space and buy it. If all are owned, buy one from any player.", false),
        GIVE_THIS_CARD_TO_A_PLAYER_2("Give this card to a player 2. On your next turn, go forward to any free space and buy it. If all are owned, buy one from any player.", false),
        GIVE_THIS_CARD_TO_A_PLAYER_3("Give this card to a player 3. On your next turn, go forward to any free space and buy it. If all are owned, buy one from any player.", false),
        GIVE_THIS_CARD_TO_A_PLAYER_4("Give this card to a player 4. On your next turn, go forward to any free space and buy it. If all are owned, buy one from any player.", false);

        /**
         * The description of the card.
         */
        @Getter
        private final String description;

        /**
         * true if the card is obligatory, false otherwise
         */
        @Getter
        private final boolean isGiftCard;

        Chance(String s, boolean isGiftCard) {
            this.description = s;
            this.isGiftCard = isGiftCard;
        }
    }
}
