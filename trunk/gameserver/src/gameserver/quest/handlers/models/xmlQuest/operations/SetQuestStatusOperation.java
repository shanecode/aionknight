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
import javax.xml.bind.annotation.XmlType;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetQuestStatusOperation")
public class SetQuestStatusOperation
    extends QuestOperation
{

    @XmlAttribute(required = true)
    protected QuestStatus status;

	/** (non-Javadoc)
	 * @see gameserver.quest.handlers.models.xmlQuest.operations.QuestOperation#doOperate(gameserver.quest.model.QuestEnv)
	 */
	@Override
	public void doOperate(QuestCookie env)
	{
        Player player = env.getPlayer();
        int questId = env.getQuestId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs!=null)
        {
        	qs.setStatus(status);
    		PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(2, questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
    		if (qs.getStatus() == QuestStatus.COMPLETE)
    			player.getController().updateNearbyQuests();
        }
	}
}