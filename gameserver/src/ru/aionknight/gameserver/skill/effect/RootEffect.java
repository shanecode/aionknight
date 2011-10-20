/*
 * This file is part of aion-unique <aion-unique.com>.
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
package ru.aionknight.gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.openaion.commons.utils.Rnd;

import ru.aionknight.gameserver.controllers.movement.ActionObserver;
import ru.aionknight.gameserver.controllers.movement.ActionObserver.ObserverType;
import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.model.gameobjects.stats.StatEnum;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import ru.aionknight.gameserver.skill.model.Effect;
import ru.aionknight.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate
{
	@XmlAttribute
	protected int resistchance;
	
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, StatEnum.ROOT_RESISTANCE, null);
	}

	@Override
	public void startEffect(final Effect effect)
	{
		final Creature effected = effect.getEffected();
		effect.setAbnormal(EffectId.ROOT.getEffectId());
		effected.getEffectController().setAbnormal(EffectId.ROOT.getEffectId());
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACKED){
				@Override
				public void attacked(Creature creature)
				{
					if (Rnd.get(0, 100) > resistchance)
						effected.getEffectController().removeEffect(effect.getSkillId());
				}
			}
		);
		  
	}

	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getEffectController().unsetAbnormal(EffectId.ROOT.getEffectId());
	}
}
