/**
 * This file is part of Aion-Knight Dev. Team [http://aion-knight.ru]
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
public class _1720ConfrontAsmodianGenerals extends QuestHandler
{
	private final static int	questId	= 1720;

	public _1720ConfrontAsmodianGenerals()
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
		if(defaultQuestOnKillPlayerEvent(env, 15, 0, 2, false) || defaultQuestOnKillPlayerEvent(env, 15, 2, 3, true))
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
