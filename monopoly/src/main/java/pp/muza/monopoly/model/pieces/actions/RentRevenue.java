package pp.muza.monopoly.model.pieces.actions;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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

    RentRevenue(int number, Player sender, int landId) {
        super("Rent Revenue", ActionType.OBLIGATION, HIGHEST_PRIORITY, number, sender);
        this.landId = landId;
    }
}
