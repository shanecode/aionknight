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
package quest.reshanta;


import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;


/**
 * @author HellBoy
 *
 */
public class _1703Defeat8thRankAsmodianSoldiers extends QuestHandler
{
	private final static int	questId	= 1703;

	public _1703Defeat8thRankAsmodianSoldiers()
	{
		super(questId);		
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(278503).addOnQuestStart(questId);
		qe.setNpcQuestData(278503).addOnTalkEvent(questId);
		qe.addOnKillPlayer(questId);
	}

	@Override
	public boolean onKillEvent(QuestCookie env)
	{
		if(defaultQuestOnKillPlayerEvent(env, 2, 0, 9, false) || defaultQuestOnKillPlayerEvent(env, 2, 9, 10, true))
			return true;
		else
			return false;
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		
		if(defaultQuestNoneDialog(env, 278503))
			return true;
		if(qs == null)
			return false;
		return defaultQuestRewardDialog(env, 278503, 1352);
	}
}
