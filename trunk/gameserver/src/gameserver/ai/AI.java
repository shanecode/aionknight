/**   
 * �������� �������� ������� Aion 2.7 �� ������� ������������� 'Aion-Knight Dev. Team' �������� 
 * ��������� ����������� ������������; �� ������ �������������� �/��� �������� ��� �������� �������� 
 * ����������� ������������ �������� GNU (GNU GPL), �������������� ������ ���������� ������������ 
 * ����������� (FSF), ���� �������� ������ 3, ���� (�� ���� ����������) ����� ����� ������� 
 * ������.
 * 
 * ��������� ���������������� � �������, ��� ��� ����� ��������, �� ��� ����� �� �� �� ���� 
 * ����������� ������������; ���� ��� ���������  �����������  ������������, ��������� � 
 * ���������������� ���������� � ������������ ��� ������������ �����. ��� ������������ �������� 
 * ����������� ������������ �������� GNU.
 * 
 * �� ������ ���� �������� ����� ����������� ������������ �������� GNU ������ � ���� ����������. 
 * ���� ��� �� ���, �������� � ���� ���������� �� (Free Software Foundation, Inc., 675 Mass Ave, 
 * Cambridge, MA 02139, USA
 * 
 * ���-c��� ������������� : http://aion-knight.ru
 * ��������� ������� ���� : Aion 2.7 - '����� ������' (������) 
 * ������ ��������� ����� : Aion-Knight 2.7 (Beta version)
 */

package gameserver.ai;

import gameserver.ai.desires.Desire;
import gameserver.ai.desires.DesireQueue;
import gameserver.ai.desires.impl.CounterBasedDesireFilter;
import gameserver.ai.desires.impl.GeneralDesireIteratorHandler;
import gameserver.ai.events.Event;
import gameserver.ai.events.handler.EventHandler;
import gameserver.ai.npcai.DummyAi;
import gameserver.ai.state.AIState;
import gameserver.ai.state.handler.StateHandler;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.ThreadPoolManager;
import javolution.util.FastMap;

import java.util.Map;
import java.util.concurrent.Future;

public abstract class AI<T extends Creature> implements Runnable
{	

	private static final DummyAi dummyAi = new DummyAi();
	protected Map<Event, EventHandler> eventHandlers = new FastMap<Event, EventHandler>();
	protected Map<AIState, StateHandler> stateHandlers = new FastMap<AIState, StateHandler>();
	protected DesireQueue desireQueue = new DesireQueue();
	protected Creature owner;
	protected AIState aiState = AIState.NONE;
	protected boolean isStateChanged = false;
	private Future<?> aiTask;	
	public void handleEvent(Event event)
	{

	}

	public void handleTalk(Player player)
	{
		
	}

	public Creature getOwner()
	{
		return owner;
	}

	public void setOwner(Creature owner)
	{
		this.owner = owner;
	}

	public AIState getAiState()
	{
		return aiState;
	}

	protected void addEventHandler(EventHandler eventHandler)
	{
		this.eventHandlers.put(eventHandler.getEvent(), eventHandler);
	}
	
	public void clearEventHandler()
	{
		this.eventHandlers.clear();
	}

	protected void addStateHandler(StateHandler stateHandler)
	{
		this.stateHandlers.put(stateHandler.getState(), stateHandler);
	}
	
	public void clearStateHandler()
	{
		this.stateHandlers.clear();
	}

	public void setAiState(AIState aiState)
	{
		if(this.aiState != aiState)
		{
			this.aiState = aiState;
			isStateChanged = true;
		}
	}

	public void analyzeState()
	{
		isStateChanged = false;
		StateHandler stateHandler = stateHandlers.get(aiState);
		if(stateHandler != null)
			stateHandler.handleState(aiState, this);
	}
	
	@Override
	public void run()
	{
		desireQueue.iterateDesires(new GeneralDesireIteratorHandler(this), new CounterBasedDesireFilter());
		if(desireQueue.isEmpty() || isStateChanged)
		{
			analyzeState();
		}
	}

	public void schedule()
	{
		if(!isScheduled())
		{
			aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
		}	
	}

	public void stop()
	{
		if(aiTask != null && !aiTask.isCancelled())
		{
			aiTask.cancel(true);
			aiTask = null;
		}
	}

	public boolean isScheduled()
	{
		return aiTask != null && !aiTask.isCancelled();
	}
	
	public void clearDesires()
	{
		this.desireQueue.clear();
	}
	
	public void addDesire(Desire desire)
	{
		this.desireQueue.add(desire);
	}
	
	public int desireQueueSize()
	{
		return desireQueue.isEmpty() ? 0 : desireQueue.size();
	}
	
	public DesireQueue getDesireQueue()
	{
		return desireQueue;
	}

	public static DummyAi dummyAi()
	{
		return dummyAi;
	}
}