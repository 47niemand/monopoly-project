package pp.muza.monopoly.model.lands;

public interface Land {

    /**
     * get name of the land
     *
     * @return return name of the land
     */
    String getName();

    /**
     * get type of the land
     *
     * @return return type of the land
     */
    Type getType();

    enum Type {
        // Start is a subclass of Land
        START(Start.class),
        // Property is a subclass of Land
        PROPERTY(Property.class),
        // Jail is a subclass of Land
        JAIL(Jail.class),
        // Parking is a subclass of Land
        PARKING(Parking.class),
        // GotoJail is a subclass of Land
        GOTO_JAIL(GotoJail.class),
        // Chance is a subclass of Land
        CHANCE(Chance.class);

        final Class<? extends Land> aClass;

        Type(Class<? extends Land> landClass) {
            aClass = landClass;
        }
    }
}
