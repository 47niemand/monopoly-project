package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * It is a notification that the player took ownership of the property.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class OwnershipPrivilege extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(OwnershipPrivilege.class);

    /**
     * the id of the land that the player took ownership of
     */
    protected final int position;

    protected OwnershipPrivilege(ActionType type, int priority, int position) {
        super(Action.BUY, type, priority);
        this.position = position;
    }

    OwnershipPrivilege(int position) {
        this(ActionType.OBLIGATION, HIGHEST_PRIORITY, position);
    }

    /**
     * this should be package-private
     **/
    public static ActionCard create(int position) {
        return new OwnershipPrivilege(position);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        // nothing to do
        return ImmutableList.of();
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                Map.of("position", position)
        );
    }
}
