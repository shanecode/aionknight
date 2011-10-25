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
import javax.xml.bind.annotation.XmlType;


import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.services.TeleportService;
import ru.aionknight.gameserver.skill.model.Effect;
import ru.aionknight.gameserver.world.World;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnEffect")
public class ReturnEffect extends EffectTemplate
{

	@Override
	public void applyEffect(Effect effect)
	{	
if(effect.getEffected() instanceof Player){
		if(effect.getEffected().getWorldId() == 300200000)
			return;
		else if(effect.getEffected().getWorldId() == 310100000)
		return;
		else if(effect.getEffected().getWorldId() == 300030000)
		return;
     }
		TeleportService.moveToBindLocation((Player) effect.getEffected(), true, 500);
	}

	@Override
	public void calculate(Effect effect)
	{
		if(effect.getEffected().isSpawned())
			super.calculate(effect);
	}
}
