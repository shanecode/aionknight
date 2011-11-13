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

package gameserver.model.gameobjects.player;

import java.util.HashSet;
import java.util.Set;
import commons.database.dao.DAOManager;
import gameserver.dao.PlayerRecipesDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.templates.recipe.RecipeTemplate;
import gameserver.network.aion.serverpackets.SM_LEARN_RECIPE;
import gameserver.network.aion.serverpackets.SM_RECIPE_DELETE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

public class RecipeList
{
	private Set<Integer>		recipeList = new HashSet<Integer>();
	
	public RecipeList (HashSet<Integer> recipeList)
	{
		this.recipeList = recipeList;
	}
	
	public Set<Integer> getRecipeList()
	{
		return recipeList;
	}

	public void addRecipe(Player player, RecipeTemplate recipeTemplate)
	{
		int recipeId = recipeTemplate.getId();
		if (!recipeList.contains(recipeId))
		{
			recipeList.add(recipeId);
			DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(player.getObjectId(), recipeId);
			PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(recipeId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CRAFT_RECIPE_LEARN(new DescriptionId(recipeTemplate.getNameid())));
		}
	}

	public void deleteRecipe(Player player, int recipeId)
	{
		if (recipeList.contains(recipeId))
		{
			recipeList.remove(recipeId);
			DAOManager.getDAO(PlayerRecipesDAO.class).deleteRecipe(player.getObjectId(), recipeId);
			PacketSendUtility.sendPacket(player, new SM_RECIPE_DELETE(recipeId));
		}
	}

	public void autoLearnRecipe (Player player, int skillId, int skillLvl)
	{
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getRecipeIdFor(player.getCommonData().getRace(), skillId, skillLvl))
		{
			player.getRecipeList().addRecipe(player, recipe);
		}
	}

	public boolean isRecipePresent(int recipeId)
	{
		return recipeList.contains(recipeId);
	}
}