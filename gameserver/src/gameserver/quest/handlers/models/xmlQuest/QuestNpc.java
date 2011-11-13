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

package gameserver.quest.handlers.models.xmlQuest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import gameserver.model.gameobjects.Npc;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestNpc", propOrder = { "dialog" })
public class QuestNpc
{

	protected List<QuestDialog>	dialog;
	@XmlAttribute(required = true)
	protected int				id;
	public boolean operate(QuestCookie env, QuestState qs)
	{
		int npcId = -1;
		if(env.getVisibleObject() instanceof Npc)
			npcId = ((Npc) env.getVisibleObject()).getNpcId();
		if (npcId != id)
			return false;
		for (QuestDialog questDialog : dialog)
		{
			if (questDialog.operate(env, qs))
			return true;
		}
		return false;
	}
}