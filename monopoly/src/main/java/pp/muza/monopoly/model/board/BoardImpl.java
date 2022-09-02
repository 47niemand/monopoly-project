package pp.muza.monopoly.model.board;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Value;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Land;

@Value
public class BoardImpl implements Board {

    ImmutableList<Land> lands;
    int startPosition = 0;

    public BoardImpl(List<Land> lands) {
        this.lands = ImmutableList.copyOf(lands);
    }

    @Override
    public Land getLand(int index) {
        return lands.get(index);
    }

    @Override
    public List<Land> getLands(List<Integer> path) {
        ImmutableList.Builder<Land> tmp = ImmutableList.builder();
        for (Integer integer : path) {
            tmp.add(getLand(integer));
        }
        return tmp.build();
    }

    private int nextPosition(int current) {
        int next;
        next = (current + 1) % lands.size();
        return next;
    }

    @Override
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

    @Override
    public List<Integer> getPathTo(int startPos, int endPos) {
        ImmutableList.Builder<Integer> path = ImmutableList.builder();
        int current = startPos;
        int distance = 0;
        while (current != endPos) {
            current = nextPosition(current);
            distance++;
            path.add(current);
            if (distance > lands.size()) {
                throw new IllegalStateException("Path is too long");
            }
        }
        return path.build();
    }

    @Override
    public List<Land> getLands() {
        return lands;
    }
}
