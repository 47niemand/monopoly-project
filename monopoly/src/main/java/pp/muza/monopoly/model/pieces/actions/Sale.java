package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
 * This card obliges the player to sale the property to the other player.
 * </p>
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Sale extends Contract {

    private static final Logger LOG = LoggerFactory.getLogger(Sale.class);

    /**
     * the id of the land to be traded.
     */
    private final int price;
    private final Player buyer;

    Sale(int position, int price, Player buyer) {
        super(ActionType.OBLIGATION, DEFAULT_PRIORITY, position);
        this.price = price;
        this.buyer = buyer;
    }

    public static ActionCard of(int position, int price, Player buyer) {
        return new Sale(position, price, buyer);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            LOG.info("Sale of the property {} to the player {}", getPosition(), buyer.getName());
            turn.doSale(position, price, buyer);
            try {
                turn.sendCard(buyer, new OwnershipPrivilege(position));
            } catch (TurnException e) {
                LOG.error("Error sending ownership privilege to the buyer {}", buyer, e);
                throw new UnexpectedErrorException(e);
            }
        } catch (TurnException | BankException e) {
            LOG.warn("Player cannot sale the property: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}
