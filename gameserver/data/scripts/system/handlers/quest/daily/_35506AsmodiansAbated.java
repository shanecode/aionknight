/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.daily;


import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;


/**
 * @author HellBoy
 *
 */
public class _35506AsmodiansAbated extends QuestHandler
{
	private final static int	questId	= 35506;

	public _35506AsmodiansAbated()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(799831).addOnTalkEvent(questId);
		qe.addOnKillPlayer(questId);
	}

	@Override
	public boolean onKillEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		if(player.getWorldId() == 210020000 || player.getWorldId() == 210040000)
		{
			if(defaultQuestOnKillPlayerEvent(env, 1, 0, 1, false) || defaultQuestOnKillPlayerEvent(env, 1, 1, 2, true))
				return true;
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(defaultQuestStartDaily(env))
			return true;
		
		if(qs == null)
			return false;
		return defaultQuestRewardDialog(env, 799831, 10002);
	}
}
