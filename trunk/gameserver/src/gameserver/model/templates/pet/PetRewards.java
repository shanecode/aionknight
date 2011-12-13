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
package gameserver.model.templates.pet;

import commons.utils.Rnd;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Rolandas
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetRewards", propOrder = { "results" })
@XmlSeeAlso({ gameserver.model.templates.pet.PetFlavour.Food.class })

public class PetRewards 
{
	@XmlElement(name="result")
    protected List<PetRewards.Result> results;
    
    @XmlAttribute(name = "type", required = true)
    protected FoodType type;
    
    @XmlAttribute(name = "loved")
    protected boolean loved = false;

    public List<PetRewards.Result> getResults()
    {
        if (results == null)
            results = new ArrayList<PetRewards.Result>();
        return this.results;
    }
    
    /**
     * Returns results with price = -1 (additionally rewarded, like during events)
     */
    public List<PetRewardDescription> getAdditionalRewards()
    {
    	List<PetRewardDescription> results = new ArrayList<PetRewardDescription>();
    	for (PetRewardDescription descr : getResults())
    	{
    		if (descr.getPrice() == -1)
    			results.add(descr);
    	}
    	return results;
    }
    
    public PetRewardDescription getRandomReward()
    {
    	for (PetRewardDescription descr : getResults())
    	{
    		if (descr.getChance() == 0)
    			continue;
   			if(Rnd.get(100) <= descr.getChance())
    			return descr;
    	}
    	return null;
    }    

    public FoodType getType()
    {
        return type;
    }

    public boolean isLoved() 
    {
        return loved;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Result extends PetRewardDescription
    {
    }
}