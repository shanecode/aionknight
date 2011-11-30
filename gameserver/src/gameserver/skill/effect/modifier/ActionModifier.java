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
package gameserver.skill.effect.modifier;

import gameserver.skill.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;



/**

 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionModifier")
public abstract class ActionModifier {

	@XmlAttribute(required = true)
	protected int delta;
	@XmlAttribute(required = true)
	protected int value;
	/**
	 * Applies modifier to original value
	 *  
	 * @param effect  
	 * @param originalValue
	 * @return int
	 */
	public abstract int analyze(Effect effect);
	
	/**
	 * Performs check of condition
	 * 
	 * @param effect
	 * @return true or false
	 */
	public abstract boolean check(Effect effect);
}
