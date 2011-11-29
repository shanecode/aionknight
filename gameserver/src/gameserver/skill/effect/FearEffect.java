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

package gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.controllers.movement.MovementType;
import gameserver.network.aion.serverpackets.SM_MOVE;
import gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import gameserver.geo.GeoEngine;
import gameserver.skill.model.Effect;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FearEffect")
public class FearEffect extends EffectTemplate
{

	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, StatEnum.FEAR_RESISTANCE, null); 
	}
	
	@Override
	public void startEffect(Effect effect)
	{
		Creature obj = effect.getEffected();
		obj.getController().cancelCurrentSkill();
		effect.setAbnormal(EffectId.FEAR.getEffectId());
		obj.getEffectController().setAbnormal(EffectId.FEAR.getEffectId());
		PacketSendUtility.broadcastPacketAndReceive(obj, new SM_TARGET_IMMOBILIZE(obj));
		obj.getController().stopMoving();
		obj.getMoveController().setNewDirection(obj.getX()+20, obj.getY()+20, obj.getZ());
		obj.getMoveController().schedule();
	}
	
	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getEffectController().unsetAbnormal(EffectId.FEAR.getEffectId());
	}
}