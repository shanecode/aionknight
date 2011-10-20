/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.sanctum;

import java.util.Collections;


import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.templates.quest.QuestItems;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.ItemService;
import ru.aionknight.gameserver.utils.PacketSendUtility;


/**
 * @author Rolandas
 * 
 */
public class _3968PalentinesRequest extends QuestHandler
{
	private final static int	questId	= 3968;

	public _3968PalentinesRequest()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(798390).addOnQuestStart(questId);
		qe.setNpcQuestData(798176).addOnTalkEvent(questId);
		qe.setNpcQuestData(204528).addOnTalkEvent(questId);
		qe.setNpcQuestData(203927).addOnTalkEvent(questId);
		qe.setNpcQuestData(798390).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 798390)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 1011);
				else
					return defaultQuestStartDialog(env);
			}
		}
		
		if(qs == null)
			return false;
		
		int var = qs.getQuestVarById(0);
		
		if(targetId == 798176)
		{
			if(qs.getStatus() == QuestStatus.START && var == 0)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 1352);
				else if(env.getDialogId() == 10000)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182206123, 1))))
					{
						qs.setQuestVar(++var);
						updateQuestStatus(env);
						PacketSendUtility
							.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					}
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 204528)
		{
			if(qs.getStatus() == QuestStatus.START && var == 1)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 1693);
				else if(env.getDialogId() == 10001)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182206124, 1))))
					{
						qs.setQuestVar(++var);
						updateQuestStatus(env);
						PacketSendUtility
							.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					}
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 203927)
		{
			if(qs.getStatus() == QuestStatus.START && var == 2)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 2034);
				else if(env.getDialogId() == 10002)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182206125, 1))))
					{
						qs.setQuestVar(++var);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility
							.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					}
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 798390)
		{
			if(env.getDialogId() == -1 && qs.getStatus() == QuestStatus.REWARD)
				return sendQuestDialog(env, 2375);
			else if(env.getDialogId() == 1009)
			{
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				player.getInventory().removeFromBagByItemId(182206123, 1);
				player.getInventory().removeFromBagByItemId(182206124, 1);
				player.getInventory().removeFromBagByItemId(182206125, 1);
				return defaultQuestEndDialog(env);
			}
			else
				return defaultQuestEndDialog(env);
		}
		return false;
	}
}