package pp.muza.monopoly.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(includeFieldNames = false)
public final class Player {

    private final String name;

    public static <K, T> K getId(T t) {
        return null;
    }
}
