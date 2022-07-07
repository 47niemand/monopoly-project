package pp.muza.monopoly.model.game.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.game.TurnPlayer;

public final class DefaultStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);

    public static final DefaultStrategy strategy = new DefaultStrategy();

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
                    actionCards.stream().map(ActionCard::getName).collect(Collectors.joining(", ")));

            for (ActionCard actionCard : actionCards) {
                try {
                    switch (actionCard.getType()) {

                        case OPTIONAL:
                            if (Math.random() < 0.5) {
                                LOG.info("Executing optional card {}", actionCard);
                                if (currentTurn.executeActionCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping optional card {}", actionCard);
                            }
                            break;
                        case CONTRACT:
                            if (Math.random() < 0.5) {
                                LOG.info("Executing contract card {}", actionCard);
                                if (currentTurn.executeActionCard(actionCard)) {
                                    actionCardsExecuted++;
                                }
                            } else {
                                LOG.info("Skipping contract card {}", actionCard);
                            }
                            break;
                        case OBLIGATION:
                        case CHANCE:
                        case KEEPABLE:
                            LOG.info("Executing card {}", actionCard);
                            if (currentTurn.executeActionCard(actionCard)) {
                                actionCardsExecuted++;
                            }
                            break;
                    }
                } catch (TurnException e) {
                    LOG.error("Error executing action card {}: {}", actionCard.getName(), e.getMessage());
                    break;
                }
            }
            loopCount++;
        } while (actionCardsExecuted > 0 && !currentTurn.isFinished());
    }
}
