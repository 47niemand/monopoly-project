package pp.muza.monopoly.model.pieces.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.consts.Constants;
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
            LOG.info("Player {} has to get income due to start", turn.getPlayer());
            res.add(new GoReward(((Start) land).getIncomeTax()));
        });
        return res;
    }

    static List<ActionCard> sellDebts(Turn turn) {
        if (Constants.allowAuction) {
            return ImmutableList.of(new ChoiceAuction(), new ChoiceContract());
        } else {
            return createContract(turn);
        }
    }

    static List<ActionCard> createAuction(Turn turn) {
        List<ActionCard> result;
        result = contract(turn, auctionFn());
        return result;
    }

    static List<ActionCard> createContract(Turn turn) {
        List<ActionCard> result;
        result = contract(turn, contractFn());
        return result;
    }

    private static List<ActionCard> contract(Turn turn, Function<IndexedEntry<Property>, ActionCard> function) {
        List<ActionCard> result;
        List<IndexedEntry<Property>> properties = turn.getProperties();
        if (properties.isEmpty()) {
            LOG.info("Player {} has no properties", turn.getPlayer());
            result = ImmutableList.of();
        } else {
            LOG.info("Creating auction for player's possession");
            result = properties.stream()
                    .peek(entry -> {
                        assert entry.getValue() == turn.getLand(entry.getIndex());
                    })
                    .map(function)
                    .collect(Collectors.toList());
        }
        return result;
    }

    private static Function<IndexedEntry<Property>, ActionCard> auctionFn() {
        return entry -> new PromoteAuction(entry.getIndex(), Constants.MIN_BID);
    }

    private static Function<IndexedEntry<Property>, ActionCard> contractFn() {
        return entry -> new Contract(entry.getIndex(), entry.getValue().getPrice());
    }
}
