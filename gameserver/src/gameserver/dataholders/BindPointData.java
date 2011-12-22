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

package gameserver.dataholders;

import gameserver.model.templates.BindPointTemplate;
import gnu.trove.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "bind_points")
@XmlAccessorType(XmlAccessType.FIELD)
public class BindPointData
{
	@XmlElement(name = "bind_point")
	private List<BindPointTemplate> bplist;
	private TIntObjectHashMap<BindPointTemplate> bindplistData	= new TIntObjectHashMap<BindPointTemplate>();
	private TIntObjectHashMap<BindPointTemplate> bindplistData2	= new TIntObjectHashMap<BindPointTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(BindPointTemplate bind: bplist)
		{
			bindplistData.put(bind.getNpcId(), bind);
			bindplistData2.put(bind.getBindId(), bind);
		}
	}

	public int size()
	{
		return bindplistData.size();
	}

	public BindPointTemplate getBindPointTemplate(int npcId)
	{
		return bindplistData.get(npcId);
	}

	public BindPointTemplate getBindPointTemplate2(int bindPointId)
	{
		return bindplistData2.get(bindPointId);
	}
}