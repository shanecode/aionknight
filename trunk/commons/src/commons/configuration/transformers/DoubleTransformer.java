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

package commons.configuration.transformers;

import java.lang.reflect.Field;
import commons.configuration.PropertyTransformer;
import commons.configuration.TransformationException;

public class DoubleTransformer implements PropertyTransformer<Double>
{
	public static final DoubleTransformer	SHARED_INSTANCE	= new DoubleTransformer();

	@Override
	public Double transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Double.parseDouble(value);
		}
		catch(Exception e)
		{
			throw new TransformationException(e);
		}
	}
}