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
package quest.gelkmaros;

import gameserver.controllers.PortalController;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.WorldMapTemplate;
import gameserver.model.templates.quest.QuestItems;
import gameserver.network.aion.SystemMessageId;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.HandlerResult;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.InstanceService;
import gameserver.services.ItemService;
import gameserver.services.QuestService;
import gameserver.services.TeleportService;
import gameserver.skill.SkillEngine;
import gameserver.skill.model.Skill;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.WorldMapInstance;
import gameserver.world.WorldMapType;

import java.util.Collections;


/**
 * @author Nephis
 * 
 */
public class _20021TheAetherMustFlow extends QuestHandler //TODO: Effect item
{

	private final static int	questId	= 20021;
	private final static int[]	npc_ids	= { 799226, 799247, 799250, 799325, 215488, 799503, 799258, 799239 };

	public _20021TheAetherMustFlow()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.addOnEnterWorld(questId);
		qe.addOnDie(questId);
		qe.setNpcQuestData(215992).addOnKillEvent(questId); 
		qe.setNpcQuestData(215995).addOnKillEvent(questId);		
		qe.setNpcQuestData(215488).addOnKillEvent(questId);
		qe.setQuestItemIds(182207603).add(questId);
		qe.setQuestItemIds(182207604).add(questId);
		qe.setQuestItemIds(164000137).add(questId);
		qe.setQuestItemIds(164000138).add(questId);
		qe.setQuestItemIds(164000139).add(questId);
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);	 
	}

	@Override
	public boolean onEnterWorldEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
       
	    if (qs.getStatus() == QuestStatus.COMPLETE)
		{
		    			if(player.getWorldId() != 300190000)
			{
				player.getInventory().removeFromBagByItemId(182207604, 1);
				player.getInventory().removeFromBagByItemId(182207603, 1);
				player.getInventory().removeFromBagByItemId(164000137, 1);
				player.getInventory().removeFromBagByItemId(164000138, 1);
				player.getInventory().removeFromBagByItemId(164000139, 1);
			}
		}

		if(qs != null && qs.getStatus() == QuestStatus.START)
		{
			int var = qs.getQuestVarById(0);
			if(var == 9)
			{
				if(player.getWorldId() != 300190000)
				{
					qs.setQuestVarById(0, 8);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if(var == 9)
		{
			qs.setQuestVarById(0, 8);
			updateQuestStatus(env);
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
		}

		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		switch(env.getTargetId())
		{
			case 215992:
				if(qs.getQuestVarById(1) < 10 && qs.getQuestVarById(0) == 4)
				{
					qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
					updateQuestStatus(env);
					return true;
				} 
				break;
			case 215995:				
				if(qs.getQuestVarById(2) < 10 && qs.getQuestVarById(0) == 4)
				{
					qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
					updateQuestStatus(env);
					return true;
				}
				break;
			case 215488:
				if(qs.getQuestVarById(0) == 8)
				{
					@SuppressWarnings("unused")
					final int instanceId = player.getInstanceId();
					QuestService.addNewSpawn(300190000, player.getInstanceId(), 799503, (float) 566.1413, (float) 813.3888, (float) 1375.138, (byte) 32, true);
					qs.setQuestVarById(0, 9);
					updateQuestStatus(env);
					return true;
				}
				else if(qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				{
					@SuppressWarnings("unused")
					final int instanceId = player.getInstanceId();
					QuestService.addNewSpawn(300190000, player.getInstanceId(), 799503, (float) 566.1413, (float) 813.3888, (float) 1375.138, (byte) 32, true);
					return true;
				} 
		}
		
		return false;
    }
	
	@Override
	public HandlerResult onItemUseEvent(final QuestCookie env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int var = qs.getQuestVarById(0);
		
		if (id != 182207603 && id != 182207604 && id != 164000137 && id != 164000138 && id != 164000139)
			return HandlerResult.UNKNOWN;
		
		if(player.getWorldId() != 300190000)
			return HandlerResult.FAILED;

		if (qs != null && qs.getStatus() == QuestStatus.COMPLETE)
		{
			if (id == 182207603 && !hasItem(player, 182207603))
			{
				if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(182207603, 1))))
					ItemService.addItem(player, 182207603, 1, 60 * 7200);
			}
			if (id == 182207604 && !hasItem(player, 182207604))
			{
				if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(182207604, 1))))
					ItemService.addItem(player, 182207604, 1, 60 * 7200);
			}
			if (id == 164000137 && !hasItem(player, 164000137))
			{
				if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(164000137, 1))))
					ItemService.addItem(player, 164000137, 1, 60 * 7200);
			}
			if (id == 164000138 && !hasItem(player, 164000138))
			{
				if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(164000138, 1))))
					ItemService.addItem(player, 164000138, 1, 60 * 7200);
			}
			if (id == 164000139 && !hasItem(player, 164000139))
			{
				if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(164000139, 1))))
					ItemService.addItem(player, 164000139, 1, 60 * 7200);
			}
			useSkill(player, item);
		 }
		else if (var == 6 || var == 7)
		{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				@Override
				public void run()
				{
					int var = qs.getQuestVarById(0);
					PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.FAILED; // don't remove from inventory
	}
	
	private void useSkill(Player player, Item item)
	{
		if (player.isItemUseDisabled(item.getItemTemplate().getDelayId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANT_USE_UNTIL_DELAY_TIME);
			return;
		}
		
		int useDelay = item.getItemTemplate().getDelayTime();
		player.addItemCoolDown(item.getItemTemplate().getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);

		if (item.getItemId() == 182207604)
		{
			int skillId = 10252;
			int level = 1;

			Skill skill = SkillEngine.getInstance().getSkill(player, skillId, level, player.getTarget(), item.getItemTemplate());
			if(skill != null)
			{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
			skill.useSkill();
			}
			return;
		}
		if (item.getItemId() == 182207603)
		{
			int skillId = 182207603 == 182207604 ? 10252 : 9831;
			int level = item.getItemId() == 182207604 ? 1 : 4;

			Skill skill = SkillEngine.getInstance().getSkill(player, skillId, level, player.getTarget(), item.getItemTemplate());
			if(skill != null)
			{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
			skill.useSkill();
			}
			return;
		}
		else if (item.getItemId() == 164000137)
		{
			int skillId = 164000137 == 182207604 ? 10252 : 9832;
			int level = item.getItemId() == 182207604 ? 1 : 4;

			Skill skill = SkillEngine.getInstance().getSkill(player, skillId, level, player.getTarget(), item.getItemTemplate());
			if(skill != null)
			{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
			skill.useSkill();
			}
			return;
		}
		else if (item.getItemId() == 164000138)
		{
			int skillId = 164000138 == 182207604 ? 10252 : 9833;
			int level = item.getItemId() == 182207604 ? 1 : 4;

			Skill skill = SkillEngine.getInstance().getSkill(player, skillId, level, player.getTarget(), item.getItemTemplate());
			if(skill != null)
			{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
			skill.useSkill();
			}
			return;
		}
		else if (item.getItemId() == 164000139)
		{
			int skillId = 164000139 == 182207604 ? 10252 : 9834;
			int level = item.getItemId() == 182207604 ? 1 : 4;

			Skill skill = SkillEngine.getInstance().getSkill(player, skillId, level, player.getTarget(), item.getItemTemplate());
			if(skill != null)
			{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
			skill.useSkill();
			}
			return;
		}					
	}

	@Override
	public boolean onLvlUpEvent(QuestCookie env)
	{
		return defaultQuestOnLvlUpEvent(env);
	}
	
	private boolean hasItem(Player player, int itemId)
	{
		return player.getInventory().getItemCountByItemId(itemId) > 0;
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
			if(targetId == 799226)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(env, 10002);
				else 
					return defaultQuestEndDialog(env);
			}
			return false;
		}
		else if(qs.getStatus() == QuestStatus.COMPLETE)
		{
			if(targetId == 799325)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(env, 2716);
				else if(env.getDialogId() == 10005)
				{
					if(player.getPlayerGroup() == null)
					{
						WorldMapTemplate world = DataManager.WORLD_MAPS_DATA.getTemplate(300190000);
						int mapname = DataManager.WORLD_MAPS_DATA.getTemplate(300190000).getMapNameId();
						if (!InstanceService.canEnterInstance(player, world.getInstanceMapId(), 0))
						{
							int timeinMinutes = InstanceService.getTimeInfo(player).get(world.getInstanceMapId())/60;
							if (timeinMinutes >= 60 )
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_HOUR(mapname, timeinMinutes/60));
							else	
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_MIN(mapname, timeinMinutes));
							
							return false;
						}
						if (!hasItem(player, 182207604))
						{
							if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(182207604, 1))))
								ItemService.addItem(player, 182207604, 1, 60 * 7200);
							else
								return true;
						}
						if (!hasItem(player, 182207603))
						{
							if (ItemService.canAddItems(player, Collections.singletonList(new QuestItems(182207603, 1))))
								ItemService.addItem(player, 182207603, 1, 60 * 7200);
							else
								return true;
						}
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300190000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService.teleportTo(player, 300190000, newInstance.getInstanceId(), 198.82353f, 221.40387f, 1098.4879f, (byte) 41);
						PortalController.setInstanceCooldown(player, 300190000, newInstance.getInstanceId());
							return true;
					}
					else
						return sendQuestDialog(env, 2717);
				}
				else
					return defaultQuestStartDialog(env);
			}
		else if(targetId == 799503)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 1)
						return sendQuestDialog(env, 4080);
				case 1013:
				case 10000:
					if(var == 1)
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						TeleportService.teleportTo(player, WorldMapType.GELKMAROS.getId(), 986.2959f, 2566.7314f, 241.11523f, (byte) 45);
						return true;
					}
			}
		}
		}
		else if(qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		if(targetId == 799226)
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
		else if(targetId == 799247)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 1)
						return sendQuestDialog(env, 1352);
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
		else if(targetId == 799250)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 2)
						return sendQuestDialog(env, 1693);
				case 10002:
					if(var == 2)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		
		else if(targetId == 799325)
		{
			switch(env.getDialogId())
			{
				case -1:
					if(var == 5 || var == 6 || var == 7)
						return sendQuestDialog(env, 2716);
				break;
				case 26:
					if(var == 3)
						return sendQuestDialog(env, 2034);
					else if(qs.getQuestVarById(1) == 10 && qs.getQuestVarById(2) == 10 && qs.getQuestVarById(0) == 4)
						return sendQuestDialog(env, 2716);
				case 10003:
					if(var == 3)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case 10005:
					if(qs.getQuestVarById(1) == 10 && qs.getQuestVarById(2) == 10 && qs.getQuestVarById(0) == 4)
					{
						if(player.getPlayerGroup() == null)
						{
							// TODO: add each item which is missing when died and returned back
							if (!ItemService.addItems(player, Collections.singletonList(new QuestItems(182207604, 1))))
								return true;
							if (!ItemService.addItems(player, Collections.singletonList(new QuestItems(182207603, 1))))
								return true;
							qs.setQuestVarById(0, var + 2);
							updateQuestStatus(env);
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(WorldMapType.TALOCS_HOLLOW.getId());
							InstanceService.registerPlayerWithInstance(newInstance, player);
							TeleportService.teleportTo(player, WorldMapType.TALOCS_HOLLOW.getId(), newInstance.getInstanceId(), 663.20984f, 845.8575f, 1380.0017f, (byte) 25);
							return true;
						}
						else
							return sendQuestDialog(env, 2717);
					}
					
					else if(var == 6 || var == 7 || var == 8)
					{
						if(player.getPlayerGroup() == null)
						{
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(WorldMapType.TALOCS_HOLLOW.getId());
							InstanceService.registerPlayerWithInstance(newInstance, player);
							TeleportService.teleportTo(player, WorldMapType.TALOCS_HOLLOW.getId(), newInstance.getInstanceId(), 198.82353f, 221.40387f, 1098.4879f, (byte) 41);
							return true;
						}
						else
							return sendQuestDialog(env, 2717);
					}
			}
		}
		
		else if(targetId == 799503)
		{
			switch(env.getDialogId())
			{
				case 26:
					if(var == 9)
						return sendQuestDialog(env, 4080);
				case 1013:
				case 10000:
					if(var == 9)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						TeleportService.teleportTo(player, WorldMapType.GELKMAROS.getId(), 986.2959f, 2566.7314f, 241.11523f, (byte) 45);
						return true;
					}
			}
		}
		
		else if(targetId == 799258)
		{
			switch(env.getDialogId())
			{
				case -1:
				case 26:
					if(var == 10)
						return sendQuestDialog(env, 1267);
				break;
				case 10010:
					if(var == 10)
					{
						qs.setQuestVarById(1, 0); // clear kills
						qs.setQuestVarById(2, 0);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		
		else if(targetId == 799239)
		{
			switch(env.getDialogId())
			{
				case -1:
				case 26:
					if(var == 11)
						return sendQuestDialog(env, 1608);
				case 10255:
					if(var == 11)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		
		return false;
	}	
}
