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

package gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;


/**
 * Handler for "/loc" command
 * 
 * @author SoulKeeper
 * @author EvilSpirit
 */
public class CM_CLIENT_COMMAND_LOC extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_CLIENT_COMMAND_LOC.class);

	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_CLIENT_COMMAND_LOC(int opcode)
	{
		super(opcode);

	}

	/**
	 * Nothing to do
	 */
	@Override
	protected void readImpl()
	{
		// empty
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		log.info("Received \"/loc\" command");
		
		sendPacket(SM_SYSTEM_MESSAGE.CURRENT_LOCATION(player.getWorldId(), player.getX(), player.getY(), player.getZ()));
	}
}
