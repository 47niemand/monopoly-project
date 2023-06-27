package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import pp.muza.monopoly.model.Fortune;

/**
 * This class represents an action that can be executed by a player.
 *
 * @author dmitro.muza
 */
public enum Action {
    /**
     * Player arrives to a land and should use this action card.
     */
    ARRIVAL(ImmutableList.of(Arrival.class, GetOrPay.class, Takeover.class)),
    /**
     * Start the auction.
     */
    AUCTION(ImmutableList.of(StartAuction.class)),
    /**
     * Bid for the property.
     */
    BID(ImmutableList.of(Bid.class)),
    /**
     * Any property-related action in which the player can buy a property.
     */
    BUY(ImmutableList.of(BaseBuy.class, Buy.class, OwnershipPrivilege.class)),
    /**
     * This is a specific card that stores the chance pile of the game.
     * <p>It should be returned to the game when the card is used.
     * A Card with this type must implement {@link Fortune} interface.
     * </p>
     */
    CHANCE(ImmutableList.of(FortuneCard.class)),
    /**
     * Contract, any property-related activity in which the player can sale a property.
     */
    CONTRACT(ImmutableList.of(Contract.class, Sale.class)),
    /**
     * Player pays the given number of coins to other players or the bank.
     */
    DEBT(ImmutableList.of(BaseDebt.class, BasePayment.class, PayRent.class, Gift.class, JailFine.class, Tax.class)),
    /**
     * Play some action.
     */
    DEFAULT(ImmutableList.of(BaseActionCard.class, SpawnMoveAndTakeover.class, ChoiceFortuneCard.class, ChoiceContract.class, ChoiceAuction.class)),
    /**
     * End turn.
     */
    END_TURN(ImmutableList.of(EndTurn.class)),
    /**
     * Go to jail.
     */
    GO_TO_JAIL(ImmutableList.of(GoToJail.class)),
    /**
     * Get income.
     */
    INCOME(ImmutableList.of(Income.class, GoReward.class, ReceiveMoney.class, RentRevenue.class)),
    /**
     * Move forward by the number of steps.
     */
    MOVE(ImmutableList.of(Move.class, OptionMove.class)),
    /**
     * Move to the land with the given id.
     */
    MOVE_TO(ImmutableList.of(MoveTo.class, MoveGetOrPay.class, MoveAndTakeover.class)),
    /**
     * New turn, the player starts a new turn with this card.
     */
    NEW_TURN(ImmutableList.of(NewTurn.class)),
    /**
     * PromoteAuction for the property on the auction.
     */
    OFFER(ImmutableList.of(BaseAuction.class, PromoteAuction.class, EndAuction.class)),
    /**
     * Birthday party.
     */
    PARTY(ImmutableList.of(BirthdayParty.class)),
    /**
     * Roll dice get random number.
     */
    ROLL_DICE(ImmutableList.of(RollDice.class)),
    /**
     * Submit offer.
     */
    SUBMIT(ImmutableList.of(Submit.class));

    @Getter
    private final List<Class<? extends BaseActionCard>> classList;

    Action(List<Class<? extends BaseActionCard>> list) {
        this.classList = ImmutableList.copyOf(list);
    }
}
