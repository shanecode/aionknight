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

import gameserver.quest.model.QuestState;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class QuestStateList
{
	private static final Logger log = Logger.getLogger(QuestStateList.class);
	
	private final SortedMap<Integer, QuestState>	_quests;
	
	/**
	 * Creates an empty quests list
	 */
	public QuestStateList()
	{
		_quests = Collections.synchronizedSortedMap(new TreeMap<Integer, QuestState>());
	}

	public boolean addQuest(int questId,  QuestState questState)
	{
		if (_quests.containsKey(questId))
		{
			log.warn("Duplicate quest. ");
			return false;
		}
		_quests.put(questId, questState);
		return true;
	}

	public boolean removeQuest(int questId)
	{
		if (_quests.containsKey(questId))
		{
			_quests.remove(questId);
			return true;
		}
		return false;
	}
	
	public QuestState getQuestState(int questId)
	{
		return _quests.get(questId);
	}

	public Collection <QuestState> getAllQuestState()
	{
		return _quests.values();
	}
}