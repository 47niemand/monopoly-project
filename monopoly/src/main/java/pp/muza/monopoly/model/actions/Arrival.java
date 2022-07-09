package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.game.Player;


/**
 * This is a special card that spawns action cards for the player, when player arrives at a land. 
 * - when player arrives at the property, depending on property owner, he/she can buy it, or he/she should pay rent.
 * - when player arrives at the goto jail, he/she should move to jail.
 * - when player arrives at the chance, he/she should draw a card.
 * - etc.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Arrival extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Arrival.class);

    private final int position;

    Arrival(int position) {
        super("Arrival", Action.ARRIVAL, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.position = position;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        Land land = turn.getLand(position);
        List<ActionCard> result;
        switch (land.getType()) {
            case PROPERTY:
                Property property = (Property) land;
                Player owner = turn.getPropertyOwner(position);
                if (owner == null) {
                    LOG.info("Property is not owned by anyone, player can buy it");
                    result = ImmutableList.of(new Buy(position, property));
                } else if (owner != turn.getPlayer()) {
                    LOG.info("Property is owned by {}, player should pay rent", owner.getName());
                    result = ImmutableList.of(new PayRent(owner, property, position, property));
                } else {
                    LOG.info("Property is owned by player, player can do nothing");
                    result = ImmutableList.of();
                }
                break;
            case GOTO_JAIL:
                result = ImmutableList.of(new GoToJail());
                break;
            case CHANCE:
                result = ImmutableList.of(turn.popChanceCard());
                break;
            default:
                result = ImmutableList.of();
                break;
        }
        return result;
    }

}
