package pp.muza.monopoly.model;

import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.ActionType;

/**
 * ActionCard is a card that can be used by a player.
 * The action card has a name, an action  {@link Action} (like buying a property, paying rent, etc.),
 * a type of card {@link ActionType} (like optional, obligation, keepable etc.),
 * and a priority.
 */
public interface ActionCard {

    /**
     * Returns a name of the action card.
     *
     * @return the name of the action card
     */
    String getName();

    /**
     * A card takes some action, and this value indicates the card's action.
     *
     * @return the action of the card
     */
    Action getAction();

    /**
     * Returns type of the action card.
     *
     * @return the type of the action card
     */
    ActionType getType();

    /**
     * Returns priority of the action card.
     *
     * @return the priority of the action card
     */
    int getPriority();


}
