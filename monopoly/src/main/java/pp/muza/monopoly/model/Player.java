package pp.muza.monopoly.model;

import lombok.Value;

/**
 * @author dmytromuza
 */
@Value
public class Player {

    String name;

    @Override
    public String toString() {
        return this.getName();
    }
}
