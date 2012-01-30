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

package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.services.LegionService;

public class CM_LEGION_MODIFY_EMBLEM extends AionClientPacket
{

	/** Emblem related information **/
	private int				legionId;
	private int				emblemVer;
	private int				red;
	private int				green;
	private int				blue;

	/**
	 * @param opcode
	 */
	public CM_LEGION_MODIFY_EMBLEM(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		legionId = readD();
		emblemVer = readH();
		readC(); // 0xFF is default; sets the transparency
		red = readC();
		green = readC();
		blue = readC();
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();

		if(activePlayer.isLegionMember())
			LegionService.getInstance().storeLegionEmblem(activePlayer, legionId, emblemVer, red, green, blue, false);
	}
}