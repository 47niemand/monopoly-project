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
     * @param start     the current position
     * @param direction the direction to move
     * @return the next position
     **/
    private int getNeighborPos(int start, Direction direction) {
        int next;
        switch (direction) {
            case NEXT:
                next = (start + 1) % landList.size();
                break;
            case PREV:
                next = (landList.size() + start - 1) % landList.size();
                break;
            default:
                throw new IllegalStateException(String.format("Direction %s is not supported", direction));
        }
        return next;
    }

    /**
     * Get the path from start (exclusive) to end (inclusive)
     *
     * @param start     start position
     * @param direction direction of the path
     * @param distance  distance of the path
     * @return path from start to end
     */
    public List<Integer> getPath(int start, Direction direction, int distance) {
        List<Integer> path = new ArrayList<>();
        int current = start;
        for (int i = distance; i > 0; i--) {
            current = getNeighborPos(current, Direction.NEXT);
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

    public enum Direction {
        NEXT, PREV
    }
}
