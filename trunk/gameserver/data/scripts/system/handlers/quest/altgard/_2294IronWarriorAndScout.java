/*
 *  This file is part of Aion-Core Extreme <http://www.aion-core.net>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Aion-Core Extreme.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.altgard;


import ru.aionknight.gameserver.dataholders.DataManager;
import ru.aionknight.gameserver.dataholders.QuestsData;
import ru.aionknight.gameserver.model.PlayerClass;
import ru.aionknight.gameserver.model.gameobjects.Npc;
import ru.aionknight.gameserver.model.gameobjects.player.Player;
import ru.aionknight.gameserver.model.templates.QuestTemplate;
import ru.aionknight.gameserver.model.templates.bonus.AbstractInventoryBonus;
import ru.aionknight.gameserver.model.templates.bonus.CoinBonus;
import ru.aionknight.gameserver.model.templates.bonus.InventoryBonusType;
import ru.aionknight.gameserver.quest.HandlerResult;
import ru.aionknight.gameserver.quest.handlers.QuestHandler;
import ru.aionknight.gameserver.quest.model.QuestCookie;
import ru.aionknight.gameserver.quest.model.QuestState;
import ru.aionknight.gameserver.quest.model.QuestStatus;
import ru.aionknight.gameserver.services.QuestService;


/**
 * @author Vincas
 */
public class _2294IronWarriorAndScout extends QuestHandler {

    private final static int questId = 2294;
    static QuestsData questsData = DataManager.QUEST_DATA;

    public _2294IronWarriorAndScout() {
        super(questId);
    }

    @Override
    public void register() {
        qe.setNpcQuestData(203659).addOnQuestStart(questId);
        qe.setNpcQuestData(203659).addOnTalkEvent(questId);
        qe.addOnQuestFinish(questId);
        qe.setQuestBonusType(InventoryBonusType.COIN).add(questId);
    }

    @Override
    public boolean onDialogEvent(QuestCookie env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestTemplate template = questsData.getQuestById(questId);
        if (targetId == 203659) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialogId() == 2) {
                    PlayerClass playerClass = player.getCommonData().getPlayerClass();
                    PlayerClass startPC = null;
                    try {
                        startPC = PlayerClass.getStartingClassFor(playerClass);
                    }
                    catch (IllegalArgumentException e) {
                        startPC = playerClass; // already a start class
                    }
                    if (startPC == PlayerClass.SCOUT || startPC == PlayerClass.WARRIOR) {
                        QuestService.startQuest(env, QuestStatus.START);
                        return sendQuestDialog(env, 1011);
                    } else {
                        return sendQuestDialog(env, 3739);
                    }
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialogId() == 2) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1011) {
                    if (player.getInventory().getItemCountByItemId(186000006) >= 2) {
                        qs.setQuestVarById(0, 0);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                } else if (env.getDialogId() == 1352) {
                    if (player.getInventory().getItemCountByItemId(186000006) >= 4) {
                        qs.setQuestVarById(0, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 6);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                }
            } else if (qs.getStatus() == QuestStatus.COMPLETE) {
                if (env.getDialogId() == 2) {
                    if ((qs.getCompleteCount() <= template.getMaxRepeatCount())) {
                        QuestService.startQuest(env, QuestStatus.START);
                        return sendQuestDialog(env, 1011);
                    } else
                        return sendQuestDialog(env, 1008);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                return defaultQuestEndDialog(env, qs.getQuestVarById(0));
            }
        }
        return false;
    }

    @Override
    public HandlerResult onBonusApplyEvent(QuestCookie env, int index, AbstractInventoryBonus bonus) {
        if (!(bonus instanceof CoinBonus))
            return HandlerResult.UNKNOWN;
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (index == 0 && qs.getQuestVarById(0) == 0 ||
                    index == 1 && qs.getQuestVarById(0) == 1)
                return HandlerResult.SUCCESS;
        }
        return HandlerResult.FAILED;
    }
}