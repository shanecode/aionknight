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
package gameserver.network.loginserver.clientpackets;

import gameserver.network.loginserver.LoginServer;
import gameserver.network.loginserver.LsClientPacket;

/**
 * This packet is request kicking player.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_REQUEST_KICK_ACCOUNT extends LsClientPacket
{
	/**
	 * account id of account that login server request to kick.
	 */
	private int			accountId;

	/**
	 * Constructs new instance of <tt>CM_REQUEST_KICK_ACCOUNT </tt> packet.
	 * @param opcode
	 */
	public CM_REQUEST_KICK_ACCOUNT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		accountId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		LoginServer.getInstance().kickAccount(accountId);
	}
}
