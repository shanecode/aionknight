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

package gameserver.world.container;

import java.util.Map;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.world.exceptions.DuplicateAionObjectException;
import javolution.util.FastMap;

public class KiskContainer
{
	private final Map<Integer, Kisk> kiskByPlayerObjectId = new FastMap<Integer, Kisk>().shared();
	
	public void add(Kisk kisk, Player player)
	{
		if (this.kiskByPlayerObjectId.put(player.getObjectId(), kisk) != null)
			throw new DuplicateAionObjectException();
	}
	
	public Kisk get(Player player)
	{
		return this.kiskByPlayerObjectId.get(player.getObjectId());
	}
	
	public void remove(Player player)
	{
		this.kiskByPlayerObjectId.remove(player.getObjectId());
	}

	public int getCount()
	{
		return this.kiskByPlayerObjectId.size();
	}
}