/**
 * This file is part of Aion-Knight Dev. Team [http://www.aion-knight.ru]
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

package gameserver.dao;

import java.util.List;
import commons.database.dao.DAO;
import gameserver.model.gameobjects.Survey;

public abstract class SurveyDAO implements DAO
{

	@Override
	public final String getClassName()
	{
		 return SurveyDAO.class.getName();
	}

	public abstract boolean deleteSurvey(int survey_id);
	public abstract List<Survey> loadSurveys(int playerId);
	public abstract Survey loadSurvey(int player_id, int survey_id);
	public abstract void addSurvey(int playerId, String title, String message, String selectText, int itemId, int itemCount);
}