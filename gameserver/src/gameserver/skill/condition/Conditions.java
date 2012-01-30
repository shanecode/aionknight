/*
 * Emulator game server Aion 2.7 from the command of developers 'Aion-Knight Dev. Team' is
 * free software; you can redistribute it and/or modify it under the terms of
 * GNU affero general Public License (GNU GPL)as published by the free software
 * security (FSF), or to License version 3 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranties related to
 * CONSUMER PROPERTIES and SUITABILITY FOR CERTAIN PURPOSES. For details, see
 * General Public License is the GNU.
 *
 * You should have received a copy of the GNU affero general Public License along with this program.
 * If it is not, write to the Free Software Foundation, Inc., 675 Mass Ave,
 * Cambridge, MA 02139, USA
 *
 * Web developers : http://aion-knight.ru
 * Support of the game client : Aion 2.7- 'Arena of Death' (Innova)
 * The version of the server : Aion-Knight 2.7 (Beta version)
 */

package gameserver.skill.condition;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Conditions", propOrder = {
    "conditions"
})
public class Conditions 
{
    @XmlElements({
        @XmlElement(name = "target", type = TargetCondition.class),
        @XmlElement(name = "self", type = SelfCondition.class),
        @XmlElement(name = "mp", type = MpCondition.class),
		@XmlElement(name = "hp", type = HpCondition.class),
        @XmlElement(name = "dp", type = DpCondition.class),
        @XmlElement(name = "playermove", type = PlayerMovedCondition.class),
        @XmlElement(name = "arrowcheck", type = ArrowCheckCondition.class)
    })
    protected List<Condition> conditions;

    /**
     * Gets the value of the conditions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the conditions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *   getConditions().add(newItem);
     * </pre>
     * 
     */
    public List<Condition> getConditions()
    {
        if (conditions == null) {
            conditions = new ArrayList<Condition>();
        }
        return this.conditions;
    }
}
