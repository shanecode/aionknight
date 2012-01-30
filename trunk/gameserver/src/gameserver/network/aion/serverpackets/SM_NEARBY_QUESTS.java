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

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.QuestService;
import java.nio.ByteBuffer;
import java.util.List;

public class SM_NEARBY_QUESTS extends AionServerPacket 
{
    private Integer[] questIds;
    private int size;

    public SM_NEARBY_QUESTS(List<Integer> questIds) 
	{
        this.questIds = questIds.toArray(new Integer[questIds.size()]);
        this.size = questIds.size();
    }


    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf) 
	{
        if (questIds == null || con.getActivePlayer() == null)
            return;
        int playerLevel = con.getActivePlayer().getLevel();
		writeC(buf, 0x00); // 2.1
		writeH(buf, (-1*size) & 0xFFFF); // 2.1
        for (int id : questIds) {
            writeH(buf, id);
            if (QuestService.checkLevelRequirement(id, playerLevel))
                writeH(buf, 0);
            else
                writeH(buf, 2);
        }
    }
}
