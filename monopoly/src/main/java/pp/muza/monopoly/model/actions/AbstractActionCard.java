package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.Turn;

/**
 * ActionThe action card is a card that can be used by the player.
 * The action card has a name, a type of action (like buying a property, paying
 * rent, etc.), a type of card (like chance, obligation, etc.), and a priority.
 * onExecute is a method that is executed when the card is using. It should be
 * overridden by the subclasses. The method execute should be called by the
 * {@link Turn}.
 * {@author dmitr}
 */
@Getter
@ToString
@EqualsAndHashCode
abstract class AbstractActionCard implements ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractActionCard.class);

    private final String name;
    private final Action action;
    private final Type type;

    @EqualsAndHashCode.Exclude
    private final int priority;

    protected AbstractActionCard(String name, Action action, Type type, int priority) {
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

}
