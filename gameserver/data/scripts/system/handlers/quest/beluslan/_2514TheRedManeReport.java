package quest.beluslan;


import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.utils.PacketSendUtility;


/*
 * author : Altaress
 */
public class _2514TheRedManeReport extends QuestHandler
{
    private final static int    questId    = 2514;

    public _2514TheRedManeReport()
    {
        super(questId);
    }
    
    @Override
    public void register()
    {
        qe.setNpcQuestData(204702).addOnQuestStart(questId);
        qe.setNpcQuestData(204702).addOnTalkEvent(questId);
        qe.setNpcQuestData(204700).addOnTalkEvent(questId);
        qe.setNpcQuestData(204763).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestCookie env)
    {
        final Player player = env.getPlayer();
        int targetId = 0;
        if(env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if(targetId == 204702)
        {
            if(qs == null || qs.getStatus() == QuestStatus.NONE)
            {
                if(env.getDialogId() == 26)
                    return sendQuestDialog(env, 1011);
                else
                    return defaultQuestStartDialog(env);
            }
            else if(qs != null && qs.getStatus() == QuestStatus.START)
            {
                if(env.getDialogId() == 26)
                    return sendQuestDialog(env, 2375);
                else if(env.getDialogId() == 1009)
                {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
                else
                    return defaultQuestEndDialog(env);
            }
            else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
            {
                return defaultQuestEndDialog(env);
            }
        }
        else if(targetId == 204700)
        {
            if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
            {
                if(env.getDialogId() == 26)
                    return sendQuestDialog(env, 1352);
                else if(env.getDialogId() == 10000)
                {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
                else
                    return defaultQuestStartDialog(env);
            }
        }
        else if(targetId == 204763)
                {
            if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1)
            {
                if(env.getDialogId() == 26)
                    return sendQuestDialog(env, 1693);
                else if(env.getDialogId() == 10001)
                {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
                else
                    return defaultQuestStartDialog(env);
            }
        }
        else if(targetId == 204702)
        {
            if(qs != null)
            {
                if(env.getDialogId() == 26 && qs.getStatus() == QuestStatus.START)
                    return sendQuestDialog(env, 2375);
                else if(env.getDialogId() == 1009)
                {
                    qs.setQuestVar(3);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return defaultQuestEndDialog(env);
                }
                else
                    return defaultQuestEndDialog(env);
            }
        }
        return false;
    }
}
