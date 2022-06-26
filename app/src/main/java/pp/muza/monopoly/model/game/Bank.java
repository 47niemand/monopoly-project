package pp.muza.monopoly.model.game;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.player.Player;

public class Bank {

    private static final Logger LOG = LoggerFactory.getLogger(Bank.class);

    private final Map<Player, BigDecimal> playerMoney = new HashMap<>();

    public BigDecimal getBalance(Player player) {
        return playerMoney.getOrDefault(player, BigDecimal.ZERO);
    }

    /**
     * Adds money to the player's balance.
     *
     * @param player the player to add money to
     * @param amount the amount of money to add
     * @throws BankException if the player wallet is full
     */
    public void addMoney(Player player, BigDecimal amount) throws BankException {
        LOG.info("Adding {} money to player {}", amount, player.getName());
        playerMoney.put(player, playerMoney.getOrDefault(player, BigDecimal.ZERO).add(amount));
        LOG.info("Player {} has {}", player.getName(), playerMoney.get(player));
    }

    /**
     * Subtracts the given amount from the player's balance.
     *
     * @param player the player to subtract money from
     * @param price  the amount of money to subtract
     * @throws BankException if the player doesn't have enough money
     */
    public void withdraw(Player player, BigDecimal price) throws BankException {
        LOG.info("Withdrawing {} money from player {}", price, player.getName());
        if (playerMoney.get(player).compareTo(price) < 0) {
            LOG.warn("Player {} has not enough money", player.getName());
            throw new BankException("Not enough money");
        }
        playerMoney.put(player, playerMoney.getOrDefault(player, BigDecimal.ZERO).subtract(price));
        LOG.info("Player {} has {}", player.getName(), playerMoney.get(player));
    }

    /**
     * Rest the bank system
     */
    public void reset() {
        playerMoney.clear();
    }
}
