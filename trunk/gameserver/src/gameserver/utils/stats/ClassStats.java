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

package gameserver.utils.stats;

import gameserver.model.PlayerClass;
import gameserver.utils.stats.enums.*;

public class ClassStats
{
	/**
	 * @param playerClass
	 * @param level
	 * @return maximum HP stat for player class and level
	 */
	public static int getMaxHpFor(PlayerClass playerClass, int level)
	{
		return MAXHP.valueOf(playerClass.toString()).getMaxHpFor(level);
	}
	
	/**
	 * @param playerClass
	 * @return power stat for player class and level
	 */
	public static int getPowerFor(PlayerClass playerClass)
	{
		return POWER.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getHealthFor(PlayerClass playerClass)
	{
		return HEALTH.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAgilityFor(PlayerClass playerClass)
	{
		return AGILITY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAccuracyFor(PlayerClass playerClass)
	{
		return ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getKnowledgeFor(PlayerClass playerClass)
	{
		return KNOWLEDGE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWillFor(PlayerClass playerClass)
	{
		return WILL.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandAttackFor(PlayerClass playerClass)
	{
		return MAIN_HAND_ATTACK.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandCritRateFor(PlayerClass playerClass)
	{
		return MAIN_HAND_CRITRATE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMainHandAccuracyFor(PlayerClass playerClass)
	{
		return MAIN_HAND_ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWaterResistFor(PlayerClass playerClass)
	{
		return WATER_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getWindResistFor(PlayerClass playerClass)
	{
		return WIND_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getEarthResistFor(PlayerClass playerClass)
	{
		return EARTH_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getFireResistFor(PlayerClass playerClass)
	{
		return FIRE_RESIST.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getMagicAccuracyFor(PlayerClass playerClass)
	{
		return MAGIC_ACCURACY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getEvasionFor(PlayerClass playerClass)
	{
		return EVASION.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getBlockFor(PlayerClass playerClass)
	{
		return BLOCK.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getParryFor(PlayerClass playerClass)
	{
		return PARRY.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAttackRangeFor(PlayerClass playerClass)
	{
		return ATTACK_RANGE.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getAttackSpeedFor(PlayerClass playerClass)
	{
		return ATTACK_SPEED.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getFlySpeedFor(PlayerClass playerClass)
	{
		return FLY_SPEED.valueOf(playerClass.toString()).getValue();
	}
	
	/**
	 * @param playerClass
	 * @return int
	 */
	public static int getSpeedFor(PlayerClass playerClass)
	{
		return SPEED.valueOf(playerClass.toString()).getValue();
	}
}