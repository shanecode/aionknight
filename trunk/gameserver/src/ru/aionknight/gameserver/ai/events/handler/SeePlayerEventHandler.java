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
import ru.aionknight.gameserver.ai.desires.impl.AggressionDesire;
import ru.aionknight.gameserver.ai.events.Event;
import ru.aionknight.gameserver.ai.state.AIState;
import ru.aionknight.gameserver.model.gameobjects.AionObject;
import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.stats.modifiers.Executor;


/**
 * @author ATracer
 *
 */
public class SeePlayerEventHandler implements EventHandler
{
	@Override
	public Event getEvent()
	{
		return Event.SEE_PLAYER;
	}

	@Override
	public void handleEvent(Event event, final AI<?> ai)
	{
		ai.getOwner().updateKnownlist();
		ai.setAiState(AIState.ACTIVE);
		if(!ai.isScheduled())
			ai.analyzeState();
		else if(ai.getDesireQueue().hasWalkingDesire())
		{
			ai.getOwner().getKnownList().doOnAllObjects(new Executor<AionObject>(){
				@Override
				public boolean run(AionObject object)
				{
					if (object instanceof Creature && ai.getOwner().isAggressiveTo((Creature)object))
					{
						ai.addDesire(new AggressionDesire((Npc)ai.getOwner(), AIState.ACTIVE.getPriority()));
						return false;
					}
					return true;
				}
			}, true);
		}
	}
}
