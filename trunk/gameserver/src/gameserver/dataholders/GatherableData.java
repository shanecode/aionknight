/**
 * This file is part of Aion-Knight [http://www.aion-knight.ru]
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

package gameserver.dataholders;

import gnu.trove.TIntObjectHashMap;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import gameserver.model.templates.GatherableTemplate;

@XmlRootElement(name = "gatherable_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherableData
{
	@XmlElement(name = "gatherable_template")
	private List<GatherableTemplate> gatherables;
	private TIntObjectHashMap<GatherableTemplate> gatherableData = new TIntObjectHashMap<GatherableTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(GatherableTemplate gatherable: gatherables)
		{
			gatherableData.put(gatherable.getTemplateId(), gatherable);
		}
		gatherables = null;
	}
	
	public int size()
	{
		return gatherableData.size();
	}

	public GatherableTemplate getGatherableTemplate(int id)
	{
		return gatherableData.get(id);
	}
}