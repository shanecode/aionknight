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
package ru.aionknight.gameserver.skill.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import ru.aionknight.gameserver.geo.GeoEngine;
import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.Summon;
import ru.aionknight.gameserver.model.gameobjects.Servant;
import ru.aionknight.gameserver.model.gameobjects.Trap;
import ru.aionknight.gameserver.model.gameobjects.NpcWithCreator;
import ru.aionknight.gameserver.model.gameobjects.stats.StatEnum;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import ru.aionknight.gameserver.skill.model.Skill;
import ru.aionknight.gameserver.utils.MathUtil;
import ru.aionknight.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirstTargetRangeProperty")
public class FirstTargetRangeProperty extends Property
{
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(FirstTargetRangeProperty.class);

	@XmlAttribute(required = true)
	protected int value;

	@Override
	public boolean set(Skill skill)
	{
		if(!skill.isFirstTargetRangeCheck())
			return true;
		
		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();
		if(firstTarget == null && skill.getTargetType() == 1)//point skill
		{
			if (MathUtil.getDistance(skill.getEffector(), skill.getX(), skill.getY(), skill.getZ()) <= value)
				return true;
			else
				return false;
		}
		else if (firstTarget == null)
			return false;

		
		if (firstTarget.getPosition().getMapId() == 0)
			log.warn("FirstTarget has mapId of 0. (" + firstTarget.getName() + ")");
		
		float distance = (float)value;
		
		//addweaponrange
		if (skill.getAddWeaponRangeProperty())
			distance += (float)skill.getEffector().getGameStats().getCurrentStat(StatEnum.ATTACK_RANGE) / 1000f;

		//tolerance
		distance += 3.0;

		//testing new firsttargetrangeproperty
		if (!MathUtil.isIn3dRange(effector, firstTarget, distance))
		{
			if (effector instanceof Player)
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_ATTACK_TOO_FAR_FROM_TARGET());
			return true;
		}

		if (!GeoEngine.getInstance().canSee(effector, firstTarget))
		{
			if ((effector instanceof Summon) || (effector instanceof Servant) || (effector instanceof Trap) || (effector instanceof NpcWithCreator))
                {
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE);
			return true;
			}
		}
		return true;
	}
	
	public int getValue()
	{
		return value;
	}

}