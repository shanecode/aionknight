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

package gameserver.quest.handlers.models.xmlQuest.conditions;

import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Mr. Poke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestVarCondition")
public class QuestVarCondition extends QuestCondition
{

	@XmlAttribute(required = true)
	protected int	value;
	@XmlAttribute(name = "var_id", required = true)
	protected int	varId;

	/** (non-Javadoc)
	 * @see gameserver.quest.handlers.template.xmlQuest.condition.QuestCondition#doCheck(gameserver.quest.model.QuestEnv)
	 */
    @Override
    public boolean doCheck(QuestCookie env)
    {
            QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
            if (qs == null)
            {
                    return false;
            }
            int var = qs.getQuestVars().getVarById(varId);
            switch (getOp())
            {
                    case EQUAL:
                            return var == value;
                    case GREATER:
                            return var > value;
                    case GREATER_EQUAL:
                            return var >= value;
                    case LESSER:
                            return var < value;
                    case LESSER_EQUAL:
                            return var <= value;
                    case NOT_EQUAL:
                            return var != value;
                    default:
                            return false;
            }
    }

}
