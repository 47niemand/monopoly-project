package pp.muza.monopoly.model.pieces.actions;

import pp.muza.monopoly.model.ActionCard;

/**
 * Synchronization card.
 * <p>
 * This card is used to synchronize some properties between the cards.
 * </p>
 *
 * @author dmytromuza
 */
public interface SyncCard extends ActionCard {

    /**
     * The method syncs the card with the specified card.
     *
     * @param card the card to sync with
     * @return the synced card
     */
    BaseActionCard sync(SyncCard card);
}
