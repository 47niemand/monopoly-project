package pp.muza.monopoly.model;

import java.util.List;

/**
 * The Board interface.
 *
 * @author dmytromuza
 */
public interface Board {

    /**
     * Returns a land at the given index.
     *
     * @param index the index of the land.
     * @return the land.
     */
    Land getLand(int index);

    /**
     * Returns lands of the positions in the board.
     *
     * @param path the path of the land to get
     * @return the list of all lands on the path
     **/
    List<Land> getLands(List<Integer> path);

    /**
     * Returns the destination from the start position for given distance.
     *
     * @param start    the start position.
     * @param distance the distance to move.
     * @return the destination land id.
     * @throws IllegalArgumentException if the distance is less than or equal to 0.
     */
    int getDestination(int start, int distance);

    /**
     * Get the path from start (exclusive) to finish (inclusive).
     *
     * @param startPos start position
     * @param endPos   finish position
     * @return path from startPos to endPos
     */
    List<Integer> getPathTo(int startPos, int endPos);

    /**
     * Returns all the lands in the board.
     *
     * @return returns all lands in the board.
     */
    List<Land> getLands();

    /**
     * Returns the start position of the board.
     *
     * @return returns the position index.
     */
    int getStartPosition();

    /**
     * Returns the size of the board.
     *
     * @return returns the size of the board.
     */
    int size();
}
