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

package quest.beluslan;

import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.quest.QuestItems;
import gameserver.network.aion.serverpackets.*;
import gameserver.quest.HandlerResult;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.ItemService;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

import java.util.Collections;

public class _2054LightuptheLighthouse extends QuestHandler
{
	
	private final static int	questId	= 2054;
	private final static int[]	npc_ids	= { 204768, 204739, 730109, 730140, 700287 };

	public _2054LightuptheLighthouse()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setQuestItemIds(182204308).add(questId);
		qe.addQuestLvlUp(questId);
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestCookie env)
	{
		return defaultQuestOnLvlUpEvent(env);
	}

	@Override
	public boolean onDialogEvent(final QuestCookie env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final Npc npc = (Npc)env.getVisibleObject();
		
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 204768)
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
		if(targetId == 204768)
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
		else if(targetId == 204739)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 1)
						return sendQuestDialog(env, 1352);
				case 1353:
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 237));
						break;
				case 10001:
					if(var == 1)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}						
			}
		}
		else if(targetId == 730109)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 3)
						return sendQuestDialog(env, 2034);
				case 10003:
					if(var == 3)
					{
						QuestService.addNewSpawn(220040000, 1, 213912, (float) npc.getX(),
										(float) npc.getY(), (float) npc.getZ(), (byte) 0, true);
						npc.getController().onDespawn(true);
						npc.getController().scheduleRespawn();	
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}						
			}
		}
		else if(targetId == 730140)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 3)
						return sendQuestDialog(env, 2120);
				case 10004:
					if(var == 3)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						npc.getController().onDespawn(true);
						npc.getController().scheduleRespawn();						
						ItemService.addItems(player, Collections.singletonList(new QuestItems(182204309, 1)));
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}						
			}
		}
		else if(targetId == 700287 && var == 4)
		{
			if (env.getDialogId() == -1)
			{
				final int targetObjectId = env.getVisibleObject().getObjectId();
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
				ThreadPoolManager.getInstance().schedule(new Runnable(){
					@Override
					public void run()
					{
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
						player.getInventory().removeFromBagByItemId(182204309, 1);
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 238));
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
				}, 3000);
			}
		}		
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestCookie env, Item item)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if(id != 182204308 || qs == null)
			return HandlerResult.UNKNOWN;
		
		if(qs.getQuestVarById(0) != 2)
			return HandlerResult.FAILED;
		
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 1000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
				player.getInventory().removeFromBagByItemId(182204308, 1);
				qs.setQuestVarById(0, 3);
				updateQuestStatus(env);
			}
		}, 1000);
		return HandlerResult.SUCCESS;
	}		
}