package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;

/**
 * This card can be used to purchase a property from the board at the player's current position.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Buy extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Buy.class);

    private final int landId;

    Buy(int landId, Property property) {
        super("Buy", Action.BUY, Type.OPTIONAL, DEFAULT_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.buyProperty(landId);
        } catch (BankException | TurnException e) {
            LOG.info("Player cannot buy property: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}
