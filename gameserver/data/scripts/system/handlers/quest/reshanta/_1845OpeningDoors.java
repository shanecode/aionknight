package quest.reshanta;

import org.openaion.gameserver.model.gameobjects.Npc;
import org.openaion.gameserver.model.gameobjects.player.Player;
import org.openaion.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.openaion.gameserver.quest.handlers.QuestHandler;
import org.openaion.gameserver.quest.model.QuestCookie;
import org.openaion.gameserver.quest.model.QuestState;
import org.openaion.gameserver.quest.model.QuestStatus;
import org.openaion.gameserver.services.QuestService;
import org.openaion.gameserver.utils.PacketSendUtility;


/**
 * @author Assholes
 * 
 */
public class _1845OpeningDoors extends QuestHandler {
	private final static int questId = 1845;

	public _1845OpeningDoors() {
		super(questId);
	}

	@Override
	public void register() {
        	qe.setNpcQuestData(278591).addOnTalkEvent(questId);
		qe.setNpcQuestData(278624).addOnTalkEvent(questId);
		qe.setNpcQuestData(798316).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env) {
	final Player player = env.getPlayer();
	int targetId = 0;
	final QuestState qs = player.getQuestStateList().getQuestState(questId);
	if (env.getVisibleObject() instanceof Npc)
	targetId = ((Npc) env.getVisibleObject()).getNpcId();

	if (qs == null || qs.getStatus() == QuestStatus.NONE) {
		if (env.getDialogId() == 1002) {
			QuestService.startQuest(env, QuestStatus.START);
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
			return true;
            } else
                	PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
	}
	if(qs == null)
	return false;

	int var = qs.getQuestVarById(0);

	if(qs.getStatus() == QuestStatus.REWARD)
	{
		if(targetId == 798316)
		{
			if(env.getDialogId() == -1)
				return sendQuestDialog(env, 2375);
			else if(env.getDialogId() == 1009)
				return sendQuestDialog(env, 5);
			else
				return defaultQuestEndDialog(env);
		}
	}
	else if(qs.getStatus() != QuestStatus.START)
	{
		return false;
	}
	if(targetId == 278591)
	{
		switch(env.getDialogId())
		{
			case 26:
				if(var == 0)
					return sendQuestDialog(env, 1352);
			case 10000:
				if(var == 0)
				{
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			return false;
		}
	}
	else if(targetId == 278624)
	{
		switch(env.getDialogId())
		{
			case 26:
				if(var == 1)
					return sendQuestDialog(env, 1693);
			case 10001:
				if(var == 1)
				{
					qs.setQuestVarById(0, var + 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);								
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			return false;
		}
	}
	return false;
     }
}
