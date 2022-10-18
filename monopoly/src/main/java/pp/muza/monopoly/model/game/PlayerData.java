package pp.muza.monopoly.model.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dmytromuza
 */
final class PlayerData {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerData.class);

    private final Player player;

    private final List<ActionCard> cards = new ArrayList<>();
    private final List<ActionCard> hold = new ArrayList<>();

    private PlayerStatus status;
    private int position;

    PlayerData(Player player) {
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
            LOG.debug("{}: set status to {}", player, status);
        } else {
            LOG.info("{}: changing status from {} to {}", this.player, this.status, status);
        }
        this.status = status;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        if (this.position != position) {
            LOG.info("{} is moving from position {} to position {}", this.player, this.position, position);
            this.position = position;
        } else {
            LOG.info("{} at position {}", this.player, this.position);
        }
    }

    public List<ActionCard> getCards() {
        return Collections.unmodifiableList(cards);
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
        LOG.info("{}: holding card {}", this.player, actionCard);
        if (cards.contains(actionCard)) {
            if (hold.contains(actionCard)) {
                LOG.warn("{}: card {} is already held", this.player, actionCard);
            } else {
                int i = cards.indexOf(actionCard);
                hold.add(cards.get(i));
            }
        } else {
            LOG.warn("{}: card {} is not in the player's hand", this.player, actionCard);
        }
    }

    public void releaseAll() {
        if (!hold.isEmpty()) {
            LOG.debug("Releasing all cards: {}", hold);
            hold.clear();
        }
    }

    public boolean canUseCard(ActionCard actionCard) {
        int priority = getCurrentPriority();
        return cards.contains(actionCard) && actionCard.getPriority() <= priority;
    }

    public List<ActionCard> getActiveCards() {
        List<ActionCard> result;
        int currentPriority = getCurrentPriority();
        result = cards.stream()
                .filter(actionCard -> actionCard.getPriority() <= currentPriority)
                .filter(actionCard -> !hold.contains(actionCard))
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
        boolean result;
        if (cards.contains(card)) {
            if (hold.contains(card)) {
                LOG.debug("Releasing card {} from hold for {}", card, player);
                result = hold.removeAll(cards);
            } else {
                LOG.debug("Card {} already in hand for {}", card, player);
                result = false;
            }
        } else {
            LOG.debug("Adding card {} to player {}", card, player);
            result = cards.add(card);
        }
        return result;
    }

    private List<ActionCard> getByCard(ActionCard card) {
        return this.cards.stream()
                .filter(actionCard -> actionCard.equals(card))
                .collect(Collectors.toList());
    }

    /**
     * Removes card from player's hand.
     *
     * @param card card to remove
     * @return removed card or null if card was not found
     */
    public ActionCard removeCard(ActionCard card) {
        LOG.debug("Removing card {} from player {}", card, player);
        ActionCard result = null;
        List<ActionCard> toRemove;
        toRemove = getByCard(card);
        if (toRemove.size() > 0) {
            result = toRemove.get(0);
        }
        if (toRemove.size() > 1) {
            LOG.warn("Found more than one card to remove: {}", toRemove);
        }
        cards.removeAll(toRemove);
        hold.removeAll(toRemove);
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
