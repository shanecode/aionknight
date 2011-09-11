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
package org.openaion.gameserver.model.gameobjects.stats.id;

import org.openaion.gameserver.model.gameobjects.stats.StatEffectType;

/**
 * @author ATracer
 *
 */
public class FusionStatEffectId extends StatEffectId
{

	private int slot;

	private FusionStatEffectId(int id, int slot)
	{
		super(id, StatEffectType.STONE_EFFECT);
		this.slot = slot;
	}
	
	/**
	 * 
	 * @param id
	 * @param slot
	 * @return FusionStatEffectId
	 */
	public static FusionStatEffectId getInstance (int id, int slot)
	{
		return new FusionStatEffectId(id, slot);
	}

	@Override
	public boolean equals(Object o)
	{
		boolean result = super.equals(o);
		result = (result)&&(o != null);
		result = (result)&&(o instanceof FusionStatEffectId);
		result = (result)&&(((FusionStatEffectId) o).slot == slot);
		return result;
	}

	@Override
	public int compareTo(StatEffectId o)
	{
		int result = super.compareTo(o);
		if (result == 0)
		{
			if (o instanceof FusionStatEffectId)
			{
				result = slot - ((FusionStatEffectId) o).slot;
			}
		}
		return result;
	}

	@Override
	public String toString()
	{
		final String str = super.toString() + ",slot:" + slot;
		return str;
	}

	public int getSlot()
	{
		return slot;
	}
}
