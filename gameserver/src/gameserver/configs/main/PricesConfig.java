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

package gameserver.configs.main;

import commons.configuration.Property;

public class PricesConfig
{
	@Property(key = "gameserver.prices.default.prices", defaultValue = "100")
	public static int DEFAULT_PRICES;
	
	@Property(key = "gameserver.prices.default.modifier", defaultValue = "100")
	public static int DEFAULT_MODIFIER;
	
	@Property(key = "gameserver.prices.default.taxes", defaultValue = "100")
	public static int DEFAULT_TAXES;
	
	@Property(key = "gameserver.prices.vendor.buymod", defaultValue = "100")
	public static int VENDOR_BUY_MODIFIER;
	
	@Property(key = "gameserver.prices.vendor.sellmod", defaultValue = "20")
	public static int VENDOR_SELL_MODIFIER;
	
}