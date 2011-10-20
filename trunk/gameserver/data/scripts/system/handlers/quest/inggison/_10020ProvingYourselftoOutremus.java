/*
 * This file is part of aion-unique <aion-unique.org>.
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
package quest.inggison;


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


/**
 * @author Nephis
 * 
 */
public class _10020ProvingYourselftoOutremus extends QuestHandler
{

	private final static int	questId	= 10020;
	private final static int[]	npc_ids	= { 798926, 798928, 730223, 730224, 730225, 798927, 798955, 700628, 700629, 700630};

	public _10020ProvingYourselftoOutremus()
	{
		super(questId);		
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(215504).addOnKillEvent(questId);
		qe.setNpcQuestData(215505).addOnKillEvent(questId);
		qe.setNpcQuestData(216463).addOnKillEvent(questId);
		qe.setNpcQuestData(216783).addOnKillEvent(questId);
		qe.setNpcQuestData(216648).addOnKillEvent(questId);
		qe.setNpcQuestData(216691).addOnKillEvent(questId);
		qe.setNpcQuestData(215508).addOnKillEvent(questId);
		qe.setNpcQuestData(216692).addOnKillEvent(questId);
		qe.setNpcQuestData(215517).addOnKillEvent(questId);
		qe.setNpcQuestData(215519).addOnKillEvent(questId);
		qe.setNpcQuestData(215509).addOnKillEvent(questId);
		qe.setNpcQuestData(216647).addOnKillEvent(questId);
		qe.setNpcQuestData(215516).addOnKillEvent(questId);
		qe.setNpcQuestData(215518).addOnKillEvent(questId);
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);	 
	}

	@Override
	public boolean onLvlUpEvent(QuestCookie env)
	{
		return defaultQuestOnLvlUpEvent(env);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 798926)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(env, 10002);
				else if(env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else return defaultQuestEndDialog(env);
			}
			return false;
		}
		else if(qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		if(targetId == 798926)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 0)
						return sendQuestDialog(env, 1011);
				case 10000:
					if(var == 0)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if(targetId == 798928)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 1)
						return sendQuestDialog(env, 1352);
					else if(qs.getQuestVarById(1) == 23 || qs.getQuestVarById(2) == 4 || qs.getQuestVarById(0) == 4)
						return sendQuestDialog(env, 2375);
				case 10001:
					if(var == 1)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}	
				case 10004:
					if(qs.getQuestVarById(1) == 23 || qs.getQuestVarById(2) == 4 || qs.getQuestVarById(0) == 4)
					{
						qs.setQuestVar(5);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}					
			}
		}
		
		else if(targetId == 798927)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 5)
						return sendQuestDialog(env, 2716);
					else if(var == 10)
						return sendQuestDialog(env, 3398);
				case 10005:
					if(var == 5)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}	
				case 10255:
					if(var == 10)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}					
			}
		}
					
		else if(targetId == 798955)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 6)
						return sendQuestDialog(env, 3057);					
				case 10006:
					if(var == 6)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;						
					}
			}
		}
		else if(targetId == 700628)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 7)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVar(8);
						updateQuestStatus(env);
					}	
			}
		}
		else if(targetId == 700629)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 8)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVar(9);
						updateQuestStatus(env);
					}	
			}
		}
		else if(targetId == 700630)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 9)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVar(10);
						updateQuestStatus(env);
					}	
			}
		}
		else if(targetId == 730223)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 2)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVarById(1,1);
						updateQuestStatus(env);
						return true;
					}	
			}
		}
		else if(targetId == 730224)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 2 && qs.getQuestVarById(1) == 1)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVarById(2,1);
						updateQuestStatus(env);
						return true;
					}					
			}
		}
		else if(targetId == 730225)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 2 && qs.getQuestVarById(1) == 1 && qs.getQuestVarById(2) == 1)
					{
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						qs.setQuestVar(3);
						updateQuestStatus(env);
						return true;
					}					
			}
		}
		return false;
	}	
	
   @Override
   public boolean onKillEvent(QuestCookie env) // FIXME: FIX VAR!!
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
        targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch(targetId)
		{
			case 215504:
			case 215505:
			case 216463:
			case 216783:
			case 216648:
			case 215519:
			case 215517:
			case 216692:
			case 216691:
			case 215518:
			case 215516:
			case 216647:
				if(qs.getQuestVarById(1) < 23 && qs.getQuestVarById(0) == 3)
				{
					qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
					updateQuestStatus(env);
					return true;
				} 
				else if(qs.getQuestVarById(1) == 22 && qs.getQuestVarById(2) == 3 && qs.getQuestVarById(0) == 3)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
				break;
			case 215508:	
			case 215509:				
				if(qs.getQuestVarById(2) < 4 && qs.getQuestVarById(0) == 3)
				{
					qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
					updateQuestStatus(env);
					return true;
				}
				else if(qs.getQuestVarById(1) == 23 && qs.getQuestVarById(2) == 3 && qs.getQuestVarById(0) == 3)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
		}
			return false;
    }
}