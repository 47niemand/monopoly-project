package pp.muza.monopoly.model.game;

import lombok.Value;

@Value
public final class IndexedEntry<T> {
    private final int index;
    private final T value;

    public IndexedEntry(int index, T value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public T getValue() {
        return value;
    }
}
