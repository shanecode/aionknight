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

package gameserver.model.group;

import gameserver.model.templates.item.ItemQuality;

public class LootGroupRules
{
	private LootRuleType lootRule;
	private LootDistribution autodistribution;
	private int common_item_above;
	private int superior_item_above;
	private int heroic_item_above;
	private int fabled_item_above;
	private int ethernal_item_above;
	private int over_ethernal;
	private int over_over_ethernal;
	public LootGroupRules()
	{
		this.lootRule = LootRuleType.ROUNDROBIN;
		this.autodistribution = LootDistribution.NORMAL;
		common_item_above = 0;
		superior_item_above = 2;
		heroic_item_above = 2;
		fabled_item_above = 2;
		ethernal_item_above = 2;
		over_ethernal = 2;
		over_over_ethernal = 0;		
	}
	
	public LootGroupRules(LootRuleType lootRule, LootDistribution autodistribution, int commonItemAbove, int superiorItemAbove,
		int heroicItemAbove, int fabledItemAbove, int ethernalItemAbove, int overEthernal, int overOverEthernal)
	{
		super();
		this.lootRule = lootRule;
		this.autodistribution = autodistribution;
		common_item_above = commonItemAbove;
		superior_item_above = superiorItemAbove;
		heroic_item_above = heroicItemAbove;
		fabled_item_above = fabledItemAbove;
		ethernal_item_above = ethernalItemAbove;
		over_ethernal = overEthernal;
		over_over_ethernal = overOverEthernal;
	}

	public int getQualityRule(ItemQuality quality)
	{
		switch(quality)
		{
			case COMMON:
					return common_item_above;
			case RARE:
					return superior_item_above;
			case LEGEND:
					return heroic_item_above;
			case UNIQUE:
					return fabled_item_above;
			case EPIC:
					return ethernal_item_above;
			case MYTHIC:
					return over_ethernal;
		}
		return 0;
	}	

	public LootRuleType getLootRule()
	{
		return lootRule;
	}

	public LootDistribution getAutodistribution()
	{
		return autodistribution;
	}

	public int getCommon_item_above()
	{
		return common_item_above;
	}

	public int getSuperior_item_above()
	{
		return superior_item_above;
	}

	public int getHeroic_item_above()
	{
		return heroic_item_above;
	}

	public int getFabled_item_above()
	{
		return fabled_item_above;
	}

	public int getEthernal_item_above()
	{
		return ethernal_item_above;
	}

	public int getOver_ethernal()
	{
		return over_ethernal;
	}

	public int getOver_over_ethernal()
	{
		return over_over_ethernal;
	}
}