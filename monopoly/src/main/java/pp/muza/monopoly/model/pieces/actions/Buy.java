package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
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
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Buy extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Buy.class);

    /**
     * the id of the land to be traded.
     */
    protected final int position;

    protected Buy(ActionType type, int priority, int position) {
        super(Action.BUY, type, priority);
        this.position = position;
    }

    Buy(int position) {
        this(ActionType.OBLIGATION, DEFAULT_PRIORITY, position);
    }

    public static ActionCard create(int position) {
        return new Buy(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = null;
        boolean finished = false;
        try {
            Player salePlayer = turn.getPropertyOwner(position);
            if (salePlayer == null) {
                LOG.debug("Buying property {} from the bank.", position);
                turn.buyProperty(position);
            } else {
                LOG.debug("Buying property {} from player {}.", position, salePlayer);
                turn.tradeProperty(salePlayer, position);
            }
        } catch (BankException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            result = ImmutableList.<ActionCard>builder().add(this)
                    .addAll(CardUtils.sellDebts(turn)).build();
            finished = true;
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action:  " + this, e);
        }
        if (!finished) {
            result = ImmutableList.of();
        }
        return result;
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("position", position)
        );
    }
}
