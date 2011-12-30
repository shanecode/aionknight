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

package gameserver.services;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerCmotionListDAO;
import gameserver.model.gameobjects.player.Cmotion;
import gameserver.model.gameobjects.player.CmotionList;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_CMOTION;
import gameserver.utils.PacketSendUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CmotionService
{
	public static boolean isExpired(long expires_time, long date)
	{
		if(expires_time > 0)
		{
			long timeLeft = (date + (expires_time * 1000L)) - System.currentTimeMillis();
			if(timeLeft < 0)
			{
				return true;
			}
		}
		return false;
	}

	public static void removeCmotion(int playerId, int cmotionId)
	{
		DAOManager.getDAO(PlayerCmotionListDAO.class).removeCmotion(playerId, cmotionId);
	}

	public static void removeExpiredCmotions(Player player)
	{
		CmotionList cmotionList = player.getCmotionList();
		List<Integer> delCmotions = new ArrayList<Integer>();
		boolean removed = false;

		for(Cmotion cmotion : cmotionList.getCmotions())
		{
			if(CmotionService.isExpired(cmotion.getCmotionExpiresTime(), cmotion.getCmotionCreationTime()))
			{
				delCmotions.add(cmotion.getCmotionId());
			}
		}

		Iterator<Integer> iterator = delCmotions.iterator();
		while(iterator.hasNext())
		{
			int cmotionId = iterator.next();
			removeCmotion(player.getObjectId(), cmotionId);
			cmotionList.remove(cmotionId);
			removed = true;
		}

		if(removed)
		{
			PacketSendUtility.sendPacket(player, new SM_CMOTION(player));
			PacketSendUtility.sendMessage(player, "The usage time of cmotion has expired.");
		}
	}
}