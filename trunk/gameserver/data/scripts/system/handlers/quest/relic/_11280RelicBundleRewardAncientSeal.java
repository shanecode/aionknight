/*
 * This file is part of aion-unique <aion-unique.org>
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.relic;


import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.QuestService;


public class _11280RelicBundleRewardAncientSeal extends QuestHandler
{
	private final static int	questId	= 0;
	private final static int	Items[][] =
	{{1011, 1352, 1693, 2034},
	{186000062, 186000061, 186000060, 186000059}};

	public _11280RelicBundleRewardAncientSeal()
	{
		super(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		int	removeItem = 0;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = 0;
		
		if(qs != null)
			var = qs.getQuestVarById(0);
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE)
		{
			if(env.getTargetId() == 278645)
			{
				switch(env.getDialogId())
				{
					case 26:
						if(QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel()))
							return sendQuestDialog(env, 1011);
						else
							return sendQuestDialog(env, 3398);
					case 1011:
					case 1352:
					case 1693:
					case 2034:
						int i = 0;
						for(int id: Items[0])
						{
							if(id == env.getDialogId())
								break;
							i++;
						}
						removeItem = Items[1][i];
						var = i;
						
						if(player.getInventory().getItemCountByItemId(removeItem) > 0)
						{
							if(qs == null)
							{
								qs = new QuestState(questId, QuestStatus.REWARD, 0, 0);
								player.getQuestStateList().addQuest(questId, qs);
							}
							else
								qs.setStatus(QuestStatus.REWARD);
							qs.setQuestVar(var);
							defaultQuestRemoveItem(env, removeItem, 1);
							return sendQuestDialog(env, var + 5);
						}
						else
							return sendQuestDialog(env, 1009);
				}
			}
		}
		
		if(qs == null)
			return false;
		
		return defaultQuestRewardDialog(env, 278645, 0, var);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(278645).addOnTalkEvent(questId);
	}
}