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

package gameserver.model.trade;

import gameserver.model.gameobjects.Item;

public class ExchangeItem
{
	private int itemObjId;
	private long itemCount;
	private int itemDesc;
	private Item item;

	/**
	 * Used when exchange item != original item
	 * 
	 * @param itemObjId
	 * @param itemCount
	 * @param item
	 */
	public ExchangeItem(int itemObjId, long itemCount, Item item)
	{
		this.itemObjId = itemObjId;
		this.itemCount = itemCount;
		this.item = item;
		this.itemDesc = item.getItemTemplate().getNameId();
	}
	
	/**
	 * @param item the item to set
	 */
	public void setItem(Item item)
	{
		this.item = item;
	}

	/**
	 * @param countToAdd
	 */
	public void addCount(long countToAdd)
	{
		this.itemCount += countToAdd;
		this.item.setItemCount(itemCount);
	}

	/**
	 * @return the newItem
	 */
	public Item getItem()
	{
		return item;
	}

	/**
	 * @return the itemObjId
	 */
	public int getItemObjId()
	{
		return itemObjId;
	}

	/**
	 * @return the itemCount
	 */
	public long getItemCount()
	{
		return itemCount;
	}

	/**
	 * @return the itemDesc
	 */
	public int getItemDesc()
	{
		return itemDesc;
	}
}
