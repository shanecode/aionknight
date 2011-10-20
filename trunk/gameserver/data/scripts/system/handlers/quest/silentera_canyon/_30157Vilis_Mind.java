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
package quest.silentera_canyon;

import java.util.Collections;


import ru.aionknight.gameserver.model.EmotionType;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.templates.quest.QuestItems;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_EMOTION;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.ItemService;
import ru.aionknight.gameserver.utils.PacketSendUtility;
import ru.aionknight.gameserver.utils.ThreadPoolManager;


public class _30157Vilis_Mind extends QuestHandler
{
	private final static int questId = 30157;
	
	public _30157Vilis_Mind()
	{
		super(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestCookie env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 204304) //Vili
			{
				if(env.getDialogId() == 26)
					return sendQuestDialog(env, 4762);
				else if(env.getDialogId() == 1011)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182209254, 1)))) //Rem�de de restauration
						return sendQuestDialog(env, 4);
					else
						return true;
					}
					else
						return defaultQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 799234) //Nep
			    player.getInventory().removeFromBagByItemId(182209254, 1); //Rem�de de restauration
				return defaultQuestEndDialog(env);
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 700570: //Statue Of Sinigalla
	            {
                    if (qs.getQuestVarById(0) == 1 && player.getInventory().getItemCountByItemId(182209254) > 0) //Rem�de de restauration
		            {
                        final int targetObjectId = env.getVisibleObject().getObjectId();
                        PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
                        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
                        ThreadPoolManager.getInstance().schedule(new Runnable()
					    {
                            @Override
                            public void run()
				            {
                                PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
                                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
                                qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                                updateQuestStatus(env);
                            }
                        }, 3000);
					}
				}
			}
		}
			return false;
	}
	
	@Override
	public void register()
	{
	    qe.setNpcQuestData(204304).addOnQuestStart(questId); //Vili
		qe.setNpcQuestData(204304).addOnTalkEvent(questId); //Vili
		qe.setNpcQuestData(799234).addOnTalkEvent(questId); //Nep
		qe.setNpcQuestData(700570).addOnActionItemEvent(questId); //Statue Of Sinigalla
		qe.setQuestItemIds(182209254).add(questId); //Rem�de de restauration
	}
}