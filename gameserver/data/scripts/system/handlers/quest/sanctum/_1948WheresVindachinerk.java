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


import gameserver.model.gameobjects.player.Player;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;


/**
 * @author HellBoy
 * 
 */
public class _1948WheresVindachinerk extends QuestHandler
{
	private final static int	questId	= 1948;

	public _1948WheresVindachinerk()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(798012).addOnQuestStart(questId);
		int[] npcs = {798012, 798004, 798132, 279006};
		for(int npc: npcs)
			qe.setNpcQuestData(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		
		if(defaultQuestNoneDialog(env, 798012))
			return true;

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if(qs.getStatus() == QuestStatus.START)
		{
			if(env.getTargetId() == 798004)
			{
				switch(env.getDialogId())
				{
					case 26:
						if(var == 0)
							return sendQuestDialog(env, 1352);
					case 10000:
						return defaultCloseDialog(env, 0, 1);
				}
			}
			else if(env.getTargetId() == 798132)
			{
				switch(env.getDialogId())
				{
					case 26:
						if(var == 1)
							return sendQuestDialog(env, 1693);
					case 10001:
						return defaultCloseDialog(env, 1, 2, true, false);
				}
			}
		}
		return defaultQuestRewardDialog(env, 279006, 2375);
	}
}
