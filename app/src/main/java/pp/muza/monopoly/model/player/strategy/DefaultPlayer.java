package pp.muza.monopoly.model.player.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.game.Turn;

public class DefaultPlayer extends Player {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlayer.class);

    public DefaultPlayer(String name) {
        super(name);
    }

    @Override
    public void playTurn(Turn currentTurn) {
        List<ActionCard> actionCards;
        int actionCardsExecuted;
        do {
            actionCardsExecuted = 0;
            actionCards = currentTurn.getActiveActionCards();
            // execute optional action cards first
            for (ActionCard actionCard : actionCards) {
                if (!actionCard.isMandatory()) {
                    try {
                        actionCard.execute(currentTurn);
                        actionCardsExecuted++;
                    } catch (ActionCardException e) {
                        LOG.error("Error executing action card {}", actionCard.getName());
                    }
                }
            }
            // execute mandatory action cards
            for (ActionCard actionCard : actionCards) {
                if (actionCard.isMandatory()) {
                    try {
                        actionCard.execute(currentTurn);
                        actionCardsExecuted++;
                    } catch (ActionCardException e) {
                        LOG.error("Error executing action card {}", actionCard.getName());
                        if (!e.isFinal) {
                            actionCardsExecuted++;
                        }
                    }
                }
            }
        } while (!actionCards.isEmpty() && actionCardsExecuted > 0);
    }
}
