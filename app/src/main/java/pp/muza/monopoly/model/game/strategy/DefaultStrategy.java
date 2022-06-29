package pp.muza.monopoly.model.game.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.turn.TurnPlayer;

public final class DefaultStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);

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

            // Execute contracts action cards
            for (ActionCard actionCard : actionCards.stream().filter(x -> x.getType() == ActionCard.Type.CONTRACT)
                    .collect(Collectors.toList())) {
                assert actionCard.getAction() == ActionCard.Action.CONTRACT;
                try {
                    currentTurn.executeActionCard(actionCard);
                    actionCardsExecuted++;
                    // at list one action card has to be executed. break the loop with 50% chance
                    if (Math.random() < 0.5) {
                        break;
                    }
                } catch (ActionCardException e) {
                    LOG.warn("Error executing action card {}", actionCard.getName());
                }
            }

            // get all chance cards that are keepable
            List<ActionCard> keepable = actionCards.stream()
                    .filter(actionCard -> actionCard.getType() == ActionCard.Type.KEEPABLE)
                    .collect(Collectors.toList());
            // try to execute all keepable cards
            for (ActionCard actionCard : keepable) {
                try {
                    currentTurn.executeActionCard(actionCard);
                    actionCardsExecuted++;
                } catch (ActionCardException e) {
                    LOG.warn("Error executing action card {}", actionCard.getName());
                }
            }

            // get all chance cards
            List<ActionCard> chances = actionCards.stream()
                    .filter(actionCard -> actionCard.getType() == ActionCard.Type.CHANCE)
                    .collect(Collectors.toList());
            // execute at least one chance card if there are any
            while (!chances.isEmpty()) {
                ActionCard chance = chances.remove((int) (Math.random() * chances.size()));
                try {
                    currentTurn.executeActionCard(chance);
                    actionCardsExecuted++;
                    break;
                } catch (ActionCardException e) {
                    LOG.warn("Error executing action card {}", chance.getName());
                }
            }

            // execute optional action cards
            for (ActionCard actionCard : actionCards.stream().filter(x -> x.getType() == ActionCard.Type.OPTIONAL)
                    .collect(Collectors.toList())) {
                assert actionCard.getType() == ActionCard.Type.OPTIONAL;
                try {
                    // execute card with 50% chance
                    if (Math.random() < 0.5) {
                        currentTurn.executeActionCard(actionCard);
                        actionCardsExecuted++;
                    }
                } catch (ActionCardException e) {
                    LOG.warn("Error executing action card {}", actionCard.getName());
                }
            }

            // try to execute mandatory action cards
            for (ActionCard actionCard : actionCards.stream().filter(x -> x.getType() == ActionCard.Type.OBLIGATION)
                    .collect(Collectors.toList())) {
                assert actionCard.getType() == ActionCard.Type.OBLIGATION;
                try {
                    currentTurn.executeActionCard(actionCard);
                    actionCardsExecuted++;
                } catch (ActionCardException e) {
                    LOG.warn("Error executing action card {}", actionCard.getName());
                    if (!e.isFinal) {
                        actionCardsExecuted++;
                    }
                }
            }
            loopCount++;
        } while (actionCardsExecuted > 0);
    }
}
