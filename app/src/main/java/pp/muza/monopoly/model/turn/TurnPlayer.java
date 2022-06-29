package pp.muza.monopoly.model.turn;

import java.util.List;

import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

public interface TurnPlayer {
    boolean isFinished();

    /**
     * get the list of action cards that can be executed at the current moment.
     *
     * @return the list of action cards that can be executed at the current moment
     */
    List<ActionCard> getActiveActionCards();

    /**
     * get the status of the player.
     *
     * @return the status of the player
     */
    Game.PlayerStatus getStatus();

    /**
     * get the owner of the land.
     *
     * @param landId the land to get the owner of
     * @return the owner of the land, null if the land is not owned
     */
    Player getLandOwner(int landId);

    /**
     * get the player properties.
     *
     * @return the player properties
     */
    List<Land.Entry<Property>> getProperties();

    /**
     * get the player
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * execute the action card.
     */
    void executeActionCard(ActionCard actionCard) throws ActionCardException;

    List<Player> getPlayers();
}
