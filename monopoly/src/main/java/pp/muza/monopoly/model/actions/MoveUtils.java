package pp.muza.monopoly.model.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Start;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmitr
 */
public final class MoveUtils  {

    private static final Logger LOG = LoggerFactory.getLogger(MoveUtils.class);
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
