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

package gameserver.quest.handlers.models.xmlQuest.operations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import gameserver.quest.model.QuestCookie;
import gameserver.services.QuestService;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectItemQuestOperation", propOrder = { "_true", "_false" })
public class CollectItemQuestOperation extends QuestOperation
{
	@XmlElement(name = "true", required = true)
	protected QuestOperations	_true;
	@XmlElement(name = "false", required = true)
	protected QuestOperations	_false;
	@XmlAttribute
	protected Boolean			removeItems;

	/** (non-Javadoc)
	 * @see gameserver.quest.handlers.models.xmlQuest.operations.QuestOperation#doOperate(gameserver.quest.model.QuestEnv)
	 */
	@Override
	public void doOperate(QuestCookie env)
	{
		if(QuestService.collectItemCheck(env, removeItems == null ? true : false))
			_true.operate(env);
		else
			_false.operate(env);
	}	
}