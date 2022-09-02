package pp.muza.monopoly.model;

import pp.muza.monopoly.errors.BankException;

/**
 * This interface represents a bank in the game.
 * There are methods to get the balance of a player, to deposit coins, to withdraw
 */
public interface Bank {

    /**
     * Returns the balance of a player.
     *
     * @param player the player.
     * @return the balance of the player.
     */
    Integer getBalance(Player player);

    /**
     * Adds coins to the player's balance.
     *
     * @param player the player to add coins to
     * @param number the number of coins to add
     * @throws BankException if the player wallet is full
     */
    void deposit(Player player, Integer number) throws BankException;

    /**
     * Subtracts the given number from the player's balance.
     *
     * @param player the player to subtract coins from
     * @param price  the number of coins to subtract
     * @throws BankException if the player doesn't have enough coins
     */
    void withdraw(Player player, Integer price) throws BankException;

    /**
     * Set the player's balance to the given number.
     *
     * @param player the player to set the balance for
     * @param number the number to set the balance to
     */
    void set(Player player, Integer number);
}
