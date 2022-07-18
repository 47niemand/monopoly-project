package pp.muza.monopoly.model.pieces.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.pieces.lands.Start;

final class CardUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CardUtils.class);

    /**
     * creates a list of action cards when a player moves to a new land.
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

    /**
     * creates contracts for player's possession
     */
    public static List<ActionCard> createContractsForPlayerPossession(Turn turn) {
        LOG.info("Creating contracts for player's possession");
        List<IndexedEntry<Property>> properties = turn.getProperties();
        return properties.stream()
                .map(property -> new Contract(property.getIndex()))
                .collect(Collectors.toList());
    }
}
