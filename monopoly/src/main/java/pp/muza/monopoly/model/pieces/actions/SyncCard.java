package pp.muza.monopoly.model.pieces.actions;

import pp.muza.monopoly.model.ActionCard;

public interface SyncCard extends ActionCard {

    BaseActionCard sync(SyncCard card);
}
