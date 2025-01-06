package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import lombok.Getter;
import pp.muza.monopoly.model.Fortune;

/**
 * This class represents an action that can be executed by a player.
 *
 * @author dmitro.muza
 */
@Getter
public enum Action {
    /**
     * Player arrives to a land and should use this action card.
     */
    ARRIVAL(List.of(Arrival.class, GetOrPay.class, Takeover.class)),
    /**
     * Start the auction.
     */
    AUCTION(List.of(StartAuction.class)),
    /**
     * Bid for the property.
     */
    BID(List.of(Bid.class)),
    /**
     * Any property-related action in which the player can buy a property.
     */
    BUY(List.of(BaseBuy.class, Buy.class, OwnershipPrivilege.class)),
    /**
     * This is a specific card that stores the chance pile of the game.
     * <p>
     * It should be returned to the game when the card is used.
     * A Card with this type must implement {@link Fortune} interface.
     * </p>
     */
    CHANCE(List.of(FortuneCard.class)),
    /**
     * Contract, any property-related activity in which the player can sale a
     * property.
     */
    CONTRACT(List.of(Contract.class, Sale.class)),
    /**
     * Player pays the given number of coins to other players or the bank.
     */
    DEBT(List.of(BaseDebt.class, BasePayment.class, PayRent.class, Gift.class, JailFine.class, Tax.class)),
    /**
     * Play some action.
     */
    DEFAULT(List.of(BaseActionCard.class, SpawnMoveAndTakeover.class, ChoiceFortuneCard.class, ChoiceContract.class,
            ChoiceAuction.class)),
    /**
     * End turn.
     */
    END_TURN(List.of(EndTurn.class)),
    /**
     * Go to jail.
     */
    GO_TO_JAIL(List.of(GoToJail.class)),
    /**
     * Get income.
     */
    INCOME(List.of(Income.class, GoReward.class, ReceiveMoney.class, RentRevenue.class)),
    /**
     * Move forward by the number of steps.
     */
    MOVE(List.of(Move.class, OptionMove.class)),
    /**
     * Move to the land with the given id.
     */
    MOVE_TO(List.of(MoveTo.class, MoveGetOrPay.class, MoveAndTakeover.class)),
    /**
     * New turn, the player starts a new turn with this card.
     */
    NEW_TURN(List.of(NewTurn.class)),
    /**
     * PromoteAuction for the property on the auction.
     */
    OFFER(List.of(BaseAuction.class, PromoteAuction.class, EndAuction.class)),
    /**
     * Birthday party.
     */
    PARTY(List.of(BirthdayParty.class)),
    /**
     * Roll dice get random number.
     */
    ROLL_DICE(List.of(RollDice.class)),
    /**
     * Submit offer.
     */
    SUBMIT(List.of(Submit.class));

    private final List<Class<? extends BaseActionCard>> classList;

    Action(List<Class<? extends BaseActionCard>> list) {
        this.classList = List.copyOf(list);
    }
}
