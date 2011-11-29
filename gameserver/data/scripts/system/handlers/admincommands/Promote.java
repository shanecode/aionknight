/**
 * This file is part of Aion-Knight Dev. Team [http://aion-knight.ru]
 *
 * Aion-Knight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aion-Knight is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aion-Knight. If not, see <http://www.gnu.org/licenses/>.
 */

package admincommands;

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;

public class Promote extends AdminCommand
{

	public Promote()
	{
		super("promote");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_PROMOTE)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if (params.length != 3 )
		{
			PacketSendUtility.sendMessage(admin, "syntax //promote <player name> <accesslevel | membership> <rolemask>");
			return;
		}

		int mask = 0;
		try
		{
			mask = Integer.parseInt(params[2]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "only numbers allowed for rolemask!");
			return;
		}

		int type = 0;
		if (params[1].toLowerCase().equals("accesslevel"))
		{
			type = 1;
			if (mask > 3 || mask < 0)
			{
				PacketSendUtility.sendMessage(admin, "accesslevel can be 0, 1, 2 or 3");
				return;
			}
		}
		else if (params[1].toLowerCase().equals("membership"))
		{
			type = 2;
			if (mask > 1 || mask < 0)
			{
				PacketSendUtility.sendMessage(admin, "membership can be 0 or 1");
				return;
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //promote <player name> <accesslevel | membership> <rolemask>");
			return;
		}

		Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "the specified player is not online.");
			return;
		}
		LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), mask, type);
	}
}