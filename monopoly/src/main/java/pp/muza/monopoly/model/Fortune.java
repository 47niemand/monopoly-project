package pp.muza.monopoly.model;

import pp.muza.monopoly.model.pieces.actions.Chance;

public interface Fortune extends ActionCard {

    /**
     * Returns a description of the fortune card.
     *
     * @return the description of the fortune card
     */
    String getDescription();

    /**
     * Return a chance card's details
     *
     * @return the chance card's details
     */
    Chance getChance();

}
