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

package gameserver.services;

import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.templates.CubeExpandTemplate;
import gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import org.apache.log4j.Logger;

public class CubeExpandService
{
	private static final Logger	log = Logger.getLogger(CubeExpandService.class);
	private static final int MIN_EXPAND	= 0;
	private static final int MAX_EXPAND	= 9;
	/**
	 * Shows Question window and expands on positive response
	 * 
	 * @param player
	 * @param npc
	 */
	public static void expandCube(final Player player, Npc npc)
	{
		final CubeExpandTemplate expandTemplate = DataManager.CUBEEXPANDER_DATA.getCubeExpandListTemplate(npc.getNpcId());

		if(expandTemplate == null)
		{
			log.error("Cube Expand Template could not be found for Npc ID: " + npc.getObjectId());
			return;
		}
		
		if(npcCanExpandLevel(expandTemplate, cubeLevel(player) + 1)
			&& validateNewSize(cubeLevel(player) + 1))
		{
			/**
			 * Check if our player can pay the cubic expand price
			 */
			final int price = getPriceByLevel(expandTemplate, cubeLevel(player) + 1);

			RequestResponseHandler responseHandler = new RequestResponseHandler(npc){
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if(price > player.getInventory().getKinahItem().getItemCount())
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CUBEEXPAND_NOT_ENOUGH_KINAH);
						return;
					}
					if(player.getInventory().decreaseKinah(price))
						expand(responder);
					
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
				}
			};

			boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING,
				responseHandler);
			if(result)
			{
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(
					SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, 0, String.valueOf(price)));
			}
		}
		else
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300430));
	}
	/**
	 * Expands the cubes
	 * @param player
	 */
	public static void expand(Player player)
	{
		if (!validateNewSize(cubeLevel(player) + 1))
			return;
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300431, "9")); // 9 Slots added
		player.setCubesize(player.getCubeSize() + 1);
		PacketSendUtility.sendPacket(player, new SM_CUBE_UPDATE(player, 0));
	}
	
	private static int cubeLevel(Player player)
	{
		int cubeLevel = player.getCubeSize();
		if(player.getCommonData().getRace() == Race.ASMODIANS)
		{
			QuestState qs = player.getQuestStateList().getQuestState(2937);
			if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				cubeLevel -= 1;
			qs = player.getQuestStateList().getQuestState(2833);
			if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				cubeLevel -= 1;
		}
		if(player.getCommonData().getRace() == Race.ELYOS)
		{
			QuestState qs = player.getQuestStateList().getQuestState(1947);
			if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				cubeLevel -= 1;
			qs = player.getQuestStateList().getQuestState(1797);
			if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				cubeLevel -= 1;
			qs = player.getQuestStateList().getQuestState(1800);
			if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				cubeLevel -= 1;
		}
		return cubeLevel;
	}
	/**
	 * Checks if new player cube is not max
	 * 
	 * @param level
	 * @return true or false
	 */
	private static boolean validateNewSize(int level)
	{
		if(level < MIN_EXPAND || level > MAX_EXPAND)
			return false;
		return true;
	}
	/**
	 * Checks if npc can expand level
	 * 
	 * @param clist
	 * @param level
	 * @return true or false
	 */
	private static boolean npcCanExpandLevel(CubeExpandTemplate clist, int level)
	{
		if(!clist.contains(level))
			return false;
		return true;
	}
	/**
	 * The guy who created cube template should blame himself :) One day I will rewrite them
	 * 
	 * @param clist
	 * @param level
	 * @return
	 */
	private static int getPriceByLevel(CubeExpandTemplate clist, int level)
	{
		return clist.get(level).getPrice();
	}
}