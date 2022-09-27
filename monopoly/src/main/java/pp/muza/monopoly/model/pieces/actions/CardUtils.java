package pp.muza.monopoly.model.pieces.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.lands.LandType;
import pp.muza.monopoly.model.pieces.lands.Start;

final class CardUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CardUtils.class);

    /**
     * creates a list of action cards when a player moves to a new land.
     */
    static List<ActionCard> onPath(Turn turn, List<Land> path) {
        List<ActionCard> res;
        res = new ArrayList<>();
        path.stream().filter(land -> land.getType() == LandType.START).findFirst().ifPresent(land -> {
            LOG.info("Player {} has to get income due to start", turn.getPlayer().getName());
            res.add(new GoReward(((Start) land).getIncomeTax()));
        });
        return res;
    }

    /**
     * creates contracts for player's possession
     */
    static List<ActionCard> createContractsForPlayerPossession(Turn turn) {
        List<IndexedEntry<Property>> properties = turn.getProperties();
        if (properties.isEmpty()) {
            LOG.info("Player {} has no properties", turn.getPlayer().getName());
        } else {
            LOG.info("Creating contracts for player's possession");
        }
        return properties.stream()
                .peek(entry -> {
                    assert entry.getValue() == turn.getLand(entry.getIndex());
                } )
                .map(entry -> new Contract(entry.getIndex()))
                .collect(Collectors.toList());
    }
}
