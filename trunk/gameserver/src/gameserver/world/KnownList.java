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

package gameserver.world;

import gameserver.model.flyring.FlyRing;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.gameobjects.stats.modifiers.ObjectContainer;
import gameserver.model.shield.Shield;
import gameserver.utils.MathUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KnownList extends ObjectContainer
{
	private static final float VisibilityDistance = 95;
	private static final float maxZvisibleDistance = 95;
	protected final VisibleObject owner;
	private long lastUpdate;
	public KnownList(VisibleObject owner)
	{
		super();
		this.owner = owner;
	}

	public void doUpdate()
	{
		if((System.currentTimeMillis() - lastUpdate) < 1000)
			return;

		findVisibleObjects();

		lastUpdate = System.currentTimeMillis();
	}

	public void clear()
	{
		AionObject obj;
		
		List<VisibleObject> toRemove = new ArrayList<VisibleObject> ();
		
		synchronized(allObjects)
		{
			for (Iterator<AionObject> i = allObjects.values().iterator(); i.hasNext(); )
			{
				obj = i.next();
				i.remove();
				if (obj instanceof VisibleObject)
				{
					toRemove.add((VisibleObject)obj);
				}
			}
		}
		
		for (int i = toRemove.size() - 1; i >= 0; i--)
		{
			VisibleObject vObj = toRemove.get(i);
			vObj.getKnownList().removeObject(owner, false);	
		}
	}

	/**
	 * Check if object is known
	 * 
	 * @param object
	 * @return true if object is known
	 */
	public boolean knowns(AionObject object)
	{
		return allObjects.containsKey(object.getObjectId());
	}

	/**
	 * Add VisibleObject to this KnownList.
	 * 
	 * @param object
	 */
	@Override
	public void storeObject (AionObject object)
	{
		if (!(object instanceof VisibleObject))
		{
			throw new RuntimeException("Cannot store "+object.getClass().getCanonicalName()+" in "+getClass().getCanonicalName());
		}
		
		boolean callSee = !knowns(object);
		
		super.storeObject(object);
		
		if (callSee)
		{
			owner.getController().see((VisibleObject)object);
		}
	}

	/**
	 * Delete VisibleObject from this KnownList.
	 * 
	 * @param object
	 */
	private void removeObject(VisibleObject object, boolean isOutOfRange)
	{
		boolean callNotSee = knowns(object);
		
		super.removeObject(object);
		
		/**
		 * object was known.
		 */
		if(callNotSee)
		{
			owner.getController().notSee(object, isOutOfRange);
		}
	}
	
	@Override
	public void removeObject (AionObject object)
	{
		if (!(object instanceof VisibleObject))
		{
			throw new RuntimeException("Cannot remove "+object.getClass().getCanonicalName()+" in "+getClass().getCanonicalName());
		}
		
		this.removeObject((VisibleObject)object, true);
	}

	/**
	 * Find objects that are in visibility range.
	 */
	private class KnownListExecutor extends Executor<AionObject>
	{
		private List<VisibleObject> objectsToDel;
		private List<VisibleObject> objectsToAdd;
		private VisibleObject owner;
		
		private KnownListExecutor (VisibleObject owner)
		{
			this.objectsToAdd = new ArrayList<VisibleObject> ();
			this.objectsToDel = new ArrayList<VisibleObject> ();
			this.owner = owner;
		}
		
		private void updateKnownObjects ()
		{
			if (owner == null || !owner.isSpawned())
				return;
			
			for (MapRegion r : owner.getActiveRegion().getNeighbours())
			{
				if (r!=null)
					r.doOnAllObjects(KnownListExecutor.this, true);
			}

			if (objectsToAdd.size() > 0)
			{
				for (VisibleObject object : objectsToAdd)
				{
					storeObject(object);
					object.getKnownList().storeObject(owner);
				}

				objectsToAdd.clear();
			}

			if (objectsToDel.size() > 0)
			{

				for (int i = objectsToDel.size() - 1; i >= 0; i--)
				{
					VisibleObject object = objectsToDel.get(i);
					removeObject(object);
					object.getKnownList().removeObject(owner);
				}

				objectsToDel.clear();
			}
		}
		
		@Override
		public boolean run(AionObject newObject)
		{
			if(newObject == owner || newObject == null || !(newObject instanceof VisibleObject))
				return true;

			if(!checkObjectInRange(owner, (VisibleObject)newObject))
			{
				if (owner.getKnownList().knowns(newObject))
				{
					objectsToDel.add((VisibleObject)newObject);
				}
				return true;
			}

			/**
			 * New object is not known.
			 */
			if(!knowns(newObject))
				objectsToAdd.add((VisibleObject)newObject);
			return true;
		}
	}
	
	protected void findVisibleObjects()
	{
		if(owner == null || !owner.isSpawned())
			return;

		KnownListExecutor kle = new KnownListExecutor(owner);
		kle.updateKnownObjects();
	}

	protected boolean checkObjectInRange(VisibleObject owner, VisibleObject newObject)
	{
		if(newObject instanceof Shield)
		{
			return newObject.getKnownList().checkObjectInRange(newObject, owner);
		}
		
		if(newObject instanceof FlyRing)
		{
			return newObject.getKnownList().checkObjectInRange(newObject, owner);
		}
		if(Math.abs(owner.getZ() - newObject.getZ()) > maxZvisibleDistance)
			return false;

		return MathUtil.isInRange(owner, newObject, VisibilityDistance);
	}
}