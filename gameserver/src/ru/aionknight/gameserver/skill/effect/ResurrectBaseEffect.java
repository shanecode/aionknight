/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.aionknight.gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


import ru.aionknight.gameserver.controllers.movement.ActionObserver;
import ru.aionknight.gameserver.controllers.movement.ActionObserver.ObserverType;
import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.skill.model.Effect;
import ru.aionknight.gameserver.world.World;


/**
 * @author blakawk
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectBaseEffect")
public class ResurrectBaseEffect extends BufEffect
{
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect);
	}

	@Override
	public void applyEffect(final Effect effect)
	{
		super.applyEffect(effect);
		
		Creature effected = effect.getEffected();
		
		if (effected instanceof Player)
		{
			ActionObserver observer = new ActionObserver (ObserverType.DEATH) {
				@Override
				public void died(Creature creature)
				{
					if(creature instanceof Player)
					{
		if(creature.getWorldId() == 300200000)
		return;
		else if(creature.getWorldId() == 310100000)
		return;
		else if(creature.getWorldId() == 300030000)
		return;
						((Player) creature).getReviveController().kiskRevive();
					}
				}
			};
			effect.getEffected().getObserveController().attach(observer);
			effect.setActionObserver(observer, position);
		}
	}
	
	@Override
	public void endEffect(Effect effect)
	{
		super.endEffect(effect);
		
		if (!effect.getEffected().getLifeStats().isAlreadyDead() && effect.getActionObserver(position) != null)
		{
			effect.getEffected().getObserveController().removeDeathObserver(effect.getActionObserver(position));
		}
	}
}
