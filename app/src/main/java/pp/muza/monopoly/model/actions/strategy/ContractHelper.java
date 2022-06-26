package pp.muza.monopoly.model.actions.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.actions.contracts.Contract;
import pp.muza.monopoly.model.lands.Property;

public class ContractHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ContractHelper.class);

    /**
     * Creates a contract for each property in the player's possession.
     *
     * @param turn the current turn
     * @return false if the player has no more properties in his possession or such contracts has already been created, true otherwise
     */
    public static boolean createContractsForPlayersPossession(Turn turn) {
        boolean result = false;
        boolean finished = false;
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        List<Integer> playerPropertiesIds = game.getPlayerProperties(player);
        if (playerPropertiesIds.size() > 0) {
            LOG.info("Creating contracts for each property in the player's ({}) possession", player.getName());
            for (int propertyId : playerPropertiesIds) {
                Property property = (Property) game.getLand(propertyId);
                Contract contract = Contract.of(propertyId, property);
                if (!turn.addActionCard(contract)) {
                    finished = true;
                    break;
                }
            }
            if (!finished) {
                result = true;
            }
        } else {
            LOG.info("Player ({}) has no properties", player.getName());
        }
        return result;
    }

}
