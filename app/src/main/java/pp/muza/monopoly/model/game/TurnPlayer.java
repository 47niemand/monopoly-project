package pp.muza.monopoly.model.game;

import java.util.List;

import pp.muza.monopoly.model.actions.ActionCard;

public interface TurnPlayer {

    List<ActionCard> getActiveActionCards();

    boolean executeActionCard(ActionCard actionCard) throws TurnException;

    boolean isFinished();
}
