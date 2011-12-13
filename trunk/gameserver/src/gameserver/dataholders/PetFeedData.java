/**
 * This file is part of Aion-Knight Dev. Team [http://aion-knight.ru]
 *
 * Aion-Knight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Aion-Knight is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a  copy  of the GNU General Public License
 * along with Aion-Knight. If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.dataholders;

import gameserver.model.templates.pet.FoodGroups;
import gameserver.model.templates.pet.FoodType;
import gameserver.model.templates.pet.PetFlavour;
import gameserver.model.templates.pet.PetRewards;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "foodGroups", "flavours" })
@XmlRootElement(name = "pet_feed")

public class PetFeedData
{
	@XmlElement(name="groups", required = true)
	protected FoodGroups foodGroups;
	
	@XmlElement(name="flavour", required = true)
	protected List<PetFeedData.Flavour> flavours;

	public FoodGroups getFoodGroups() 
	{
		return foodGroups;
	}

	public List<PetFeedData.Flavour> getFlavours() 
	{
		if (flavours == null)
			flavours = new ArrayList<PetFeedData.Flavour>();

		return this.flavours;
	}

	public synchronized PetFlavour getFlavour(int flavour, int feedItemId)
	{
		List<FoodType> foodTypes = foodGroups.getFoodTypes(feedItemId);
		if (foodTypes.size() == 0 || PetFlavour.isReward(feedItemId))
			return null;

		for (PetFlavour f : flavours)
		{
			if (f.getId() != flavour)
				continue;
			
			List<PetRewards> rewards = null;
			for (FoodType foodType : foodTypes)
			{
				rewards = f.getRewards(foodType);
				if (rewards.size() != 0 && rewards.get(0).getResults().size() != 0)
					return f;
			}
		}
		
		return null;
	}

	public int size()
	{
		return getFlavours().size();
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Flavour extends PetFlavour
	{
	}
}