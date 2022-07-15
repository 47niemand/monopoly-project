package pp.muza.monopoly.model.bank;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.errors.BankException;

public class BankImpl implements Bank {

    private static final Logger LOG = LoggerFactory.getLogger(BankImpl.class);

    private final Map<Player, BigDecimal> playerMoney = new HashMap<>();

    @Override
    public BigDecimal getBalance(Player player) {
        return playerMoney.getOrDefault(player, BigDecimal.ZERO);
    }

    @Override
    public void deposit(Player player, BigDecimal amount) throws BankException {
        LOG.info("Adding {} money to {}", amount, player.getName());
        playerMoney.put(player, playerMoney.getOrDefault(player, BigDecimal.ZERO).add(amount));
        LOG.info("{} has {}", player.getName(), playerMoney.get(player));
    }

    @Override
    public void withdraw(Player player, BigDecimal price) throws BankException {
        LOG.info("Withdrawing {} money from player {}", price, player.getName());
        if (playerMoney.get(player).compareTo(price) < 0) {
            LOG.warn("Player {} has not enough money {}, current balance: {}", player.getName(), price, playerMoney.get(player));
            throw new BankException("Not enough money");
        }
        playerMoney.put(player, playerMoney.getOrDefault(player, BigDecimal.ZERO).subtract(price));
        LOG.info("Player {} has {}", player.getName(), playerMoney.get(player));
    }

    @Override
    public void set(Player player, BigDecimal amount) {
        LOG.info("Setting {} money to {}", amount, player.getName());
        playerMoney.put(player, amount);
    }
}
