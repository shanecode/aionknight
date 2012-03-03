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

package loginserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import loginserver.network.aion.AionAuthResponse;
import loginserver.network.aion.AionConnection;
import loginserver.network.aion.AionServerPacket;

public class SM_PLAY_FAIL extends AionServerPacket
{
    /**
     * response - why play fail
     */
    private final AionAuthResponse response;

    /**
     * Constructs new instance of <tt>SM_PLAY_FAIL</tt> packet.
     *
     * @param response auth response
     */
    public SM_PLAY_FAIL(AionAuthResponse response)
    {
        super(0x06);

        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeC(buf, getOpcode());
        writeD(buf, response.getMessageId());
    }
}