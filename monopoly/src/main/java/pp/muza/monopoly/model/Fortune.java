package pp.muza.monopoly.model;

import pp.muza.monopoly.model.pieces.actions.Chance;

/**
 * Fortune card interface.
 *
 * @author dmytromuza
 */
public interface Fortune extends ActionCard {

    /**
     * Return a chance card's details
     *
     * @return the chance card's details
     */
    Chance getChance();

}
