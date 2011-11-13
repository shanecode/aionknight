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
package gameserver.model.gameobjects;


import gameserver.controllers.StaticObjectController;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.spawn.SpawnTemplate;
import gameserver.world.WorldPosition;

/**

 *
 */
public class StaticObject extends VisibleObject
{
	public StaticObject(int objectId, StaticObjectController controller,
		SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
	{
		super(objectId, controller, spawnTemplate, objectTemplate, new WorldPosition());
		controller.setOwner(this);
	}

	@Override
	public String getName()
	{
		return objectTemplate.getName();
	}
}