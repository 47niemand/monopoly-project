package pp.muza.monopoly.entry;

import lombok.Value;

/**
 * @author dmytromuza
 */
@Value
public class IndexedEntry<T> {
    int index;
    T value;
}
