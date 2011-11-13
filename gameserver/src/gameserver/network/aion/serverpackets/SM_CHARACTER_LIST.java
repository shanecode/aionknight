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

package gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import gameserver.model.account.Account;
import gameserver.model.account.PlayerAccountData;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.PlayerInfo;

public class SM_CHARACTER_LIST extends PlayerInfo
{
	private final int	playOk2;
	public SM_CHARACTER_LIST(int playOk2)
	{
		this.playOk2 = playOk2;
	}
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, playOk2);

		Account account = con.getAccount();
		writeC(buf, account.size());

		for(PlayerAccountData playerData : account.getSortedAccountsList())
		{
			writePlayerInfo(buf, playerData);

			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeC(buf, 0);
			writeC(buf, 0);
			writeB(buf, new byte[28]);
		}
	}
}