package pp.muza.monopoly.model.actions.cards;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Start;

/**
 * The player moves to a new position on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MoveTo extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(MoveTo.class);

    private final int position;

    MoveTo(int position) {
        super("MoveTo", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.position = position;
    }

    public static ActionCard of(int landId) {
        return new MoveTo(landId);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> res;
        LOG.info("{} moved to {} ({})", turn.getPlayer().getName(), position, turn.getLand(position).getName());
        List<Land> path = turn.moveTo(position);
        if (path.size() == 0) {
            LOG.warn("Staying on the same land");
            res = ImmutableList.of();
        } else {
            res = onArrival(turn, path, position);
        }
        return res;
    }

    /**
     * creates a list of action cards when the player moves to a new land.
     */
    static List<ActionCard> onArrival(Turn turn, List<Land> path, int position) {
        List<ActionCard> res;
        res = new ArrayList<>();
        res.add(new Arrival(position));
        path.stream().filter(land -> land.getType() == Land.Type.START).findFirst().ifPresent(land -> {
            LOG.info("Player {} has to get income due to start", turn.getPlayer().getName());
            res.add(new Income(((Start) land).getIncomeTax()));
        });
        return res;
    }
}
