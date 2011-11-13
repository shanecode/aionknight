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
package gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.Race;


/**

 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntryPoint")
public class EntryPoint
{
	@XmlAttribute(name = "mapid")
	protected int	mapId;
	@XmlAttribute(name = "x")
	protected float	x;
	@XmlAttribute(name = "y")
	protected float	y;
	@XmlAttribute(name = "z")
	protected float	z;
	@XmlAttribute(name = "race")
	protected Race	race;
	/**
	 * @return the mapId
	 */
	public int getMapId()
	{
		return mapId;
	}
	/**
	 * @return the x
	 */
	public float getX()
	{
		return x;
	}
	/**
	 * @return the y
	 */
	public float getY()
	{
		return y;
	}
	/**
	 * @return the z
	 */
	public float getZ()
	{
		return z;
	}
	/**
	 * @return the race
	 */
	public Race getRace()
	{
		return race;
	}
}