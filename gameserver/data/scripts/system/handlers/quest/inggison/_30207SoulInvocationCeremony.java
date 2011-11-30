/** * This file is part of Aion-Knight Dev. Team [http://aion-knight.ru]
 *
 * Aion-Knight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General  Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aion-Knight is distributed  in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General  Public License for more details.
 *
 * You should have received a copy of the GNU General  Public License
 * along with Aion-Knight. If not, see <http://www.gnu.org/licenses/>.
 */

package quest.inggison;


import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;

/**
 * @author Fennek
 * 
 */

public class _30207SoulInvocationCeremony	extends QuestHandler
{
	private final static int questId = 30207;
	
	public _30207SoulInvocationCeremony()
	{
		super (questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(798941).addOnQuestStart(questId); //Pilomenes start
		qe.setNpcQuestData(798941).addOnTalkEvent(questId); // Pilomenes
	}
	
	@Override
	public boolean onDialogEvent(final QuestCookie env)
	{
		//Instanceof
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		// ------------------------------------------------------------
        // NPC Quest :
		// Pilomenes start
		if (qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if (targetId == 798941) //Pilomenes start
			{
				switch (env.getDialogId())
				{
				case 26:
					return sendQuestDialog(env, 1011);
				case 1008:
					return sendQuestDialog(env, 2375);
				case 34:
					if (player.getInventory().getItemCountByItemId(182209609) < 20)
					{
						return sendQuestDialog(env, 2716);
					}
					player.getInventory().removeFromBagByItemId(182209609, 20);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
			}
			return false;
		}
		return false;
	}
}
