package pp.muza.monopoly.model.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.game.Player;

import java.util.List;

import static pp.muza.monopoly.model.actions.CardUtils.createContractsForPlayerPossession;

/**
 * This card can be used to purchase a property from the board.
 * If property is not owned, the player should buy it.
 * If someone else owns the property, the player have to pay a price to the owner and then the property is bought.
 * If player has not enough money, he can contract his property to earn money.
 * It is obligation card, so if player cannot buy the property, he loses the game.
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
