/**   
 * �������� �������� ������� Aion 2.7 �� ������� ������������� 'Aion-Knight Dev. Team' �������� 
 * ��������� ����������� ������������; �� ������ �������������� �/��� �������� ��� �������� �������� 
 * ����������� ������������ �������� GNU (GNU GPL), �������������� ������ ���������� ������������ 
 * ����������� (FSF), ���� �������� ������ 3, ���� (�� ���� ����������) ����� ����� ������� 
 * ������.
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

package gameserver.controllers.instances;

import gameserver.ai.events.Event;
import gameserver.controllers.NpcController;
import gameserver.controllers.movement.StartMovingListener;
import gameserver.model.EmotionType;
import gameserver.model.PlayerClass;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import gameserver.services.InstanceService;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

public class HaramelController extends NpcController {

	@Override
	public void onDialogRequest(final Player player) {
		getOwner().getAi().handleEvent(Event.TALK);

		switch (getOwner().getNpcId()) {
			//Lift
			case 730321:
				TeleportService.teleportTo(player, 300200000, player.getInstanceId(), 220, 213, 126, 0);
				break;
			//Dimensional Gate
			case 700852:
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(), 3000, 1));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getOwner().getObjectId()), true);

				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().attach(new StartMovingListener() {

					@Override
					public void moved() {
						player.getController().cancelTask(TaskId.ITEM_USE);
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(), 3000, 0));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getOwner().getObjectId()), true);
					}
				});
				player.getController().addNewTask(TaskId.ITEM_USE,
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(), 3000, 0));
							PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getOwner().getObjectId()), true);
							analyzePortation(player);
						}

						private void analyzePortation(final Player player) {
							if(player.getTribe().equals("PC")) {
								TeleportService.teleportTo(player, 210030000, 1, 2539, 834, 104, 0);
							} else {
								TeleportService.teleportTo(player, 220030000, 1, 2909, 1456, 252, 0);
							}
						}
					}, 3000));
				break;
		}
	}

	@Override
	public void onDie(Creature lastAttacker) {
		super.onDie(lastAttacker);
		Npc owner = getOwner();
		Player player;
		player = (Player) lastAttacker;

		if(owner.getNpcId() == 216922)
		{
			owner.getController().onDelete();
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 457));
			InstanceService.addNewSpawn(300200000, player.getInstanceId(), 700852, (float) 224.59f, (float) 331.14f, (float) 142.89f, (byte) 90, true);
			PlayerClass playerClass = player.getCommonData().getPlayerClass();
			if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
			InstanceService.addNewSpawn(300200000, player.getInstanceId(), 700829, (float) 224.13f, (float) 268.60f, (float) 144.89f, (byte) 90, true);
			else if (playerClass ==PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
			InstanceService.addNewSpawn(300200000, player.getInstanceId(), 700830, (float) 224.13f, (float) 268.60f, (float) 144.89f, (byte) 90, true);
			else if (playerClass ==PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
			InstanceService.addNewSpawn(300200000, player.getInstanceId(), 700831, (float) 224.13f, (float) 268.60f, (float) 144.89f, (byte) 90, true);
			else if (playerClass ==PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
			InstanceService.addNewSpawn(300200000, player.getInstanceId(), 700832, (float) 224.13f, (float) 268.60f, (float) 144.89f, (byte) 90, true);      	
		}
	}
}