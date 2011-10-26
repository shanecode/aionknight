/*
* This file is part of aion-unique <aion-unique.org>.
*
*  aion-unique is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  aion-unique is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
*/
package quest.verteron;


import ru.aionknight.gameserver.model.EmotionType;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_EMOTION;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.utils.PacketSendUtility;
import ru.aionknight.gameserver.utils.ThreadPoolManager;


/**
* @author Rhys2002
* @modified Frost
*
*/
public class _1156StolenVillageSeal extends QuestHandler
{
   
	private final static int   questId   = 1156;

	public _1156StolenVillageSeal()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203128).addOnQuestStart(questId);
		qe.setNpcQuestData(203128).addOnTalkEvent(questId);
		qe.setNpcQuestData(700003).addOnTalkEvent(questId);
		qe.setNpcQuestData(798003).addOnTalkEvent(questId);
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
			if(targetId == 203128)
			{
            if(env.getDialogId() == 26)
               return sendQuestDialog(env, 1011);
            else
               return defaultQuestStartDialog(env);
			}
		}
	  
		if(qs == null)
			return false;
			
		int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 798003)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(env, 2375);
				else if(env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else return defaultQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 700003:
				{
					if (var == 0 && env.getDialogId() == 26)
					    return sendQuestDialog(env, 1352);
					else if(env.getDialogId() == 10000)
					{
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);									
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}						
					else if(env.getDialogId() == 1353)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));				
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
									public void run()
									{
										PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));								
										PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
										sendQuestDialog(env, 1353);
										return;
									}
						}, 3000);					
					}
				}
			}
		}
		return false;
	}
}