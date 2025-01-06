package pp.muza.monopoly.model.pieces.actions;

import lombok.Getter;

/**
 * There are chances cards in the game.
 *
 * @author dmytromuza
 */
@Getter
public enum Chance {
    /**
     * Advance to Mayfair. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_MAYFAIR(false, "Advance to Mayfair. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * Advance to Yellow or Rainbow. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_YELLOW_OR_RAINBOW(false, "Advance to Yellow or Rainbow. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * Advance to Green or Violet. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_GREEN_OR_VIOLET(false, "Advance to Green or Violet. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * Advance to Blue or Orange. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_BLUE_OR_ORANGE(false, "Advance to Blue or Orange. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * Advance to Red or Indigo. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_INDIGO_OR_RED(false, "Advance to Red or Indigo. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * Advance to Go Kart. If one is available, get it for free, otherwise pay rent to the owner.
     */
    ADVANCE_TO_GO_KARTS(false, "Advance to Go Kart. If one is available, get it for free, otherwise pay rent to the owner."),
    /**
     * You won a prize!
     */
    PRIZE(false, "You won a prize!"),
    /**
     * Happy Birthday! Everyone gives you a present.
     */
    BIRTHDAY(false, "Happy Birthday! Everyone gives you a present."),
    /**
     * You have to pay a luxury tax.
     */
    LUXURY_TAX(false, "You have to pay a luxury tax."),
    /**
     * Advance to Go, collect the start bonus.
     */
    ADVANCE_TO_GO(false, "Advance to Go, collect the start bonus."),
    /**
     * Move forward one space or take another chance card.
     */
    MOVE_FORWARD_ONE_SPACE(false, "Move forward one space or take another chance card."),
    /**
     * Move forward up five spaces. Get ahead up to five spaces.
     */
    MOVE_FORWARD_UP_TO_5_SPACES(false, "Move forward up to five spaces."),
    /**
     * Get out of Jail Free. You can get out of jail free, keep this card until needed.
     */
    GET_OUT_OF_JAIL_FREE(true, "Get out of Jail Free. You can get out of jail free, keep this card until needed."),
    /**
     * Give this card to the Player 1, Player 1, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.
     */
    GIVE_THIS_CARD_TO_A_PLAYER_1(false, "Give this card to Player 1, Player 1, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player."),
    /**
     * Give this card to the Player 2, Player 2, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.
     */
    GIVE_THIS_CARD_TO_A_PLAYER_2(false, "Give this card to Player 2, Player 2, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player."),
    /**
     * Give this card to the Player 3, Player 3, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.
     */
    GIVE_THIS_CARD_TO_A_PLAYER_3(false, "Give this card to Player 3, Player 3, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player."),
    /**
     * Give this card to the Player 4, Player 4, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.
     */
    GIVE_THIS_CARD_TO_A_PLAYER_4(false, "Give this card to Player 4, Player 4, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.");

    /**
     * true if the card is keepable, false otherwise
     */
    private final boolean isKeepable;
    private final String description;

    Chance(boolean isKeepable, String description) {
        this.isKeepable = isKeepable;
        this.description = description;
    }
}
