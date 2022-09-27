package pp.muza.monopoly.model.pieces.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;

/**
 * A player receives coins from this card due to a rent.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RentRevenue extends ReceiveMoney {

    private static final Logger LOG = LoggerFactory.getLogger(RentRevenue.class);

    /**
     * The land on which the rent is received.
     */
    private final int landId;

    RentRevenue(int value, Player sender, int landId) {
        super("Rent Revenue", ActionType.OBLIGATION, HIGHEST_PRIORITY, value, sender);
        this.landId = landId;
    }

    public static ActionCard of(int value, Player sender, int landId) {
        return new RentRevenue(value, sender, landId);
    }
}
