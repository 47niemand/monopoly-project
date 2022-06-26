package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.actions.strategy.NewTurn;

/**
 * The turn of the game.
 * <p>
 * The turn is composed of a list of action cards.
 * There are different action cards (i.e. "Pay Rent", "New Turn", "Buy Land",
 * ...)
 * Players can execute action cards (actionCard.execute()).
 * Each action card has a boolean indicating if it is a mandatory: the ones that
 * have to be used during the turn (isMandatory=true) and the ones that are not.
 * By executing an action card, the player can move to the next land or buy a
 * property or pay rent or pay tax etc. Or, player can get new action(s) card.
 * Actions can be executed in a priority order (the lowest first).
 * getActiveActionCards returns the list of action cards that can be executed at
 * the current moment.
 * The turn can be considered as finished when there are no more mandatory
 * action cards to execute.
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode
public class Turn {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private final Game game;
    private final Player player;
    @Getter(AccessLevel.PROTECTED)
    private final List<ActionCard> actionCards = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private final List<ActionCard> usedActionCards = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private boolean isFinished;

    public Turn(Game game, Player player) {
        LOG.info("New turn for player {}", player.getName());
        this.game = game;
        this.player = player;
        initialize();
    }

    private void initialize() {
        addActionCard(NewTurn.of());
    }

    public void endTurn() {
        isFinished = true;
    }

    public boolean addActionCard(ActionCard actionCards) {
        LOG.info("Adding action card {}", actionCards);
        // add the action card to the player's hand if it is not already in the player's
        // hand
        if (!this.actionCards.contains(actionCards)) {
            this.actionCards.add(actionCards);
            return true;
        }
        LOG.info("Action card {} already in the player's hand", actionCards.getName());
        return false;
    }

    public List<ActionCard> getActiveActionCards() {
        if (isFinished) {
            return ImmutableList.of();
        }
        int currentPriority = actionCards.stream().filter(ActionCard::isMandatory).mapToInt(ActionCard::getPriority)
                .min().orElse(0);
        return actionCards.stream().filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .collect(Collectors.toList());
    }

    public void markActionCardAsUsed(ActionCard actionCard) {
        LOG.info("Mark action card {} as used", actionCard.getName());
        if (actionCards.remove(actionCard)) {
            usedActionCards.add(actionCard);
        } else {
            throw new IllegalArgumentException("Action card not found");
        }
    }

    public void finish() {
        LOG.info("Turn finished for player {}", player.getName());
        isFinished = true;
    }
}
