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

package gameserver.task;

import javolution.util.FastList;
import java.util.Collection;

public abstract class FIFOSimpleExecutableQueue<T> extends FIFOExecutableQueue
{
	private final FastList<T> queue = new FastList<T>();
	public final void execute(T t)
	{
		synchronized (queue)
		{
			queue.addLast(t);
		}
		
		execute();
	}
	
	public final void executeAll(Collection<T> c)
	{
		synchronized (queue)
		{
			queue.addAll(c);
		}
		
		execute();
	}
	
	public final void remove(T t)
	{
		synchronized (queue)
		{
			queue.remove(t);
		}
	}
	
	@Override
	protected final boolean isEmpty()
	{
		synchronized (queue)
		{
			return queue.isEmpty();
		}
	}
	
	protected final T removeFirst()
	{
		synchronized (queue)
		{
			return queue.removeFirst();
		}
	}
	
	@Override
	protected abstract void removeAndExecuteFirst();
}