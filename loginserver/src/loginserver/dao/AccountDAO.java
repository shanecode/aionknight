/*
 * Emulator game server Aion 2.7 from the command of developers 'Aion-Knight Dev. Team' is
 * free software; you can redistribute it and/or modify it under the terms of
 * GNU affero general Public License (GNU GPL)as published by the free software
 * security (FSF), or to License version 3 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranties related to
 * CONSUMER PROPERTIES and SUITABILITY FOR CERTAIN PURPOSES. For details, see
 * General Public License is the GNU.
 *
 * You should have received a copy of the GNU affero general Public License along with this program.
 * If it is not, write to the Free Software Foundation, Inc., 675 Mass Ave,
 * Cambridge, MA 02139, USA
 *
 * Web developers : http://aion-knight.ru
 * Support of the game client : Aion 2.7- 'Arena of Death' (Innova)
 * The version of the server : Aion-Knight 2.7 (Beta version)
 */

package loginserver.dao;

import loginserver.model.Account;
import commons.database.dao.DAO;

public abstract class AccountDAO implements DAO
{
    /**
     * Returns account by name or null
     *
     * @param name account name
     * @return account object or null
     */
    public abstract Account getAccount(String name);

    /**
     * Retuns account id or -1 in case of error
     *
     * @param name name of account
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
     * @param account account to insert
     * @return true if was inserted, false in other case
     */
    public abstract boolean insertAccount(Account account);

    /**
     * Updates account in database
     *
     * @param account account to update
     * @return true if was updated, false in other case
     */
    public abstract boolean updateAccount(Account account);

    /**
     * Updates lastServer field of account
     *
     * @param accountId  account id
     * @param lastServer last accessed server
     * @return was updated successful or not
     */
    public abstract boolean updateLastServer(int accountId, byte lastServer);

    /**
     * Updates last ip that was used to access an account
     *
     * @param accountId account id
     * @param ip        ip address
     * @return was update successful or not
     */
    public abstract boolean updateLastIp(int accountId, String ip);

    /**
     * Get last ip that was used to access an account
     *
     * @param accountId account id
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
    
	/**
	 * Updates last mac that was used to access an account
	 * 
	 * @param accountId
	 *          account id
	 * @param mac
	 *          mac address
	 * @return was update successful or not
	 */
	public abstract boolean updateLastMac(int accountId, String mac);
    
}