/**
 * This file is part of Aion-Knight dev. Team [http://aion-knight.ru]
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
import javax.xml.bind.annotation.XmlType;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractItemAction")
public abstract class AbstractItemAction
{
	/**
	 * Check if an item can be used.
	 * @param player
	 * @param parentItem
	 * @param targetItem
	 * @return
	 */
	public abstract boolean canAct(Player player, Item parentItem, Item targetItem);

	/**
	 * @param player
	 * @param parentItem
	 * @param targetItem
	 */
	public abstract void act(Player player, Item parentItem, Item targetItem);
}