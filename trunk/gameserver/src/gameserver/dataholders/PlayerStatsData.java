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

import gnu.trove.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.stats.CalculatedPlayerStatsTemplate;
import gameserver.model.templates.stats.PlayerStatsTemplate;

@XmlRootElement(name = "player_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData
{
	@XmlElement(name = "player_stats", required = true)
	private List<PlayerStatsType> templatesList = new ArrayList<PlayerStatsType>();
	
	private final TIntObjectHashMap<PlayerStatsTemplate> playerTemplates = new TIntObjectHashMap<PlayerStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PlayerStatsType pt : templatesList)
		{
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());
			playerTemplates.put(code, pt.getTemplate());
		}
		playerTemplates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		playerTemplates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		playerTemplates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		playerTemplates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		playerTemplates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		playerTemplates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		playerTemplates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		playerTemplates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		playerTemplates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		playerTemplates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		playerTemplates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SPIRIT_MASTER));
		playerTemplates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		
		templatesList.clear();
		templatesList = null;
	}

	public PlayerStatsTemplate getTemplate(Player player)
	{
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if(template == null)
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		return template;
	}

	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level)
	{
		PlayerStatsTemplate template =  playerTemplates.get(makeHash(playerClass, level));
		if(template == null)
			template = getTemplate(playerClass, 0);
		return template;
	}

	public int size()
	{
		return playerTemplates.size();
	}
	
	@XmlRootElement(name="playerStatsTemplateType")
	private static class PlayerStatsType
	{
		@XmlAttribute(name = "class", required = true)
		private PlayerClass requiredPlayerClass;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;

		@XmlElement(name="stats_template")
		private PlayerStatsTemplate template;

		public PlayerClass getRequiredPlayerClass()
		{
			return requiredPlayerClass;
		}

		public int getRequiredLevel()
		{
			return requiredLevel;
		}

		public PlayerStatsTemplate getTemplate()
		{
			return template;
		}
	}

	private static int makeHash(PlayerClass playerClass, int level)
	{
		return level << 8 | playerClass.ordinal();
	}
}