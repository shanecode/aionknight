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
package gameserver.utils.collections.cachemap;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.apache.log4j.Logger;

/**
 * This class is a simple map implementation for cache usage.<br>
 * <br>
 * Values from the map will be removed after the first garbage collector run if there isn't any strong reference to the
 * value object.
 * 
 * @author Luno
 * 
 */
class WeakCacheMap<K, V> extends AbstractCacheMap<K, V> implements CacheMap<K, V>
{
	private static final Logger	log	= Logger.getLogger(WeakCacheMap.class);

	/**
	 * This class is a {@link WeakReference} with additional responsibility of holding key object
	 * 
	 * @author Luno
	 */
	private class Entry extends WeakReference<V>
	{
		private K	key;

		Entry(K key, V referent, ReferenceQueue<? super V> q)
		{
			super(referent, q);
			this.key = key;
		}

		K getKey()
		{
			return key;
		}
	}

	WeakCacheMap(String cacheName, String valueName)
	{
		super(cacheName, valueName, log);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected synchronized void cleanQueue()
	{
		Entry en = null;
		while((en = (Entry) refQueue.poll()) != null)
		{
			K key = en.getKey();
			if(log.isDebugEnabled())
				log.debug(cacheName + " : cleaned up " + valueName + " for key: " + key);
			cacheMap.remove(key);
		}
	}

	@Override
	protected Reference<V> newReference(K key, V value, ReferenceQueue<V> vReferenceQueue)
	{
		return new Entry(key, value, vReferenceQueue);
	}
}