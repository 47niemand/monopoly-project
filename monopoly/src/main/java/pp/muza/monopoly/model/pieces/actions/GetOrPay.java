package pp.muza.monopoly.model.pieces.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.lands.LandType;

import java.util.List;


/**
 * A Player arrives at a property.
 * If one is available, get it for free, otherwise pay rent to the owner.
 *
 * @author dmytromuza
 */

@Getter
@EqualsAndHashCode(callSuper = true)
public final class GetOrPay extends Arrival {

    private static final Logger LOG = LoggerFactory.getLogger(GetOrPay.class);

    GetOrPay(int position) {
        super(position);
    }

    public static ActionCard create(int position) {
        return new GetOrPay(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        Land land = turn.getLand(position);
        List<ActionCard> result;
        if (land.getType() == LandType.PROPERTY) {
            Player owner = turn.getPropertyOwner(position);
            if (owner == null) {
                LOG.info("Player {} is getting property {} for free.", turn.getPlayer(), land);
                result = ImmutableList.of(new OwnershipPrivilege(position));
            } else if (owner != turn.getPlayer()) {
                LOG.info("Player {} is obligated to pay rent to {} for {}", turn.getPlayer(), owner, land);
                result = ImmutableList.of(new PayRent(turn.getRent(position), owner, position));
            } else {
                LOG.info("Property {} is owned by player, nothing to do", land);
                result = ImmutableList.of();
            }
        } else {
            LOG.warn("Land {} is not a property, nothing to do", land);
            result = ImmutableList.of();
        }
        return result;
    }
}
