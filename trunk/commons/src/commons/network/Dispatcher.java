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

package commons.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import org.apache.log4j.Logger;
import commons.options.Assertion;


/**
 * Dispatcher that dispatch SelectionKeys set selected by Selector.
 */
public abstract class Dispatcher extends Thread
{
	/**
	 * Logger for Dispatcher
	 */
	private static final Logger				log		= Logger.getLogger(Dispatcher.class);

	/**
	 * Selector thats selecting ready keys.
	 */
	Selector								selector;

	/**
	 * ThreadPool on witch disconnection tasks will be executed.
	 */
	private final DisconnectionThreadPool	dcPool;
	/**
	 * Object on witch register vs selector.select are synchronized
	 */
	private final Object					gate	= new Object();

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param dcPool
	 * @throws IOException
	 */
	public Dispatcher(String name, DisconnectionThreadPool dcPool) throws IOException
	{
		super(name);
		this.selector = SelectorProvider.provider().openSelector();
		this.dcPool = dcPool;
	}

	/**
	 * Add connection to pendingClose list, so this connection will be closed by this <code>Dispatcher</code> as soon as
	 * possible.
	 * 
	 * @param con
	 * 
	 * @see commons.network.Dispatcher#closeConnection(commons.network.AConnection)
	 */
	abstract void closeConnection(AConnection con);

	/**
	 * Dispatch Selected keys and process pending close.
	 * 
	 * @throws IOException
	 */
	abstract void dispatch() throws IOException;

	/**
	 * @return Selector of this Dispatcher
	 */
	public final Selector selector()
	{
		return this.selector;
	}

	/**
	 * Dispatching Selected keys and processing pending close.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		for(;;)
		{
			try
			{
				dispatch();

				synchronized(gate)
				{
				}
			}
			catch(Exception e)
			{
				log.error("Dispatcher error! " + e, e);
			}
		}
	}

	/**
	 * Register new client connected to this Dispatcher and set SelectionKey (result of registration) as this key of
	 * given AConnection.
	 * 
	 * @param ch
	 * @param ops
	 * @param att
	 * @throws IOException
	 */
	public final void register(SelectableChannel ch, int ops, AConnection att) throws IOException
	{
		synchronized(gate)
		{
			selector.wakeup();
			att.setKey(ch.register(selector, ops, att));
		}
	}

	/**
	 * Register new Acceptor this Dispatcher and return SelectionKey (result of registration).
	 * 
	 * @param ch
	 * @param ops
	 * @param att
	 * @return SelectionKey representing this registration.
	 * @throws IOException
	 */
	public final SelectionKey register(SelectableChannel ch, int ops, Acceptor att) throws IOException
	{
		synchronized(gate)
		{
			selector.wakeup();
			return ch.register(selector, ops, att);
		}
	}

	/**
	 * Accept new connection.
	 * 
	 * @param key
	 */
	final void accept(SelectionKey key)
	{
		try
		{
			((Acceptor) key.attachment()).accept(key);
		}
		catch(Exception e)
		{
			log.error("Error while accepting connection: +" + e, e);
		}
	}

	/**
	 * Read data from socketChannel represented by SelectionKey key. Parse and Process data. Prepare buffer for next
	 * read.
	 * 
	 * @param key
	 */
	final void read(SelectionKey key)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		AConnection con = (AConnection) key.attachment();

		ByteBuffer rb = con.readBuffer;

		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if(Assertion.NetworkAssertion)
		{
			assert con.readBuffer.hasRemaining();
		}

		/** Attempt to read off the channel */
		int numRead;
		try
		{
			numRead = socketChannel.read(rb);
		}
		catch(IOException e)
		{
			closeConnectionImpl(con);
			return;
		}

		if(numRead == -1)
		{
			/**
			 * Remote entity shut the socket down cleanly. Do the same from our end and cancel the channel.
			 */
			closeConnectionImpl(con);
			return;
		}
		else if(numRead == 0)
		{
			return;
		}

		rb.flip();
		while(rb.remaining() > 2 && rb.remaining() >= rb.getShort(rb.position()))
		{
			/** got full message */
			if(!parse(con, rb))
			{
				closeConnectionImpl(con);
				return;
			}
		}
		if(rb.hasRemaining())
		{
			con.readBuffer.compact();

			/**
			 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
			 */
			if(Assertion.NetworkAssertion)
			{
				assert con.readBuffer.hasRemaining();
			}
		}
		else
			rb.clear();
	}

	/**
	 * Parse data from buffer and prepare buffer for reading just one packet - call processData(ByteBuffer b).
	 * 
	 * @param con
	 *           Connection
	 * @param buf
	 *           Buffer with packet data
	 * @return True if packet was parsed.
	 */
	private boolean parse(AConnection con, ByteBuffer buf)
	{
		short sz = 0;
		try
		{
			sz = buf.getShort();
			if(sz > 1)
				sz -= 2;
			ByteBuffer b = (ByteBuffer) buf.slice().limit(sz);
			b.order(ByteOrder.LITTLE_ENDIAN);
			/** read message fully */
			buf.position(buf.position() + sz);

			return con.processData(b);
		}
		catch(IllegalArgumentException e)
		{
			log.warn("Error on parsing input from client - account: " + con + " packet size: " + sz + " real size:"
				+ buf.remaining(), e);
			return false;
		}
	}

	/**
	 * Write as much as possible data to socketChannel represented by SelectionKey key. If all data were written key
	 * write interest will be disabled.
	 * 
	 * @param key
	 */
	final void write(SelectionKey key)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		AConnection con = (AConnection) key.attachment();

		int numWrite;
		ByteBuffer wb = con.writeBuffer;
		/** We have not writted data */
		if(wb.hasRemaining())
		{
			try
			{
				numWrite = socketChannel.write(wb);
			}
			catch(IOException e)
			{
				closeConnectionImpl(con);
				return;
			}

			if(numWrite == 0)
			{
				log.info("Write " + numWrite + " ip: " + con.getIP());
				return;
			}

			/** Again not all data was send */
			if(wb.hasRemaining())
				return;
		}

		while(true)
		{
			wb.clear();
			boolean writeFailed = !con.writeData(wb);

			if(writeFailed)
			{
				wb.limit(0);
				break;
			}

			/** Attempt to write to the channel */
			try
			{
				numWrite = socketChannel.write(wb);
			}
			catch(IOException e)
			{
				closeConnectionImpl(con);
				return;
			}

			if(numWrite == 0)
			{
				log.info("Write " + numWrite + " ip: " + con.getIP());
				return;
			}

			/** not all data was send */
			if(wb.hasRemaining())
				return;
		}

		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if(Assertion.NetworkAssertion)
		{
			assert !wb.hasRemaining();
		}

		/**
		 * We wrote away all data, so we're no longer interested in writing on this socket.
		 */
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

		/** We wrote all data so we can close connection that is "PandingClose" */
		if(con.isPendingClose())
			closeConnectionImpl(con);
	}

	/**
	 * Connection will be closed [onlyClose()] and onDisconnect() method will be executed on another thread
	 * [DisconnectionThreadPool] after getDisconnectionDelay() time in ms. This method may only be called by current
	 * Dispatcher Thread.
	 * 
	 * @param con
	 */
	protected final void closeConnectionImpl(AConnection con)
	{
		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if(Assertion.NetworkAssertion)
			assert Thread.currentThread() == this;

		if(con.onlyClose())
			dcPool.scheduleDisconnection(new DisconnectionTask(con), con.getDisconnectionDelay());
	}
}