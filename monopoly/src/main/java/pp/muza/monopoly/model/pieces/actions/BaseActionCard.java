package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

import java.util.List;

/**
 * Base class for all action cards.
 * onExecute is a method that is executed when the card using.
 * It should be overridden by the subclasses.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class BaseActionCard implements ActionCard {

    public static final int HIGHEST_PRIORITY = 0;
    public static final int HIGH_PRIORITY = 50;
    public static final int NEW_TURN_PRIORITY = 100;
    public static final int DEFAULT_PRIORITY = 1000;
    public static final int LOW_PRIORITY = 10000;
    private static final Logger LOG = LoggerFactory.getLogger(BaseActionCard.class);

    private final String name;
    private final Action action;
    private final ActionType type;

    @EqualsAndHashCode.Exclude
    private final int priority;

    protected BaseActionCard(String name, Action action, ActionType type, int priority) {
        assert action.getClassList().contains(this.getClass());
        this.name = name;
        this.action = action;
        this.type = type;
        this.priority = priority;
    }

    protected BaseActionCard(Action action, ActionType type, int priority) {
        assert action.getClassList().contains(this.getClass());
        this.name = this.getClass().getSimpleName();
        this.action = action;
        this.type = type;
        this.priority = priority;
    }

    @SuppressWarnings("unused")
    protected boolean canBeUsed(Turn turn) {
        return true;
    }

    /**
     * Execute the action card.
     * This method is called when the card is playing.
     * Should be overridden by the subclasses.
     *
     * @param turn the turn of the game.
     * @return list of action cards spawned by the action card.
     */
    protected abstract List<ActionCard> onExecute(Turn turn);

    /**
     * Execute the action card. This method is called by the game engine.
     *
     * @param turn the turn of the game.
     * @return list of action cards spawned by the action card.
     */
    public final List<ActionCard> play(Turn turn) {
        LOG.debug("Executing card {} for player {}", this, turn.getPlayer().getName());
        List<ActionCard> result = this.onExecute(turn);
        LOG.debug("Resulting: {}", result);
        return result;
    }

}
