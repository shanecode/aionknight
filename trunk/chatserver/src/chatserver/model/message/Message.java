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

package chatserver.model.message;

import chatserver.model.ChatClient;
import chatserver.model.channel.Channel;

public class Message
{
	private Channel channel;
	private byte[] text;
	private ChatClient sender;
	
	/**
	 * 
	 * @param channel
	 * @param text
	 */
	public Message(Channel channel, byte[] text, ChatClient sender)
	{
		this.channel = channel;
		this.text = text;
		this.sender = sender;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel()
	{
		return channel;
	}

	/**
	 * @return the text
	 */
	public byte[] getText()
	{
		return text;
	}	
	
	public int size()
	{
		return text.length;
	}

	/**
	 * @return the sender
	 */
	public ChatClient getSender()
	{
		return sender;
	}
}