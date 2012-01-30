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

package gameserver.network.aion.serverpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import java.nio.ByteBuffer;
import java.util.Map;

public class SM_MACRO_LIST extends AionServerPacket
{
	private Player	player;

	/**
	 * Constructs new <tt>SM_MACRO_LIST </tt> packet
	 */
	public SM_MACRO_LIST(Player player)
	{
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());// player oid
		writeC(buf, 0x01);// unk

		final int size = player.getMacroList().getSize();

		writeH(buf, -size);// (-)count

		if(size > 0)
		{
			for(Map.Entry<Integer, String> entry : player.getMacroList().entrySet())
			{
				writeC(buf, entry.getKey());// order
				writeS(buf, entry.getValue());// xml
			}
		}
	}
}
