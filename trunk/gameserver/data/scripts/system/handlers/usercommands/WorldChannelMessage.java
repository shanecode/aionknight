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

package usercommands;

import gameserver.configs.main.CustomConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.CustomChannel;
import gameserver.utils.i18n.CustomMessageId;
import gameserver.utils.i18n.LanguageHandler;

public class WorldChannelMessage extends CustomChannel
{
	public WorldChannelMessage ()
	{
		super(LanguageHandler.translate(CustomMessageId.CHANNEL_COMMAND_WORLD), Player.CHAT_FIXED_ON_WORLD);
	}
	
	@Override
	public void executeCommand(Player player, String params)
	{
		if (!CustomConfig.CHANNEL_WORLD_ENABLED)
		{
			PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.CHANNEL_WORLD_DISABLED, Player.getChanCommand(Player.CHAT_FIXED_ON_WORLD), Player.getChanCommand(Player.CHAT_FIXED_ON_ELYOS), Player.getChanCommand(Player.CHAT_FIXED_ON_ASMOS)));
			return;
		}
		
		super.executeCommand(player, params);
	}
}