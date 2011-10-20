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
package quest.reshanta;


import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.QuestService;
import ru.aionknight.gameserver.world.zone.ZoneName;


/**
 * @author Rhys2002
 * 
 */
public class _2701TheGovernorsSummons extends QuestHandler
{

	private final static int	questId	= 2701;

	public _2701TheGovernorsSummons()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(278001).addOnTalkEvent(questId);
		qe.setQuestEnterZone(ZoneName.RUSSET_PLAZA_400010000).add(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 278001)
			return false;
		if(qs.getStatus() == QuestStatus.START)
		{
			if(env.getDialogId() == 26)
				return sendQuestDialog(env, 10002);
			else if(env.getDialogId() == 1009)
			{
				qs.setStatus(QuestStatus.REWARD);
				qs.setQuestVarById(0, 1);
				updateQuestStatus(env);
				return sendQuestDialog(env, 5);
			}
			return false;
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(env.getDialogId() == 18)
			{
				int[] ids = {2071, 2072, 2073, 2074, 2075, 2076};
				for (int id : ids)
				{
					QuestService.startQuest(new QuestCookie(env.getVisibleObject(), env.getPlayer(), id, env.getDialogId()), QuestStatus.LOCKED);
				}
			}
			return defaultQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestCookie env, ZoneName zoneName)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(zoneName != ZoneName.RUSSET_PLAZA_400010000)
			return false;
		if(qs != null)
			return false;
		QuestService.startQuest(env, QuestStatus.START);
		return true;
	}

}