package ru.aionknight.gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


import ru.aionknight.gameserver.model.gameobjects.Creature;
import ru.aionknight.gameserver.skill.model.Effect;
import ru.aionknight.gameserver.skill.model.SkillType;


/**
 * @author kecimis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuffBindEffect")
public class BuffBindEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(Effect effect)
	{
		final Creature effected = effect.getEffected();
		effect.setAbnormal(EffectId.BIND.getEffectId());
		effected.getEffectController().setAbnormal(EffectId.BIND.getEffectId());
		if (effected.getCastingSkill() != null && effected.getCastingSkill().getSkillTemplate().getType() == SkillType.PHYSICAL)
			effected.getController().cancelCurrentSkill();
	}
	
	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getEffectController().unsetAbnormal(EffectId.BIND.getEffectId());
	}
}
