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

package chatserver.network.aion.clientpackets;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import chatserver.model.channel.Channel;
import chatserver.model.channel.Channels;
import chatserver.model.message.Message;
import chatserver.network.aion.AbstractClientPacket;
import chatserver.network.netty.handler.ClientChannelHandler;
import chatserver.service.BroadcastService;

public class CM_CHANNEL_MESSAGE extends AbstractClientPacket
{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(CM_CHANNEL_MESSAGE.class);

	private int					channelId;
	private byte[]				content;

	/**
	 * 
	 * @param channelBuffer
	 * @param gameChannelHandler
	 * @param opCode
	 */
	public CM_CHANNEL_MESSAGE(ChannelBuffer channelBuffer, ClientChannelHandler gameChannelHandler)
	{
		super(channelBuffer, gameChannelHandler, 0x18);
	}

	@Override
	protected void readImpl()
	{
		readH();
		readC();
		readD();
		readD();
		channelId = readD();
		int lenght = readH() * 2;
		content = readB(lenght);
	}

	@Override
	protected void runImpl()
	{
		Channel channel = Channels.getChannelById(channelId);
		Message message = new Message(channel, content, clientChannelHandler.getChatClient());
		BroadcastService.getInstance().broadcastMessage(message);
	}

	@Override
	public String toString()
	{
		return "CM_CHANNEL_MESSAGE [channelId=" + channelId + ", content=" + Arrays.toString(content) + "]";
	}
}