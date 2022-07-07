package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.List;

import static pp.muza.monopoly.model.actions.cards.PayRent.createContractsForPlayerPossession;

/**
 * A contract for a property.
 * <p>
 * The player who owns the property can sale it to get a profit.
 * This card spawns when the player has no enough money to pay obligations.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Trade extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Trade.class);

    private final int landId; // the id of the land to be traded
    private final Property property; // the property being sent


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
                turn.buyProperty(landId, property);
            } else {
                turn.tradeProperty(salePlayer, landId, property);
            }
        } catch (BankException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            return ImmutableList.<ActionCard>builder().addAll(createContractsForPlayerPossession(turn)).add(this).build();
        } catch (TurnException e) {
            LOG.info("Player cannot trade property: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}
