package pp.muza.monopoly.model.bank;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.model.Bank;
import pp.muza.monopoly.model.Player;

public class BankImpl implements Bank {

    private static final Logger LOG = LoggerFactory.getLogger(BankImpl.class);

    private final Map<Player, Integer> playerCoins = new HashMap<>();

    @Override
    public Integer getBalance(Player player) {
        return playerCoins.getOrDefault(player, 0);
    }

    @Override
    public void deposit(Player player, Integer number) throws BankException {
        LOG.info("Adding {} coins to {}", number, player.getName());
        playerCoins.put(player, playerCoins.getOrDefault(player, 0) + number);
        LOG.info("{} has {}", player.getName(), playerCoins.get(player));
    }

    @Override
    public void withdraw(Player player, Integer price) throws BankException {
        LOG.info("Withdrawing {} coin(s) from player {}", price,  player.getName());
        if (playerCoins.get(player).compareTo(price) < 0) {
            LOG.warn("{} has not enough coins {}, current balance: {}", player.getName(), price, playerCoins.get(player));
            throw new BankException("Not enough coins");
        }
        playerCoins.put(player, playerCoins.getOrDefault(player, 0) - price);
        LOG.info("{} has {} coin(s)", player.getName(), playerCoins.get(player));
    }

    @Override
    public void set(Player player, Integer number) {
        LOG.info("Putting {} coins in {}'s account", number, player.getName());
        playerCoins.put(player, number);
    }
}
