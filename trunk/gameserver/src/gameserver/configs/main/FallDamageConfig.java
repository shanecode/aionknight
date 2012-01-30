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

package gameserver.configs.main;

import commons.configuration.Property;

public class FallDamageConfig
{
	@Property(key = "gameserver.fall.damage.active", defaultValue = "true")
	public static boolean ACTIVE_FALL_DAMAGE;

	@Property(key = "gameserver.fall.damage.percentage", defaultValue = "1.0")
	public static float FALL_DAMAGE_PERCENTAGE;

	@Property(key = "gameserver.fall.damage.distance.minimum", defaultValue = "10")
	public static int MINIMUM_DISTANCE_DAMAGE;

	@Property(key = "gameserver.fall.damage.distance.maximum", defaultValue = "50")
	public static int MAXIMUM_DISTANCE_DAMAGE;

	@Property(key = "gameserver.fall.damage.distance.midair", defaultValue = "200")
	public static int MAXIMUM_DISTANCE_MIDAIR;
}