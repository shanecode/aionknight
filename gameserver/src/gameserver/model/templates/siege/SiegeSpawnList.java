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

package gameserver.model.templates.siege;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "siege_spawn")
public class SiegeSpawnList
{
	@XmlAttribute(name = "location_id")
	protected int		locationId;

	@XmlElement(name = "guards")
	protected SiegeGuards guards;
	
	@XmlElement(name = "instance_portal")
	protected InstancePortalTemplate instancePortal;
	
	@XmlElement(name = "fortress_general")
	protected FortressGeneralTemplate fortressGeneral;
	
	@XmlElement(name = "fortress_gate")
	protected List<FortressGateTemplate> fortressGates;
	
	@XmlElement(name = "artifact")
	protected ArtifactTemplate artifact;
	
	@XmlElement(name = "aetheric_field")
	protected AethericFieldTemplate aethericField;
	
	
	public int getLocationId()
	{
		return locationId;
	}
	
	public SiegeGuards getGuards()
	{
		return guards;
	}
	
	public InstancePortalTemplate getInstancePortalTemplate()
	{
		return instancePortal;
	}
	
	public FortressGeneralTemplate getFortressGeneralTemplate()
	{
		return fortressGeneral;
	}
	
	public List<FortressGateTemplate> getFortressGatesTemplates()
	{
		return fortressGates;
	}
	
	public ArtifactTemplate getArtifactTemplate()
	{
		return artifact;
	}
	
	public AethericFieldTemplate getAethericFieldTemplate()
	{
		return aethericField;
	}
	
}
