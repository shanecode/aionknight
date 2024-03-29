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

package gameserver.network.aion.clientpackets;

import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.serverpackets.SM_INGAMESHOP;
import gameserver.network.aion.serverpackets.SM_INGAMESHOP_BALANCE;
import gameserver.network.aion.serverpackets.SM_INGAMESHOP_ITEM;
import gameserver.network.aion.serverpackets.SM_INGAMESHOP_ITEMS;
import gameserver.services.CashShopManager;
import gameserver.services.CashShopManager.ShopItem;

public class CM_INGAMESHOP extends AionClientPacket
{
	public CM_INGAMESHOP(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		AionConnection client = getConnection();
		int type = readC();
		switch(type)
		{
		
			case 0x01:
				int itemId = readD();//item id
				ShopItem item = CashShopManager.getInstance().getItem(itemId);
				if(item != null)
					client.sendPacket(new SM_INGAMESHOP_ITEM(item));
				break;

			case 0x02:
				@SuppressWarnings("unused")
				int vector = readD();//Load all categories ?
				client.sendPacket(new SM_INGAMESHOP(1, CashShopManager.getInstance().getCategories()));
				break;

			case 0x04:
				client.sendPacket(new SM_INGAMESHOP_ITEMS(CashShopManager.getInstance().getItems(1, 0), 1, 0, CashShopManager.getInstance().getItemsCount()));
				client.sendPacket(new SM_INGAMESHOP_ITEMS(CashShopManager.getInstance().getRankItems(), 0, 0, CashShopManager.getInstance().getItemsCount()));
				break;

			case 0x08:
				int catId = readD();//categoty id, 1-All
				int page = readD();//page
				client.sendPacket(new SM_INGAMESHOP_ITEMS(CashShopManager.getInstance().getItems(catId, page), catId, page, CashShopManager.getInstance().getItemsCount(catId)));
				client.sendPacket(new SM_INGAMESHOP_ITEMS(CashShopManager.getInstance().getRankItems(), 0, 0, CashShopManager.getInstance().getItemsCount()));
				break;

			case 0x10:
				client.sendPacket(new SM_INGAMESHOP_BALANCE());
			break;

			case 0x20:
				itemId = readD();
				int count = readD();
				CashShopManager.getInstance().buyItem(client.getActivePlayer(), itemId, count);
				client.sendPacket(new SM_INGAMESHOP_BALANCE());
				break;

			case 0x40:
				itemId = readD();
				count = readD();
				String receiver = readS();//receiver name
				String message = readS();//message
				CashShopManager.getInstance().giftItem(client.getActivePlayer(), itemId, count, receiver, message);
				client.sendPacket(new SM_INGAMESHOP_BALANCE());
				break;
		}
	}

	@Override
	protected void runImpl()
	{
		
	}
}
