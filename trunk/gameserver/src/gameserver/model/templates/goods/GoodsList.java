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

package gameserver.model.templates.goods;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GoodsList")
public class GoodsList {

	@XmlAttribute
	protected int id;
	protected boolean limited = false;
	protected List<GoodsList.Item> item;
	protected List<Integer> itemIdList;
	protected List<GoodsList.Item> itemsList;

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		itemIdList = new ArrayList<Integer>();
		itemsList = new ArrayList<GoodsList.Item>();
		
		if(item == null)
			return;
		
		for(Item it : item)
		{
			itemIdList.add(it.getId());

			if(it.getBuylimit() > 0)
				this.limited = true;

			itemsList.add(it);
		}
		item = null;
	}	

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the itemIdList
	 */
	public List<Integer> getItemIdList()
	{
		return itemIdList;
	}

	public List<GoodsList.Item> getItemsList()
	{
		return itemsList;
	}

	public boolean isLimited()
	{
		return limited;
	}

	/**
	 * <p>Java class for anonymous complex type.
	 * 
	 * <p>The following schema fragment specifies the expected content contained within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *  &lt;complexContent>
	 *    &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *      &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
	 *    &lt;/restriction>
	 *  &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Item {

		@XmlAttribute
		protected int id;
		@XmlAttribute
		protected int buylimit;
		@XmlAttribute
		protected int selllimit;

		public int getId() {
			return id;
		}

		public int getBuylimit() {
			return buylimit;
		}

		public int getSelllimit() {
			return selllimit;
		}
	}

}
