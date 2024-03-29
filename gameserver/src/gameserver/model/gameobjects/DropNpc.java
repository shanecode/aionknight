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

package gameserver.model.gameobjects;

import gameserver.model.drop.DropItem;
import gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DropNpc
{
	private List<Integer>			allowedList			= new ArrayList<Integer>();
	private List<Player>			inRangePlayers		= new ArrayList<Player>();
	private Player					lootingPlayer		= null;
	private int						groupSize			= 0;
	private int						npcId				= 0;
	private LinkedList<DropItem>	specialItemList		= null;
	private DropNpc					nextSpecialDropNpc	= null;

	public DropNpc(List<Integer> allowedList, int npcId)
	{
		this.allowedList = allowedList;
		this.npcId = npcId;
	}

	/**
	 * Everyone is allowed to loot
	 */
	public void setFreeLooting()
	{
		allowedList = null;
	}

	/**
	 * @return true if playerObjId is found in list
	 */
	public boolean containsKey(int playerObjId)
	{
		if(allowedList == null)
			return true;
		return allowedList.contains(playerObjId);
	}

	/**
	 * @param player
	 *           the lootingPlayer to set
	 */
	public void setBeingLooted(Player player)
	{
		this.lootingPlayer = player;
	}

	/**
	 * @return lootingPlayer
	 */
	public Player getBeingLooted()
	{
		return lootingPlayer;
	}	
	
	/**
	 * @return the beingLooted
	 */
	public boolean isBeingLooted()
	{
		return lootingPlayer != null;
	}

	/**
	 * @param groupSize
	 */
	public void setGroupSize(int groupSize)
	{
		this.groupSize = groupSize;
	}

	/**
	 * @return groupSize
	 */
	public int getGroupSize()
	{
		return groupSize;
	}

	/**
	 * @param inRangePlayers
	 */
	public void setInRangePlayers(List<Player> inRangePlayers)
	{
		this.inRangePlayers = inRangePlayers;
	}

	/**
	 * @return the inRangePlayers
	 */
	public List<Player> getInRangePlayers()
	{
		return inRangePlayers;
	}

	/**
	 * Adds an item to the list of items that should be rolled/bid on.
	 * 
	 * @param item
	 * @return True if the item is added to the list of special drops.
	 */
	public boolean addSpecialItem(DropItem item)
	{
		if(item.isRegisteredSpecial())
			return false;
		item.setRegisteredSpecial();

		if(specialItemList == null)
		{
			specialItemList = new LinkedList<DropItem>();
		}
		specialItemList.add(item);
		return true;
	}

	/**
	 * @return True if there are items to roll/bid on.
	 */
	public boolean hasSpecialItems()
	{
		if(specialItemList == null)
		{
			return false;
		}
		return !specialItemList.isEmpty();
	}

	/**
	 * Delete all the special items from the special items list.
	 */
	public void resetSpecialItems()
	{
		if(specialItemList != null)
		{
			specialItemList.clear();
		}
		specialItemList = null;
	}

	/**
	 * @return The next item to roll/bid on, or null if there is no item.
	 */
	public DropItem getNextSpecialItem()
	{
		if(specialItemList == null)
		{
			return null;
		}
		return specialItemList.poll();
	}

	/**
	 * Add the next DropNpc object that has special items for the group/alliance.
	 * 
	 * @param dropNpc
	 */
	public void addSpecialDropNpc(DropNpc dropNpc)
	{
		if(npcId == dropNpc.npcId)
		{
			return;
		}
		
		if(nextSpecialDropNpc != null)
		{
			if (nextSpecialDropNpc.npcId != dropNpc.npcId)
			{
				nextSpecialDropNpc.addSpecialDropNpc(dropNpc);
			}
			
			return;
		}
		
		nextSpecialDropNpc = dropNpc;
	}

	/**
	 * @return The next DropNpc object with special items that have to be rolled/bid on, or null if there is no next npc.
	 */
	public DropNpc getNextSpecialDropNpc()
	{
		return nextSpecialDropNpc;
	}

	/**
	 * @return The id of the Npc this drop is linked to.
	 */
	public int getNpcId()
	{
		return npcId;
	}
}
