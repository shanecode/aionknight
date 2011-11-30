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

import gameserver.model.templates.stats.SummonStatsTemplate;
import gnu.trove.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "summon_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class SummonStatsData
{
	@XmlElement(name = "summon_stats", required = true)
	private List<SummonStatsType> summonTemplatesList = new ArrayList<SummonStatsType>();
	
	private final TIntObjectHashMap<SummonStatsTemplate> summonTemplates = new TIntObjectHashMap<SummonStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (SummonStatsType st : summonTemplatesList)
		{
			int code1 = makeHash(st.getNpcIdDark(), st.getRequiredLevel());
			summonTemplates.put(code1, st.getTemplate());
			int code2 = makeHash(st.getNpcIdLight(), st.getRequiredLevel());
			summonTemplates.put(code2, st.getTemplate());
		}		
	}

	public SummonStatsTemplate getSummonTemplate(int npcId, int level)
	{
		SummonStatsTemplate template =  summonTemplates.get(makeHash(npcId, level));
		if(template == null)
			template = summonTemplates.get(makeHash(201022, 10));//TEMP till all templates are done
		return template;
	}

	public int size()
	{
		return summonTemplates.size();
	}
	
	@XmlRootElement(name="summonStatsTemplateType")
	private static class SummonStatsType
	{
		@XmlAttribute(name = "npc_id_dark", required = true)
		private int npcIdDark;
		@XmlAttribute(name = "npc_id_light", required = true)
		private int npcIdLight;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;
		@XmlElement(name="stats_template")
		private SummonStatsTemplate template;

		public int getNpcIdDark()
		{
			return npcIdDark;
		}

		public int getNpcIdLight()
		{
			return npcIdLight;
		}

		public int getRequiredLevel()
		{
			return requiredLevel;
		}

		public SummonStatsTemplate getTemplate()
		{
			return template;
		}
	}

	private static int makeHash(int npcId, int level)
	{
		return npcId << 8 | level;
	}
}