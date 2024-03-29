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

package quest.event;

import gameserver.model.gameobjects.player.Player;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;

public class _80009The_Cake_Is_The_Truth extends QuestHandler
{
    private final static int questId = 80009;
	
    public _80009The_Cake_Is_The_Truth()
	{
        super(questId);
    }
	
    @Override
    public boolean onDialogEvent(QuestCookie env)
	{
        Player player = env.getPlayer();
		
        if (env.getTargetId() == 0)
            return defaultQuestStartItem(env);
			
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null)
            return false;
			
        int var = qs.getQuestVarById(0);
		
        if (qs.getStatus() == QuestStatus.START)
		{
            if (env.getTargetId() == 798417)
			{
                switch (env.getDialogId())
				{
                    case 26:
                        if (var == 0)
                            return sendQuestDialog(env, 2375);
                    case 1009:
                        defaultQuestRemoveItem(env, 182214007, 1);
                        return defaultCloseDialog(env, 0, 1, true, true);
                }
            }
        }
        return defaultQuestRewardDialog(env, 798417, 0);
    }
	
    @Override
    public void register()
	{
        qe.setNpcQuestData(798417).addOnTalkEvent(questId);
    }
}