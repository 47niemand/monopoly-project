package pp.muza.monopoly.model.actions.cards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.cards.chance.Chance;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.lands.Start;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.turn.Turn;

import java.util.List;

public final class ActionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ActionUtils.class);

    private ActionUtils() {
    }

    /**
     * Creates a contract for each property in the player's possession.
     *
     * @param turn the current turn
     * @return false if the player has no more properties in his possession or such
     * contracts has already been created, true otherwise
     */
    public static boolean createContractsForPlayersPossession(Turn turn) {

        Player player = turn.getPlayer();
        List<Land.Entry<Property>> playerProperties = turn.getProperties();

        if (playerProperties.size() > 0) {
            LOG.info("Creating contracts for each property in the player's ({}) possession", player.getName());

            for (Land.Entry<Property> property : playerProperties) {
                boolean result = turn.addActionCard(new Contract(property.getPosition(), property.getLand()));
                if (!result) {
                    return false;
                }
            }
        } else {
            LOG.info("Player ({}) has no properties", player.getName());
            return false;
        }
        return true;
    }

    /**
     * Creates action cards for the player's current position.
     *
     * @param turn   the current turn
     * @param landId the land id of the player's current position
     * @param land   the land of the player's current position
     */
    public static void onArrival(Turn turn, int landId, Land land) {
        Player player = turn.getPlayer();
        switch (land.getType()) {
            case CHANCE:
                LOG.info("Player {} has to get chance card", player.getName());
                // get chance card from deck
                Chance chance = turn.popChanceCard();
                turn.addActionCard(chance);
                break;
            // if player stop at a property
            case PROPERTY:
                // if property is not owned, ask player to buy it
                // if property is owned, ask player to pay rent
                Property property = (Property) land;
                Player owner = turn.getLandOwner(landId);
                if (owner != null && !player.equals(owner)) {
                    LOG.info("Player {} has to pay rent to player {}", player.getName(), owner.getName());
                    // spawn a new action card to pay rent
                    turn.addActionCard(new PayRent(owner, property));
                } else if (player.equals(owner)) {
                    LOG.info("Player on its own property, no need to pay rent");
                } else {
                    // spawn a new action card to buy the property
                    LOG.info("Player {} can buy property {}", player.getName(), property.getName());
                    turn.addActionCard(new Buy(landId, property));
                }
                break;
            case GOTO_JAIL:
                // spawn a new action card to go to jail
                turn.addActionCard(new GoToJail());
                break;
            default:
                LOG.info("nothing to do for player {} on land {}", player.getName(), land.getName());
                break;
        }
    }

    /**
     * Because of the passed lands, this method generates action cards for the
     * player.
     *
     * @param turn  the current turn
     * @param lands lands the player has passed
     */

    private static void doPassedStart(Turn turn, List<Land> lands) {
        for (Land land : lands) {
            if (land.getType() == Land.Type.START) {
                // spawn a new action card to get income due to start
                LOG.info("Player {} has to get income due to start", turn.getPlayer().getName());
                turn.addActionCard(new Income(((Start) land).getIncomeTax()));
                break;
            }
        }
    }

    /**
     * Creates action cards for the player's move on given distance.
     *
     * @param turn     the current turn
     * @param distance the distance the player should move on
     */
    public static void onMove(Turn turn, int distance) {
        int endPos = turn.getDestination(distance);
        List<Land> path = turn.moveTo(endPos);
        ActionUtils.doPassedStart(turn, path);
        assert turn.getLand(endPos) == path.get(path.size() - 1);
        ActionUtils.onArrival(turn, endPos, turn.getLand(endPos));
    }

    /**
     * Creates action cards for the player's move on specified lands.
     *
     * @param turn   the current turn
     * @param landId the land the player should move on
     */
    public static void onMoveTo(Turn turn, int landId) {
        List<Land> path = turn.moveTo(landId);
        if (path.size() == 0) {
            LOG.warn("Staying on the same land");
        } else {
            assert path.get(path.size() - 1) == turn.getLand(landId);
        }
        ActionUtils.doPassedStart(turn, path);
        ActionUtils.onArrival(turn, landId, turn.getLand(landId));
    }
}
