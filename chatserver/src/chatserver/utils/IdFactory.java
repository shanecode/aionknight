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

package chatserver.utils;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class IdFactory
{
	private final BitSet		idList		= new BitSet();
	private final ReentrantLock	lock		= new ReentrantLock();
	private AtomicInteger		nextMinId	= new AtomicInteger(1);

	protected static IdFactory	instance	= new IdFactory();

	public int nextId()
	{
		try
		{
			lock.lock();
			int id = idList.nextClearBit(nextMinId.intValue());
			idList.set(id);
			nextMinId.incrementAndGet();
			return id;
		}
		finally
		{
			lock.unlock();
		}
	}

	public static IdFactory getInstance()
	{
		return instance;
	}
}