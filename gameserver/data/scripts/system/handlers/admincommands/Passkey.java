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

package admincommands;

import commons.database.dao.DAOManager;
import gameserver.configs.administration.AdminConfig;
import gameserver.dao.PlayerDAO;
import gameserver.dao.PlayerPasskeyDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.AdminCommand;

public class Passkey extends AdminCommand
{
	public Passkey()
	{
		super("passkey");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_PASSKEY)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax: //passkey <player> <passkey>");
			return;
		}

		String name = Util.convertName(params[0]);
		int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
		if (accountId == 0)
		{
			PacketSendUtility.sendMessage(admin, "player " + name + " can't find!");
			PacketSendUtility.sendMessage(admin, "syntax: //passkey <player> <passkey>");
			return;
		}

		try
		{
			Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "parameters should be number!");
			return;
		}

		String newPasskey = params[1];
		if (!(newPasskey.length() > 5 && newPasskey.length() < 9))
		{
			PacketSendUtility.sendMessage(admin, "passkey is 6~8 digits!");
			return;
		}

		DAOManager.getDAO(PlayerPasskeyDAO.class).updateForcePlayerPasskey(accountId, newPasskey);
		LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", -1, admin.getObjectId());
	}
}