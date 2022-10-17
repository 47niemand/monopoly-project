package pp.muza.monopoly.model.bank;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.errors.BankError;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Player;

/**
 * Stores the bank state.
 *
 * @author dmytromuza
 */
public class BankImpl implements Bank {

    private static final Logger LOG = LoggerFactory.getLogger(BankImpl.class);

    private final Map<Player, Integer> playerCoins = new HashMap<>();

    private void checkCoins(int coins) {
        if (coins < 0) {
            throw new IllegalArgumentException(BankError.NEGATIVE_VALUE.getMessage());
        }
    }

    @Override
    public int getBalance(Player player) {
        return playerCoins.getOrDefault(player, 0);
    }

    @Override
    public void deposit(Player player, int value) throws BankException {
        checkCoins(value);
        LOG.info("Adding {} coins to {}", value, player.getName());
        playerCoins.put(player, playerCoins.getOrDefault(player, 0) + value);
        LOG.info("{} has {} coin(s)", player.getName(), playerCoins.get(player));
    }

    @Override
    public void withdraw(Player player, int value) throws BankException {
        checkCoins(value);
        LOG.info("Withdrawing {} coin(s) from player {}", value, player.getName());
        if (playerCoins.get(player).compareTo(value) < 0) {
            LOG.warn("{} has not enough coins {}, current balance: {}", player.getName(), value, playerCoins.get(player));
            throw new BankException(BankError.NOT_ENOUGH_COINS);
        }
        playerCoins.put(player, playerCoins.getOrDefault(player, 0) - value);
        LOG.info("{} has {} coin(s)", player.getName(), playerCoins.get(player));
    }

    @Override
    public void set(Player player, int value) {
        LOG.info("Putting {} coin(s) in {}'s account", value, player.getName());
        playerCoins.put(player, value);
    }
}
