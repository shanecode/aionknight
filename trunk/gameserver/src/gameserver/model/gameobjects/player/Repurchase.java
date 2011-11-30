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

package gameserver.model.gameobjects.player;

import gameserver.model.gameobjects.Item;

import java.util.LinkedHashMap;

public class Repurchase
{
	private LinkedHashMap<Integer, Item> items;

	public Repurchase()
	{
	    items = new LinkedHashMap<Integer, Item>();
	}

	public void addItem(int itemObjId, Item item)
	{
	    items.put(itemObjId, item);
	}

	public void removeItem(int itemObjId)
	{
		if(items.containsKey(itemObjId))
		{
			LinkedHashMap<Integer, Item> newItems = new LinkedHashMap<Integer, Item>();
			for(int itemObjIds : items.keySet())
			{
				if(itemObjId != itemObjIds)
					newItems.put(itemObjIds, items.get(itemObjIds));
			}
			this.items = newItems;
		}
	}

	public Item getItem(int itemObjId)
	{
		if(items.containsKey(itemObjId))
			return items.get(itemObjId);
		
		return null;
	}

	public LinkedHashMap<Integer, Item> getRepurchaseItems()
	{
		return items;
	}
}