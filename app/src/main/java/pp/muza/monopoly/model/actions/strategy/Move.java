package pp.muza.monopoly.model.actions.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Board;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.lands.Start;

/**
 * The player moves to a new position on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Move extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Move.class);

    private final int distance;

    Move(int distance) {
        super("Move", ActionType.MOVE, 10, true);
        this.distance = distance;
    }

    @Override
    protected void onExecute(Turn turn) {
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        int startPos = game.getPlayerPos(player);
        List<Integer> path = game.getBoard().getPath(startPos, Board.Direction.NEXT, distance);
        List<Land> lands = game.getBoard().getLands(path);
        int finalPos = path.get(path.size() - 1);
        game.setPlayerPos(player, finalPos);
        Land destination = lands.get(lands.size() - 1);

        switch (destination.getType()) {
            // if player stop at a property
            case PROPERTY:
                // if property is not owned, ask player to buy it
                // if property is owned, ask player to  pay rent
                Property property = (Property) destination;
                Player owner = game.getLandPlayer(finalPos);
                if (owner != null && !player.equals(owner)) {
                    LOG.info("Player {} has to pay rent to player {}", player.getName(), owner.getName());
                    // spawn a new action card to pay rent
                    turn.addActionCard(new PayRent(owner, property));
                } else if (player.equals(owner)) {
                    LOG.info("Player on its own property, no need to pay rent");
                } else {
                    // spawn a new action card to buy the property
                    LOG.info("Player {} can buy property {}", player.getName(), property.getName());
                    turn.addActionCard(new Buy(finalPos, property));
                }
                break;
            case GOTO_JAIL:
                // spawn a new action card to go to jail
                turn.addActionCard(new GoToJail());
                break;
        }

        // check if player pass the start
        for (Land land : lands) {
            if (land.getType() == Land.Type.START) {
                // spawn a new action card to get income due to start
                turn.addActionCard(new Income(((Start) land).getIncomeTax()));
                break;
            }
        }
    }
}
