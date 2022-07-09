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
 * This strategy executes action cards in the order they are in the player's hand.
 * It can skip optional cards with a probability of 1/2
 */
public final class DefaultStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);

    public static final DefaultStrategy STRATEGY = new DefaultStrategy();

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
                    switch (actionCard.getType()) {

                        case OPTIONAL:
                            if (Math.random() < 0.5) {
                                if (currentTurn.playCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping optional card {}", actionCard.getName());
                            }
                            break;
                        case CONTRACT:
                            if (Math.random() < 0.5) {
                                if (currentTurn.playCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping contract card {}", actionCard.getName());
                            }
                            break;
                        case OBLIGATION:
                        case CHOOSE:
                        case KEEPABLE:
                            if (currentTurn.playCard(actionCard)) {
                                actionCardsExecuted++;
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown action card type: " + actionCard.getType());
                    }
                } catch (TurnException e) {
                    LOG.error("Error playing action card {}: {}", actionCard.getName(), e.getMessage());
                    break;
                }
            }
            loopCount++;
        } while (actionCardsExecuted > 0 && !currentTurn.isFinished());
    }
}
