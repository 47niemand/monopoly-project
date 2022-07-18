package pp.muza.monopoly.model;

import java.math.BigDecimal;

import pp.muza.monopoly.errors.BankException;

public interface Bank {

    /**
     * Returns the balance of a player.
     *
     * @param player the player.
     * @return the balance of the player.
     */
    BigDecimal getBalance(Player player);

    /**
     * Adds money to the player's balance.
     *
     * @param player the player to add money to
     * @param amount the amount of money to add
     * @throws BankException if the player wallet is full
     */
    void deposit(Player player, BigDecimal amount) throws BankException;

    /**
     * Subtracts the given amount from the player's balance.
     *
     * @param player the player to subtract money from
     * @param price  the amount of money to subtract
     * @throws BankException if the player doesn't have enough money
     */
    void withdraw(Player player, BigDecimal price) throws BankException;

    /**
     * Set the player's balance to the given amount.
     *
     * @param player the player to set the balance for
     * @param amount the amount to set the balance to
     */
    void set(Player player, BigDecimal amount);
}
