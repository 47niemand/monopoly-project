package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;

/**
 * A player receives coins from this card due to a rent.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RentRevenue extends ReceiveMoney {

    /**
     * The land on which the rent is received.
     */
    private final int position;

    RentRevenue(int value, Player sender, int position) {
        super(HIGHEST_PRIORITY, value, sender);
        this.position = position;
    }

    public static ActionCard of(int value, Player sender, int position) {
        return new RentRevenue(value, sender, position);
    }
}
