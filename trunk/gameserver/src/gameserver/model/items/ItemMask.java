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

package gameserver.model.items;

public class ItemMask
{
	public static final int LIMIT_ONE = 1;
	public static final int TRADEABLE = (1 << 1);//can_exchange
	public static final int SELLABLE = (1 << 2);//can_sell_to_npc
	public static final int STORABLE_IN_WH = (1 << 3);//can_deposit_to_character_warehouse
	public static final int STORABLE_IN_AWH = (1 << 4);//can_deposit_to_account_warehouse
	public static final int STORABLE_IN_LWH = (1 << 5);//can_deposit_to_guild_warehouse
	public static final int BREAKABLE = (1 << 6);
	public static final int SOUL_BOUND = (1 << 7);//soul_bind
	public static final int REMOVE_LOGOUT = (1 << 8);//remove when logout, temporary items
	public static final int NO_ENCHANT = (1 << 9);// 1 = cannot be enchanted
	public static final int CAN_PROC_ENCHANT = (1 << 10);// ???
	public static final int CAN_COMPOSITE_WEAPON = (1 << 11);// if fusion is allowed
	public static final int REMODELABLE = (1 << 12);//cannot_changeskin == 0
	public static final int CAN_SPLIT = (1 << 13);
	public static final int DELETABLE = (1 << 14);//item_drop_permitted
	public static final int DYEABLE = (1 << 15);//can_dye
}