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
package quest.sanctum;


import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.utils.PacketSendUtility;

/**
 * @author Orpheo
 * 
 */
public class _1936WhatNerisonSaw extends QuestHandler
{
	private final static int	questId	= 1936;

	public _1936WhatNerisonSaw()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203833).addOnQuestStart(questId);
		qe.setNpcQuestData(203833).addOnTalkEvent(questId);
		qe.setNpcQuestData(204573).addOnTalkEvent(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestCookie env)
	{
      final Player player = env.getPlayer();
      final QuestState qs = player.getQuestStateList().getQuestState(questId);
      final QuestState qs2 = player.getQuestStateList().getQuestState(1935);
      if(qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
    	  return false;
      qs.setStatus(QuestStatus.START);
      updateQuestStatus(env);
      return true;
	}
   
	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 203833)
		{
			if(qs == null)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 1011);
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 204573)
		{
			if(qs != null)
			{
				if(env.getDialogId() == 26 && qs.getStatus() == QuestStatus.START)
					return sendQuestDialog(env, 2375);
				else if(env.getDialogId() == 1009)
				{
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return defaultQuestEndDialog(env);
				}
				else
					return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
}
