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

import gameserver.configs.administration.AdminConfig;
import gameserver.model.alliance.PlayerAllianceMember;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.legion.Legion;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;

public class MoveToMe extends AdminCommand
{

	/**
	 * Constructor.
	 */

	public MoveToMe()
	{
		super("movetome");
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_MOVETOME)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //movetome <player name> [group|alliance|legion]");
			return;
		}

		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));

		if (playerToMove == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		if (playerToMove == admin)
		{
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}
		
		if(params.length == 2)
		{
			if(params[1].equals("alliance"))
			{
				if(playerToMove.getPlayerAlliance() != null)
				{
					for(PlayerAllianceMember m : playerToMove.getPlayerAlliance().getMembers())
					{
						if(m != null && m.getPlayer() != null)
						{
							port(admin, m.getPlayer());
						}
					}
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "This player is not in alliance.");
					port(admin, playerToMove);
				}
			}
			else if(params[1].equals("group"))
			{
				if(playerToMove.isInGroup() && playerToMove.getPlayerGroup() != null)
				{
					for(Player p : playerToMove.getPlayerGroup().getMembers())
					{
						if(p != null)
							port(admin, p);
					}
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "This player is not in group.");
					port(admin, playerToMove);
				}
			}
			else if(params[1].equals("legion"))
			{
				if(playerToMove.getLegion() != null)
				{
					Legion legion = playerToMove.getLegion();
					for(Integer pid : legion.getLegionMembers())
					{
						Player target = World.getInstance().findPlayer(pid);
						if(target != null)
							port(admin, target);
					}
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "This player is not in a legion.");
					port(admin, playerToMove);
				}
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "syntax //movetome <player name> [group|alliance|legion]");
				return;
			}
		}
		else
			port(admin, playerToMove);

		
	}
	
	private void port(Player admin, Player playerToMove)
	{
		TeleportService.teleportTo(playerToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(), 0);
		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to your location.");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by " + admin.getName() + ".");
	}
	
}
