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

package loginserver.network.gameserver;

import java.nio.ByteBuffer;
import loginserver.network.gameserver.GsConnection.State;
import loginserver.network.gameserver.clientpackets.CM_ACCOUNT_AUTH;
import loginserver.network.gameserver.clientpackets.CM_ACCOUNT_DISCONNECTED;
import loginserver.network.gameserver.clientpackets.CM_ACCOUNT_LIST;
import loginserver.network.gameserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import loginserver.network.gameserver.clientpackets.CM_BAN;
import loginserver.network.gameserver.clientpackets.CM_GS_AUTH;
import loginserver.network.gameserver.clientpackets.CM_GS_CHARACTER_COUNT;
import loginserver.network.gameserver.clientpackets.CM_LS_CONTROL;
import org.apache.log4j.Logger;

public class GsPacketHandler
{
	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(GsPacketHandler.class);

	/**
	 * Reads one packet from given ByteBuffer
	 * 
	 * @param data
	 * @param client
	 * @return GsClientPacket object from binary data
	 */
	public static GsClientPacket handle(ByteBuffer data, GsConnection client)
	{
		GsClientPacket msg = null;
		State state = client.getState();
		int id = data.get() & 0xff;

		switch (state)
		{
			case CONNECTED:
			{
				switch (id)
				{
					case 0x00:
						msg = new CM_GS_AUTH(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
			case AUTHED:
			{
				switch (id)
				{
					case 0x01:
						msg = new CM_ACCOUNT_AUTH(data, client);
						break;
					case 0x02:
						msg = new CM_ACCOUNT_RECONNECT_KEY(data, client);
						break;
					case 0x03:
						msg = new CM_ACCOUNT_DISCONNECTED(data, client);
						break;
					case 0x04:
						msg = new CM_ACCOUNT_LIST(data, client);
						break;
					case 0x05:
						msg = new CM_LS_CONTROL(data, client);
						break;
					case 0x06:
						msg = new CM_BAN(data, client);
						break;
					case 0x07:
						msg = new CM_GS_CHARACTER_COUNT(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
		}
		return msg;
	}

	/**
	 * Logs unknown packet.
	 * 
	 * @param state
	 * @param id
	 */
	private static void unknownPacket(State state, int id)
	{
		log.warn(String.format("Unknown packet recived from Game Server: 0x%02X state=%s", id, state.toString()));
	}
}
