package pp.muza.monopoly.model.pieces.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * A player receives coins from the card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class GoReward extends Income {

    private static final Logger LOG = LoggerFactory.getLogger(GoReward.class);

    GoReward(int number) {
        super("Go Reward", HIGHEST_PRIORITY, number);
    }
}
