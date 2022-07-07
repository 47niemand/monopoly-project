package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.util.List;

/**
 * This is a special card that spawns action cards for the player, when player arrives at a land
 * for instance:
 * when player arrives at the property, depending on property owner, he/she can buy it, or he/she should pay rent.
 * when player arrives at the goto jail, he/she should move to jail.
 * when player arrives at the chance, he/she should draw a card.
 * etc.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Arrival extends ActionCard {

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
                    result = ImmutableList.of(new Buy(position, property));
                } else {
                    result = ImmutableList.of(new PayRent(owner, property));
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
