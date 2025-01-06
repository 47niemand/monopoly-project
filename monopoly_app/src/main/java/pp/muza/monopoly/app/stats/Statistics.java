package pp.muza.monopoly.app.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.model.Player;

/**
 * @author dmytromuza
 */
public class Statistics {

    private final List<TurnInfo> turns = new ArrayList<>();
    private final Map<Integer, TurnInfo> turnInfo = new LinkedHashMap<>();
    private final Map<Player, Set<Integer>> playerTurns = new LinkedHashMap<>();

    public void addTurnInfo(TurnInfo turn) {
        turns.add(turn);
        turnInfo.put(turn.getTurnNumber(), turn);
        playerTurns.computeIfAbsent(turn.getPlayerInfo().getPlayer(), k -> new LinkedHashSet<>())
                .add(turn.getTurnNumber());
    }

    public List<TurnInfo> getTurns() {
        return turns;
    }

    public TurnInfo getTurnInfo(int turnNumber) {
        return turnInfo.get(turnNumber);
    }

    public Set<Integer> getPlayerTurns(Player player) {
        return playerTurns.get(player);
    }

    public Integer getLastPlayersTurn(Player player) {
        return playerTurns.get(player).stream().max(Integer::compareTo).orElse(null);
    }

    public Integer getPreviousPlayersTurn(Player player, int turnNumber) {
        return playerTurns.get(player).stream().filter(t -> t < turnNumber).max(Integer::compareTo).orElse(null);
    }

}
