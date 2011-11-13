/**
 * This file is part of Aion-Knight Dev. Team [http://aion-knight.ru]
 *
 * Aion-Knight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Aion-Knight is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a  copy  of the GNU General Public License
 * along with Aion-Knight. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.model.gameobjects.stats.modifiers;

import java.util.Collection;

import org.apache.log4j.Logger;

import gameserver.model.gameobjects.AionObject;
import gameserver.utils.ThreadPoolManager;


/**
 * @author blakawk
 *
 */
public abstract class Executor<T extends AionObject>
{
	private static final Logger log = Logger.getLogger(Executor.class);
	
	public abstract boolean run (T object);
	
	private final void runImpl (Collection<T> objects)
	{
		try {
			for (T o : objects)
			{
				if (!Executor.this.run(o))
					break;
			}
		}
		catch (Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}
	
	public final void execute (final Collection<T> objects, boolean now)
	{
		if (now)
		{
			runImpl(objects);
		}
		else
		{
			ThreadPoolManager.getInstance().execute(new Runnable () {
				@Override
				public void run ()
				{
					runImpl(objects);
				}
			});
		}
	}
	
	public final void execute (final Collection<T> objects)
	{
		execute(objects,false);
	}
}
