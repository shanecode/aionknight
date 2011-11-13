/**
 * This file is part of Aion-Knight <Aion-Knight.com>.
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

public class HTMLConfig
{
	@Property(key = "html.root", defaultValue = "./data/static_data/HTML/")
	public static String HTML_ROOT;
	
	@Property(key = "html.cache.file", defaultValue = "./html.cache")
	public static String HTML_CACHE_FILE;
	
	@Property(key = "html.encoding", defaultValue = "UTF-8")
	public static String HTML_ENCODING;
}