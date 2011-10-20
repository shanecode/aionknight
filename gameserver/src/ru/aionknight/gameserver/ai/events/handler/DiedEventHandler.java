/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.aionknight.gameserver.ai.events.handler;


import ru.aionknight.gameserver.ai.AI;
import ru.aionknight.gameserver.ai.events.Event;
import ru.aionknight.gameserver.ai.state.AIState;

/**
 * @author ATracer
 *
 */
public class DiedEventHandler implements EventHandler
{
	@Override
	public Event getEvent()
	{
		return Event.DIED;
	}

	@Override
	public void handleEvent(Event event, AI<?> ai)
	{
		ai.setAiState(AIState.NONE);
	}
}
