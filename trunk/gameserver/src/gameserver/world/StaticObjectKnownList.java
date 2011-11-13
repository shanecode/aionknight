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

package gameserver.world;

import java.util.ArrayList;
import java.util.List;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;

public class StaticObjectKnownList extends NpcKnownList
{

	public StaticObjectKnownList(VisibleObject owner)
	{
		super(owner);
	}

	@Override
	protected void findVisibleObjects()
	{
		if(owner == null || !owner.isSpawned())
			return;
		
		final List<VisibleObject> objectsToAdd = new ArrayList<VisibleObject>();
		
		for (MapRegion r : owner.getActiveRegion().getNeighbours())
		{
			r.doOnAllPlayers(new Executor<Player>(){
				@Override
				public boolean run(Player newObject)
				{
					if(newObject == owner || newObject == null)
						return true;
					
					if(!checkObjectInRange(owner, newObject))
						return true;
					
					objectsToAdd.add(newObject);

					return true;
				}
			}, true);
		}
		
		for (VisibleObject object : objectsToAdd)
		{
			owner.getKnownList().storeObject(object);
			object.getKnownList().storeObject(owner);
		}
		
		objectsToAdd.clear();
	}
}