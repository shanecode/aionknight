package ru.aionknight.loginserver.dao;

import org.openaion.commons.database.dao.DAO;

import ru.aionknight.loginserver.model.Account;


/**
 * DAO that manages accounts.
 *
 * @author SoulKeeper
 */
public abstract class AccountDAO implements DAO
{
	/**
	 * Returns account by name or null
	 *
	 * @param name
	 *            account name
	 * @return account object or null
	 */
	public abstract Account getAccount(String name);

	/**
	 * Retuns account id or -1 in case of error
	 *
	 * @param name
	 *            name of account
	 * @return id or -1 in case of error
	 */
	public abstract int getAccountId(String name);

	/**
	 * Reruns account count If error occured - returns -1
	 *
	 * @return account count
	 */
	public abstract int getAccountCount();

	/**
	 * Inserts new account to database. Sets account ID to id that was generated by DB.
	 *
	 * @param account
	 *            account to insert
	 * @return true if was inserted, false in other case
	 */
	public abstract boolean insertAccount(Account account);

	/**
	 * Updates account in database
	 *
	 * @param account
	 *            account to update
	 * @return true if was updated, false in other case
	 */
	public abstract boolean updateAccount(Account account);

	/**
	 * Updates lastServer field of account
	 *
	 * @param accountId
	 *            account id
	 * @param lastServer
	 *            last accessed server
	 * @return was updated successful or not
	 */
	public abstract boolean updateLastServer(int accountId, byte lastServer);

	/**
	 * Updates last ip that was used to access an account
	 *
	 * @param accountId
	 *            account id
	 * @param ip
	 *            ip address
	 * @return was update successful or not
	 */
	public abstract boolean updateLastIp(int accountId, String ip);

	/**
	 * Get last ip that was used to access an account
	 *
	 * @param accountId
	 *            account id
	 * @return ip address
	 */
	public abstract String getLastIp(int accountId);

	/**
	 * Returns uniquire class name for all implementations
	 *
	 * @return uniquire class name for all implementations
	 */
	@Override
	public final String getClassName()
	{
		return AccountDAO.class.getName();
	}
}
