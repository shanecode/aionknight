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

import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_MACRO_RESULT;
import gameserver.services.PlayerService;
import org.apache.log4j.Logger;


/**
 * Packet that is responsible for macro deletion.<br>
 * Client sends id in the macro list.<br>
 * For instance client has 4 macros and we are going to delete macro #3.<br>
 * Client sends request to delete macro #3.<br>
 * And macro #4 becomes macro #3.<br>
 * So we have to use a list to store macros properly.
 */
public class CM_MACRO_DELETE extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_MACRO_DELETE.class);

	/**
	 * Macro id that has to be deleted
	 */
	private int					macroPosition;
	
	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_MACRO_DELETE(int opcode)
	{
		super(opcode);
	}

	/**
	 * Reading macro id
	 */
	@Override
	protected void readImpl()
	{
		macroPosition = readC();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl()
	{
		log.debug("Request to delete macro #" + macroPosition);

		PlayerService.removeMacro(getConnection().getActivePlayer(), macroPosition);
		
		sendPacket(SM_MACRO_RESULT.SM_MACRO_DELETED);
	}
}