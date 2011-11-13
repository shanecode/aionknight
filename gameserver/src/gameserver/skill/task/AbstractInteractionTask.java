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
package gameserver.skill.task;

import java.util.concurrent.Future;


import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.ThreadPoolManager;


/**

 *
 */
public abstract class AbstractInteractionTask
{
	private Future<?> task;
	private int interval = 2500;
	
	protected Player requestor;
	protected VisibleObject responder;
	
	/**
	 * 
	 * @param requestor
	 * @param responder
	 */
	public AbstractInteractionTask(Player requestor, VisibleObject responder, int skillLvlDiff)
	{
		super();
		if(skillLvlDiff == 99999)
			this.interval = 1500;
		else
			this.interval = 2500;
		this.requestor = requestor;
		if(responder == null)
			this.responder = requestor;
		else
			this.responder = responder;
	}

	/**
	 * Called on each interaction 
	 * 
	 * @return
	 */
	protected abstract boolean onInteraction();
	
	/**
	 * Called when interaction is complete
	 */
	protected abstract void onInteractionFinish();
	
	/**
	 * Called before interaction is started
	 */
	protected abstract void onInteractionStart();
	
	/**
	 * Called when interaction is not complete and need to be aborted
	 */
	protected abstract void onInteractionAbort();
	
	/**
	 * Interaction scheduling method
	 */
	public void start()
	{
		onInteractionStart();
		
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run()
			{
				if(!validateParticipants())
					abort();
					
				boolean stopTask = onInteraction();
				if(stopTask)
					stop();
			}
	
		}, 1000, interval);
	}
	
	/**
	 * Stop current interaction
	 */
	public void stop()
	{
		onInteractionFinish();
		
		if(task != null && !task.isCancelled())
		{
			task.cancel(true);
			task = null;
		}
	}
	
	/**
	 * Abort current interaction
	 */
	public void abort()
	{
		onInteractionAbort();
		stop();
	}
	
	/**
	 * 
	 * @return true or false
	 */
	public boolean isInProgress()
	{
		return task != null && !task.isCancelled();
	}
	/**
	 * 
	 * @return true or false
	 */
	public boolean validateParticipants()
	{
		return requestor != null;
	}
}
