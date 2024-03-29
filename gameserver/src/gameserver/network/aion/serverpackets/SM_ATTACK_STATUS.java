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

import gameserver.model.gameobjects.Creature;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import java.nio.ByteBuffer;

public class SM_ATTACK_STATUS extends AionServerPacket
{
    private Creature creature;
    private TYPE type;
    private int skillId;
    private int value;
    private int logId;
   
    public static enum TYPE
    {
    	NATURAL_HP(3), USED_HP(4), REGULAR(5), HP(7), DELAYDAMAGE(10),
    	FALL_DAMAGE(17), HEALED_MP(19), ABSORBED_MP(20), MP(21),
    	NATURAL_MP(22), FP_RINGS(23), FP(25), NATURAL_FP(26);
    	
    	private int value;
    	
    	private TYPE(int value)
    	{
    		this.value = value;
    	}
    	
    	public int getValue()
    	{
    		return this.value;
    	}
    }
	
    public SM_ATTACK_STATUS(Creature creature, TYPE type, int value, int skillId, int logId)
    {
    	this.creature = creature;
		this.type = type;
		this.skillId = skillId;
		this.value = value;
    	this.logId = logId;
    }
    
    public SM_ATTACK_STATUS(Creature creature, TYPE type, int value)
    {
    	this.creature = creature;
		this.type = type;
		this.skillId = 0;
		this.value = value;
    	this.logId = 170;
    }
    
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		writeD(buf, creature.getObjectId());
		writeD(buf, value);
		writeC(buf, type.getValue());
		writeC(buf, creature.getLifeStats().getHpPercentage());
		writeH(buf, skillId);
		writeH(buf, logId);
	}	
}