package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A Player can use this card to purchase a property from the board.
 * If the property is not owned, the player should buy it.
 * If someone else owns the property, the player has to pay the price to the
 * owner and then the property is bought.
 * If a player hasn't enough coins, he can contract his property to earn coins.
 * Buying properties is compulsory, so if the player cannot buy the property,
 * he loses the game.
 */
public final class Buy extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Buy.class);

    /**
     * the id of the land to be traded.
     */
    private final int landId;

    Buy(int landId) {
        super("Buy", Action.BUY, ActionType.OBLIGATION, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = null;
        boolean finished = false;
        try {
            Player salePlayer = turn.getPropertyOwner(landId);
            if (salePlayer == null) {
                LOG.debug("Buying property {} from the bank.", landId);
                turn.buyProperty(landId);
            } else {
                LOG.debug("Buying property {} from player {}.", landId, salePlayer.getName());
                turn.tradeProperty(salePlayer, landId);
            }
        } catch (BankException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            result = ImmutableList.<ActionCard>builder().add(this)
                    .addAll(CardUtils.createContractsForPlayerPossession(turn)).build();
            finished = true;
        } catch (TurnException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        if (!finished) {
            result = ImmutableList.of();
        }
        return result;
    }
}
