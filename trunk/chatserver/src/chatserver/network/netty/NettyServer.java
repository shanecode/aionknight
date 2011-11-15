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

package chatserver.network.netty;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import chatserver.configs.Config;
import chatserver.network.netty.pipeline.LoginToClientPipeLineFactory;
import chatserver.network.netty.pipeline.LoginToGamePipelineFactory;

public class NettyServer
{
	private static final Logger logger = Logger.getLogger(NettyServer.class);
	private final ChannelGroup channelGroup	= new DefaultChannelGroup(NettyServer.class.getName());
	private final LoginToClientPipeLineFactory loginToClientPipeLineFactory;
	private final LoginToGamePipelineFactory loginToGamePipelineFactory;
	private ChannelFactory loginToClientChannelFactory;
	private ChannelFactory loginToGameChannelFactory;

	public NettyServer()
	{
		this.loginToClientPipeLineFactory = new LoginToClientPipeLineFactory();
		this.loginToGamePipelineFactory = new LoginToGamePipelineFactory();
		initialize();
	}

	public void initialize()
	{
		loginToClientChannelFactory = initChannelFactory();
		loginToGameChannelFactory = initChannelFactory();

		Channel loginToClientChannel = initChannel(loginToClientChannelFactory,
			Config.CHAT_ADDRESS, loginToClientPipeLineFactory);
		Channel loginToGameChannel = initChannel(loginToGameChannelFactory, Config.GAME_ADDRESS,
			loginToGamePipelineFactory);

		channelGroup.add(loginToClientChannel);
		channelGroup.add(loginToGameChannel);

		logger.info("Chat Server started");
	}

	private NioServerSocketChannelFactory initChannelFactory()
	{
		return new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(),
			Runtime.getRuntime().availableProcessors() * 2 + 1);
	}

	private Channel initChannel(ChannelFactory channelFactory, InetSocketAddress address,
		ChannelPipelineFactory channelPipelineFactory)
	{
		ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
		bootstrap.setPipelineFactory(channelPipelineFactory);
		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);
		bootstrap.setOption("readWriteFair", true);

		return bootstrap.bind(address);
	}

	public void shutdownAll()
	{
		ChannelGroupFuture future = channelGroup.close();
		future.awaitUninterruptibly();
		loginToClientChannelFactory.releaseExternalResources();
		loginToGameChannelFactory.releaseExternalResources();
	}
}