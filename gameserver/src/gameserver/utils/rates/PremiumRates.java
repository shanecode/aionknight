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

package gameserver.utils.rates;

import gameserver.configs.main.RateConfig;

public class PremiumRates extends Rates
{
	@Override
	public int getGroupXpRate()
	{
		return RateConfig.PREMIUM_GROUPXP_RATE;
	}

	@Override
	public float getApNpcRate()
	{
		return RateConfig.PREMIUM_AP_NPC_RATE;
	}

	@Override
	public float getApPlayerRate()
	{
		return RateConfig.PREMIUM_AP_PLAYER_RATE;
	}

	@Override
	public int getDropRate()
	{
		return RateConfig.PREMIUM_DROP_RATE;
	}
	
	@Override
	public int getChestDropRate()
	{
		return RateConfig.PREMIUM_CHEST_DROP_RATE;
	}

	@Override
	public int getQuestKinahRate()
	{
		return RateConfig.PREMIUM_QUEST_KINAH_RATE;
	}

	@Override
	public int getQuestXpRate()
	{
		return RateConfig.PREMIUM_QUEST_XP_RATE;
	}

	@Override
	public int getXpRate()
	{
		return RateConfig.PREMIUM_XP_RATE;
	}

	@Override
	public float getCraftingXPRate()
	{
		return RateConfig.PREMIUM_CRAFTING_XP_RATE;
	}
	
	@Override
	public float getCraftingLvlRate()
	{
		return RateConfig.PREMIUM_CRAFTING_LVL_RATE;
	}

	@Override
	public float getGatheringXPRate()
	{
		return RateConfig.PREMIUM_GATHERING_XP_RATE;
	}
	
	@Override
	public float getGatheringLvlRate()
	{
		return RateConfig.PREMIUM_GATHERING_LVL_RATE;
	}

	@Override
	public int getKinahRate()
	{
		return RateConfig.PREMIUM_KINAH_RATE;
	}
}