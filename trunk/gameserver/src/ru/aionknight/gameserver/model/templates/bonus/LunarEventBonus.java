package ru.aionknight.gameserver.model.templates.bonus;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openaion.commons.utils.Rnd;

import ru.aionknight.gameserver.dataholders.DataManager;
import ru.aionknight.gameserver.model.gameobjects.Item;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.gameobjects.player.Storage;
import ru.aionknight.gameserver.model.templates.quest.QuestItems;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import ru.aionknight.gameserver.services.ItemService;
import ru.aionknight.gameserver.utils.PacketSendUtility;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunarEventBonus")
public class LunarEventBonus extends SimpleCheckItemBonus
{
	static final InventoryBonusType type = InventoryBonusType.LUNAR; 
	
	@Override
	public InventoryBonusType getType()
	{
		return type;
	}
	
	@Override
	public boolean canApply(Player player, int itemId, int questId)
	{
		if(!super.canApply(player, itemId, questId))
			return false;

		Storage storage = player.getInventory();
		if(storage.getItemCountByItemId(checkItem) < count)
			return false;
		else if(storage.isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.MSG_FULL_INVENTORY);
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean apply(Player player, Item item)
	{
		List<Integer> itemIds =
			DataManager.ITEM_DATA.getBonusItems(type, bonusLevel, bonusLevel + 1);
		
		if(itemIds.size() == 0)
			return true;
		
		int itemId = itemIds.get(Rnd.get(itemIds.size()));
		return ItemService.addItems(player, Collections.singletonList(new QuestItems(itemId, 1)));
	}

}
