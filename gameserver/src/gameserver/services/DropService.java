/*
 * Emulator game server Aion 2.7 from the command of developers 'Aion-Knight Dev. Team' is
 * free software; you can redistribute it and/or modify it under the terms of
 * GNU affero general Public License (GNU GPL)as published by the free software
 * security (FSF), or to License version 3 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranties related to
 * CONSUMER PROPERTIES and SUITABILITY FOR CERTAIN PURPOSES. For details, see
 * General Public License is the GNU.
 *
 * You should have received a copy of the GNU affero general Public License along with this program.
 * If it is not, write to the Free Software Foundation, Inc., 675 Mass Ave,
 * Cambridge, MA 02139, USA
 *
 * Web developers : http://aion-knight.ru
 * Support of the game client : Aion 2.7- 'Arena of Death' (Innova)
 * The version of the server : Aion-Knight 2.7 (Beta version)
 */

package gameserver.services;

import commons.database.dao.DAOManager;
import commons.utils.Rnd;
import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.DropConfig;
import gameserver.dao.DropListDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.EmotionType;
import gameserver.model.NpcType;
import gameserver.model.alliance.PlayerAllianceMember;
import gameserver.model.drop.DropItem;
import gameserver.model.drop.DropList;
import gameserver.model.drop.DropTemplate;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.DropNpc;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.QuestStateList;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.templates.drops.NpcDrop;
import gameserver.model.templates.item.ItemCategory;
import gameserver.model.templates.item.ItemQuality;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.*;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.stats.DropRewardEnum;
import gameserver.world.World;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

public class DropService
{
	private static final Logger log = Logger.getLogger(DropService.class);

	private DropList dropList;

	private Map<Integer, Set<DropItem>>	currentDropMap = new FastMap<Integer, Set<DropItem>>().shared();
	private Map<Integer, DropNpc> dropRegistrationMap  = new FastMap<Integer, DropNpc>().shared();

	/**
	 * Integer Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњ / Id Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р В Р вЂ№Р В Р’В Р В Р РЏР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°
	 */
	private Map<Integer, DropNpc> specialDropMap = new FastMap<Integer, DropNpc>().shared();
	private final ReentrantLock specialDropLock  = new ReentrantLock();
	
	public static final DropService getInstance()
	{
		return SingletonHolder.instance;
	}

	private DropService()
	{
		dropList = new DropList();
		for(NpcDrop drop : DataManager.DROPLIST_DATA.getDrops())
		{
			for(gameserver.model.templates.drops.DropItem di : drop.getDropItems())
			{
				DropTemplate t = new DropTemplate(drop.getNpcId(), di.getItemId(), di.getMin(), di.getMax(), di.getChance());
				dropList.addDropTemplate(drop.getNpcId(), t);
			}
		}
				
		DropList sqlList = DAOManager.getDAO(DropListDAO.class).load();
		TIntObjectHashMap<Set<DropTemplate>> sqlTemplates = sqlList.getAll();
		sqlTemplates.forEachEntry(new TIntObjectProcedure<Set<DropTemplate>>(){
			
			@Override
			public boolean execute(int arg0, Set<DropTemplate> arg1)
			{
				if(dropList.getDropsFor(arg0) != null)
				{
					Set<DropTemplate> xmlDrops = dropList.getDropsFor(arg0);
					for(DropTemplate sqlDrop : arg1)
					{
						boolean xmlExists = false;
						for(DropTemplate xmlDrop : xmlDrops)
						{
							if(xmlDrop.getItemId() == sqlDrop.getItemId())
							{
								xmlExists = true;
								break;
							}
						}
						if(!xmlExists)
							dropList.addDropTemplate(arg0, sqlDrop);
						else
						{
							if(CustomConfig.GAMESERVER_DROPLIST_MASTER_SOURCE.equals("sql"))
							{
								dropList.removeDrop(arg0, sqlDrop.getItemId());
								dropList.addDropTemplate(arg0, sqlDrop);
							}
						}
					}
				}
				else
				{
					for(DropTemplate t : arg1)
					{
						dropList.addDropTemplate(arg0, t);
					}
				}
				return true;
			}
		});
		
		log.info(dropList.getSize() + " npc drops loaded");
	}

	/**
	 * @return Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ
	 */
	public DropList getDropList()
	{
		return dropList;
	}

	/**
	 * Р В Р’В Р вЂ™Р’В Р В Р Р‹Р РЋРЎСџР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›, Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ NPC Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ - Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚Сњ
	 * 
	 * @param npc
	 */
	public void registerDrop(Npc npc, Player player, int lvl)
	{
		List<Player> players = new ArrayList<Player>();
		players.add(player);
		registerDrop(npc, player, lvl, players);
	}

