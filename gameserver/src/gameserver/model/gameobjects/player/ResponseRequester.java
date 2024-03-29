/*
 * Emulator game server Aion 2.7 from the command of developers 'Aion-Knight Dev. Team' is
 * free software; you can redistribute it and/or modify it under the terms of
 * GNU affero general Public License (GNU GPL)as published by the free software
 * security (FSF), or to License version 3 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranties related to
 * CONSUMER PROPERTIES and SUITABILITY FOR CERTAIN PURPOSES. For details, see
 * General Public License is the GNU.
 *
 * You should have received a copy of the GNU affero general Public License along with this program.
 * If it is not, write to the Free Software Foundation, Inc., 675 Mass Ave,
 * Cambridge, MA 02139, USA
 *
 * Web developers : http://aion-knight.ru
 * Support of the game client : Aion 2.7- 'Arena of Death' (Innova)
 * The version of the server : Aion-Knight 2.7 (Beta version)
 */

package gameserver.model.gameobjects.player;

import gameserver.network.aion.AionServerPacket;
import gameserver.utils.PacketSendUtility;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseRequester
{
	private Player										player;
	private HashMap<Integer, RequestResponseHandler>	map			= new HashMap<Integer, RequestResponseHandler>();
	private ArrayList<ResponseRequesterEntry>			waitingList	= new ArrayList<ResponseRequesterEntry>();
	private static Logger								log			= Logger.getLogger(ResponseRequester.class);

	public ResponseRequester(Player player)
	{
		this.player = player;
	}

	/**
	 * Adds this handler to this messageID, returns false if there already exists one
	 * @param messageId ID of the request message
	 * @return true or false
	 */
	public synchronized boolean putRequest(int messageId, RequestResponseHandler handler)
	{
		if (map.containsKey(messageId))
			return false;

		map.put(messageId, handler);
		return true;
	}

	/**
	 * Adds this handler to the messageId, puts the messageID on a waitinglist if there is already a message of this
	 * type send to the player.
	 * 
	 * @param messageId
	 *           The Id of the message.
	 * @param handler
	 *           The handler for the response.
	 * @param packet
	 *           The message to send to the player.
	 * @return
	 */
	public synchronized boolean sendRequest(int messageId, RequestResponseHandler handler, AionServerPacket packet)
	{
		if(map.containsKey(messageId))
		{
			waitingList.add(new ResponseRequesterEntry(messageId, handler, packet));
			return true;
		}

		map.put(messageId, handler);
		PacketSendUtility.sendPacket(player, packet);
		return true;
	}

	/**
	 * Send the next request of the same type.
	 * 
	 * @return True if a new request is send, else false.
	 */
	private boolean sendWaitingRequest(int messageId)
	{
		for(ResponseRequesterEntry rre : waitingList)
		{
			if(rre.getMessageId() == messageId)
			{
				map.put(messageId, rre.getHandler());
				PacketSendUtility.sendPacket(player, rre.getPacket());
				waitingList.remove(rre);
				return true;
			}
		}
		map.remove(messageId);
		return false;
	}

	/**
	 * Responds to the given message ID with the given response
	 * Returns success
	 * @param messageId
	 * @param response
	 * @return Success
	 */
	public synchronized boolean respond(int messageId, int response)
	{
		RequestResponseHandler handler = map.get(messageId);
		if (handler != null)
		{
			log.debug("RequestResponseHandler triggered for response code " + messageId + " from " + player.getName());
			handler.handle(player, response);
			sendWaitingRequest(messageId);
			return true;
		}
		return false;
	}

	/**
	 * Automatically responds 0 to all requests, passing the given player as the responder
	 */
	public synchronized void denyAll()
	{
		for (RequestResponseHandler handler : map.values())
		{
			handler.handle(player, 0);
		}
		for(ResponseRequesterEntry rre : waitingList)
		{
			rre.getHandler().handle(player, 0);
		}

		map.clear();
		waitingList.clear();
	}
}
