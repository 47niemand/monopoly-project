package pp.muza.monopoly.model;

import pp.muza.monopoly.model.pieces.lands.LandType;

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
    LandType getType();

}
