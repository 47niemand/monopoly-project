package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;


/**
 * A Player arrives at a property and must buy it.
 * If the property is owned by other player, the player must buy that property by paying the owner.
 * If the player owns the property, nothing happens.
 * If anyone does not own the property, the player can buy it from the bank.
 */

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Takeover extends Arrival {

    private static final Logger LOG = LoggerFactory.getLogger(Takeover.class);

    Takeover(int position) {
        super("Takeover", position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        Land land = turn.getLand(position);
        List<ActionCard> result;
        switch (land.getType()) {
            case PROPERTY:
                Player owner = turn.getPropertyOwner(position);
                if (owner == null || owner != turn.getPlayer()) {
                    LOG.info("No one owns the {}, {} can purchase it", land.getName(), turn.getPlayer().getName());
                    result = ImmutableList.of(new Buy(position));
                } else {
                    LOG.info("Property {} is owned by player, nothing to do", land.getName());
                    result = ImmutableList.of();
                }
                break;
            default:
                result = ImmutableList.of();
                break;
        }
        return result;
    }

}