package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnError;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.game.impl.TurnImpl;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;
import pp.muza.monopoly.model.pieces.actions.SyncCard;


/**
 * Base implementation of the {@link PlayTurn} interface.
 * Marked as abstract because it is not intended to be used directly.
 *
 * @author dmytromuza
 */
public abstract class BasePlayTurn implements PlayTurn {

    public static final int MAX_STEPS_PER_TURN = 30;
    private static final Logger LOG = LoggerFactory.getLogger(BasePlayTurn.class);
    private final Player player;
    private final int turnNumber;
    private final List<ActionCard> usedCards = new ArrayList<>();
    private final Turn turn;
    private boolean finished = false;
    private int steps = 0;

    protected BasePlayTurn(Player player, int turnNumber) {
        this.player = player;
        this.turnNumber = turnNumber;
        this.turn = new TurnImpl(baseGame().getGame(), player) {};
    }

    /**
     * Returns the game. should be implemented by the subclass.
     *
     * @return the game.
     */
    protected abstract BaseGame baseGame();

    private void checkFinished() throws TurnException {
        if (finished) {
            LOG.error("Turn {} is already finished", turnNumber);
            throw new TurnException(TurnError.THE_TURN_IS_FINISHED);
        }
    }

    void markFinished() {
        finished = true;
    }

    Turn getTurn() {
        return turn;
    }

    private void markUsed(ActionCard actionCard, PlayerData playerData, int currentPriority) {
        if (usedCards.remove(actionCard)) {
            LOG.warn("Card {} was already used", actionCard);
        }
        usedCards.add(actionCard);
        if (actionCard.getType() == ActionType.CHOOSE) {
            List<ActionCard> chooses = playerData.getCards()
                    .stream()
                    .filter(it -> it.getType() == ActionType.CHOOSE && it.getPriority() <= currentPriority)
                    .collect(Collectors.toList());
            LOG.debug("Removing choose cards from player's hand: {}", chooses.stream().map(ActionCard::getName).collect(Collectors.toList()));
            for (ActionCard choose : chooses) {
                playerData.removeCard(choose);
            }
        }
        if (actionCard.getAction() == Action.CHANCE) {
            LOG.debug("Returning chance card {} to the game", actionCard);
            baseGame().getBackChanceCard(actionCard);
        }
    }

    /**
     * Plays a card
     *
     * @return returns true if the card played and false if the card was not played
     * @throws TurnException if the player cannot play the card
     */
    private ActionCard doPlayCard(ActionCard actionCard) throws TurnException {
        PlayerData playerData = baseGame().playerData(player);
        boolean cardUsed;
        boolean newCardsSpawned = false;
        BaseActionCard card;
        if (playerData.getActiveCards().contains(actionCard)) {
            card = (BaseActionCard) playerData.removeCard(actionCard);
            if (card != null) {
                assert card.equals(actionCard);
                assert actionCard.equals(card);
                LOG.debug("Playing card: {}", card);
                if (card instanceof SyncCard && actionCard instanceof SyncCard) {
                    LOG.debug("Syncing cards: {} with {}", card, actionCard);
                    card = ((SyncCard) card).sync((SyncCard) actionCard);
                }
                List<ActionCard> result = card.play(turn);
                if (result.size() > 0) {
                    LOG.info("Card '{}' has been played by {}, and received the following cards: {}",
                            card.getName(),
                            player.getName(),
                            result.stream().map(ActionCard::getName).collect(Collectors.toList()));
                } else {
                    LOG.info("{} received no cards.", player.getName());
                }

                cardUsed = !result.contains(card);
                for (ActionCard newCard : result) {
                    if (playerData.addCard(newCard)) {
                        if (!newCardsSpawned && !card.equals(newCard)) {
                            newCardsSpawned = true;
                        }
                    }
                }
            } else {
                LOG.error("Card '{}' is not found in the player's active cards.", actionCard.getName());
                throw new IllegalStateException("Card not found on player's hand");
            }
        } else if (baseGame().getCards(player).contains(actionCard)) {
            LOG.warn("Card '{}' is not active for player '{}'", actionCard.getName(), player.getName());
            throw new TurnException(TurnError.THE_CARD_IS_NOT_ACTIVE);
        } else {
            LOG.warn("Player {} tried to play a card that is not in his hand: {}", player.getName(), actionCard.getName());
            throw new TurnException(TurnError.THE_CARD_IS_NOT_IN_THE_PLAYER_S_HAND);
        }
        return cardUsed || newCardsSpawned ? card : null;
    }

    @Override
    public void playCard(ActionCard card) throws TurnException {
        if (card == null) {
            throw new TurnException(TurnError.THE_CARD_IS_NULL);
        }
        checkFinished();
        steps++;
        if (steps > MAX_STEPS_PER_TURN) {
            throw new IllegalStateException("Too many steps in the turn.");
        }
        LOG.debug("Step {}", steps);
        PlayerData playerData = baseGame().playerData(player);
        int currentPriority = playerData.getCurrentPriority();
        LOG.debug("{}'s current priority: {}", player.getName(), currentPriority);
        LOG.info("'{}' is being played by {}", card.getName(), player.getName());
        ActionCard playedCard = doPlayCard(card);
        boolean result = playedCard != null;
        LOG.debug("Card '{}' played: {}", card, result);
        if (result) {
            markUsed(playedCard, playerData, currentPriority);
        } else {
            // hold card to prevent using it again
            playerData.holdCard(card);
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public TurnInfo getTurnInfo() {
        return TurnInfo.builder()
                .turnNumber(turnNumber)
                .playerInfo(baseGame().getPlayerInfo(player))
                .activeCards(baseGame().getActiveCards(player))
                .usedCards(usedCards)
                .isFinished(finished)
                .build();
    }

    @Override
    public List<Player> getPlayers() {
        return baseGame().getPlayers();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void endTurn() throws TurnException {
        checkFinished();
        LOG.info("End turn for {}", player.getName());
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        try {
            baseGame().finishTurn(turn);
        } catch (GameException e) {
            LOG.error("Error finishing turn: {}", this, e);
            throw new UnexpectedErrorException(e);
        }
        finished = true;
    }

}
