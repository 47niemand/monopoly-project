package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.Property;

/**
 * A Player can use this card to purchase a property from the board.
 * If the property is not owned, the player should buy it.
 * If someone else owns the property, the player has to pay the price to the
 * owner and then the property is bought.
 * If a player hasn't enough money, he can contract his property to earn money.
 * It is an obligation card, so if the player cannot buy the property, he loses
 * the game.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Trade extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Trade.class);

    /**
     * the id of the land to be traded
     */
    private final int landId;
    /**
     * the property being sent
     */
    private final Property property;

    Trade(int landId, Property property) {
        super("Tade", Action.CONTRACT, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            Player salePlayer = turn.getPropertyOwner(landId);
            if (salePlayer == null) {
                turn.buyProperty(landId);
            } else {
                turn.tradeProperty(salePlayer, landId);
            }
        } catch (BankException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            return ImmutableList.<ActionCard>builder().add(this)
                    .addAll(CardUtils.createContractsForPlayerPossession(turn)).build();
        } catch (TurnException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}
