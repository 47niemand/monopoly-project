package pp.muza.monopoly.stuff;

import lombok.Value;

/**
 * @author dmytromuza
 */
@Value
public class IndexedEntry<T> {
    int index;
    T value;
}
