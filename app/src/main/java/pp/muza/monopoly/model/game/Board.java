package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Board<K> {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<K> landList;
    private final int startPosition = 0;

    public K getLand(int pos) {
        return landList.get(pos);
    }

    /**
     * Returns lands of the position in the board.
     *
     * @param path the path of the land to get
     * @return the list of all lands on the path
     **/
    public List<K> getLands(List<Integer> path) {
        List<K> res = new ArrayList<>();
        for (Integer integer : path) {
            res.add(getLand(integer));
        }
        return res;
    }

    /**
     * Returns the next position depending on the start position with the given
     * direction.
     *
     * @param start the current position
     * @return the next position
     **/
    private int getNextPos(int start) {
        int next;
        next = (start + 1) % landList.size();
        return next;
    }

    public int getDestination(int start, int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        int current = start;
        for (int i = steps; i > 0; i--) {
            current = getNextPos(current);
        }
        return current;
    }

    /**
     * Get the path from start (exclusive) to finish (inclusive).
     * 
     * @param startPos start position
     * @param endPos   finish position
     * @return path from startPos to endPos
     */
    public List<Integer> getPathTo(int startPos, int endPos) {
        List<Integer> path = new ArrayList<>();
        int current = startPos;
        while (current != endPos) {
            current = getNextPos(current);
            path.add(current);
        }
        return path;
    }

    /**
     * Returns list of all lands in the board.
     *
     * @return list of lands
     */
    public List<K> getLands() {
        return ImmutableList.copyOf(landList);
    }
}
