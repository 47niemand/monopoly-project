package pp.muza.monopoly.model;

public interface Land {

    /**
     * Returns the name of the land.
     *
     * @return the name of the land
     */
    String getName();

    /**
     * Returns the type of the land.
     *
     * @return the type of the land
     */
    Type getType();

    enum Type {
        // type of land
        START,
        PROPERTY,
        JAIL,
        PARKING,
        GOTO_JAIL,
        CHANCE
    }
}
