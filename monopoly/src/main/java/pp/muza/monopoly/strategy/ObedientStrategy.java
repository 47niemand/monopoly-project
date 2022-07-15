package pp.muza.monopoly.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Strategy;
import pp.muza.monopoly.model.TurnPlayer;
import pp.muza.monopoly.errors.TurnException;

/**
 * This strategy executes all action cards in the order they are in the player's
 * hand.
 */
public final class ObedientStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(ObedientStrategy.class);

    public static final ObedientStrategy STRATEGY = new ObedientStrategy();

    @Override
    public void playTurn(TurnPlayer currentTurn) {
        List<ActionCard> actionCards;
        int actionCardsExecuted;
        int loopCount = 1;
        do {
            if (loopCount > 20) {
                throw new RuntimeException("Too many loops in turn execution");
            }
            LOG.info("Step {}", loopCount);
            actionCardsExecuted = 0;
            actionCards = currentTurn.getActiveActionCards();

            for (ActionCard actionCard : actionCards) {
                try {
                    if (currentTurn.playCard(actionCard)) {
                        actionCardsExecuted++;
                    }
                } catch (TurnException e) {
                    LOG.warn("Error playing card {}: {}", actionCard.getName(), e.getMessage());
                    break;
                }
            }
            loopCount++;
        } while (actionCardsExecuted > 0 && !currentTurn.isFinished());
    }

}
