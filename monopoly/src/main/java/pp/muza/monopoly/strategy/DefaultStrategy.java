package pp.muza.monopoly.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.actions.ChanceCard;
import pp.muza.monopoly.model.game.PlayerStatus;
import pp.muza.monopoly.model.game.Strategy;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.game.TurnPlayer;

import java.util.Collections;
import java.util.List;

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
            // add some randomness to the order of the cards
            Collections.shuffle(actionCards);
            for (ActionCard actionCard : actionCards) {
                try {
                    switch (actionCard.getType()) {
                        case KEEPABLE:
                            if (currentTurn.getStatus() == PlayerStatus.IN_JAIL && actionCard.getAction() == ActionCard.Action.CHANCE && ((Chance) actionCard).getCard() == ChanceCard.GET_OUT_OF_JAIL_FREE) {
                                // if player in jail and has chance card to get out, play it
                                if (currentTurn.playCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            }
                            break;
                        case OPTIONAL:
                            if (Math.random() < 0.5) {
                                // play optional card with probability 1/2
                                if (currentTurn.playCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping optional card {}", actionCard.getName());
                            }
                            break;
                        case CONTRACT:
                            if (Math.random() < 0.5) {
                                // play contract card with probability 1/2
                                if (currentTurn.playCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping contract card {}", actionCard.getName());
                            }
                            break;
                        case OBLIGATION:
                        case CHOOSE:
                            // try to play action card
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
