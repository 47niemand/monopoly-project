package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;

/**
 * @author dmytromuza
 */
public final class PlayerData {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerData.class);

    final Player player;
    private final List<ActionCard> cards = new ArrayList<>();
    private final List<ActionCard> hold = new ArrayList<>();
    PlayerStatus status;
    int position;

    public PlayerData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public void setStatus(PlayerStatus status) {
        if (this.status == null) {
            LOG.debug("{}: set status to {}", player.getName(), status);
        } else {
            LOG.info("{}: changing status from {} to {}", this.player.getName(), this.status, status);
        }
        this.status = status;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        if (this.position != position) {
            LOG.info("{} is moving from position {} to position {}", this.player.getName(), this.position, position);
            this.position = position;
        } else {
            LOG.info("{} at position {}", this.player.getName(), this.position);
        }
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

    public void holdCard(ActionCard actionCard) {
        ActionCard card = removeCard(actionCard);
        if (card != null) {
            LOG.debug("Holding card: {}", card);
            hold.add(card);
        } else {
            LOG.warn("Cannot hold card: {}", actionCard);
        }
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

    /**
     * Adds the card to the player's hand. card can be added only if the player does not have it already.
     *
     * @param card the card to add
     * @return true if the card was added and false if the card was not added
     */
    public boolean addCard(ActionCard card) {
        if (cards.contains(card)) {
            LOG.debug("Card {} already on player's hand", card);
            return false;
        }
        LOG.debug("{}: adding card '{}'", player.getName(), card);
        return cards.add(card);
    }

    /**
     * Removes card from player's hand.
     *
     * @param card card to remove
     * @return removed card or null if card was not found
     */
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

    @Override
    public String toString() {
        return "BaseGame.PlayerData(player=" + this.getPlayer()
                + ", status=" + this.getStatus()
                + ", position=" + this.getPosition()
                + ", cards=" + this.cards.stream().map(ActionCard::getName).collect(Collectors.toList())
                + ", hold=" + this.hold.stream().map(ActionCard::getName).collect(Collectors.toList())
                + ")";
    }

    public void assign(PlayerInfo playerInfo) {

    }
}
