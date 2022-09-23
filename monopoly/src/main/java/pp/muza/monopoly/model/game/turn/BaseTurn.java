package pp.muza.monopoly.model.game.turn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.game.BaseGame;
import pp.muza.monopoly.model.game.PlayerContext;
import pp.muza.monopoly.model.pieces.actions.Action;
import pp.muza.monopoly.model.pieces.actions.ActionType;
import pp.muza.monopoly.model.pieces.actions.BaseActionCard;


abstract class BaseTurn implements PlayTurn {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTurn.class);

    private final BaseGame baseGame;
    protected final Player player;
    protected final int turnNumber;

    protected boolean finished = false;
    private final List<ActionCard> usedCards = new ArrayList<>();
    private int steps = 0;

    protected BaseTurn(BaseGame game, Player player, int turnNumber) {
        this.baseGame = game;
        this.player = player;
        this.turnNumber = turnNumber;
    }

    void checkFinished() throws TurnException {
        if (finished) {
            throw new TurnException("The turn is finished.");
        }
    }

    private boolean doPlayCard(ActionCard actionCard) throws TurnException {
        PlayerContext playerContext = baseGame.playerContext(player);
        boolean cardUsed;
        boolean newCardsSpawned = false;
        if (playerContext.getActiveCards().contains(actionCard)) {
            BaseActionCard card = (BaseActionCard) playerContext.removeCard(actionCard);
            if (card != null) {
                LOG.debug("Playing card: {}", card);
                List<ActionCard> result = card.play((Turn) this);
                cardUsed = !result.contains(card);
                for (ActionCard newCard : result) {
                    if (playerContext.addCard(newCard)) {
                        if (!newCardsSpawned && !card.equals(newCard)) {
                            newCardsSpawned = true;
                        }
                    }
                }
            } else {
                throw new IllegalStateException("Card not found on player's hand");
            }
        } else if (baseGame.getCards(player).contains(actionCard)) {
            throw new TurnException("The card is not active.");
        } else {
            throw new TurnException("The card is not in the player's hand.");
        }
        return cardUsed || newCardsSpawned;
    }

    @Override
    public void playCard(ActionCard actionCard) throws TurnException {
        checkFinished();
        steps++;
        if (steps > 30) {
            throw new IllegalStateException("Too many steps in the turn.");
        }
        LOG.debug("Step {}", steps);
        if (actionCard == null) {
            LOG.info("Player {} skipped turn", player);
            finishTurn();
            return;
        }
        PlayerContext playerContext = baseGame.playerContext(player);
        int currentPriority = playerContext.getCurrentPriority();
        LOG.debug("{}'s current priority: {}", player.getName(), currentPriority);
        LOG.info("'{}' is being played by {}", actionCard.getName(), player.getName());
        boolean result = doPlayCard(actionCard);
        LOG.debug("Card '{}' played: {}", actionCard, result);
        if (usedCards.remove(actionCard)) {
            LOG.warn("{}: card '{}' was already used", player.getName(), actionCard);
            // it is not an error if the card was already used.
        }
        if (result) {
            markUsed(actionCard, playerContext, currentPriority);
        } else {
            // hold card to prevent using it again
            playerContext.holdCard(actionCard);
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
                .activeCards(baseGame.getActiveCards(player))
                .playerInfo(baseGame.getPlayerInfo(player))
                .board(baseGame.getBoard())
                .players(baseGame.getPlayers())
                .propertyOwners(baseGame.getPropertyOwners())
                .build();
    }


    @Override
    public List<Player> getPlayers() {
        return baseGame.getPlayers();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    private void markUsed(ActionCard actionCard, PlayerContext playerContext, int currentPriority) {
        usedCards.add(actionCard);
        if (actionCard.getType() == ActionType.CHOOSE) {
            List<ActionCard> chooses = playerContext.getCards()
                    .stream()
                    .filter(it -> it.getType() == ActionType.CHOOSE && it.getPriority() <= currentPriority)
                    .collect(Collectors.toList());
            LOG.debug("Removing choose cards from player's hand: {}", chooses.stream().map(ActionCard::getName).collect(Collectors.toList()));
            for (ActionCard choose : chooses) {
                playerContext.removeCard(choose);
            }
        }
        if (actionCard.getAction() == Action.CHANCE) {
            LOG.debug("Returning chance card {} to the game", actionCard);
            baseGame.getBackChanceCard(actionCard);
        }
    }

    @Override
    public void finishTurn() throws TurnException {
        checkFinished();
        LOG.info("Finishing turn for player {}", player.getName());
        LOG.info("Used cards: {}",
                usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        try {
            baseGame.finishTurn(player);
        } catch (GameException e) {
            throw new IllegalStateException(e);
        }
        finished = true;
    }

}
