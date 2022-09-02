package pp.muza.monopoly.model;



import pp.muza.monopoly.model.pieces.lands.PropertyColor;

public interface Property extends Land {

    /**
     * Returns the price of the property.
     *
     * @return the price of the property
     */
    Integer getPrice();

    /**
     * Returns the color of the property.
     *
     * @return the color of the property
     */
    PropertyColor getColor();

    /**
     * Returns the asset of the property.
     *
     * @return the asset of the property
     */
    Asset getAsset();

}
