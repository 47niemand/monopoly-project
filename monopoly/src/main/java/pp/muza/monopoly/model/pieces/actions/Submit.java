package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Biding;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class Submit extends BaseActionCard implements Biding {

    private static final Logger LOG = LoggerFactory.getLogger(Submit.class);

    private final Player bidder;
    private final int position;
    @EqualsAndHashCode.Exclude
    private final int price;

    Submit(Player bidder, int position, int price) {
        super(Action.SUBMIT, ActionType.CHOICE, IDLE_PRIORITY);
        this.bidder = bidder;
        this.position = position;
        this.price = price;
    }

    public static ActionCard create(Player bidder, int position, int price) {
        return new Submit(bidder, position, price);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.error("Player cant play this card");
        // it should be played by EndAuction card
        return ImmutableList.of(this);
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("bidder", bidder, "position", position, "price", price)
        );
    }
}
