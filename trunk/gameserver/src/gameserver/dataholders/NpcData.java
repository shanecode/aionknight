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

package gameserver.dataholders;

import gameserver.configs.main.CustomConfig;
import gameserver.model.templates.NpcTemplate;
import gnu.trove.TIntObjectHashMap;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "npc_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcData
{
	@XmlElement(name = "npc_template")
	private List<NpcTemplate> npcs;
	private TIntObjectHashMap<NpcTemplate>	npcData	= new TIntObjectHashMap<NpcTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		npcData.clear();
		for(NpcTemplate npc: npcs)
		{
			if(CustomConfig.NPC_DYNAMIC_STAT && !npc.getName().equals("Fire Spirit")
				&& !npc.getName().equals("Earth Spirit")
				&& !npc.getName().equals("Water Spirit")
				&& !npc.getName().equals("Wind Spirit")
				&& !npc.getName().equals("Magma Spirit")
				&& !npc.getName().equals("Tempest Spirit"))
			{
				int npcLevel = npc.getLevel();
				float rankModifier;
				float powerModifier;
				switch(npc.getRank())
				{
					case NORMAL: rankModifier = 2.0f; powerModifier = 1.0f; break;
					case ELITE: rankModifier = 3.0f; powerModifier = 1.3f; break;
					case HERO: rankModifier = 3.5f; powerModifier = 1.5f; break;
					case LEGENDARY: rankModifier = 4.0f; powerModifier = 1.7f; break;
					default: rankModifier = 1.0f; powerModifier = 1.0f; break;
				}
				int baseStat = Math.round((npcLevel * rankModifier));
				
				npc.getStatsTemplate().setAccuracy(baseStat);
				npc.getStatsTemplate().setBlock(baseStat);
				npc.getStatsTemplate().setCrit(baseStat);
				npc.getStatsTemplate().setEvasion(baseStat);
				npc.getStatsTemplate().setMagicAccuracy(baseStat);
				npc.getStatsTemplate().setMdef(baseStat);
				npc.getStatsTemplate().setParry(baseStat);
				npc.getStatsTemplate().setPdef(baseStat);
				npc.getStatsTemplate().setPower(Math.round(baseStat / powerModifier));
			}
			
			npcData.put(npc.getTemplateId(), npc);
		}
	}
	public int size()
	{
		return npcData.size();
	}
	
	public List<NpcTemplate> getTemplates()
	{
		return npcs;
	}

	public NpcTemplate getNpcTemplate(int id)
	{
		return npcData.get(id);
	}
	
	public void setTemplates(List<NpcTemplate> templates)
	{
		npcs.clear();
		npcs = null;
		npcs = templates;
		afterUnmarshal(null, null);
	}
}