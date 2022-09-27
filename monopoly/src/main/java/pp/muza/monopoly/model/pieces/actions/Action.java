package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import pp.muza.monopoly.model.Fortune;

public enum Action {
    /**
     * Player arrives to a land and should use this action card.
     */
    ARRIVAL(ImmutableList.of(Arrival.class, Takeover.class)),
    /**
     * Any property-related action in which the player can buy a property.
     */
    BUY(ImmutableList.of(Buy.class)),
    /**
     * chance; this is a specific card that stores the chance pile of the game.
     * It should be returned to the game when the card is used.
     * A Card with this type must implement {@link Fortune} interface.
     */
    CHANCE(ImmutableList.of(FortuneCard.class)),
    /**
     * Contract, any property-related activity in which the player can sale a property.
     */
    CONTRACT(ImmutableList.of(Contract.class)),
    /**
     * End turn.
     */
    END_TURN(ImmutableList.of(EndTurn.class)),
    /**
     * Get a gift.
     */
    GIFT(ImmutableList.of(SpawnGiftCard.class, TakeFortuneCard.class)),
    /**
     * Go to jail.
     */
    GO_TO_JAIL(ImmutableList.of(GoToJail.class)),
    /**
     * Get income.
     */
    INCOME(ImmutableList.of(Income.class, GoReward.class, ReceiveMoney.class, RentRevenue.class)),
    /**
     * Move by numbing of steps.
     */
    MOVE(ImmutableList.of(Move.class, OptionMove.class)),
    /**
     * Move to the land with the given id.
     */
    MOVE_TO(ImmutableList.of(MoveTo.class, OptionMoveTo.class, MoveAndTakeover.class)),
    /**
     * New turn, the player starts a new turn with this card.
     */
    NEW_TURN(ImmutableList.of(NewTurn.class)),
    /**
     * Birthday party.
     */
    PARTY(ImmutableList.of(BirthdayParty.class)),
    /**
     * Player pays the given number of coins to other players.
     */
    PAY(ImmutableList.of(Payment.class, PayRent.class, Gift.class)),
    /**
     * roll dice get random number.
     * this card can be added only before the {@link Action#END_TURN} card.
     */
    ROLL_DICE(ImmutableList.of(RollDice.class)),
    /**
     * Pay tax or fine to the bank.
     */
    TAX(ImmutableList.of(Tax.class, JailFine.class));

    @Getter
    private final List<Class<? extends BaseActionCard>> classList;

    Action(List<Class<? extends BaseActionCard>> list) {
        this.classList = ImmutableList.copyOf(list);
    }
}
