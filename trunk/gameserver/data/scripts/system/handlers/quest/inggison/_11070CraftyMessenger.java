/**
 * This file is part of Aion X Emu <aionxemu.com>
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package quest.inggison;

import java.util.Collections;


import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.quest.QuestItems;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.ItemService;
import gameserver.utils.PacketSendUtility;

/**
 * @author Leunam
 *
 */
public class _11070CraftyMessenger extends QuestHandler {
	private final static int questId = 11070;
	private final static int[] npc_ids ={ 798945, 798943, 798949, 798932 };
	
	public _11070CraftyMessenger() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.setNpcQuestData(798945).addOnQuestStart(questId);
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);	
	}

	@Override
	public boolean onDialogEvent(QuestCookie env) {
	final Player player = env.getPlayer();
	int targetId = 0;
	if (env.getVisibleObject() instanceof Npc)
	targetId = ((Npc) env.getVisibleObject()).getNpcId();
	QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 798945)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
                  {
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 1011);
				else if(env.getDialogId() == 1002)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182206859, 3))))
						return defaultQuestStartDialog(env);
					else
						return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
	if(qs == null)
	return false;
		
	int var = qs.getQuestVarById(0);
	if(qs.getStatus() == QuestStatus.REWARD)
	{
		if(targetId == 798945)
		{
			if (env.getDialogId() == -1)
				return sendQuestDialog(env, 2375);
			else if (env.getDialogId() == 1009)
				return sendQuestDialog(env, 5);
			else return defaultQuestEndDialog(env);
		}
	}
	else if(qs.getStatus() != QuestStatus.START)
	{
		return false;
	}
		if(targetId == 798943)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 0)
						return sendQuestDialog(env, 1352);
				case 10000:
					if(var == 0)
					{
						player.getInventory().removeFromBagByItemId(182206859, 1);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);								
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				return false;
			}
		}			
		else if(targetId == 798949)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 1)
						return sendQuestDialog(env, 1693);
				case 10001:
					if(var == 1)
					{
						player.getInventory().removeFromBagByItemId(182206859, 1);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);								
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				return false;
			}
		}
		else if(targetId == 798932)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 2)
						return sendQuestDialog(env, 2034);
				case 10002:
					if(var == 2)
					{
						player.getInventory().removeFromBagByItemId(182206859, 1);
						qs.setQuestVarById(0, var + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);								
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				return false;
			}
		}						
	return false;
      }
}
