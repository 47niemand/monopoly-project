package pp.muza.monopoly.model.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.game.Turn;

public class ActionCardExecute {

    private static final Logger LOG = LoggerFactory.getLogger(ActionCardExecute.class);

    public static List<ActionCard> execute(Turn turn, ActionCard actionCard) {
        LOG.info("Executing card {}", actionCard);
        List<ActionCard> result = actionCard.onExecute(turn);
        LOG.info("Resulting: {}", result.stream().map(ActionCard::getName).collect(Collectors.toList()));
        return result;
    }

}
