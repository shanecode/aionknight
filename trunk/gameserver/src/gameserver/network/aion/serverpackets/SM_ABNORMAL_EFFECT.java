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
import gameserver.skill.model.Effect;
import java.nio.ByteBuffer;
import java.util.Collection;

public class SM_ABNORMAL_EFFECT extends AionServerPacket
{	
	private int effectedId;
	private int abnormals;
	private Collection<Effect> effects;
	
	public SM_ABNORMAL_EFFECT(int effectedId, int abnormals,  Collection<Effect> effects)
	{
		this.effects = effects;
		this.abnormals = abnormals;
		this.effectedId = effectedId;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, effectedId); 
		writeC(buf, 1);
		writeD(buf, 0);
		writeD(buf, abnormals);
		writeD(buf, 0);

		writeH(buf, effects.size()); 
		
		for(Effect effect : effects)
		{
			writeH(buf, effect.getSkillId()); 
			writeC(buf, effect.getSkillLevel());
			writeC(buf, effect.getTargetSlot()); 
			writeD(buf, effect.getElapsedTime());
		}
	}
}