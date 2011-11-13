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
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticObject;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.spawn.SpawnEngine;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;


/**
 * @author Luno
 * 
 */

public class ReloadSpawns extends AdminCommand
{

	public ReloadSpawns()
	{
		super("reload_spawn");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_RELOADSPAWNS)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		// despawn all 
		World.getInstance().doOnAllObjects(new Executor<AionObject>(){
			@Override
			public boolean run(AionObject obj)
			{
				if (obj instanceof Npc || obj instanceof Gatherable || obj instanceof StaticObject)
				{
					((VisibleObject) obj).getController().delete();
				}
				return true;
			}
		});
		
		// spawn all;
		SpawnEngine.getInstance().spawnAll(); 
	}
}
