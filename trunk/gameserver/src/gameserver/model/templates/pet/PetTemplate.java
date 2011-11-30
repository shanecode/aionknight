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
package gameserver.model.templates.pet;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylar
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pet")
public class PetTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int			id;
	
	@XmlAttribute(name = "name", required = true)
	private String		name;
	
	@XmlAttribute(name = "nameid", required = true)
	private int			nameId;
	
	@XmlAttribute(name = "eggid", required = false)
	private int			eggId;
	
	@XmlElement(name = "petstats", required = true)
	private PetStatsTemplate statsTemplate;
	
	@XmlElement(name = "petfunction")
	private List<PetFunctionTemplate> functionTemplates;

	public int getPetId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getNameId()
	{
		return nameId;
	}
	
	public int getEggId()
	{
		return eggId;
	}
	
	public PetStatsTemplate getStatsTemplate()
	{
		return statsTemplate;
	}
	
	public List<PetFunctionTemplate> getFunctionTemplates()
	{
		if (functionTemplates == null)
			return new ArrayList<PetFunctionTemplate>();
		return functionTemplates;
	}
	
	public int getFoodFlavourId()
	{
		for (PetFunctionTemplate t : getFunctionTemplates())
		{
			if ("food".equals(t.getType()))
				return t.getId();
		}
		return 0;
	}
	
	void afterUnmarshal (Unmarshaller u, Object parent)
	{
		
	}
}
