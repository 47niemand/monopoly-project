package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameError;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;
import pp.muza.monopoly.model.pieces.actions.SyncCard;

/**
 * The base implementation of the game. Marked as abstract because it is not intended to be used directly.
 *
 * @author dmytromuza
 */
public abstract class BaseTurn {

    public static final int MAX_STEPS_PER_TURN = 100;
    private static final Logger LOG = LoggerFactory.getLogger(BaseTurn.class);

    private final Player player;
    private final int turnNumber;
    private final Turn turn;
    private final PlayTurn playTurn;

    private final List<ActionCard> usedCards = new ArrayList<>();
    private boolean finished = false;
    private int steps = 0;

    protected BaseTurn(Player player, int turnNumber) {
        this.player = player;
        this.turnNumber = turnNumber;
        this.turn = new TurnImpl(baseGame().getGame(), player);
        this.playTurn = new PlayTurnImpl(this);
    }

    private static void markNotPlayed(ActionCard card, PlayerData playerData) {
        if (card.getType() == ActionType.PROFIT) {
            // Profit cards can be played only once
            LOG.debug("Profit card {} can be played only once", card);
            playerData.removeCard(card);
        } else {
            // hold card to prevent using it again
            LOG.debug("Card not played, holding it: {}", card);
            playerData.holdCard(card);
        }
    }

    /**
     * Returns the game. should be implemented by the subclass.
     *
     * @return the game.
     */
    protected abstract BaseGame baseGame();

    PlayTurn playTurn() {
        return playTurn;
    }

    private void checkFinished() throws TurnException {
        if (finished) {
            LOG.error("Turn {} is already finished", turnNumber);
            throw new TurnException(GameError.THE_TURN_IS_FINISHED);
        }
    }

    void markFinished() {
        finished = true;
    }

    Turn getTurn() {
        return turn;
    }

    private void markPlayed(ActionCard actionCard, PlayerData playerData, int currentPriority) {
        if (usedCards.remove(actionCard)) {
            LOG.warn("Card {} was already used", actionCard);
        }
        usedCards.add(actionCard);
        if (actionCard.getType() == ActionType.CHOICE) {
            List<ActionCard> chooses = playerData.getCards()
                    .stream()
                    .filter(it -> it.getType() == ActionType.CHOICE && it.getPriority() <= currentPriority)
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
        if (playerData.canUseCard(actionCard)) {
            card = (BaseActionCard) playerData.removeCard(actionCard);
            if (card != null) {
                assert card.equals(actionCard);
                assert actionCard.equals(card);
                if (card instanceof SyncCard && actionCard instanceof SyncCard) {
                    LOG.debug("Syncing cards: {} with {}", card, actionCard);
                    card = ((SyncCard) card).sync((SyncCard) actionCard);
                }
                LOG.debug("Playing card: {}", card);
                List<ActionCard> result = card.play(turn);
                if (result.size() > 0) {
                    LOG.info("Card '{}' has been played by {}, and received the following cards: {}",
                            card,
                            player,
                            result.stream().map(ActionCard::getName).collect(Collectors.toList()));
                } else {
                    LOG.info("{} received no cards.", player);
                }
                cardUsed = !result.contains(card);
                if (!cardUsed) {
                    LOG.debug("Card {} was not used", card);
                }
                for (ActionCard newCard : result) {
                    if (playerData.addCard(newCard)) {
                        if (!newCardsSpawned && !card.equals(newCard)) {
                            newCardsSpawned = true;
                        }
                    }
                }
            } else {
                LOG.error("Card '{}' is not found in the player's active cards.", actionCard);
                throw new IllegalStateException("Card not found on player's hand");
            }
        } else if (playerData.getCards().contains(actionCard)) {
            LOG.warn("Card '{}' is not active for player '{}'", actionCard, player);
            throw new TurnException(GameError.THE_CARD_IS_NOT_ACTIVE);
        } else {
            LOG.warn("Player {} tried to play a card that is not in his hand: {}", player, actionCard);
            throw new TurnException(GameError.THE_CARD_IS_NOT_IN_THE_PLAYER_S_HAND);
        }
        return cardUsed || newCardsSpawned ? card : null;
    }

    void playCard(ActionCard card) throws TurnException {
        if (card == null) {
            throw new TurnException(GameError.THE_CARD_IS_NULL);
        }
        checkFinished();
        steps++;
        if (steps > MAX_STEPS_PER_TURN) {
            throw new IllegalStateException("Too many steps in the turn.");
        }
        LOG.debug("Step {}", steps);
        PlayerData playerData = baseGame().playerData(player);
        int currentPriority = playerData.getCurrentPriority();
        LOG.debug("{}'s current priority: {}", player, currentPriority);
        LOG.info("'{}' is being played by {}", card, player);
        ActionCard playedCard = doPlayCard(card);
        boolean result = playedCard != null;
        if (result) {
            markPlayed(playedCard, playerData, currentPriority);
        } else {
            markNotPlayed(card, playerData);
        }
    }

    boolean isFinished() {
        return finished;
    }

    TurnInfo getTurnInfo() {
        return TurnInfo.builder()
                .turnNumber(turnNumber)
                .stepNumber(steps)
                .playerInfo(baseGame().getPlayerInfo(player))
                .activeCards(baseGame().getActiveCards(player))
                .usedCards(usedCards)
                .isFinished(finished)
                .build();
    }

    List<Player> getPlayers() {
        return baseGame().getPlayers();
    }

    Player getPlayer() {
        return player;
    }

    void endTurn() throws TurnException {
        checkFinished();
        LOG.info("End turn for {}", player);
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        try {
            baseGame().finishTurn(turn);
        } catch (GameException e) {
            throw new UnexpectedErrorException("Error finishing turn", e);
        }
        finished = true;
    }

}
