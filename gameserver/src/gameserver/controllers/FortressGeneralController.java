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
package gameserver.controllers;


import gameserver.controllers.attack.AttackStatus;
import gameserver.model.gameobjects.*;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.siege.FortressGeneral;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.services.SiegeService;

/**
 * @author Sylar
 *
 */
public class FortressGeneralController extends NpcController
{

	@Override
	public void doReward()
	{
		super.doReward();

		/**Creature master = creature.getMaster();
		if(master instanceof Player)
		{
			Player player = (Player) master;
			
			if(player.getPlayerGroup() == null) //solo
			{
				// Exp reward
				long expReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());
				player.getCommonData().addExp(expReward);

				// DP reward
				int currentDp = player.getCommonData().getDp();
				int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
				player.getCommonData().setDp(dpReward + currentDp);
				
				// AP reward
				WorldType worldType = sp.getWorld().getWorldMap(player.getWorldId()).getWorldType();
				if(worldType == WorldType.ABYSS)
				{
					int apReward = StatFunctions.calculateSoloAPReward(player, getOwner());
					player.getCommonData().addAp(apReward);
				}
				
				sp.getQuestEngine().onKill(new QuestEnv(getOwner(), player, 0 , 0));
			}
			else
			{
				sp.getGroupService().doReward(player, getOwner());
			}
		}*/
	}

	@Override
	public void onRespawn()
	{
		super.onRespawn();
	}

	@Override
	public void onDie(Creature lastAttacker)
	{
		super.onDie(lastAttacker);
		if(lastAttacker instanceof Player || lastAttacker instanceof Summon || lastAttacker instanceof Trap 
				|| lastAttacker instanceof Homing || lastAttacker instanceof Servant || lastAttacker instanceof SkillAreaNpc)
		{
			SiegeService.getInstance().onFortressCaptured(getOwner(), lastAttacker, getOwner().getRewardGroups(), getOwner().getRewardAlliances());
		}
		else
		{
			// Taken by Balaur
			if(lastAttacker != null)
				SiegeService.getInstance().onFortressCaptured(getOwner(), lastAttacker);
		}
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, int logId, AttackStatus status, boolean notifyAttackedObservers, boolean sendPacket)
	{
		super.onAttack(creature, skillId, type, damage, logId, status, notifyAttackedObservers, sendPacket);
		if(creature instanceof Player || creature instanceof Summon || creature instanceof Trap)
		{
			Player sender;
			if(creature instanceof Player)
				sender = (Player)creature;
			else if(creature instanceof Summon)
				sender = ((Summon)creature).getMaster();
			else if(creature instanceof Trap)
				sender = (Player)((Trap)creature).getCreator();
			else
				return;
			
			if(!sender.isEnemyNpc(getOwner()))
				return;
			
			if(sender.isInAlliance())
			{
				getOwner().registerAllianceGroup(sender.getPlayerAlliance().getPlayerAllianceGroupForMember(sender.getObjectId()));
			}
			else if(sender.isInGroup())
			{
				getOwner().registerGroup(sender.getPlayerGroup());
			}
		}
	}
	
	@Override
	public void onStartMove()
	{
		super.onStartMove();
	}
	
	@Override
	public void onMove()
	{
		super.onMove();
	}
	
	@Override
	public void onStopMove()
	{
		super.onStopMove();
	}

	@Override
	public FortressGeneral getOwner()
	{
		return (FortressGeneral) super.getOwner();
	}
}
