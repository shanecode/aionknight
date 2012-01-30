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

package loginserver.network.gameserver;

import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import commons.network.packet.BaseClientPacket;

/**
 * Base class for every GameServer -> LS Client Packet
 */
public abstract class GsClientPacket extends BaseClientPacket<GsConnection>
{
    /**
     * Logger for this class.
     */
    private static final Logger log = Logger.getLogger(GsClientPacket.class);

    /**
     * Creates new packet instance.
     *
     * @param buf    packet data
     * @param client client
     * @param opcode packet id
     */
    protected GsClientPacket(ByteBuffer buf, GsConnection client, int opcode)
    {
        super(buf, opcode);
        setConnection(client);
    }

    /**
     * run runImpl catching and logging Throwable.
     */
    @Override
    public final void run()
    {
        try
        {
            runImpl();
        }
        catch (Throwable e)
        {
            log.warn("error handling gs (" + getConnection().getIP() + ") message " + this, e);
        }
    }

    /**
     * Send new GsServerPacket to connection that is owner of this packet. This method is equivalent to:
     * getConnection().sendPacket(msg);
     *
     * @param msg
     */
    protected void sendPacket(GsServerPacket msg)
    {
        getConnection().sendPacket(msg);
    }
}
