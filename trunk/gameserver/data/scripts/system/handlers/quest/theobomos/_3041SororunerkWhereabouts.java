package quest.theobomos;

import java.util.Collections;


import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.templates.quest.QuestItems;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.ItemService;
import ru.aionknight.gameserver.utils.PacketSendUtility;


/**
 * @author Assholes
 *
 */
public class _3041SororunerkWhereabouts extends QuestHandler {
	private final static int questId = 3041;
	private final static int[] npc_ids = { 798167,700378 };
	
	public _3041SororunerkWhereabouts() {
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.setNpcQuestData(798167).addOnQuestStart(questId);
		for(int npc_id : npc_ids)
			qe.setNpcQuestData(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env) {
	final Player player = env.getPlayer();
	int targetId = 0;
	if (env.getVisibleObject() instanceof Npc)
	targetId = ((Npc) env.getVisibleObject()).getNpcId();
	QuestState qs = player.getQuestStateList().getQuestState(questId);
	if(targetId == 798167)
	{
                        if(qs == null || qs.getStatus() == QuestStatus.NONE)
                        {
                                if(env.getDialogId() == 26)
                                         return sendQuestDialog(env, 1011);
                                else
                                        return defaultQuestStartDialog(env);
                        }
	}
	if(qs == null)
	return false;

	int var = qs.getQuestVarById(0);
	if(qs.getStatus() == QuestStatus.REWARD)
	{
		if(targetId == 798167)
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
	if(targetId == 700378)
	{
		switch(env.getDialogId())
		{
			case -1:
				if(var == 0)
					return sendQuestDialog(env, 1352);
			case 10000:
				if(var == 0)
				{
					if (ItemService.addItems(player, Collections.singletonList(new QuestItems(182208031, 1))));
					qs.setQuestVarById(0, var + 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);			
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					return true;
				}
			return false;
		}
	}
	return false;
    }
}