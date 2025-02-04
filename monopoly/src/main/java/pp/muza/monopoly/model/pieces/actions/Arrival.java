package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * This card spawns action cards for the player, when player arrives at a land.
 * <p>
 * For example:
 * <ul>
 * <li>when player arrives at the property, depending on the property owner,
 * player can buy it, or player should pay rent.</li>
 * <li>when player arrives at the goto jail, player should move to jail.</li>
 * <li>when player arrives at the chance, player should draw a card.</li>
 * <li>etc.</li>
 * </ul>
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Arrival extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Arrival.class);

    protected final int position;

    Arrival(int position) {
        super(Action.ARRIVAL, ActionType.OBLIGATION, DEFAULT_PRIORITY);
        this.position = position;
    }

    public static ActionCard create(int position) {
        return new Arrival(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        Land land = turn.getLand(position);
        List<ActionCard> result;
        switch (land.getType()) {
            case PROPERTY:
                Player owner = turn.getPropertyOwner(position);
                if (owner == null) {
                    LOG.info("No one owns the {}, {} can purchase it", land, turn.getPlayer());
                    result = ImmutableList.of(new Buy(position));
                } else if (owner != turn.getPlayer()) {
                    LOG.info("Player {} is obligated to pay rent to {} for {}", turn.getPlayer(), owner, land);
                    result = ImmutableList.of(new PayRent(turn.getRent(position), owner, position));
                } else {
                    LOG.info("Property {} is owned by player, nothing to do", land);
                    result = ImmutableList.of();
                }
                break;
            case GOTO_JAIL:
                result = ImmutableList.of(new GoToJail());
                break;
            case CHANCE:
                result = ImmutableList.of(turn.popFortuneCard());
                break;
            default:
                result = ImmutableList.of();
                break;
        }
        return result;
    }

}
