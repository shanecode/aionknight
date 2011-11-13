/** This file is part of Aion-Knight dev. Team [http://aion-knight.ru]
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

package gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skill.SkillEngine;
import gameserver.skill.model.Skill;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapType;
import org.apache.log4j.Logger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ArenaSkillUseAction")
public class ArenaSkillUseAction extends AbstractItemAction
{

  @XmlAttribute
  protected int skillid;

  @XmlAttribute
  protected int level;

  public int getSkillid()
  {
    return skillid;
  }

  public int getLevel()
  {
    return level;
  }

  public boolean canAct(Player player, Item parentItem, Item targetItem)
  {
    Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
    if (skill == null)
      return false;
    if (player.getWorldId() != WorldMapType.EMPYREAN_CRUCIBLE.getId())
      return false;
    return skill.canUseSkill();
  }

  public void act(Player player, Item parentItem, Item targetItem)
  {
    Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
    if (skill != null)
    {
      PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
      skill.setItemObjectId(parentItem.getObjectId().intValue());
      skill.useSkill();
    }
    else
    {
      Logger.getLogger(SkillUseAction.class).error("Skill id is null for SkillUseAction !");
    }
  }
}