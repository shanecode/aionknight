/**
 * This file is part of Aion-Knight [http://aion-knight.ru]
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
package gameserver.controllers;

import org.apache.log4j.Logger;

import gameserver.configs.main.CustomConfig;
import gameserver.model.ChatType;
import gameserver.model.Race;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.templates.BindPointTemplate;
import gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import gameserver.network.aion.serverpackets.SM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.WorldType;


/**

 *
 */
public class BindpointController extends NpcController
{
	private static Logger log = Logger.getLogger(BindpointController.class);

	private BindPointTemplate bindPointTemplate;

	/**
	 * @param bindPointTemplate the bindPointTemplate to set
	 */
	public void setBindPointTemplate(BindPointTemplate bindPointTemplate)
	{
		this.bindPointTemplate = bindPointTemplate;
	}

	private void bindHere(Player player)
	{
		RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner())
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if (responder.getCommonData().getBindPoint() != bindPointTemplate.getBindId())
				{
					if (responder.getInventory().getKinahItem().getItemCount()>= bindPointTemplate.getPrice())
					{
						if(!responder.getInventory().decreaseKinah(bindPointTemplate.getPrice()))
							return;
						
						responder.getCommonData().setBindPoint(bindPointTemplate.getBindId());
						TeleportService.sendSetBindPoint(responder);
						PacketSendUtility.broadcastPacket(responder, new SM_LEVEL_UPDATE(responder.getObjectId(), 2, responder.getCommonData().getLevel()), true);
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DEATH_REGISTER_RESURRECT_POINT());
					}
					else
					{
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE());
						return;
					}
				}
			}
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				//do nothing
			}
		};

		boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION, responseHandler);
		if (requested)
		{
			String price = Integer.toString(bindPointTemplate.getPrice());
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION, 0, price));
		}
	}

	@Override
	public void onDialogRequest(Player player)
	{
		if (bindPointTemplate == null)
		{
			log.info("There is no bind point template for npc: " + getOwner().getNpcId());
			return;
		}

		if (player.getCommonData().getBindPoint() == bindPointTemplate.getBindId())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ALREADY_REGISTER_THIS_RESURRECT_POINT());
			return;
		}

		WorldType worldType = World.getInstance().getWorldMap(player.getWorldId()).getWorldType();
		if (!CustomConfig.ENABLE_CROSS_FACTION_BINDING)
		{
			if( worldType == WorldType.ASMODAE && player.getCommonData().getRace()==Race.ELYOS )
			{
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "Elyos cannot bind in Asmodian territory.", ChatType.ANNOUNCEMENTS));
				return;

			}
			if( worldType == WorldType.ELYSEA && player.getCommonData().getRace()==Race.ASMODIANS )
			{
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "Asmodians cannot bind in Elyos territory.", ChatType.ANNOUNCEMENTS));
				return;
			}
			if( worldType == WorldType.ABYSS )
			{
				if( player.getCommonData().getRace()==Race.ELYOS && player.getTarget().getObjectTemplate().getTemplateId()==700401 )
				{
					PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "Elyos cannot bind in Asmodian territory.", ChatType.ANNOUNCEMENTS));
					return;
				}
				if( player.getCommonData().getRace()==Race.ASMODIANS && player.getTarget().getObjectTemplate().getTemplateId()==730071 )
				{
					PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "Asmodians cannot bind in Elyos territory.", ChatType.ANNOUNCEMENTS));
					return;
				}
			}
		}
		if( worldType == WorldType.PRISON)
		{
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You cannot bind here.", ChatType.ANNOUNCEMENTS));
			return;
		}
		else
		{
			bindHere(player);
		}

	}
}
