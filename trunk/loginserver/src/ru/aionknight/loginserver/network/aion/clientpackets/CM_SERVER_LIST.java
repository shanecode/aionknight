package ru.aionknight.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;


import ru.aionknight.loginserver.GameServerTable;
import ru.aionknight.loginserver.controller.AccountController;
import ru.aionknight.loginserver.network.aion.AionAuthResponse;
import ru.aionknight.loginserver.network.aion.AionClientPacket;
import ru.aionknight.loginserver.network.aion.AionConnection;
import ru.aionknight.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;


/**
 * @author -Nemesiss-
 */
public class CM_SERVER_LIST extends AionClientPacket
{
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private int	accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private int	loginOk;

	/**
	 * Constructs new instance of <tt>CM_SERVER_LIST </tt> packet.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_SERVER_LIST(ByteBuffer buf, AionConnection client)
	{
		super(buf, client, 0x05);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		accountId = readD();
		loginOk = readD();
		readD();// unk
		readD();
		readD();
		readH();
		readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection con = getConnection();
		if (con.getSessionKey().checkLogin(accountId, loginOk))
		{
			if (GameServerTable.getGameServers().size() == 0) {
				con.close(new SM_LOGIN_FAIL(AionAuthResponse.NO_GS_REGISTERED), true);
			} else {
				AccountController.loadCharactersCount(accountId);
			}
		}
		else
		{
			/**
			 * Session key is not ok - inform client that smth went wrong - dc client
			 */
			con.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), true);
		}
	}
}
