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

package gameserver.model;

public enum NpcType
{
	ATTACKABLE(0), NEUTRAL(2), AGGRESSIVE(8), NON_ATTACKABLE(38), RESURRECT(38), 
	POSTBOX(38), USEITEM(38), PORTAL(38), CHEST(38), ARTIFACT(38), ARTIFACT_PROTECTOR(38);
	
	private int someClientSideId;
	
	private NpcType(int id)
	{
		this.someClientSideId = id;
	}
	
	public int getId()
	{
		return someClientSideId;
	}
}