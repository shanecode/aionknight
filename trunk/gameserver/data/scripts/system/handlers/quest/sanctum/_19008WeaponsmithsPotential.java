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
import gameserver.services.QuestService;


/**
 * @author HellBoy
 * 
 */
public class _19008WeaponsmithsPotential extends QuestHandler
{
	private final static int	questId	= 19008;
	private final static int	questStartNpc = 203788;
	private final static int	secondNpc = 203789;
	private final static int[]	recipesItemIds = {152201706, 152201707};
	private final static int[]	recipesIds = {155001744, 155001745};

	public _19008WeaponsmithsPotential()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(questStartNpc).addOnQuestStart(questId);
		qe.setNpcQuestData(questStartNpc).addOnTalkEvent(questId);
		qe.setNpcQuestData(secondNpc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		
		if(defaultQuestNoneDialog(env, questStartNpc, 4762))
			return true;

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if(qs.getStatus() == QuestStatus.START)
		{
			switch(env.getTargetId())
			{
				case secondNpc:
					switch(env.getDialogId())
					{
						case 26:
							if(var == 0)
								return sendQuestDialog(env, 1011);
							else if(var == 2)
								return sendQuestDialog(env, 4080);
						case 10009:
							if(player.getInventory().decreaseKinah(251000))
							{
								defaultQuestGiveItem(env, 152020250, 1);
								return defaultCloseDialog(env, var, 1, recipesItemIds[0], 1, 0, 0);
							}
							else
								return sendQuestDialog(env, 4400);
						case 10019:
							if(player.getInventory().decreaseKinah(334500))
							{
								defaultQuestGiveItem(env, 152020250, 1);
								return defaultCloseDialog(env, var, 1, recipesItemIds[1], 1, 0, 0);
							}
							else
								return sendQuestDialog(env, 4400);
					}
					break;
				case questStartNpc:
					switch(env.getDialogId())
					{
						case 26:
							if(var == 1)
								return sendQuestDialog(env, 1352);
						case 34:
							if (var == 1)
							{
								if(QuestService.collectItemCheck(env, true))
								{
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									return sendQuestDialog(env, 5);
								}
								else
								{
									int checkFailId = 3398;
									if(player.getRecipeList().isRecipePresent(recipesIds[0]) || player.getRecipeList().isRecipePresent(recipesIds[1]))
										checkFailId = 2716;
									else if(player.getInventory().getItemCountByItemId(recipesItemIds[0]) > 0 || player.getInventory().getItemCountByItemId(recipesItemIds[1]) > 0)
										checkFailId = 3057;
									
									if(checkFailId == 3398)
									{
										qs.setQuestVar(2);
										updateQuestStatus(env);
									}
									return sendQuestDialog(env, checkFailId);
								}
							}
							break;
					}
					break;
			}
		}
		return defaultQuestRewardDialog(env, questStartNpc, 0);
	}
}