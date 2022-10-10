package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Game;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;


public abstract class BaseTurn implements PlayTurn {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTurn.class);
    public static final int MAX_STEPS_PER_TURN = 30;

    private final Player player;
    private final int turnNumber;
    private boolean finished = false;
    private final List<ActionCard> usedCards = new ArrayList<>();
    private int steps = 0;

    private final Turn turn = new TurnImpl() {

        @Override
        protected Game game() {
            return baseGame().getGame();
        }

        @Override
        protected Player player() {
            return player;
        }
    };

    BaseTurn(Player player, int turnNumber) {
        this.player = player;
        this.turnNumber = turnNumber;
    }

    /**
     * Returns the game. should be implemented by the subclass.
     *
     * @return the game.
     */
    protected abstract BaseGame baseGame();

    private void checkFinished() throws TurnException {
        if (finished) {
            throw new TurnException("The turn is finished.");
        }
    }

    /**
     * Plays the card
     *
     * @return returns true if the card played and false if the card was not played
     * @throws TurnException if the player cannot play the card
     */

    private boolean doPlayCard(ActionCard actionCard) throws TurnException {
        PlayerData playerData = baseGame().playerData(player);
        boolean cardUsed;
        boolean newCardsSpawned = false;
        if (playerData.getActiveCards().contains(actionCard)) {
            BaseActionCard card = (BaseActionCard) playerData.removeCard(actionCard);
            if (card != null) {
                LOG.debug("Playing card: {}", card);
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
                throw new IllegalStateException("Card not found on player's hand");
            }
        } else if (baseGame().getCards(player).contains(actionCard)) {
            throw new TurnException("The card is not active.");
        } else {
            throw new TurnException("The card is not in the player's hand.");
        }
        return cardUsed || newCardsSpawned;
    }

    @Override
    public void playCard(ActionCard card) throws TurnException {
        if (card == null) {
            throw new TurnException("The card is null.");
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
        boolean result = doPlayCard(card);
        LOG.debug("Card '{}' played: {}", card, result);
        if (usedCards.remove(card)) {
            LOG.debug("{}: card '{}' was already played", player.getName(), card.getName());
            // it is not an error if the card was already used.
        }
        if (result) {
            markUsed(card, playerData, currentPriority);
        } else {
            // hold card to prevent using it again
            playerData.holdCard(card);
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    void markFinished() {
        finished = true;
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

    private void markUsed(ActionCard actionCard, PlayerData playerData, int currentPriority) {
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
            throw new RuntimeException(e);
        }
        finished = true;
    }

    public Turn getTurn() {
        return turn;
    }
}
