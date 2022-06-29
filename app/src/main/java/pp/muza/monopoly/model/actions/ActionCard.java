package pp.muza.monopoly.model.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.turn.Turn;

/**
 * ActionThe action card is a card that can be used by the player.
 * The action card has a name, a type of action (like buying a property, paying
 * rent, etc.), a type of card (like chance, obligation, etc.), and a priority.
 * onExecute is a method that is executed when the card is using. It should be
 * overridden by the subclasses. The method execute should be called by the
 * {@link Turn}.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class ActionCard {

    public static final int HIGH_PRIORITY = 0;
    public static final int NEW_TURN_PRIORITY = 10;
    public static final int DEFAULT_PRIORITY = 100;
    public static final int LOW_PRIORITY = 10000;

    private static final Logger LOG = LoggerFactory.getLogger(ActionCard.class);

    private final String name;
    private final Action action;
    private final Type type;

    @EqualsAndHashCode.Exclude
    private final int priority;

    protected ActionCard(String name, Action action, Type type, int priority) {
        this.name = name;
        this.action = action;
        this.priority = priority;
        this.type = type;
    }

    protected abstract void onExecute(Turn turn) throws ActionCardException;

    public final void execute(Turn turn) throws ActionCardException {
        LOG.info("Executing action card: {}", this);
        try {
            onExecute(turn);
            turn.removeCardsWhenUsed(this);
        } catch (ActionCardException e) {
            LOG.warn("Action card execution failed: {}", this);
            throw e;
        }
    }

    public enum Type {
        OPTIONAL(false), OBLIGATION(true), CHANCE(true), KEEPABLE(false), CONTRACT(false);

        private final boolean mandatory;

        Type(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public boolean isMandatory() {
            return mandatory;
        }

    }

    public enum Action {
        NEW_TURN, // New turn
        ROLL_DICE, // roll dice get random number
        MOVE, // move to next land
        ARRIVAL, // player arrives to a land
        BUY, // buy property
        PAY_RENT, // pay rent
        TAX, // pay tax or fine
        CONTRACT, // contract
        GO_TO_JAIL, // go to jail
        CHANCE, // get chance card, this is a special card that stores the chance pile of the
                // game. it should be returned to the game when the card is used.
        INCOME, // get income
        END_TURN, // end turn
        GIFT // get gift
        ;

        Action() {
        }
    }
}
