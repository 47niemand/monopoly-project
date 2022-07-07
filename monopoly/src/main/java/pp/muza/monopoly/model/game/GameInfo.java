package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import pp.muza.monopoly.model.actions.cards.Chance;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;

import java.util.List;

/**
 * This class represents the state of the game.
 */
@Value
final class GameInfo {
    private final List<Player> players;
    private final List<PlayerInfo> playerInfo;
    private final Board<Land> board;
    private final List<Chance> chanceCards;
    private final int currentPlayerIndex;
    private final int turnNumber;
    private final int maxTurns;

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
