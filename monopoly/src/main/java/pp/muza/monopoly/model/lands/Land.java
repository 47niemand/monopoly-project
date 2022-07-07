package pp.muza.monopoly.model.lands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@AllArgsConstructor
public abstract class Land {

    private final String name;
    private final Type type;

    public enum Type {
        START(Start.class), // Start is a subclass of Land
        PROPERTY(Property.class), // Property is a subclass of Land
        JAIL(Jail.class), // Jail is a subclass of Land
        PARKING(Parking.class), // Parking is a subclass of Land
        GOTO_JAIL(GotoJail.class), // GotoJail is a subclass of Land
        CHANCE(Chance.class); // Chance is a subclass of Land

        final Class<? extends Land> aClass;

        Type(Class<? extends Land> landClass) {
            aClass = landClass;
        }
    }
}
