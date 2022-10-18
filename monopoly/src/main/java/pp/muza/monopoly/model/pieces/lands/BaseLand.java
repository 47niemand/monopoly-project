package pp.muza.monopoly.model.pieces.lands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import pp.muza.monopoly.model.Land;

/**
 * @author dmytromuza
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode
public abstract class BaseLand implements Land {

    private final String name;
    private final LandType type;

    @Override
    public String toString() {
        return this.getName();
    }
}
