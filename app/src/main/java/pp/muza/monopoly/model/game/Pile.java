package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Pile<T> {

    private final LinkedList<T> cards;

    public Pile(Collection<T> cards) {
        this.cards = new LinkedList<>(cards);
    }

    public Pile() {
        this.cards = new LinkedList<>();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void add(T card) {
        cards.add(card);
    }

    // add cards
    public void add(Collection<T> cards) {
        this.cards.addAll(cards);
    }

    public T pop() {
        return cards.pop();
    }

    public void returnCard(T t) {
        cards.addLast(t);
    }

    public T foundAndPopCard(T card) {
        T c = cards.stream()
                .filter(c1 -> c1.equals(card))
                .findFirst().orElse(null);
        if (c != null) {
            if (!cards.remove(c)) {
                throw new IllegalStateException("Could not remove card from pile");
            }
        }
        return c;
    }

    public List<T> popAll() {
        List<T> list = new ArrayList<>(cards);
        cards.clear();
        return list;

    }
}
