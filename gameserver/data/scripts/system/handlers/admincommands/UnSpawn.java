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

package admincommands;

import commons.database.dao.DAOManager;
import gameserver.configs.administration.AdminConfig;
import gameserver.dao.SpawnDAO;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawn.SpawnTemplate;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;
import org.apache.log4j.Logger;
import java.util.Map;
import java.util.Map.Entry;

public class UnSpawn extends AdminCommand
{
	public UnSpawn()
	{
		super("unspawn");
	}

	private boolean despawn (int objectId, Player admin, boolean delete)
	{
		World world = World.getInstance();
		boolean result = false;
		int spawnId = 0;

		AionObject object = world.findAionObject(objectId);
		if (object != null && object instanceof VisibleObject)
		{
			VisibleObject vObject = (VisibleObject)object;
			spawnId = vObject.getSpawn().getSpawnId();
			if (vObject.isSpawned())
			{
				try
				{
					world.despawn(vObject);
					result = true;
				}
				catch (NullPointerException e)
				{
					Logger.getLogger(UnSpawn.class).warn("Trying to despawn already spawned object", e);
					result = false;
				}
			}
			else
			{
				result = true;
			}

			vObject.getController().delete();

			if (spawnId > 0)
			{
				result = DAOManager.getDAO(SpawnDAO.class).setSpawned(vObject.getSpawn().getSpawnId(), -1, false) && result;
				if (delete)
				{
					result = DAOManager.getDAO(SpawnDAO.class).deleteSpawn(vObject.getSpawn().getSpawnId()) && result;
				}
			}
		}

		if (result)
		{
			PacketSendUtility.sendMessage(admin, (spawnId>0?"Spawn #" + spawnId + " ":"") + object.getClass().getSimpleName()+" \""+ object.getName() + "\" "+ (delete?"deleted":"despawned"));
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "An error occured while " + (delete?"deleting":"despawning") + " " + (spawnId>0?"spawn #" + spawnId + " ":"") + object == null ? " the object was null." : object.getClass().getSimpleName()+" \""+ object.getName() + "\" ");
		}

		return result;
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		String syntax = "usage: //unspawn <group name> | <spawn id> | <object id> <delete>";

		if (admin.getAccessLevel() < AdminConfig.COMMAND_SPAWN)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		String groupName = null;
		int objectId = -1;
		int spawnId = -1;
		boolean delete = false;

		if (params != null && params.length > 2)
		{
			PacketSendUtility.sendMessage(admin, syntax);
			return;
		}

		if (params == null || params.length >= 0)
		{
			if (params.length == 1)
			{
				if ("delete".equalsIgnoreCase(params[0]))
				{
					delete = true;
				}
				else
				{
					try
					{
						if (params[0].startsWith("#"))
						{
							spawnId = Integer.parseInt(params[0].substring(1));
						}
						else
						{
							objectId = Integer.parseInt(params[0]);
						}

						if (spawnId < -1 || objectId < -1 || spawnId == 0 || objectId == 0)
						{
							PacketSendUtility.sendMessage(admin, "Invalid id "+params[0]+" specified");
							PacketSendUtility.sendMessage(admin, syntax);
							return;
						}
					}
					catch (NumberFormatException e)
					{
						groupName = params[0];
					}
				}
			}

			if (params.length == 2)
			{
				groupName = params[0];
				if ("delete".equalsIgnoreCase(params[1]))
				{
					delete = true;
				}
				else
				{
					PacketSendUtility.sendMessage(admin, syntax);
					return;
				}
			}

			if (groupName == null && spawnId == -1)
			{
				if (admin.getTarget() == null)
				{
					PacketSendUtility.sendMessage(admin, "You didn't target any NPC");
					PacketSendUtility.sendMessage(admin, syntax);
					return;
				}
				else
				{
					if (admin.getTarget() instanceof Npc)
					{
						Npc npc = (Npc)admin.getTarget();
						objectId = npc.getObjectId();
					}
					else
					{
						PacketSendUtility.sendMessage(admin, "The selected target is not an Npc");
						return;
					}
				}
			}
		}

		if (groupName != null)
		{
			Map<Integer, SpawnTemplate> spawns = DAOManager.getDAO(SpawnDAO.class).listSpawns(admin.getObjectId(), groupName, SpawnDAO.SpawnType.SPAWNED);
			if (spawns == null)
			{
				PacketSendUtility.sendMessage(admin, "Cannot find group "+groupName);
				return;
			}

			boolean result = true;
			for (Entry<Integer, SpawnTemplate> spawn : spawns.entrySet())
			{
				result = despawn(spawn.getKey(), admin, delete) && result;
			}

			DAOManager.getDAO(SpawnDAO.class).setGroupSpawned(admin.getObjectId(), groupName, false);

			if (delete)
			{
				result = DAOManager.getDAO(SpawnDAO.class).deleteSpawnGroup(admin.getObjectId(), groupName) && result;
			}

			if (result)
			{
				PacketSendUtility.sendMessage(admin, ">>> Group \""+groupName+"\" " + (delete?"deleted":"despawned") + " with success !");
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "!!! An error occured while " + (delete?"deleting":"despawning") + " \""+groupName+"\" !");
			}
		}
		else
		{
			if(spawnId > 0)
			{
				objectId = DAOManager.getDAO(SpawnDAO.class).getSpawnObjectId(spawnId, true);
				if (objectId <= 0)
				{
					PacketSendUtility.sendMessage(admin, "Spawn #"+spawnId+" was not found");
					return;
				}
			}

			if(objectId > 0)
			{
				despawn(objectId, admin, delete);
				return;
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "Object #"+objectId+" does not exists");
				return;
			}
		}
	}
}