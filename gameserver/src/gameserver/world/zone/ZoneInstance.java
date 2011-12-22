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

package gameserver.world.zone;

import gameserver.model.templates.zone.Point2D;
import gameserver.model.templates.zone.ZoneTemplate;
import java.util.Collection;

public class ZoneInstance
{
	private int	corners;
	private float xCoordinates[];
	private float yCoordinates[];
	private ZoneTemplate template;
	private Collection<ZoneInstance> neighbors;
	public ZoneInstance(ZoneTemplate template)
	{
		this.template = template;
		this.corners = template.getPoints().getPoint().size();
		xCoordinates = new float[corners];
		yCoordinates = new float[corners];
		for(int i = 0; i < corners; i++)
		{
			Point2D point = template.getPoints().getPoint().get(i);
			xCoordinates[i] = point.getX();
			yCoordinates[i] = point.getY();
		}
	}

	public int getCorners()
	{
		return corners;
	}

	public float[] getxCoordinates()
	{
		return xCoordinates;
	}

	public float[] getyCoordinates()
	{
		return yCoordinates;
	}

	public Collection<ZoneInstance> getNeighbors()
	{
		return neighbors;
	}

	public void setNeighbors(Collection<ZoneInstance> neighbours)
	{
		this.neighbors = neighbours;
	}

	public ZoneTemplate getTemplate()
	{
		return template;
	}

	public float getTop()
	{
		return template.getPoints().getTop();
	}

	public float getBottom()
	{
		return template.getPoints().getBottom();
	}

	public boolean isBreath()
	{
		return template.isBreath();
	}

	public int getPriority()
	{
		return template.getPriority();
	}
}