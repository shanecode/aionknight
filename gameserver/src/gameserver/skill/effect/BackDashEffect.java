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

package gameserver.skill.effect;

import gameserver.configs.main.CustomConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import gameserver.skill.action.DamageType;
import gameserver.skill.model.Effect;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackDashEffect")
public class BackDashEffect extends DamageEffect
{
	
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, DamageType.PHYSICAL, true);
	}
	
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect);
		if (CustomConfig.GEODATA_EFFECTS_ENABLED)
		{
			Player player = (Player)effect.getEffector();
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(player.getHeading()));
			float x2 = (float)(player.getX() + (25 * Math.cos(Math.PI+radian)));
			float y2 = (float)(player.getY() + (25 * Math.sin(Math.PI+radian)));
			float z2 = player.getZ() + 0.5f;
			World.getInstance().updatePosition(player, x2, y2, z2,player.getHeading(), false);
			PacketSendUtility.broadcastPacket(player, new SM_FORCED_MOVE(player, x2, y2, z2), true);
		}
	}
}