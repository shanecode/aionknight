/**
 * This file is part of Aion-Knight Dev. Team [http://www.aion-knight.ru]
 *
 * Aion-Knight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Aion-Knight is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a  copy  of the GNU General Public License
 * along with Aion-Knight. If not, see <http://www.gnu.org/licenses/>.
 */
package quest.reshanta;


import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;


/**
 * @author HellBoy
 *
 */
public class _1719ConfrontAsmodianOfficers extends QuestHandler
{
	private final static int	questId	= 1719;

	public _1719ConfrontAsmodianOfficers()
	{
		super(questId);		
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(278501).addOnQuestStart(questId);
		qe.setNpcQuestData(278501).addOnTalkEvent(questId);
		qe.addOnKillPlayer(questId);
	}

	@Override
	public boolean onKillEvent(QuestCookie env)
	{
		if(defaultQuestOnKillPlayerEvent(env, 10, 0, 9, false) || defaultQuestOnKillPlayerEvent(env, 10, 9, 10, true))
			return true;
		else
			return false;
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		
		if(defaultQuestNoneDialog(env, 278501))
			return true;
		if(qs == null)
			return false;
		return defaultQuestRewardDialog(env, 278501, 1352);
	}
}
