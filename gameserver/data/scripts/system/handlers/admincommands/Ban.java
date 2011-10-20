/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import org.openaion.commons.database.dao.DAOManager;

import ru.aionknight.gameserver.configs.administration.AdminConfig;
import ru.aionknight.gameserver.dao.PlayerDAO;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.network.loginserver.LoginServer;
import ru.aionknight.gameserver.utils.PacketSendUtility;
import ru.aionknight.gameserver.utils.Util;
import ru.aionknight.gameserver.utils.chathandlers.AdminCommand;
import ru.aionknight.gameserver.world.World;


/**
 * @author Watson
 * 
 */
public class Ban extends AdminCommand
{
	public Ban()
	{
		super("ban");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_BAN)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command!");
			return;
		}

		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player name> <account | ip | full> <time in minutes>");
			return;
		}

		// We need to get player's account ID
		String name = Util.convertName(params[0]);
		int accountId = 0;

		// First, try to find player in the World
		Player player = World.getInstance().findPlayer(name);
		if (player != null)
			accountId = player.getClientConnection().getAccount().getId();

		// Second, try to get account ID of offline player from database
		if (accountId == 0)
			accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

		// Third, fail
		if (accountId == 0)
		{
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player name> <account | ip | full> <time in minutes>");
			return;
		}

		byte type = 3; // Default: full
		if (params.length > 1)
		{
			// Smart Matching
			String stype = params[1].toLowerCase();
			if (("account").startsWith(stype))
				type = 1;
			else if (("ip").startsWith(stype))
				type = 2;
			else if (("full").startsWith(stype))
				type = 3;
			else
			{
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player name> <account | ip | full> <time in minutes>");
				return;
			}
		}

		int time = 0; // Default: infinity
		if (params.length > 2)
		{
			try
			{
				time = Integer.parseInt(params[2]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player name> <account | ip | full> <time in minutes>");
				return;
			}
		}

		LoginServer.getInstance().sendBanPacket(type, accountId, "", time, admin.getObjectId());
	}
}