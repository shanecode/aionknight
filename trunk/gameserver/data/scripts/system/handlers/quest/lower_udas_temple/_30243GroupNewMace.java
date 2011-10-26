/*
 *  This file is part of aion-unique <aion-unique.org>
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-unique. If not, see <http://www.gnu.org/licenses/>.
 */
package quest.lower_udas_temple;


import ru.aionknight.gameserver.model.gameobjects.Item;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.templates.quest.QuestItems;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import ru.aionknight.gameserver.quest.HandlerResult;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.ItemService;
import ru.aionknight.gameserver.services.QuestService;
import ru.aionknight.gameserver.services.ZoneService;
import ru.aionknight.gameserver.utils.PacketSendUtility;
import ru.aionknight.gameserver.utils.ThreadPoolManager;
import ru.aionknight.gameserver.world.zone.ZoneName;

import java.util.Collections;


public class _30243GroupNewMace extends QuestHandler
{
	private final static int	questId	= 30243;

	public _30243GroupNewMace()
	{
		super(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 799032)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 4762);
				else
					return defaultQuestStartDialog(env);
			}
			
			else if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if(env.getDialogId() == -1)
				{
					if(QuestService.collectItemCheck(env, false))
						return sendQuestDialog(env, 2716);	
				}
				else
					return defaultQuestStartDialog(env);
			}
			
			else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(env, 10002);
				else if(env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestCookie env, Item item)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if(id == 186000107)
		{
			if(!ZoneService.getInstance().isInsideZone(player, ZoneName.DEBILKARIM_FORGE_300160000))
				return HandlerResult.FAILED;
			if(qs == null)
				return HandlerResult.UNKNOWN;
			if(qs.getQuestVarById(0) != 0)
				return HandlerResult.FAILED;
			if(player.getInventory().getItemCountByItemId(100100711) == 0 || player.getInventory().getItemCountByItemId(186000099) == 0
				|| player.getInventory().getItemCountByItemId(186000106) < 20 || player.getInventory().getItemCountByItemId(186000107) == 0)
				return HandlerResult.FAILED;
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				@Override
				public void run()
				{
					ItemService.addItems(player, Collections.singletonList(new QuestItems(182209641, 1)));
					PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
					player.getInventory().removeFromBagByItemId(186000099, 1);
					player.getInventory().removeFromBagByItemId(186000106, 20);
					player.getInventory().removeFromBagByItemId(186000107, 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.UNKNOWN;
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(799032).addOnQuestStart(questId);
		qe.setNpcQuestData(799032).addOnTalkEvent(questId);
		qe.setQuestItemIds(186000107).add(questId);
	}
}