package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import pp.muza.monopoly.model.Fortune;

public enum Action {
    /**
     * New turn, the player starts a new turn with this card.
     */
    NEW_TURN(ImmutableList.of(NewTurn.class)),
    /**
     * roll dice get random number.
     */
    ROLL_DICE(ImmutableList.of(RollDice.class)),
    /**
     * Move by numbing of steps.
     */
    MOVE(ImmutableList.of(Move.class, OptionMove.class)),
    /**
     * Move to the land with the given id.
     */
    MOVE_TO(ImmutableList.of(MoveTo.class, OptionMoveTo.class, MoveAndTrade.class)),
    /**
     * Player arrives to a land and should use this card.
     */
    ARRIVAL(ImmutableList.of(Arrival.class)),
    /**
     * Player pays the given number of coins to other players.
     */
    PAY(ImmutableList.of(Payment.class, PayRent.class, Gift.class)),
    /**
     * Pay tax or fine to the bank.
     */
    TAX(ImmutableList.of(Tax.class, JailFine.class)),
    /**
     * Any property-related action in which the player can buy a property.
     */
    BUY(ImmutableList.of(Buy.class)),
    /**
     * Contract, any property-related activity in which the player can sale a property.
     */
    CONTRACT(ImmutableList.of(Contract.class)),
    /**
     * Go to jail.
     */
    GO_TO_JAIL(ImmutableList.of(GoToJail.class)),
    /**
     * chance; this is a specific card that stores the chance pile of the game.
     * It should be returned to the game when the card is used.
     * A Card with this type must implement {@link Fortune} interface.
     */
    CHANCE(ImmutableList.of(FortuneCard.class)),
    /**
     * Get income.
     */
    INCOME(ImmutableList.of(Income.class, GoReward.class, ReceiveMoney.class, RentRevenue.class)),
    /**
     * End turn.
     */
    END_TURN(ImmutableList.of(EndTurn.class)),
    /**
     * Get a gift.
     */
    GIFT(ImmutableList.of(SpawnGiftCard.class, TakeFortuneCard.class)),
    /**
     * Birthday party.
     */
    PARTY(ImmutableList.of(BirthdayParty.class));

    @Getter
    private final List<Class<? extends BaseActionCard>> classList;

    Action(List<Class<? extends BaseActionCard>> list) {
        this.classList = ImmutableList.copyOf(list);
    }
}
