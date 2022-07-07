package pp.muza.monopoly.model.game;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Board<K> {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<K> landList;
    private final int startPosition = 0;

    public K getLand(int position) {
        return landList.get(position);
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
    private int nextPosition(int start) {
        int next;
        next = (start + 1) % landList.size();
        return next;
    }

    public int getDestination(int start, int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        int current = start;
        for (int i = distance; i > 0; i--) {
            current = nextPosition(current);
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
            current = nextPosition(current);
            path.add(current);
        }
        return path;
    }

    public List<K> getLands() {
        return ImmutableList.copyOf(landList);
    }

}
