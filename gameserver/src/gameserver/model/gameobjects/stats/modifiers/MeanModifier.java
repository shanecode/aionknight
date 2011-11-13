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
package gameserver.model.gameobjects.stats.modifiers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.stats.StatModifierPriority;


/**
 * @author blakawk
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeanModifier")
public class MeanModifier extends StatModifier
{
	@XmlAttribute
	private int min;
	
	@XmlAttribute
	private int max;
	
	@Override
	public int apply(int baseStat, int currentStat)
	{
		return baseStat + Math.round((min+max)/2.0f);
	}
	
	@Override
	public StatModifierPriority getPriority()
	{
		return StatModifierPriority.HIGH;
	}
	
	@Override
	public String toString()
	{
		String s = super.toString()+",m:"+min+",M:"+max;
		return s;
	}
	
	public int getMin()
	{
		return min;
	}

	public int getMax()
	{
		return max;
	}
}