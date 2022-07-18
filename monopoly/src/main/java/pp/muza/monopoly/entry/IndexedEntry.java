package pp.muza.monopoly.entry;

import lombok.Value;

@Value
public class IndexedEntry<T> {
    int index;
    T value;
}
