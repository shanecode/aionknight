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


import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;


/**
 * @author Bio
 * 
 */
public class _3928TheClericPreceptorsTask extends QuestHandler
{
	private final static int	questId	= 3928;
	private final int			skillId = 1165;
	private final int			mainNpcId = 203707;

	public _3928TheClericPreceptorsTask()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(mainNpcId).addOnQuestStart(questId);
		qe.setNpcQuestData(mainNpcId).addOnTalkEvent(questId);
		qe.setQuestSkillIds(skillId).add(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(targetId == mainNpcId)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 4762);
				else
					return defaultQuestStartDialog(env);
			}
			else if(qs.getStatus() == QuestStatus.REWARD)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onSkillUseEvent(QuestCookie env, int skillUsedId)
	{
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if(qs.getStatus() != QuestStatus.START)
			return false;

		if(skillUsedId == skillId)
		{
			if(var >= 0 && var < 9)
			{
				qs.setQuestVarById(0, var+1);
				updateQuestStatus(env);
				return true;
			}
			else if(var == 9)
			{
				qs.setQuestVarById(0, var+1);
				updateQuestStatus(env);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}