	/**
	 * Р В Р’В Р вЂ™Р’В Р В Р Р‹Р РЋРЎСџР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›, Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ NPC Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ - Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚Сњ
	 * 
	 * @param npc
	 * @param player
	 * @param lvl
	 * @param players
	 *           Р В Р’В Р вЂ™Р’В Р В Р’В Р В РІР‚в„–Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В¦ Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р В Р вЂ№Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњ, Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ.
	 */
	public void registerDrop(Npc npc, Player player, int lvl, List<Player> players)
	{
		int npcUniqueId = npc.getObjectId();
		int npcTemplateId = npc.getObjectTemplate().getTemplateId();
		
		Set<DropItem> droppedItems = new HashSet<DropItem>();
		Set<DropTemplate> templates = dropList.getDropsFor(npcTemplateId);
		Set<DropTemplate> worldDrops = npc.getWorldDrops(player);
		if (worldDrops != null)
		{
			if (templates == null)
				templates = worldDrops;
			else
				templates.addAll(worldDrops);
		}
		
		int normalDropPercentage = 100;
		int craftItemDropPercentage = 100;
		if(!DropConfig.DISABLE_DROP_REDUCTION && npc.getObjectTemplate().getNpcType() != NpcType.CHEST)
		{
			normalDropPercentage = DropRewardEnum.dropRewardFrom(npc.getLevel() - lvl);
			
			// Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†РІР‚С›РЎС›Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІР‚С”Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р В Р РЏ, Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р… Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В¦ Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚Сљ:
			craftItemDropPercentage = 100 - ((100 - normalDropPercentage) / 2);
		}

		if(templates != null)
		{
			int OrangeDrops	= 0;
			int	GoldDrops = 0;
			int	BlueDrops = 0;
			int Hearts = 0;
			
			float playerDropRate = player.getRates().getDropRate();
			float normalDropRate = playerDropRate * normalDropPercentage / 100F;
			float craftItemDropRate = playerDropRate * craftItemDropPercentage / 100F;
			Map<ItemCategory, Double> categoryChances = new HashMap<ItemCategory, Double>();

			QuestStateList questStates = player.getQuestStateList();
			for(DropTemplate dropTemplate : templates)
			{
				DropItem dropItem = new DropItem(dropTemplate);
				if (!DropConfig.DISABLE_DROP_REDUCTION && npc.getObjectTemplate().getNpcType() != NpcType.CHEST) 
				{
					TIntArrayList questIds = QuestEngine.getInstance().getQuestsForCollectItem(dropTemplate.getItemId());
					normalDropRate = playerDropRate * normalDropPercentage / 100F;
					craftItemDropRate = playerDropRate * craftItemDropPercentage / 100F;
					
					for (int index = 0; index < questIds.size(); index++)
					{
						int questId = questIds.get(index);
						QuestState qs = questStates.getQuestState(questId);
						int maxRepeat = DataManager.QUEST_DATA.getQuestById(questId).getMaxRepeatCount();
						if (qs == null || qs.getStatus() != QuestStatus.COMPLETE || qs.canRepeat(maxRepeat))
						{
							// Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†РІР‚С™Р’В¬Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р В Р вЂ№Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ, Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р… Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В» Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р В Р вЂ№Р В Р вЂ Р Р†Р вЂљРЎв„ўР вЂ™Р’В¬Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦
							craftItemDropRate = normalDropRate = playerDropRate;
							break;
						}
					}
				}
				
				float calculatedRate = 0;
				ItemTemplate dropListItems = ItemService.getItemTemplate(dropTemplate.getItemId());
				if (dropListItems == null)
					continue;
				
				ItemCategory category = dropListItems.getItemCategory();
				if (DropConfig.ITEMCATEGORY_RESTRICTION_ENABLED)
				{
					if (categoryChances.containsKey(category))
						calculatedRate = (float)(-categoryChances.get(category)) / 100F;
				}
				
				if(dropTemplate.getItemId() >= 152000000 && dropTemplate.getItemId() < 153000000)
				{
					calculatedRate += craftItemDropRate;
					dropItem.calculateCount(player, npc.getNpcId(), calculatedRate);
				}
				else if (category == ItemCategory.HEART)
				{
					calculatedRate += normalDropRate * 10F;
					dropItem.calculateCount(player, npc.getNpcId(), calculatedRate);					
				}
				else
				{
					calculatedRate += normalDropRate;
					dropItem.calculateCount(player, npc.getNpcId(), calculatedRate);
				}

				if(dropItem.getCount() > 0)
				{
					if (DropConfig.ITEMCATEGORY_RESTRICTION_ENABLED)
					{
						double newChance = dropItem.getLootChance();
						if (categoryChances.containsKey(category))
						{
							newChance += categoryChances.get(category);
							if (newChance > calculatedRate * 100)
								continue;
						}
						categoryChances.put(dropListItems.getItemCategory(), newChance);
					}
					
					if (dropTemplate.getItemId() == 182400001)
					{
						dropItem.setCount(dropItem.getCount() * player.getRates().getKinahRate());
					}
					
					if (category == ItemCategory.HEART)
					{
						// Р В Р’В Р вЂ™Р’В Р В Р’В Р В РІР‚в„–Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС› Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС› 1 Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ ;)
						if (Hearts > 0)
							continue;
						Hearts++;
					}
					
					if(DropConfig.DROPQUANTITY_RESTRICTION_ENABLED)
					{
						if(dropListItems.getItemQuality() == ItemQuality.LEGEND)
						{
							if(BlueDrops >= DropConfig.DROPQUANTITY_RESTRICTION_BLUE)
								continue;
							else
							{
								BlueDrops++;
								droppedItems.add(dropItem);
							}
						}
						else if(dropListItems.getItemQuality() == ItemQuality.UNIQUE)
						{
							if(GoldDrops >= DropConfig.DROPQUANTITY_RESTRICTION_GOLD)
								continue;
							else
							{
								GoldDrops++;
								droppedItems.add(dropItem);
							}
						}
						else if(dropListItems.getItemQuality() == ItemQuality.EPIC)
						{
							if(OrangeDrops >= DropConfig.DROPQUANTITY_RESTRICTION_ORANGE)
								continue;
							else
							{
								OrangeDrops++;
								droppedItems.add(dropItem);
							}
						}
						else
							droppedItems.add(dropItem);
					}
					else
						droppedItems.add(dropItem);
				}
			}
			
			templates.clear();
			templates = null;
		}
		
		QuestService.getQuestDrop(droppedItems, npc, player);
		
		// Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†РІР‚С™Р’В¬Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњ
		int index = 1;
		for (DropItem drop: droppedItems)
			drop.setIndex(index++);
		
		currentDropMap.put(npcUniqueId, droppedItems);

		// TODO: Р В Р’В Р вЂ™Р’В Р В РЎвЂ”Р РЋРІР‚вЂќР В РІР‚В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦ Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° null.
		if(player != null)
		{
			List<Player> dropPlayers = new ArrayList<Player>();

			if (player.isInAlliance())
			{
				dropRegistrationMap.put(npcUniqueId, new DropNpc(AllianceService.getInstance().getMembersToRegistrateByRules(
                        player.getPlayerAlliance(), npc), npcUniqueId));
				
				// Fetch Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ
				DropNpc dropNpc = dropRegistrationMap.get(npcUniqueId);
				dropNpc.setInRangePlayers(players);
				dropNpc.setGroupSize(dropNpc.getInRangePlayers().size());

				for(PlayerAllianceMember member : player.getPlayerAlliance().getMembers())
				{
					Player allianceMember = member.getPlayer();

					if(allianceMember != null)
						if(dropNpc.containsKey(allianceMember.getObjectId()))
							dropPlayers.add(allianceMember);
				}
			}
			else if (player.isInGroup())
			{
				dropRegistrationMap.put(npcUniqueId, new DropNpc(GroupService.getInstance().getMembersToRegistrateByRules(player,
					player.getPlayerGroup(), npc), npcUniqueId));
				
				// Fetch Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В  Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ
				DropNpc dropNpc = dropRegistrationMap.get(npcUniqueId);
				dropNpc.setInRangePlayers(players);
				dropNpc.setGroupSize(dropNpc.getInRangePlayers().size());
				for(Player member : player.getPlayerGroup().getMembers())
				{
					if(member != null && member.isOnline()){
						if(dropNpc.containsKey(member.getObjectId()))
							dropPlayers.add(member);
					}
				}
			}
			else
			{
				List<Integer> singlePlayer = new ArrayList<Integer>();
				singlePlayer.add(player.getObjectId());
				dropPlayers.add(player);
				dropRegistrationMap.put(npcUniqueId, new DropNpc(singlePlayer, npcUniqueId));
			}

			for(Player p : dropPlayers)
			{
				PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npcUniqueId, 0));
			}
		}
	}

	/**
	 * After NPC respawns - drop should be unregistered //TODO more correct - on despawn
	 * 
	 * @param npc
	 */
	public void unregisterDrop(Npc npc)
	{
		int npcUniqueId = npc.getObjectId();
		currentDropMap.remove(npcUniqueId);
		if(dropRegistrationMap.containsKey(npcUniqueId))
		{
			dropRegistrationMap.remove(npcUniqueId);
		}
	}

	/**
	 * Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†РІР‚С›РЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В¦ NPC Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРІвЂћвЂ“Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС› Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°
	 * 
	 * @param player
	 * @param npcId
	 */
	public void requestDropList(Player player, int npcId)
	{
		if(player == null || !dropRegistrationMap.containsKey(npcId))
			return;

		DropNpc dropNpc = dropRegistrationMap.get(npcId);
		if(!dropNpc.containsKey(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_NO_RIGHT());
			return;
		}

		if(dropNpc.isBeingLooted())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_FAIL_ONLOOTING());
			return;
		}

		dropNpc.setBeingLooted(player);

		Set<DropItem> dropItems = currentDropMap.get(npcId);
		
		if(dropItems == null)
		{
			dropItems = Collections.emptySet();
		}

		PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
		// PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, size > 0 ? size - 1 : size));
		PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
		player.unsetState(CreatureState.ACTIVE);
		player.setState(CreatureState.LOOTING);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, npcId), true);
	}

	/**
	 * Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В­Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р… Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р… Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦, Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р В Р вЂ№Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р В РІР‚В° Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В 
	 * @param player
	 * @param npcId
	 * @param close
	 */
	public void requestDropList(Player player, int npcId, boolean close)
	{
		if(!dropRegistrationMap.containsKey(npcId) || player == null)
			return;

		DropNpc dropNpc = dropRegistrationMap.get(npcId);
		dropNpc.setBeingLooted(null);

		player.unsetState(CreatureState.LOOTING);
		player.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);

		Set<DropItem> dropItems = currentDropMap.get(npcId);
		AionObject obj = World.getInstance().findAionObject(npcId);
		if (obj instanceof Npc)
		{
			Npc npc = (Npc)obj;
			if(npc != null)
			{
				if(dropItems == null || dropItems.isEmpty())
				{
					npc.getController().onDespawn(true);
					return;
				}

				PacketSendUtility.broadcastPacket(npc, new SM_LOOT_STATUS(npcId, 0));
				dropNpc.setFreeLooting();
			}
		}
	}

	/**
	 * Р В Р’В Р вЂ™Р’В Р В Р вЂ Р В РІР‚С™Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В· Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС› Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°.
	 * 
	 * @param player
	 *           Р В Р’В Р вЂ™Р’В Р В РЎвЂ”Р РЋРІР‚вЂќР В РІР‚В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ, Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚Сњ.
	 * @param npcId
	 *           Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†РІР‚С™Р’В¬Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±.
	 * @param itemIndex
	 *           Р В Р’В Р вЂ™Р’В Р В РЎвЂ”Р РЋРІР‚вЂќР В РІР‚В¦Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚Сљ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р вЂ Р Р†Р вЂљРЎв„ўР вЂ™Р’В¬Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В¦ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В .
	 */
	public void requestDropItem(final Player player, int npcId, int itemIndex)
	{
		final Set<DropItem> dropItems = currentDropMap.get(npcId);
		final DropNpc dropNpc = dropRegistrationMap.get(npcId);

		// Р В Р’В Р вЂ™Р’В Р В Р вЂ Р В РІР‚С™Р РЋРЎС™Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚Сњ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В» Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦
		if(dropItems == null || dropNpc == null)
		{
			return;
		}

		// TODO Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р В Р РЏ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В·Р В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В¶Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р Р†РІР‚С›РІР‚вЂњР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р вЂ™Р’В¦ Р В Р’В Р В Р вЂ№Р В Р’В Р В Р вЂ°Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р вЂ Р Р†Р вЂљРЎвЂєР Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В 

		DropItem requestedItem = null;

		synchronized(dropItems)
		{
			for(DropItem dropItem : dropItems)
			{
				if(dropItem.getIndex() == itemIndex)
				{
					requestedItem = dropItem;
					break;
				}
			}
		}

		if(requestedItem == null || requestedItem.isProcessed())
			return;

		ItemTemplate itemTemplate = ItemService.getItemTemplate(requestedItem.getDropTemplate().getItemId());
		if(itemTemplate == null)
		{
			log.warn("Item id " + requestedItem.getDropTemplate().getItemId()
				+ " can't be found in the item template.");
			return;
		}

		if(!itemTemplate.isTradeable() && !requestedItem.isQuestDropForEachMemeber() && (player.isInGroup() || player.isInAlliance()))
		{
			final DropItem lootItem = requestedItem;
			RequestResponseHandler rrh = new RequestResponseHandler(player){
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					continueRequestDropItem(player, dropItems, dropNpc, lootItem);
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›
				}
			};
			SM_QUESTION_WINDOW question = new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_CONFIRM_LOOT, 0,
				new DescriptionId(itemTemplate.getNameId()));
			player.getResponseRequester().sendRequest(SM_QUESTION_WINDOW.STR_CONFIRM_LOOT, rrh, question);
		}
		else
			continueRequestDropItem(player, dropItems, dropNpc, requestedItem);
	}

	private void continueRequestDropItem(final Player player, Set<DropItem> dropItems, DropNpc dropNpc, final DropItem requestedItem)
	{
		if(requestedItem == null || requestedItem.isProcessed())
			return;

		if(CustomConfig.ANNOUNCE_RAREDROPS && !player.getInventory().isFull())
		{
			final ItemTemplate itemTemplate = ItemService.getItemTemplate(requestedItem.getDropTemplate().getItemId());
			if(itemTemplate.getItemQuality() == ItemQuality.UNIQUE || itemTemplate.getItemQuality() == ItemQuality.EPIC)
			{
				final int pRaceId = player.getCommonData().getRace().getRaceId();
				final int pMap = player.getWorldId();
				final int pRegion = player.getActiveRegion().getRegionId();
				final int pInstance = player.getInstanceId();
				
				World.getInstance().doOnAllPlayers(new Executor<Player>(){
					@Override
					public boolean run(Player other)
					{
						if(other.getObjectId() == player.getObjectId() || !other.isSpawned())
						{
							return true;
						}

						int oRaceId = other.getCommonData().getRace().getRaceId();
						int oMap = other.getWorldId();
						int oRegion = other.getActiveRegion().getRegionId();
						int oInstance = other.getInstanceId();

						if(oRaceId == pRaceId && oMap == pMap && oRegion == pRegion && oInstance == pInstance)
						{
						    PacketSendUtility.sendPacket(other,SM_SYSTEM_MESSAGE.STR_FORCE_ITEM_WIN(player.getCommonData().getName(),new DescriptionId(itemTemplate.getNameId())));
						}
						return true;
					}
				});
			}
		}

		if(requestedItem != null)
		{
			if(requestedItem.isItemWonNotCollected() && player != requestedItem.getWinningPlayer())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_ANOTHER_OWNER_ITEM());
				return;
			}

			long currentDropItemCount = requestedItem.getCount();
			int itemId = requestedItem.getDropTemplate().getItemId();

			ItemTemplate itemTemplate = ItemService.getItemTemplate(itemId);
			ItemQuality quality = ItemQuality.COMMON;
			if(itemTemplate == null)
				log.warn("Item id " + itemId + " can't be found in the item template.");
			else
				quality = itemTemplate.getItemQuality();

			if(!requestedItem.isItemWonNotCollected() && !requestedItem.isFreeForAll())
			{
				if(player.isInGroup() || player.isInAlliance())
				{
					if(player.isInGroup())
						requestedItem.setDistributionType(player.getPlayerGroup().getLootGroupRules().getQualityRule(quality));
					else
						requestedItem.setDistributionType(player.getPlayerAlliance().getLootAllianceRules().getQualityRule(quality));
					
					if(requestedItem.getDistributionType() > 1)
					{
						int groupAllianceId = 0;
						if(player.isInGroup())
						{
							groupAllianceId = player.getPlayerGroup().getObjectId();
						}
						else
						{
							groupAllianceId = player.getPlayerAlliance().getObjectId();
						}

						addSpecialItem(groupAllianceId, dropNpc, requestedItem);
					}
				}
			}

			if((!player.isInGroup() && !player.isInAlliance()) || requestedItem.getDistributionType() == 0
				|| requestedItem.isFreeForAll()
				|| (requestedItem.isItemWonNotCollected() && player == requestedItem.getWinningPlayer()))
			{
				currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount);
				requestedItem.setWinningPlayer(null);
			}

			if(currentDropItemCount == 0)
			{
				requestedItem.setProcessed();
				dropItems.remove(requestedItem);
			}
			else
			{
				// Р В Р’В Р вЂ™Р’В Р В Р вЂ Р В РІР‚С™Р РЋРЎвЂєР В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р… Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СљР В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљ Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р В Р вЂ№Р В Р Р‹Р Р†Р вЂљРЎС™Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р В Р вЂ№Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В» Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ, Р В Р’В Р В Р вЂ№Р В Р’В Р В Р вЂ°Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р С—РЎвЂ”Р вЂ¦Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћ Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р В Р вЂ№Р В Р вЂ Р В РІР‚С™Р РЋРІвЂћСћР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°
				requestedItem.setCount(currentDropItemCount);
			}

			// Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎвЂќР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В±Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В»Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’ВµР В Р’В Р вЂ™Р’В Р В Р’В Р Р†Р вЂљР’В¦Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’Вµ Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р РЋРІР‚СљР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎСљР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В° Р В Р’В Р вЂ™Р’В Р В РЎС›Р Р†Р вЂљРїС—Р…Р В Р’В Р В Р вЂ№Р В Р’В Р Р†Р вЂљРЎв„ўР В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРЎС›Р В Р’В Р вЂ™Р’В Р В Р Р‹Р Р†Р вЂљРІР‚СњР В Р’В Р вЂ™Р’В Р В РІР‚в„ўР вЂ™Р’В°
			resendDropList(dropNpc.getBeingLooted(), dropNpc.getNpcId(), dropItems);
		}
	}
	
	private void resendDropList(Player player, int npcId, Set<DropItem> dropItems)
	{
		if(!dropItems.isEmpty())
		{
			if(player != null)
			{
				boolean hasItemsForPlayer = false;
				for(DropItem item : dropItems)
				{
					if(item.hasQuestPlayerObjId(player.getObjectId()))
					{
						hasItemsForPlayer = true;
						break;
					}
				}
				if(hasItemsForPlayer)
				{
					PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 3));
					player.unsetState(CreatureState.LOOTING);
					player.setState(CreatureState.ACTIVE);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);
				}
			}
		}
		else
		{
			if(player != null)
			{
				PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 3));
				player.unsetState(CreatureState.LOOTING);
				player.setState(CreatureState.ACTIVE);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);
			}
			AionObject obj = World.getInstance().findAionObject(npcId);
			if(obj instanceof Npc)
			{
				Npc npc = (Npc) obj;
				if(npc != null)
				{
					npc.getController().onDespawn(true);
				}
			}
		}
	}

	/**
	 * Add an item that should be rolled/bid on.
	 * 
	 * @param groupAllianceId
	 *           The id of the group or alliance.
	 * @param dropNpc
	 * @param specialItem
	 */
	private void addSpecialItem(int groupAllianceId, DropNpc dropNpc, DropItem specialItem)
	{
		if(!dropNpc.addSpecialItem(specialItem))
			return;

		specialDropLock.lock();
		try
		{
			DropNpc currentSpecialNpc = specialDropMap.get(groupAllianceId);
			if(currentSpecialNpc == null)
			{
				specialDropMap.put(groupAllianceId, dropNpc);
				sendBidRollPackets(groupAllianceId);
			}
			else
			{
				try
				{
					currentSpecialNpc.addSpecialDropNpc(dropNpc);
				}
				catch(StackOverflowError soe)
				{
					// This does NOT fix any errors, it just wraps up the StackOverflowError error so it doesn't take
					// 1000 lines in the error log.
					specialDropMap.remove(groupAllianceId);
					throw new Error("StackOverflowError");
				}
			}
		}
		finally
		{
			specialDropLock.unlock();
		}
	}

	/**
	 * Sends the packets to roll/bid on an item.
	 * 
	 * @param groupAllianceId
	 */
	private void sendBidRollPackets(final int groupAllianceId)
	{
		// FIXME find the players in range when the rolling/bidding starts.
		// Store the npc location in DropNpc.
		final DropNpc dropNpc = getNextSpecialNpc(groupAllianceId);
		if(dropNpc == null)
			return;

		final DropItem requestedItem = dropNpc.getNextSpecialItem();
		if(requestedItem == null)
		{
			// For an unknown reason requestedItem can be null.
			// When this happens assume that the current NPC has no more special items and restart the method to check
			// for other NPC's
			dropNpc.resetSpecialItems();
			specialDropMap.remove(groupAllianceId);
			return;
		}

		int itemId = requestedItem.getDropTemplate().getItemId();

		// Start the timeout task and let it wait 20 seconds of it's rolling or 35 seconds if it's bidding.
		int timeout = 20000;
		if(requestedItem.getDistributionType() == 3)
			timeout = 35000;
		ScheduledFuture<?> future = ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				specialLootTimeout(groupAllianceId, dropNpc, requestedItem);
			}
		}, timeout);
		requestedItem.setSpecialDropTimeout(future);

		// Send the packet to all members
		SM_GROUP_LOOT sgl = new SM_GROUP_LOOT(groupAllianceId, itemId, requestedItem.getIndex(), dropNpc.getNpcId(),
			requestedItem.getDistributionType());
		for(Player member : dropNpc.getInRangePlayers())
		{
			if(member.isOnline())
			{
				requestedItem.addSpecialPlayer(member);
				PacketSendUtility.sendPacket(member, sgl);
			}
		}
	}

	/**
	 * Cancel the current special item loot.
	 * 
	 * @param groupAllianceId
	 */
	private void specialLootTimeout(int groupAllianceId, DropNpc dropNpc, DropItem requestedItem) {
		requestedItem.setSpecialDropTimeout(null);

		distributeSpecialItem(requestedItem, groupAllianceId, dropNpc.getNpcId(), requestedItem.getIndex());
	}

	/**
	 * @param groupAllianceId
	 *           The id of the group or alliance.
	 * @return The DropNpc that has special items to roll/bid on, or null if there is no next Npc.
	 */
	private DropNpc getNextSpecialNpc(int groupAllianceId)
	{
		specialDropLock.lock();
		try
		{
			DropNpc currentSpecialNpc = specialDropMap.get(groupAllianceId);
			if(currentSpecialNpc == null)
				return null;

			if(!currentSpecialNpc.hasSpecialItems())
			{ // changed the special npc to the next npc with roll/bid items or null.
				currentSpecialNpc = currentSpecialNpc.getNextSpecialDropNpc();
				specialDropMap.put(groupAllianceId, currentSpecialNpc);
			}

			if(currentSpecialNpc == null)
			{
				specialDropMap.remove(groupAllianceId);
			}
			return currentSpecialNpc;
		}
		finally
		{
			specialDropLock.unlock();
		}
	}

	/**
	 * Called from CM_GROUP_LOOT to handle rolls
	 * 
	 * @param player
	 * @param groupAllianceId
	 * @param roll
	 * @param itemId
	 * @param itemIndex
	 * @param npcId
	 */
	public void handleRoll(Player player, int groupAllianceId, int roll, int itemId, int itemIndex, int npcId,
		int distibutionType)
	{
		if(dropRegistrationMap.get(npcId) == null)
			return;

		switch(roll)
		{
			case 0:
				SM_GROUP_LOOT sglGiveUp = new SM_GROUP_LOOT(groupAllianceId, itemId, itemIndex, npcId, distibutionType,
					player.getObjectId(), 0);
				PacketSendUtility.sendPacket(player, sglGiveUp);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_ME());
				if(player.isInGroup() || player.isInAlliance())
				{
					SM_SYSTEM_MESSAGE giveup = SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_OTHER(player.getName());
					for(Player member : dropRegistrationMap.get(npcId).getInRangePlayers())
					{
						if(!player.equals(member))
						{
							PacketSendUtility.sendPacket(member, sglGiveUp);
							PacketSendUtility.sendPacket(member, giveup);
						}
					}
				}
				handleSpecialLoot(player, groupAllianceId, 0, itemId, itemIndex, npcId);
				break;
			case 1:
				int luck = Rnd.get(1, 100);
				SM_GROUP_LOOT sglRoll = new SM_GROUP_LOOT(groupAllianceId, itemId, itemIndex, npcId, distibutionType,
					player.getObjectId(), luck);
				PacketSendUtility.sendPacket(player, sglRoll);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_ME(luck));
				if(player.isInGroup() || player.isInAlliance())
				{
					SM_SYSTEM_MESSAGE sysMsgRoll = SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_OTHER(player.getName(), luck);
					for(Player member : dropRegistrationMap.get(npcId).getInRangePlayers())
					{
						if(!player.equals(member))
						{
							PacketSendUtility.sendPacket(member, sglRoll);
							PacketSendUtility.sendPacket(member, sysMsgRoll);
						}
					}
				}
				handleSpecialLoot(player, groupAllianceId, luck, itemId, itemIndex, npcId);
				break;
		}
	}

	/**
	 * Called from CM_GROUP_LOOT to handle bids
	 * 
	 * @param player
	 * @param groupAllianceId
	 * @param bid
	 * @param itemId
	 * @param itemIndex
	 * @param npcId
	 */
	public void handleBid(Player player, int groupAllianceId, long bid, int itemId, int itemIndex, int npcId)
	{
		long kinahAmount = player.getInventory().getKinahItem().getItemCount(); 
		if(bid > 0)
		{
			if(kinahAmount < bid)
			{
				bid = 0;// Set BID to 0 if player has bid more KINAH then they have in inventory
			}
			handleSpecialLoot(player, groupAllianceId, bid, itemId, itemIndex, npcId);
		}
		else
			handleSpecialLoot(player, groupAllianceId, 0, itemId, itemIndex, npcId);
	}

	/**
	 * @param Checks all players have Rolled or Bid then Distributes items accordingly
	 */
	private void handleSpecialLoot(Player player, int groupAllianceId, long bidRollValue, int itemId, int itemIndex, int npcId)
	{
		DropNpc dropNpc = dropRegistrationMap.get(npcId);
		
		Set<DropItem> dropItems = currentDropMap.get(npcId);
		if(dropNpc == null || dropItems == null)
			return;

		DropItem requestedItem = null;

		synchronized(dropItems)
		{
			for(DropItem dropItem : dropItems)
			{
				if(dropItem.getIndex() == itemIndex)
				{
					requestedItem = dropItem;
					break;
				}
			}
		}
		if(requestedItem == null || requestedItem.getDropTemplate().getItemId() != itemId
			|| requestedItem.isProcessed())
			return;

		//Removes player from ARRAY once they have rolled or bid
		if(requestedItem.containsSpecialPlayer(player))
		{
			requestedItem.delSpecialPlayer(player);
		}
		else
			return;

		if(bidRollValue > requestedItem.getHighestValue())
		{
			requestedItem.setHighestValue(bidRollValue);
			requestedItem.setWinningPlayer(player);
		}

		if(requestedItem.getSpecialPlayerSize() != 0)
			return;

		// Cancel the timeout task
		requestedItem.cancelTimeoutTask();

		distributeSpecialItem(requestedItem, groupAllianceId, npcId, itemIndex);
	}

	private void distributeSpecialItem(DropItem requestedItem, int groupAllianceId, int npcId, int itemIndex)
	{
		DropNpc dropNpc = dropRegistrationMap.get(npcId);
		
		if(dropNpc == null)
			return;

		//Check if there is a Winning Player registeredIf not all members must have passed...
		if(requestedItem.getWinningPlayer() == null)
		{
			requestedItem.setFreeForAll(true);
		}
		else
		{
			Player player = requestedItem.getWinningPlayer();
			long currentDropItemCount = requestedItem.getCount();
			int itemId = requestedItem.getDropTemplate().getItemId();

			switch(requestedItem.getDistributionType())
			{
				case 2:
					winningRollActions(groupAllianceId, player, itemId, npcId, requestedItem);
					break;
				case 3:
					winningBidActions(player, itemId, npcId, requestedItem.getHighestValue());
					break;
			}

			// handles distribution of item to correct player and messages accordingly
			if(player.getInventory().isFull())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
				requestedItem.setItemWonNotCollected(true);
			}
			else
			{
				Set<DropItem> dropItems = currentDropMap.get(npcId);
				currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount);

				if(currentDropItemCount != 0)
				{
					requestedItem.setCount(currentDropItemCount);
					requestedItem.setItemWonNotCollected(true);
				}
				else
				{
					requestedItem.setProcessed();
					dropItems.remove(requestedItem);
				}

				// show updated drop list
				resendDropList(dropNpc.getBeingLooted(), npcId, dropItems);
			}
		}

		sendBidRollPackets(groupAllianceId);
	}

	/** 
	 * @param Displays messages when item gained via ROLLED
	 */	
	private void winningRollActions(int groupAllianceId, Player player, int itemId, int npcId, DropItem requestedItem)
	{
		DescriptionId itemNameId = new DescriptionId(ItemService.getItemTemplate(itemId).getNameId());
		SM_GROUP_LOOT sglWinner = new SM_GROUP_LOOT(groupAllianceId, itemId, requestedItem.getIndex(), npcId,
			requestedItem.getDistributionType(), player.getObjectId());
		PacketSendUtility.sendPacket(player, sglWinner);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_ME(itemNameId));
		
		if(player.isInGroup() || player.isInAlliance())
		{
			SM_SYSTEM_MESSAGE msgWinner = SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_OTHER(player.getName(), itemNameId);
			for(Player member : dropRegistrationMap.get(npcId).getInRangePlayers())
			{
				if(!player.equals(member))
				{
					PacketSendUtility.sendPacket(member, sglWinner);
					PacketSendUtility.sendPacket(member, msgWinner);
				}
			}
		}
	}

	/**
	 * @param Displays messages/removes and shares kinah when item gained via BID
	 */	
	private void winningBidActions(Player player, int itemId, int npcId, long highestValue)
	{
		DropNpc dropNpc = dropRegistrationMap.get(npcId);
		
		if((player.isInGroup() || player.isInAlliance()) && dropNpc.getGroupSize() > 1)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_ME(highestValue));
			if(!player.getInventory().decreaseKinah(highestValue))
				return;

			long distributeKinah = highestValue / (dropNpc.getGroupSize() - 1);
			for(Player member : dropNpc.getInRangePlayers())
			{
				if(!player.equals(member))
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_OTHER(player.getName(), highestValue));
					member.getInventory().increaseKinah(distributeKinah);
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_DISTRIBUTE(highestValue, dropNpc.getGroupSize() - 1, distributeKinah));
				}
			}
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final DropService instance = new DropService();
	}
}
