/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.aionknight.gameserver.model.siege;


import ru.aionknight.gameserver.ai.npcai.AggressiveAi;
import ru.aionknight.gameserver.ai.npcai.MonsterAi;
import ru.aionknight.gameserver.configs.main.CustomConfig;
import ru.aionknight.gameserver.controllers.ArtifactProtectorController;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.templates.VisibleObjectTemplate;
import ru.aionknight.gameserver.model.templates.spawn.SpawnTemplate;

/**
 * @author Sylar
 *
 */
public class ArtifactProtector extends Npc
{
	
	private Artifact artifact;
	
	public ArtifactProtector(int objId, ArtifactProtectorController controller, SpawnTemplate spawn, VisibleObjectTemplate objectTemplate)
	{
		super(objId, controller, spawn, objectTemplate);
	}
	
	@Override
	public ArtifactProtectorController getController()
	{
		return (ArtifactProtectorController) super.getController();
	}
	
	public void setArtifact(Artifact artifact)
	{
		this.artifact = artifact;
	}
	
	public Artifact getArtifact()
	{
		return artifact;
	}
	
	@Override
	public void initializeAi()
	{
		if(isAggressive() && !CustomConfig.DISABLE_MOB_AGGRO)
			this.ai = new AggressiveAi();
		else
			this.ai = new MonsterAi();

		ai.setOwner(this);	
	}
	
}
