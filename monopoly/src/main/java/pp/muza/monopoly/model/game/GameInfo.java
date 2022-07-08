package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import pp.muza.monopoly.model.actions.Chance;
import pp.muza.monopoly.model.lands.Board;
import pp.muza.monopoly.model.lands.Land;

import java.util.List;

/**
 * This class represents the state of the game.
 */
@Value
class GameInfo {
    List<Player> players;
    List<PlayerInfo> playerInfo;
    Board<Land> board;
    List<Chance> chanceCards;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns;

    public GameInfo(Game game) {
        this.players = ImmutableList.copyOf(game.getPlayers());
        ImmutableList.Builder<PlayerInfo> playerInfoBuilder = ImmutableList.builder();
        for (Player player : players) {
            playerInfoBuilder.add(game.getPlayerInfo(player));
        }
        this.playerInfo = playerInfoBuilder.build();
        this.board = game.getBoard();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.turnNumber = game.getTurnNumber();
        this.chanceCards = game.getChanceCards();
        this.maxTurns = game.getMaxTurns();
    }
}
