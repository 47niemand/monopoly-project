package pp.muza.monopoly.model.lands;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class GotoJail extends Land {

    public GotoJail() {
        super("GotoJail", Land.Type.GOTO_JAIL);
    }
}
