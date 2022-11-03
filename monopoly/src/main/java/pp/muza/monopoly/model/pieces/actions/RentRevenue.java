package pp.muza.monopoly.model.pieces.actions;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;

/**
 * A player receives coins from this card due to a rent.
 *
 * @author dmytromuza
 */
@Getter
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

    public static ActionCard create(int value, Player sender, int position) {
        return new RentRevenue(value, sender, position);
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("position", position)
        );
    }
}
