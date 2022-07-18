package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

/**
 * Base class for all action cards.
 * onExecute is a method that is executed when the card using.
 * It should be overridden by the subclasses.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class BaseActionCard implements ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BaseActionCard.class);

    private final String name;
    private final Action action;
    private final Type type;

    @EqualsAndHashCode.Exclude
    private final int priority;

    protected BaseActionCard(String name, Action action, Type type, int priority) {
        this.name = name;
        this.action = action;
        this.priority = priority;
        this.type = type;
    }

    /**
     * Execute the action card.
     *
     * @param turn the turn of the game.
     * @return list of action cards spawned by the action card.
     */
    protected abstract List<ActionCard> onExecute(Turn turn);

    public List<ActionCard> play(Turn turn) {
        LOG.debug("Executing card {} for player {}", this, turn.getPlayer().getName());
        List<ActionCard> result = this.onExecute(turn);
        LOG.debug("Resulting: {}", result);
        return result;
    }

}
