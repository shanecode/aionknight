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

import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.GroupConfig;
import gameserver.controllers.attack.AggroInfo;
import gameserver.controllers.attack.KillList;
import gameserver.model.alliance.PlayerAlliance;
import gameserver.model.alliance.PlayerAllianceMember;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.group.PlayerGroup;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.stats.AbyssRankEnum;
import gameserver.utils.stats.StatFunctions;
import javolution.util.FastMap;
import java.util.ArrayList;
import java.util.List;

public class PvpService
{
	public static boolean pvpZoneReward = true;
	public static boolean getPvpZoneReward()
	{
		return pvpZoneReward;
	}
	public static void setPvpZoneReward(boolean reward)
	{
		pvpZoneReward = reward;
	}
	public static final PvpService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private FastMap<Integer, KillList> pvpKillLists;
	
	private PvpService()
	{
		pvpKillLists = new FastMap<Integer, KillList>();
	}
	
	/**
	 * @param winnerId
	 * @param victimId
	 * @return
	 */
	private int getKillsFor(int winnerId, int victimId)
	{
		KillList winnerKillList = pvpKillLists.get(winnerId);
		
		if (winnerKillList == null)
			return 0;
		return winnerKillList.getKillsFor(victimId);
	}

	/**
	 * @param winnerId
	 * @param victimId
	 */
	private void addKillFor(int winnerId, int victimId)
	{
		KillList winnerKillList = pvpKillLists.get(winnerId);
		if (winnerKillList == null)
		{
			winnerKillList = new KillList();
			pvpKillLists.put(winnerId, winnerKillList);
		}
		winnerKillList.addKillFor(victimId);
	}
	
	/**
	 * @param victim
	 */
	public void doReward(Player victim)
	{
		// winner is the player that receives the kill count
		final Player winner = victim.getAggroList().getMostPlayerDamage();

		int totalDamage = victim.getAggroList().getTotalDamage();

		if (totalDamage == 0 || winner == null)
		{
			return;
		}

		// Check PVP Reward is Enabled
		if(CustomConfig.PVPREWARD_ENABLE)
		{
			int kills = (winner.getAbyssRank().getAllKill() + 1);

			if (kills % CustomConfig.PVPREWARD_KILLS_NEEDED1 == 0)
			{
				ItemService.addItem(winner, CustomConfig.PVPREWARD_ITEM_REWARD1, 1);
				PacketSendUtility.sendMessage(winner, "Congratulations, you get a " + "[item: " + CustomConfig.PVPREWARD_ITEM_REWARD1 + "] for " + CustomConfig.PVPREWARD_KILLS_NEEDED1 + " new pvp kills");
			}
			if (kills % CustomConfig.PVPREWARD_KILLS_NEEDED2 == 0)
			{
				ItemService.addItem(winner, CustomConfig.PVPREWARD_ITEM_REWARD2, 1);
				PacketSendUtility.sendMessage(winner, "Congratulations, you get a " + "[item: " + CustomConfig.PVPREWARD_ITEM_REWARD2 + "] for " + CustomConfig.PVPREWARD_KILLS_NEEDED2 + " new pvp kills");
			}
			if (kills % CustomConfig.PVPREWARD_KILLS_NEEDED3 == 0)
			{
				ItemService.addItem(winner, CustomConfig.PVPREWARD_ITEM_REWARD3, 1);
				PacketSendUtility.sendMessage(winner, "Congratulations, you get a " + "[item: " + CustomConfig.PVPREWARD_ITEM_REWARD3 + "] for " + CustomConfig.PVPREWARD_KILLS_NEEDED3 + " new pvp kills");
			}
		}

		PacketSendUtility.sendMessage(victim, Integer.toString(victim.getWorldId()));
		PacketSendUtility.sendMessage(victim, Boolean.toString(pvpZoneReward));
		//Don't count kill & AP if its disabled
		if (victim.getWorldId() == 300100000 && !pvpZoneReward)
			return;
		// Add Player Kill to record.
		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
			winner.getAbyssRank().setAllKill();
		
		// Announce that player has died.
		PacketSendUtility.broadcastPacketAndReceive(victim,
			SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));
		
		// Keep track of how much damage was dealt by players
		// so we can remove AP based on player damage...
		int playerDamage = 0;
		boolean success = false;
		
