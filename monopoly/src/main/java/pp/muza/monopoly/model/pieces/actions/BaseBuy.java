package pp.muza.monopoly.model.pieces.actions;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionType;

/**
 * It is a base class for all actions that are related to taking ownership of
 * the property.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseBuy extends BaseActionCard {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseBuy.class);
    /**
     * the id of the land to be traded.
     */
    protected final int position;

    protected BaseBuy(ActionType type, int priority, int position) {
        super(Action.BUY, type, priority);
        this.position = position;
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("position", position));
    }
}
