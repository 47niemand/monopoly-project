package pp.muza.monopoly.model.game;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
public final class PlayerContext {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerContext.class);

    final Player player;
    PlayerStatus status;
    int position;
    private final List<ActionCard> cards = new ArrayList<>();
    private final List<ActionCard> hold = new ArrayList<>();

    public PlayerContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public int getPosition() {
        return this.position;
    }

    public List<ActionCard> getCards() {
        return Stream.of(cards, hold).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    public int getCurrentPriority() {
        boolean seen1 = false, seen2 = false;
        int best1 = 0, best2 = 0;
        for (ActionCard actionCard1 : cards) {
            if (actionCard1.getType().isMandatory()) {
                int actionCardPriority = actionCard1.getPriority();
                if (!seen1 || actionCardPriority < best1) {
                    seen1 = true;
                    best1 = actionCardPriority;
                }
            } else {
                int actionCardPriority = actionCard1.getPriority();
                if (!seen2 || actionCardPriority < best2) {
                    seen2 = true;
                    best2 = actionCardPriority;
                }
            }
        }
        return seen1 ? best1 : (seen2 ? best2 : BaseActionCard.LOW_PRIORITY);
    }

    public boolean holdCard(ActionCard actionCard) {
        ActionCard card = removeCard(actionCard);
        if (card != null) {
            LOG.debug("Holding card: {}", card);
            hold.add(card);
            return true;
        } else {
            LOG.warn("Cannot hold card: {}", actionCard);
        }
        return false;
    }

    public void releaseAll() {
        for (ActionCard holdCard : hold) {
            LOG.debug("Releasing card: {}", holdCard);
            addCard(holdCard);
        }
        hold.clear();
    }

    public List<ActionCard> getActiveCards() {
        List<ActionCard> result;
        int currentPriority = getCurrentPriority();
        result = cards.stream()
                .filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .sorted(Comparator.comparing(ActionCard::getPriority))
                .collect(Collectors.toUnmodifiableList());
        return result;
    }


    public boolean addCard(ActionCard card) {
        if (cards.contains(card) || hold.contains(card)) {
            LOG.debug("Card {} already on player's hand", card);
            return false;
        }
        LOG.debug("{}: adding card '{}'", player.getName(), card);
        return cards.add(card);
    }

    public ActionCard removeCard(ActionCard card) {
        ActionCard result = null;
        List<ActionCard> removed;
        removed = cards.stream()
                .filter(actionCard -> actionCard.equals(card))
                .collect(Collectors.toList());
        if (removed.size() > 1) {
            LOG.warn("{}: more than one card '{}' found", player.getName(), card);
            result = removed.get(0);
        } else if (removed.size() == 1) {
            result = removed.get(0);
        }
        cards.removeAll(removed);
        hold.removeAll(removed);
        return result;
    }

    public void setStatus(PlayerStatus status) {
        if (this.status == null) {
            LOG.debug("{}: set status to {}", player.getName(), status);
        } else {
            LOG.info("{}: changing status from {} to {}", this.player.getName(), this.status, status);
        }
        this.status = status;
    }

    public void setPosition(int position) {
        if (this.position != position) {
            LOG.info("{} is moving from position {} to position {}", this.player.getName(), this.position, position);
            this.position = position;
        } else {
            LOG.info("{} at position {}", this.player.getName(), this.position);
        }
    }

}
