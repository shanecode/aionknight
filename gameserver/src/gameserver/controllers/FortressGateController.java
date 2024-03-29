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

package gameserver.controllers;

import gameserver.model.DescriptionId;
import gameserver.model.Race;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.Trap;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.siege.FortressGate;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.TeleportService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;

public class FortressGateController extends NpcController
{	

	@Override
	public void onDie(Creature lastAttacker)
	{
		final Player destroyer;
		if(lastAttacker instanceof Player)
			destroyer = (Player)lastAttacker;
		else if(lastAttacker instanceof Trap)
			destroyer = (Player)((Trap)lastAttacker).getMaster();
		else if(lastAttacker instanceof Summon)
			destroyer = ((Summon)lastAttacker).getMaster();
		else
			destroyer = null;
		
		if(destroyer != null)
		{
			final int raceMsgId;
			if(destroyer.getCommonData().getRace() == Race.ELYOS)
				raceMsgId = 900240;
			else
				raceMsgId = 900241;
			getOwner().getKnownList().doOnAllPlayers(new Executor<Player>(){
				
				@Override
				public boolean run(Player object)
				{
					PacketSendUtility.sendPacket(object, new SM_SYSTEM_MESSAGE(1400305, destroyer.getName(), new DescriptionId(raceMsgId*2+1)));
					return true;
				}
			}, true);
		}
		super.onDelete();
	}
	
	@Override
	public void onDialogRequest(Player p)
	{
		
		if(((p.getCommonData().getRace() == Race.ELYOS)&&(getOwner().getObjectTemplate().getRace()==Race.PC_LIGHT_CASTLE_DOOR))||((p.getCommonData().getRace() == Race.ASMODIANS)&&(getOwner().getObjectTemplate().getRace()==Race.PC_DARK_CASTLE_DOOR)))
		
		{
			RequestResponseHandler gateHandler = new RequestResponseHandler(p)
			{
				@Override
				public void denyRequest(Creature requester, Player p)
				{
					// Close window
				}
				
				@Override
				public void acceptRequest(Creature requester, Player p)
				{
					double radian = Math.toRadians(MathUtil.convertHeadingToDegree(p.getHeading()));
					int worldId = getOwner().getWorldId();
					float x = (float)(p.getX() + (10 * Math.cos(radian)));
					float y = (float)(p.getY() + (10 * Math.sin(radian)));
					float z = p.getZ() + 0.5f;
					TeleportService.teleportTo(p, worldId, x, y, z, 1);
				}
			};
			if(p.getResponseRequester().putRequest(160017, gateHandler))
			{
				PacketSendUtility.sendPacket(p, new SM_QUESTION_WINDOW(160017, p.getObjectId()));
			}
		}
	}
	
	@Override
	public void onRespawn()
	{
		super.onRespawn();
	}

	@Override
	public FortressGate getOwner()
	{
		return (FortressGate) super.getOwner();
	}
}