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

package gameserver.services;

import org.apache.log4j.Logger;

import commons.database.DatabaseFactory;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.EmotionType;
import gameserver.model.Race;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Monster;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.group.PlayerGroup;
import gameserver.services.InstanceService;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_STAGE_STEP_STATUS;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.world.World;
import commons.utils.Rnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmpyreanCrucibleService
{
	List<VisibleObject> monsterSpawns = new ArrayList<VisibleObject>();
	int currentStage = 1;
	int currentRound = 1;
	int currentRoundNpcCount = 0;
	int pointsReward = 0;
	private PlayerGroup registeredGroup = null;
	static int mapId = 300300000;
	private Map<Player,Integer> registeredPlayers = new HashMap<Player,Integer>();//key = player, value = player state (0 = active, 1 = ready room, 2 = excluded)
	int stage;
	int param;
	int round;
	int type;
	int itemId;
	boolean hasReward = false;
	private final static int[] code = {35464, 36464, 37464, 38464, 8392, 43856, 13784, 49248, 19176, 54640};
	/**
	 ** Array with reward points
	 */
	private final static int[] rewardPoints = {1615, 1000, 722, 1000, 1665, 1285, 4800, 7200, 9665, 2500};
	/**
	 ** Array with type for SM_STAGE_STEP_STATUS
	 */
	private final static int[] typeArray = {1, 1, 1, 1, 3, 4, 6, 7, 9, 10};
	/**
	 ** Array with coordinates when player die
	 */
	private final static float[] dieX = {381.192f, 381.192f, 381.192f, 381.192f, 1261.0006f, 1594.374f, 1816.27f, 1777.57f, 1357.69f, 1751.23f};
	private final static float[] dieY = {348.942f, 348.942f, 348.942f, 348.942f, 831.7126f, 150.303366f, 795.125f, 1726.25f, 1747.89f, 1256.68f};
	private final static float[] dieZ = {96.7476f, 96.7476f, 96.7476f, 96.7476f, 358.60562f, 128.69188f, 470.034f, 304f, 319.336f, 394.238f};
	private final static int[] dieH = {59, 59, 59, 59, 100, 103, 58, 29, 64, 29};
	/**
	 ** Array with coordinates when player change stage or return to stage
	 */
	private final static float[] chsX = {345.25f, 345.25f, 345.25f, 345.25f, 1256.8428f, 1638.0f, 1794.908f, 1775.9249f, 1303.6643f, 1766.1079f};
	private final static float[] chsY = {349.40f, 349.40f, 349.40f, 349.40f, 801.94794f, 134.0f, 811.9936f, 1760.295f, 1732.8148f, 1289.0673f};
	private final static float[] chsZ = {96.09f, 96.09f, 96.09f, 96.09f, 358.60562f, 126.0f, 469.3501f, 303.69543f, 316.09506f, 394.23755f};

	public class MonsterSpawnNode {
		int npcId = 0;
		int flag = 0; //0 = normal, 1 = do not add to kill_list
		float x = 0;
		float y = 0;
		float z = 0;
		byte h = 0;
		MonsterSpawnNode(int npcId, int flag, float x, float y, float z, byte h){
			this.npcId = npcId;
			this.flag = flag;
			this.x = x;
			this.y = y;
			this.z = z;
			this.h = h;
		}
	}
	
	public boolean start(final PlayerGroup group){
		PacketSendUtility.sendMessage(group.getGroupLeader(), "teleport in 20s.");

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				World.getInstance().doOnAllPlayers(new Executor<Player>() {
					@Override
					public boolean run(Player player) {
						PacketSendUtility.sendSysMessage(player, "test started");
						return true;
					}
				});
				startEvent(group);
			}
		}, 20000);
		return false;
	}	
	public void startEvent(PlayerGroup group){
		this.registeredGroup = group;
		
		sendScore2Group();
		for(Player player : registeredGroup.getMembers())
			{
			registeredPlayers.put(player, 0);
			player.setInEmpyrean(true);
			PacketSendUtility.sendPacket(player, new SM_STAGE_STEP_STATUS(2, 35464, 1));
			}
		sendWaves(); 
	}

	private void sendWaves()
	{
		final int tmpStage = currentStage;
		final int tmpRound = currentRound;

		type = typeArray[currentStage-1];
		sendMsg2group(2, tmpStage, tmpRound, type);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sendWave(getNpcsFromDB(mapId, tmpStage, tmpRound, registeredGroup.getGroupLeader().getCommonData().getRace()));
			}
		}, 5000);
	}
	private void sendStageComplete(int stage)
	{
		final int tmpStage = currentStage;
		
		for(Player player : registeredGroup.getMembers())
			 {
			PacketSendUtility.sendPacket(player, new SM_STAGE_STEP_STATUS(2, stage*1000+105, 0));
			}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sendWave(getNpcsFromDB(mapId, tmpStage-1, 10, registeredGroup.getGroupLeader().getCommonData().getRace()));
			}
		}, 2000);
	}
	private void sendStageStart()
	{
		final int tmpStage = currentStage;
		
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sendWave(getNpcsFromDB(mapId, tmpStage, 0, registeredGroup.getGroupLeader().getCommonData().getRace()));
			}
		}, 2000);
	}

	/**
	 * @param i
	 * @param j
	 * @param k
	 */
	private void sendMsg2group(int param, int stage, int round, int type)
	{
		for(Player player : registeredGroup.getMembers()) {
			if(round == 6)
				PacketSendUtility.sendPacket(player, new SM_STAGE_STEP_STATUS(2, code[stage-1]+6, type)); 
			else
				PacketSendUtility.sendPacket(player, new SM_STAGE_STEP_STATUS(2, code[stage-1]+round, type)); 
		}
	}
	private void sendWave(List<MonsterSpawnNode> spawns)
	{  		
		monsterSpawns.clear();
		if(spawns == null){
			return;
		}
		for(MonsterSpawnNode node : spawns)
		{
			if(node.flag < 2){
				VisibleObject ob = InstanceService.addNewSpawn(300300000, registeredGroup.getGroupLeader().getInstanceId(), node.npcId, node.x, node.y, node.z, node.h, true);
				if(node.flag == 0)
					monsterSpawns.add(ob);
			}
		}
	}
	/**
	 * @param owner
	 * @param group
	 */
	public void onGroupReward(Monster creature, PlayerGroup group)
	{
		sendScore2Group(creature);
		currentRoundNpcCount++;
		if(monsterSpawns.size() > currentRoundNpcCount){
			return;
		}
		currentRound++;
		currentRoundNpcCount = 0;
		if(currentRound>5)
		{ 			
			currentRound=1; 
			currentStage++;
			sendStageComplete(currentStage-1);
		return;
		}

		sendWaves();
	}
	private void sendScore2Group(Monster monster)
	{
		int instanceTime = (int) ((registeredGroup.getInstanceStartTime() + 14400000) - System.currentTimeMillis());
		int pointsReward = rewardPoints[currentStage-1];
		int grandTotal = registeredGroup.getGroupInstancePoints() + pointsReward;
		registeredGroup.setGroupInstancePoints(grandTotal);
		for(Player player : registeredGroup.getMembers())
			if(player.getWorldId() == mapId){
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(mapId, instanceTime, registeredGroup, grandTotal, 0, false));
		}
	}
	private void sendScore2Group()
	{
		int instanceTime = (int) ((registeredGroup.getInstanceStartTime() + 14400000) - System.currentTimeMillis());
		int grandTotal = registeredGroup.getGroupInstancePoints();
		for(Player player : registeredGroup.getMembers()){
			if(player.getWorldId() == mapId){
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(mapId, instanceTime, registeredGroup, grandTotal, 0, false));
				if(!player.getLifeStats().isAlreadyDead()){
				player.getLifeStats().increaseHp(TYPE.HP, player.getLifeStats().getMaxHp() + 1);
				player.getLifeStats().increaseMp(TYPE.MP, player.getLifeStats().getMaxMp() + 1);
				}
			}
		}
	}
	/**
	 * @param monster
	 * @param groupLeader
	 * @return
	 */
	public void doReward()
	{	
		if(hasReward)
			return;
		hasReward = true;	
		int instanceTime = (int) ((registeredGroup.getInstanceStartTime() + 14400000) - System.currentTimeMillis());
		int grandTotal = registeredGroup.getGroupInstancePoints();
		int rewardCount = grandTotal / (registeredGroup.size()) / 10;
		for(Player player : registeredGroup.getMembers()){
			if(player.getWorldId() == mapId){
				ItemService.addItem(player, 186000130, rewardCount);
					if (player.getCommonData().getRace().getRaceId() == 0)
						itemId = 186000124;
					else itemId = 186000125;
				//PacketSendUtility.sendMessage(player, "You received "+rewardCount+" arena signs from total"+(grandTotal/68));	
				player.getInventory().removeFromBagByItemId(itemId, player.getInventory().getItemCountByItemId(itemId));
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(mapId, instanceTime, registeredGroup, grandTotal, rewardCount, true));
			}
		}
	}

	/**
	 * 
	 */
	public void doReward(Player player)
	{
		int instanceTime = (int) ((registeredGroup.getInstanceStartTime() + 14400000) - System.currentTimeMillis());
		int grandTotal = registeredGroup.getGroupInstancePoints();
		int rewardCount = (grandTotal / registeredGroup.size()); 
			if(player.getWorldId() == mapId)
			{
				ItemService.addItem(player, 186000130, rewardCount);
				//PacketSendUtility.sendMessage(player, "You received "+rewardCount+" arena points from total  "+grandTotal);
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(mapId, instanceTime, registeredGroup, grandTotal, rewardCount, true));
	        }
	}
	
	public void groupUnreg()
	{
		registeredGroup.setEmpyreanCrucible(null);
		registeredGroup = null;
	}

	public static boolean isInEmpyreanCrucible(int worldId)
	{
		if(worldId == mapId)
			return true;

		return false;
	}
	/**
	 * @param player
	 * @param lastAttacker
	 */
	public void onDie(final Player player, Creature lastAttacker)
	{
		player.getEffectController().removeAllEffects();
		player.getController().cancelCurrentSkill();
		registeredPlayers.put(player, 1);

		Summon summon = player.getSummon();
		if(summon != null)
			summon.getController().release(UnsummonType.UNSPECIFIED);

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
		player.getObserveController().notifyDeath(player);

		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				player.getReviveController().skillRevive(false);
				TeleportService.teleportTo(player, mapId, player.getInstanceId(), dieX[currentStage-1], dieY[currentStage-1], dieZ[currentStage-1], dieH[currentStage-1]);
				checkInstanceEnd();
			}
		}, 5000);
	}

	public void returnStage(final Player player)
	{
			if(player.getWorldId() == mapId){
				TeleportService.teleportTo(player, 300300000, player.getInstanceId(), chsX[currentStage-1], chsY[currentStage-1], chsZ[currentStage-1], 0);
			}			
	}

	/**
	 * used to see if all players are dead and doReward can be called
	 */
	private void checkInstanceEnd()
	{
		for(java.util.Map.Entry<Player, Integer> entrySet : registeredPlayers.entrySet()){
			if(entrySet.getValue() < 1)
				if(entrySet.getKey().isOnline() && entrySet.getKey().getWorldId() == mapId)//only consider player as active if player is still online and not in ready room
			return;																	//TODO: When we implement chests, need to change this because they can return!!
		}
		doReward();
	}	

	/**
	 * @param player
	 */
	public void changeStage()
	{
		for(Player player : registeredGroup.getMembers()){
			if(player.getLifeStats().isAlreadyDead()){
				player.getReviveController().skillRevive(false);
			}
			if(player.getWorldId() == mapId){
				if(currentStage > 4)
				{TeleportService.teleportTo(player, 300300000, player.getInstanceId(), chsX[currentStage-1], chsY[currentStage-1], chsZ[currentStage-1], 0);}

				PacketSendUtility.sendPacket(player, new SM_STAGE_STEP_STATUS(2, code[currentStage-1], typeArray[currentStage-1]));
			}
		}
		sendStageStart();
 	}
 		public void changeStart()
 	{
 			sendWaves();
 	}
 	
	
	public void exitArena(Player paramPlayer)
  {
    PacketSendUtility.sendPacket(paramPlayer, new SM_QUEST_ACCEPTED(4, 0, 0));
    int i = Rnd.get(1, 2);
    int j = Rnd.get(1, 8);
    if (paramPlayer.getWorldId() == 300300000)
      if (paramPlayer.getCommonData().getRace() == Race.ASMODIANS)
        TeleportService.teleportTo(paramPlayer, 120080000,  571 + i, 260 + j, 93.480003356933594F, 2200);
      else
        TeleportService.teleportTo(paramPlayer, 110070000,  510 + i, 230 + j, 126.97586822509766F, 2200);
    PlayerGroup localPlayerGroup = paramPlayer.getPlayerGroup();
    Iterator localIterator = localPlayerGroup.getMembers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (!(localPlayer.equals(paramPlayer)))
        PacketSendUtility.sendPacket(localPlayer, SM_SYSTEM_MESSAGE.STR_MSG_FRIENDLY_LEAVE_IDARENA(paramPlayer.getName()));
    }
    paramPlayer.setInstancePlayerScore(0);
  }

	
 	/**
	 * 
	 * @param worldId
	 * @param stage
	 * @param round
	 * @param playerRace
	 * @return
 	 */
 	private List<MonsterSpawnNode> getNpcsFromDB(int worldId, int stage, int round, Race playerRace)
 	{
		Connection con = null;
		List<MonsterSpawnNode> tmpList = new ArrayList<EmpyreanCrucibleService.MonsterSpawnNode>();
		boolean result = false;
		final String LIST_SPAWNS_QUERY = "SELECT * FROM `special_spawns` WHERE `world_id` = ? AND `stage` = ? AND `round` = ? AND (`race` = ? OR `race` = 'ALL')";

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LIST_SPAWNS_QUERY);
			
			stmt.setInt(1, mapId);
			stmt.setInt(2, stage);
			stmt.setInt(3, round);
			stmt.setString(4, playerRace.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				tmpList.add( new MonsterSpawnNode(rs.getInt("npc_id"), rs.getInt("flag"), rs.getFloat("x"), rs.getFloat("y"), rs.getFloat("z"), rs.getByte("h")));
			}
			stmt.close();
			con.close();
			result = true;
		}
		catch(Exception e)
		{
			Logger.getLogger(EmpyreanCrucibleService.class).error("[ERROR]: Error loading spawns for stage: "+stage+". round: "+round+". race: "+playerRace.toString());
		}finally
		{
			DatabaseFactory.close(con);
		}
		if(result)
			return tmpList;
		else
			return null;
	}
}