		// Distribute AP to groups and players that had damage.
		for(AggroInfo aggro : victim.getAggroList().getFinalDamageList(true))
		{
			if (aggro.getAttacker() instanceof Player)
			{
				success = rewardPlayer(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerGroup)
			{
				success = rewardPlayerGroup(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance)
			{
				success = rewardPlayerAlliance(victim, totalDamage, aggro);
			}
			
			// Add damage last, so we don't include damage from same race. (Duels, Arena)
			if (success)
				playerDamage += aggro.getDamage();
		}
		
		// Apply lost AP to defeated player
		final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
		final int apActuallyLost = (int)(apLost * playerDamage / totalDamage);
		
		if (apActuallyLost > 0 && !DredgionInstanceService.isDredgion(victim.getWorldId()))
			victim.getCommonData().addAp(-apActuallyLost);
			
	}

	
	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro)
	{
		//Don't count kill & AP if its disabled
		if (victim.getWorldId() == 300100000 && !pvpZoneReward)
			return false;
		// Reward Group
		PlayerGroup group = ((PlayerGroup)aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		// TODO: NPE if leader is offline? Store race in group.
		if (group.getGroupLeader().getCommonData().getRace() == victim.getCommonData().getRace())
			return false;
		
		// Find group members in range
		List<Player> players = new ArrayList<Player>();
		
		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;

		if(DredgionInstanceService.isDredgion(victim.getWorldId()))
		{
			Player winner = victim.getAggroList().getMostPlayerDamage();
			DredgionInstanceService.getInstance().doPvpReward(winner, victim);
			return true;
		}

		for(Player member : group.getMembers())
		{
			if(MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE))
			{
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead())
				{
					QuestEngine.getInstance().onKill(new QuestCookie(victim, member, 0 , 0));
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}
		
		// They are all dead or out of range.
		if (players.isEmpty())
			return false;

		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		float groupApPercentage = (float)aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupApPercentage / players.size());
		
		if (apRewardPerMember > 0)
		{
			for(Player member : players)
			{
				int memberApGain = 1;
				if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
					memberApGain = Math.round(apRewardPerMember * member.getRates().getApPlayerRate());
				
				member.getCommonData().addAp(memberApGain);
				this.addKillFor(member.getObjectId(), victim.getObjectId());
			}
		}
		
		return true;
	}

	
	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro)
	{
		//Don't count kill & AP if its disabled
		if (victim.getWorldId() == 300100000 && !pvpZoneReward)
			return false;
		// Reward Alliance
		PlayerAlliance alliance = ((PlayerAlliance)aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		if (alliance.getCaptain().getCommonData().getRace() == victim.getCommonData().getRace())
			return false;
		
		// Find group members in range
		List<Player> players = new ArrayList<Player>();
		
		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;
		
		for(PlayerAllianceMember allianceMember : alliance.getMembers())
		{
			if (!allianceMember.isOnline()) continue;
			Player member = allianceMember.getPlayer();
			if(MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE))
			{
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead())
				{
					QuestEngine.getInstance().onKill(new QuestCookie(victim, member, 0 , 0));
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}
		
		// They are all dead or out of range.
		if (players.isEmpty())
			return false;
		
		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		float groupApPercentage = (float)aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupApPercentage / players.size());
		
		if (apRewardPerMember > 0)
		{
			for(Player member : players)
			{
				int memberApGain = 1;
				if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
					memberApGain = Math.round(apRewardPerMember * member.getRates().getApPlayerRate());
				member.getCommonData().addAp(memberApGain);
				this.addKillFor(member.getObjectId(), victim.getObjectId());
			}
		}
		
		return true;
	}


	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if player is not same race
	 */
	private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro)
	{
		//Don't count kill & AP if its disabled
		if (victim.getWorldId() == 300100000 && !pvpZoneReward)
			return false;
		// Reward Player
		Player winner = ((Player)aggro.getAttacker());

		QuestEngine.getInstance().onKill(new QuestCookie(victim, winner, 0 , 0));

		// Don't Reward Player of Same Faction.
		if (winner.getCommonData().getRace() == victim.getCommonData().getRace())
			return false;

		int baseApReward = 1;

		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
			baseApReward = StatFunctions.calculatePvpApGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());

		int apPlayerReward = Math.round(baseApReward  * winner.getRates().getApPlayerRate() * aggro.getDamage() / totalDamage);

		if(!DredgionInstanceService.isDredgion(victim.getWorldId()))
		{
			winner.getCommonData().addAp(apPlayerReward);
			this.addKillFor(winner.getObjectId(), victim.getObjectId());
		}

		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final PvpService instance = new PvpService();
	}
}
