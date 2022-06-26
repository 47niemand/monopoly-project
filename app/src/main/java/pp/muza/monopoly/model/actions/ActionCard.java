package pp.muza.monopoly.model.actions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.contracts.Contract;
import pp.muza.monopoly.model.actions.strategy.Buy;
import pp.muza.monopoly.model.actions.strategy.EndTurn;
import pp.muza.monopoly.model.actions.strategy.GoToJail;
import pp.muza.monopoly.model.actions.strategy.Income;
import pp.muza.monopoly.model.actions.strategy.Move;
import pp.muza.monopoly.model.actions.strategy.NewTurn;
import pp.muza.monopoly.model.actions.strategy.PayRent;
import pp.muza.monopoly.model.actions.strategy.RollDice;
import pp.muza.monopoly.model.actions.strategy.Tax;
import pp.muza.monopoly.model.game.Turn;

/**
 * ActionThe action card is a card that can be used by the player.
 * The action card has a name, a type, a priority, a boolean that indicates that card is mandatory, and a boolean that indicates if the card is usable.
 * onExecute is a method that is executed when the card is used. It should be overridden by the subclasses. The method execute should be called by the turn.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(ActionCard.class);

    private final String name;
    private final ActionType type;
    private final int priority;
    private final boolean isMandatory;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private boolean isExecuted;

    protected ActionCard(String name, ActionType type, int priority, boolean isMandatory) {
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.isMandatory = isMandatory;
        this.isExecuted = false;
    }

    protected abstract void onExecute(Turn turn) throws ActionCardException;

    public final void execute(Turn turn) throws ActionCardException {
        if (isExecuted) {
            LOG.warn("ActionCard {} has already been executed", name);
            throw new ActionCardException("ActionCard already executed", this);
        }
        LOG.info("Executing action card: {}", this);
        try {
            onExecute(turn);
            isExecuted = true;
        } catch (Exception e) {
            LOG.warn("Action card execution failed: {}", this);
            throw e;
        }
        turn.markActionCardAsUsed(this);
    }

    public enum ActionType {
        NEW_TURN(NewTurn.class), //
        ROLL_DICE(RollDice.class), // roll dice get random number
        MOVE(Move.class), // move to next land
        BUY(Buy.class), // buy property
        PAY_RENT(PayRent.class), // pay rent
        TAX(Tax.class), // pay tax
        CONTRACT(Contract.class), // contract
        GO_TO_JAIL(GoToJail.class), // go to jail
        //TODO: CHANCE(Chance.class), // get chance card
        INCOME(Income.class), // get income
        END_TURN(EndTurn.class); // end turn

        final Class<? extends ActionCard> aClass;

        ActionType(Class<? extends ActionCard> actionCardClass) {
            this.aClass = actionCardClass;
        }
    }


}
