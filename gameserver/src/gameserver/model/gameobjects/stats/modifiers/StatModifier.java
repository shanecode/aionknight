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

package gameserver.model.gameobjects.stats.modifiers;

import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.model.gameobjects.stats.StatModifierPriority;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Modifier")
public abstract class StatModifier implements Comparable<StatModifier>
{
	@XmlAttribute
	private StatEnum name;
	
	@XmlAttribute
	private boolean bonus;
	
	protected static int MODIFIER_ID = 0;
	
	protected int id;
	
	public StatModifier()
	{
		nextId();
	}
	
	protected void setStat(StatEnum stat)
	{
		this.name = stat;
	}
	
	protected void setBonus(boolean bonus)
	{
		this.bonus = bonus;
	}
	
	protected void nextId()
	{
		MODIFIER_ID = (MODIFIER_ID + 1) % Integer.MAX_VALUE;
		id = MODIFIER_ID;
	}
	
	public StatEnum getStat ()
	{
		return name;
	}
	
	public boolean isBonus ()
	{
		return bonus;
	}
	
	@Override
	public int compareTo(StatModifier o)
	{
		int result = getPriority().getValue() - o.getPriority().getValue();
		if (result==0)
		{
			result = id - o.id;
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean result = (o!=null);
		result = (result)&&(o instanceof StatModifier);
		result = (result)&&(((StatModifier)o).id==id);
		return result;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(',');
		sb.append("i:"+id+ ',');
		sb.append("s:"+name+ ',');
		sb.append("b:"+bonus);
		return sb.toString();
	}
	
	public abstract int apply (int baseValue, int currentValue);
	
	public abstract StatModifierPriority getPriority();
}
