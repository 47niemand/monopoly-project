package pp.muza.monopoly.model.lands;

import com.google.common.collect.ImmutableList;
import lombok.Value;

import java.util.List;

@Value
public class Board<K> {

    ImmutableList<K> landList;
    int startPosition = 0;

    public Board(List<K> landList) {
        this.landList = ImmutableList.copyOf(landList);
    }

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
        ImmutableList.Builder<K> tmp = ImmutableList.builder();
        for (Integer integer : path) {
            tmp.add(getLand(integer));
        }
        return tmp.build();
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
        ImmutableList.Builder<Integer> path = ImmutableList.builder();
        int current = startPos;
        while (current != endPos) {
            current = nextPosition(current);
            path.add(current);
        }
        return path.build();
    }

    public List<K> getLands() {
        return landList;
    }
}
