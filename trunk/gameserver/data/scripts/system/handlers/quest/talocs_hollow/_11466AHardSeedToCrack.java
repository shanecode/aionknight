/*
 * This file is part of aion-unique.
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
package quest.talocs_hollow;


import ru.aionknight.gameserver.model.EmotionType;
import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.model.gameobjects.Item;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_EMOTION;
import ru.aionknight.gameserver.quest.HandlerResult;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.QuestService;
import ru.aionknight.gameserver.utils.PacketSendUtility;


public class _11466AHardSeedToCrack extends QuestHandler
{
	private final static int	questId	= 11466;

	public _11466AHardSeedToCrack()
	{
		super(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(env.getTargetId() == 0 && env.getQuestId() == questId)
			return defaultQuestStartItem(env);
		
		if(qs == null)
			return false;
		
		if (env.getTargetId() == 279000)
		{
			if (qs.getStatus() == QuestStatus.START)
			{
				if (env.getDialogId() == -1)
				{
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
				}
			}
			else if (qs.getStatus() == QuestStatus.REWARD && env.getDialogId() == 1009)
			{
				// FIXME: don't broadcast to everyone; now thanks to player and everyone
				PacketSendUtility.sendPacket(player, new SM_EMOTION((Creature)env.getVisibleObject(), 
					EmotionType.EMOTE, 15, player.getObjectId()));
				return sendQuestDialog(env, 5);
			}
		}
		return defaultQuestRewardDialog(env, 279000, 2375);
		
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestCookie env, Item item)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int id = item.getItemTemplate().getTemplateId();

		if (id != 182209524)
			return HandlerResult.UNKNOWN;
		
		boolean lvlCheck = QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel());
		
		if (qs != null || !lvlCheck)
			return HandlerResult.FAILED;
		
		return HandlerResult.SUCCESS; 
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(279000).addOnTalkEvent(questId);
		qe.setQuestItemIds(182209524).add(questId);
	}

}
