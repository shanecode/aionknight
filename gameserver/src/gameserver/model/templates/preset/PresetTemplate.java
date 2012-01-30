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

package gameserver.model.templates.preset;

import gameserver.model.Gender;
import gameserver.model.PlayerClass;
import gameserver.model.templates.item.ItemRace;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PresetTemplate", propOrder = { "height", "hairType", "faceType", "hairRGB",
												"lipsRGB", "skinRGB", "detail" })
public class PresetTemplate
{
	private float height;
	
	@XmlElement(name = "hair_type")
	private int hairType;
	
	@XmlElement(name = "face_type")
	private int faceType;
	
	@XmlElement(name = "hair_color", required = true)
	private String hairRGB;
	
	@XmlElement(name = "lip_color", required = true)
	private String lipsRGB;
	
	@XmlElement(name = "skin_color", required = true)
	private String skinRGB;
	
	@XmlElement(required = true)
	private String detail;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "class", required = true)
	private PlayerClass class_defined;
	
	@XmlAttribute(name = "race", required = true)
	private ItemRace race;
	
	@XmlAttribute(name = "gender", required = true)
	private Gender gender;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the class_defined
	 */
	public PlayerClass getPcClass()
	{
		return class_defined;
	}

	/**
	 * @return the race
	 */
	public ItemRace getRace()
	{
		return race;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender()
	{
		return gender;
	}

	/**
	 * @return the height
	 */
	public float getHeight()
	{
		return height;
	}

	/**
	 * @return the hairType
	 */
	public int getHairType()
	{
		return hairType;
	}

	/**
	 * @return the faceType
	 */
	public int getFaceType()
	{
		return faceType;
	}

	/**
	 * @return the hairRGB
	 */
	public String getHairRGB()
	{
		return hairRGB;
	}

	/**
	 * @return the lipsRGB
	 */
	public String getLipsRGB()
	{
		return lipsRGB;
	}

	/**
	 * @return the skinRGB
	 */
	public String getSkinRGB()
	{
		return skinRGB;
	}

	/**
	 * @return the detail
	 */
	public String getDetail()
	{
		return detail;
	}

}
