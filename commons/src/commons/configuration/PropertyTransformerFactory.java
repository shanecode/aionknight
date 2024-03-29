/**
 * ������� �������� �� ������� ������������� 'Aion-Knight Dev. Team' �������� ��������� 
 * ����������� ������������; �� ������ �������������� �/��� �������� ��� �������� �������� 
 * ����������� ������������ �������� GNU (GNU GPL), �������������� ������ ���������� 
 * ������������ ����������� (FSF), ���� �������� ������ 3, ���� (�� ���� ����������) ����� 
 * ����� ������� ������.
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

package commons.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;
import commons.configuration.transformers.BooleanTransformer;
import commons.configuration.transformers.ByteTransformer;
import commons.configuration.transformers.CharTransformer;
import commons.configuration.transformers.ClassTransformer;
import commons.configuration.transformers.DoubleTransformer;
import commons.configuration.transformers.EnumTransformer;
import commons.configuration.transformers.FileTransformer;
import commons.configuration.transformers.FloatTransformer;
import commons.configuration.transformers.InetSocketAddressTransformer;
import commons.configuration.transformers.IntegerTransformer;
import commons.configuration.transformers.LongTransformer;
import commons.configuration.transformers.PatternTransformer;
import commons.configuration.transformers.ShortTransformer;
import commons.configuration.transformers.StringTransformer;
import commons.utils.ClassUtils;

public class PropertyTransformerFactory
{
	@SuppressWarnings("rawtypes")
	public static PropertyTransformer newTransformer(Class clazzToTransform, Class<? extends PropertyTransformer> tc)
		throws TransformationException
	{
		if(tc == PropertyTransformer.class)
		{
			tc = null;
		}

		if(tc != null)
		{
			try
			{
				return tc.newInstance();
			}
			catch(Exception e)
			{
				throw new TransformationException("Can't instantiate property transfromer", e);
			}
		}
		else
		{
			if(clazzToTransform == Boolean.class || clazzToTransform == Boolean.TYPE)
			{
				return BooleanTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Byte.class || clazzToTransform == Byte.TYPE)
			{
				return ByteTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Character.class || clazzToTransform == Character.TYPE)
			{
				return CharTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Double.class || clazzToTransform == Double.TYPE)
			{
				return DoubleTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Float.class || clazzToTransform == Float.TYPE)
			{
				return FloatTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Integer.class || clazzToTransform == Integer.TYPE)
			{
				return IntegerTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Long.class || clazzToTransform == Long.TYPE)
			{
				return LongTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Short.class || clazzToTransform == Short.TYPE)
			{
				return ShortTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == String.class)
			{
				return StringTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform.isEnum())
			{
				return EnumTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == File.class)
			{
				return FileTransformer.SHARED_INSTANCE;
			}
			else if(ClassUtils.isSubclass(clazzToTransform, InetSocketAddress.class))
			{
				return InetSocketAddressTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Pattern.class)
			{
				return PatternTransformer.SHARED_INSTANCE;
			}
			else if(clazzToTransform == Class.class)
			{
				return ClassTransformer.SHARED_INSTANCE;
			}
			else
			{
				throw new TransformationException("Transformer not found for class " + clazzToTransform.getName());
			}
		}
	}
}