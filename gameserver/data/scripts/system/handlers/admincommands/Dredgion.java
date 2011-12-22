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

package admincommands;

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.services.DredgionInstanceService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;

public class Dredgion extends AdminCommand
{
	public Dredgion()
	{
		super("dredgion");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_DREDGION)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command.");
			return;
		}

		World.getInstance().doOnAllPlayers(new Executor<Player> () {
			@Override
			public boolean run(Player player)
			{
				DredgionInstanceService.getInstance().sendDredgionEntry(player);
				return true;
			}
		});
	}
}