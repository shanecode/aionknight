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
package gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.skill.model.Effect;
import gameserver.skill.model.HealType;



/**

 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemHealMpEffect")
public class ItemHealMpEffect
extends AbstractHealEffect
{

	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect,HealType.MP);
	}

	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect,HealType.MP,false);
	}
	
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		return ((Player) effect.getEffected()).getLifeStats().getCurrentMp();
	}
	@Override
	protected int getMaxCurStatValue(Effect effect)
	{
		return effect.getEffected().getGameStats().getCurrentStat(StatEnum.MAXMP);
	}
}
