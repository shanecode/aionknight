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

package gameserver.model.siege;

import gameserver.controllers.FortressGateController;
import gameserver.model.gameobjects.Npc;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.spawn.SpawnTemplate;

public class FortressGate extends Npc
{
	private int fortressId;
	private FortressGateArtifact gateArtifact;
	private int spawnStaticId;
	
	public FortressGate(int objId, FortressGateController controller, SpawnTemplate spawn, VisibleObjectTemplate objectTemplate, int fortressId, int staticId)
	{
		super(objId, controller, spawn, objectTemplate);
		this.fortressId = fortressId;
		this.spawnStaticId = staticId;
	}
	
	public int getFortressId()
	{
		return fortressId;
	}
	
	public void setArtifact(FortressGateArtifact artifact)
	{
		gateArtifact = artifact;
		gateArtifact.setRelatedGate(this);
	}
	
	public int getStaticId()
	{
		return spawnStaticId;
	}
	
}