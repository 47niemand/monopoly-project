package pp.muza.monopoly.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Strategy;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.game.TurnPlayer;

/**
 * This strategy executes all action cards in the order they are in the player's hand.
 */
public final class ObedientStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(ObedientStrategy.class);

    public static final ObedientStrategy strategy = new ObedientStrategy();

    @Override
    public void playTurn(TurnPlayer currentTurn) {
        List<ActionCard> actionCards;
        int actionCardsExecuted;
        int loopCount = 1;
        do {
            if (loopCount > 20) {
                throw new RuntimeException("Too many loops in turn execution");
            }
            LOG.info("Executing step {}", loopCount);
            actionCardsExecuted = 0;
            actionCards = currentTurn.getActiveActionCards();
            LOG.info("Active action cards: {}",
                    actionCards.stream().map(ActionCard::getName).collect(Collectors.toList()));

            for (ActionCard actionCard : actionCards) {
                try {
                    LOG.info("Executing card {}", actionCard.getName());
                    if (currentTurn.executeActionCard(actionCard)) {
                        actionCardsExecuted++;
                    }
                } catch (TurnException e) {
                    LOG.warn("Error executing action card {}: {}", actionCard.getName(), e.getMessage());
                    break;
                }
            }
            loopCount++;
        } while (actionCardsExecuted > 0 && !currentTurn.isFinished());
    }
}
