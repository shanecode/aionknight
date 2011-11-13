/**  
 * This file is part of Aion-Knight [http://www.aion-knight.ru]
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
package gameserver.ai.state;

/**

 *
 */
public enum AIState
{
	THINKING(5),
	TALKING(4),
	AGGRO(3),
	ACTIVE(3),
	USESKILL(3),
	ATTACKING(2),
	RESTING(1),
	MOVINGTOHOME(1),
	NONE(0);
	
	private int priority;
	
	private AIState(int priority)
	{
		this.priority = priority;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public boolean isIdle()
	{
		return this.ordinal() == 0 || this.ordinal() == 3;
	}
}
