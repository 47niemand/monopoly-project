package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

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
 * sale of a property.
 * <p>
 * This card obligates the player to sale the property to the other player.
 * </p>
 *
 * @author dmytromuza
 */
@Getter

@EqualsAndHashCode(callSuper = true)
public final class Sale extends Contract {

    private static final Logger LOG = LoggerFactory.getLogger(Sale.class);


    private final Player buyer;

    Sale(int position, int price, Player buyer) {
        super(ActionType.PROFIT, HIGHEST_PRIORITY, position, price);
        this.buyer = buyer;
    }

    public static ActionCard create(int position, int price, Player buyer) {
        return new Sale(position, price, buyer);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            LOG.info("Sale of the property {} to the player {}", getPosition(), buyer);
            turn.doSale(position, price, buyer);
            try {
                turn.sendCard(buyer, new OwnershipPrivilege(position));
            } catch (TurnException e) {
                throw new UnexpectedErrorException("Error sending ownership privilege to the buyer " + buyer, e);
            }
        } catch (TurnException | BankException e) {
            LOG.warn("Player cannot sale the property: {}", e.getMessage());
            return ImmutableList.of(this);
        }
        return ImmutableList.of();
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                Map.of("buyer", buyer)
        );
    }
}
