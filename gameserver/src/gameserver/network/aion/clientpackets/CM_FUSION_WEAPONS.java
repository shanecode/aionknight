/**
 * This file is part of Aion-Knight <Aion-Knight.smfnew.com>.
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
package gameserver.network.aion.clientpackets;


import gameserver.network.aion.AionClientPacket;
import gameserver.services.ArmsfusionService;
/**
 * 
 * @author Sylar
 * 
 */
public class CM_FUSION_WEAPONS extends AionClientPacket
{
	
	public CM_FUSION_WEAPONS(int opcode)
	{
		super(opcode);
	}
	
	private int firstItemId;
	private int secondItemId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		readD();
		firstItemId = readD();
		secondItemId = readD();
		/**
		 * Temporary: fusion price fixed to 50000 kinah
		 * TODO: find price value
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		ArmsfusionService.fusionWeapons(getConnection().getActivePlayer(), firstItemId, secondItemId);
	}
}