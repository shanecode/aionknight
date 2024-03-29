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

package gameserver.services;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.zone.ZoneName;

public class ArenaService
{
	public static final ArenaService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	public boolean isEnemy(Player effector, Player effected)
	{
		if(effector == effected)
			return false;
        return !isInSameGroup(effector, effected) && !isInSameAlliance(effector, effected) && effector.getInArena() && effected.getInArena();

    }
	
	public void onDie(final Player player, Creature lastAttacker)
	{
		player.getEffectController().removeAllEffects();
		player.getController().cancelCurrentSkill();
		
		Summon summon = player.getSummon();
		if(summon != null)
			summon.getController().release(UnsummonType.UNSPECIFIED);

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
		player.getObserveController().notifyDeath(player);
		
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				if(isInTrinielArena(player))
					TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f, 0);
				if(isInSanctumArena(player))
					TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f, 0);
			}
		}, 5000);
	}
	
	public boolean isInArena(Player player)
	{
        return isInTrinielArena(player) || isInSanctumArena(player);
    }
	
	private boolean isInTrinielArena(Player player)
	{
		int world = player.getWorldId();
        return world == 120010000 && ZoneService.getInstance().isInsideZone(player, ZoneName.TRINIEL_PVP_ZONE);
    }
	
	private boolean isInSanctumArena(Player player)
	{
		int world = player.getWorldId();
        return world == 110010000 && ZoneService.getInstance().isInsideZone(player, ZoneName.COLISEUM_PVP_ZONE);
    }
	
	public boolean isInSameGroup(Player player1, Player player2)
	{
		if(player1.isInGroup() && player2.isInGroup())
		{
			if(player1.getPlayerGroup().getGroupId() == player2.getPlayerGroup().getGroupId())
				return true;
		}
		return false;
	}
	
	public boolean isInSameAlliance(Player player1, Player player2)
	{
		if(player1.isInAlliance() && player2.isInAlliance())
		{
			if(player1.getPlayerAlliance().getAllianceIdFor(player1.getObjectId()) == player2.getPlayerAlliance().getAllianceIdFor(player2.getObjectId()))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("synthetic-access")
	public static class SingletonHolder
	{
		protected static final ArenaService instance = new ArenaService();
	}
}
