package pp.muza.monopoly.model.pieces.actions;

import lombok.Getter;

/**
 * There are chances cards in the game.
 */
public enum Chance {
    /**
     * all available chance cards
     */
    ADVANCE_TO_MAYFAIR("Advance to Mayfair", "If one is available get it for free, otherwise pay rent to the owner.", false),
    ADVANCE_TO_YELLOW_OR_RAINBOW("Advance to Yellow or Rainbow", "If one is available get it for free, otherwise pay rent to the owner.", false),
    ADVANCE_TO_GREEN_OR_VIOLET("Advance to Green or Violet", "If one is available get it for free, otherwise pay rent to the owner.", false),
    ADVANCE_TO_BLUE_OR_ORANGE("Advance to Blue or Orange", "Advance to Blue or Orange. If one is available get it for free, otherwise pay rent to the owner.", false),
    ADVANCE_TO_INDIGO_OR_RED("Advance to Indigo or Red", "Advance to Indigo or Red. If one is available get it for free, otherwise pay rent to the owner.", false),
    ADVANCE_TO_GO_KARTS("Advance to Go Kart", "Advance to Go Kart. If one is available get it for free, otherwise pay rent to the owner.", false),
    PRIZE("Income", "You won a prize!", false),
    BIRTHDAY("Birthday", "Happy Birthday! Everyone gives you a present.", false),
    LUXURY_TAX("Luxury JailFine", "You have to pay a luxury tax.", false),
    ADVANCE_TO_GO("Advance to Go", "Advance to Go, collect the start bonus.", false),
    MOVE_FORWARD_ONE_SPACE("Move forward", "Move forward one space or take another chance card.", false),
    MOVE_FORWARD_UP_TO_5_SPACES("Move forward up 5 spaces", "Get ahead up to five spaces.", false),
    GET_OUT_OF_JAIL_FREE("Get out of Jail Free", "You can get out of jail free, keep this card until needed.", true),
    GIVE_THIS_CARD_TO_A_PLAYER_1("Give this card to the Player 1", "Player 1, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.", false),
    GIVE_THIS_CARD_TO_A_PLAYER_2("Give this card to the Player 2", "Player 2, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.", false),
    GIVE_THIS_CARD_TO_A_PLAYER_3("Give this card to the Player 3", "Player 3, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.", false),
    GIVE_THIS_CARD_TO_A_PLAYER_4("Give this card to the Player 4", "Player 4, on your turn, go forward to any free property and buy it. If all are owned, buy one from any player.", false);

    @Getter
    private final String name;

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

    Chance(String name, String description, boolean isGiftCard) {
        this.name = name;
        this.description = description;
        this.isGiftCard = isGiftCard;
    }
}
