/**   
 * �������� �������� ������� Aion 2.7 �� ������� ������������� 'Aion-Knight Dev. Team' �������� 
 * ��������� ����������� ������������; �� ������ �������������� �/��� �������� ��� �������� �������� 
 * ����������� ������������ �������� GNU (GNU GPL), �������������� ������ ���������� ������������ 
 * ����������� (FSF), ���� �������� ������ 3, ���� (�� ���� ����������) ����� ����� ������� 
 * ������.
 * 
 * ��������� ���������������� � �������, ��� ��� ����� ��������, �� ��� ����� �� �� �� ���� 
 * ����������� ������������; ���� ��� ���������  �����������  ������������, ��������� � 
 * ���������������� ���������� � ������������ ��� ������������ �����. ��� ������������ �������� 
 * ����������� ������������ �������� GNU.
 * 
 * �� ������ ���� �������� ����� ����������� ������������ �������� GNU ������ � ���� ����������. 
 * ���� ��� �� ���, �������� � ���� ���������� �� (Free Software Foundation, Inc., 675 Mass Ave, 
 * Cambridge, MA 02139, USA
 * 
 * ���-c��� ������������� : http://aion-knight.ru
 * ��������� ������� ���� : Aion 2.7 - '����� ������' (������) 
 * ������ ��������� ����� : Aion-Knight 2.7 (Beta version)
 */

package gameserver.ai.desires.impl;

import gameserver.ai.AI;
import gameserver.ai.desires.AbstractDesire;
import gameserver.ai.state.AIState;
import gameserver.configs.main.CustomConfig;
import gameserver.controllers.attack.AttackResult;
import gameserver.controllers.attack.AttackStatus;
import gameserver.geo.GeoEngine;
import gameserver.model.ShoutEventType;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.siege.ArtifactProtector;
import gameserver.model.siege.FortressGate;
import gameserver.model.siege.FortressGeneral;
import gameserver.network.aion.serverpackets.SM_ATTACK;
import gameserver.services.NpcShoutsService;
import gameserver.skill.SkillEngine;
import gameserver.skill.model.Skill;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import java.util.Collections;

public final class AggressionDesire extends AbstractDesire
{
	protected  Npc	npc;
	
	public AggressionDesire(Npc npc, int desirePower)
	{
		super(desirePower);
		this.npc = npc;
	}
	
	@Override
	public boolean handleDesire(AI<?> ai)
	{
		if(npc == null)
			return false;
		
		if(npc instanceof FortressGate || npc instanceof Kisk)
			return false;
		
		npc.getKnownList().doOnAllObjects(new Executor<AionObject>(){
			@Override
			public boolean run(AionObject visibleObject)
			{
				if (visibleObject == null)
					return true;
				
				// NPCs wont aggro each other if NPC_RELATION_AGGRO is false
				if(!CustomConfig.NPC_RELATION_AGGRO && !(visibleObject instanceof Player))
					return true;
					
				if (visibleObject instanceof Creature)
				{
					final Creature creature = (Creature) visibleObject;
					
					if(creature.getLifeStats()==null || creature.getLifeStats().isAlreadyDead())
						return true;
					
					// Hack for FortressGenerals aggro
					if(npc instanceof FortressGeneral || npc instanceof ArtifactProtector)
					{
						if(creature instanceof Player)
						{
							Player p = (Player)creature;
							if(p.getCommonData().getRace() == npc.getObjectTemplate().getRace())
								return true;
						}
					}
					
					if(!npc.canSee(creature))
						return true;

					if(!npc.isAggressiveTo(creature))
						return true;

					if(!MathUtil.isIn3dRange(npc, creature, npc.getAggroRange()) || !GeoEngine.getInstance().canSee(npc, creature))
						return true;
					
					if(npc.hasWalkRoutes())
					{
						npc.getMoveController().stop();
						npc.getController().stopMoving();
					}
					
					if(npc instanceof FortressGeneral)
					{
						int pullskill = 17195; //wide area pull
						Skill caster = SkillEngine.getInstance().getSkill(npc, pullskill, 1, creature);
						caster.useSkill();
					}

					npc.getAi().setAiState(AIState.NONE); // TODO: proper aggro emotion on aggro range enter
					PacketSendUtility.broadcastPacket(npc, new SM_ATTACK(npc, creature, 0,
						633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));
					NpcShoutsService.getInstance().handleEvent(npc, creature, ShoutEventType.START);
					ThreadPoolManager.getInstance().schedule(new Runnable(){
						@Override
						public void run()
						{
							npc.getAggroList().addHate(creature, 1);
						}
					}, 1000);
					return false;
				
				}
				return true;
			}
		}, true);

		return true;
	}

	@Override
	public int getExecutionInterval()
	{
		return 2;
	}

	@Override
	public void onClear()
	{
		// TODO Auto-generated method stub
		
	}
}
