/*
 * Emulator game server Aion 2.7 from the command of developers 'Aion-Knight Dev. Team' is
 * free software; you can redistribute it and/or modify it under the terms of
 * GNU affero general Public License (GNU GPL)as published by the free software
 * security (FSF), or to License version 3 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranties related to
 * CONSUMER PROPERTIES and SUITABILITY FOR CERTAIN PURPOSES. For details, see
 * General Public License is the GNU.
 *
 * You should have received a copy of the GNU affero general Public License along with this program.
 * If it is not, write to the Free Software Foundation, Inc., 675 Mass Ave,
 * Cambridge, MA 02139, USA
 *
 * Web developers : http://aion-knight.ru
 * Support of the game client : Aion 2.7- 'Arena of Death' (Innova)
 * The version of the server : Aion-Knight 2.7 (Beta version)
 */

package gameserver.model.templates.item;

import gameserver.dataholders.DataManager;
import gameserver.itemengine.actions.AbstractItemAction;
import gameserver.itemengine.actions.ItemActions;
import gameserver.itemengine.actions.QuestStartAction;
import gameserver.model.Gender;
import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.stats.modifiers.StatModifier;
import gameserver.model.items.ItemBonus;
import gameserver.model.items.ItemId;
import gameserver.model.items.ItemMask;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.itemset.ItemSetTemplate;
import gameserver.model.templates.stats.ModifiersTemplate;
import org.apache.commons.lang.StringUtils;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.TreeSet;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item_templates")
public class ItemTemplate extends VisibleObjectTemplate
{
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String				id;

	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate	modifiers;

	@XmlElement(name = "actions", required = false)
	protected ItemActions		actions;
	
	@XmlElement(name = "bonus", required = false)
	protected ItemBonus			bonusInfo;
	
	@XmlAttribute(name = "mask")
	private int					mask;

	@XmlAttribute(name = "slot")
	private int					itemSlot;
	
	@XmlAttribute(name = "usedelayid")
	private int					useDelayId;
	
	@XmlAttribute(name = "usedelay")
	private int					useDelay;

	@XmlAttribute(name = "equipment_type")
	private EquipType			equipmentType = EquipType.NONE;

	@XmlAttribute(name = "cash_item")
	private int					cashItem;

	@XmlAttribute(name = "dmg_decal")
	private int					dmgDecal;

	@XmlAttribute(name = "weapon_boost")
	private int					weaponBoost;

	@XmlAttribute(name = "price")
	private int					price;

	@XmlAttribute(name = "ap")
	private int					abyssPoints;

	@XmlAttribute(name = "ai")
	private int					abyssItem;

	@XmlAttribute(name = "aic")
	private int					abyssItemCount;
	
	@XmlAttribute(name = "ei")
	private int					extraItem;

	@XmlAttribute(name = "eic")
	private int					extraItemCount;
	
	@XmlAttribute(name = "ci")
	private int					couponItem;

	@XmlAttribute(name = "cic")
	private int					couponItemCount;

	@XmlAttribute(name = "max_stack_count")
	private int					maxStackCount = 1;

	@XmlAttribute(name = "level")
	private int					level;

	@XmlAttribute(name = "quality")
	private ItemQuality			itemQuality;

	@XmlAttribute(name = "item_type")
	private ItemType			itemType;

	@XmlAttribute(name = "item_category")
	private ItemCategory		itemCategory;

	@XmlAttribute(name = "weapon_type")
	private WeaponType			weaponType;

	@XmlAttribute(name = "armor_type")
	private ArmorType			armorType;

	@XmlAttribute(name = "attack_type")
	private EAttackType				attackType = EAttackType.PHYSICAL;

	@XmlAttribute(name = "attack_gap")
	private float				attackGap;					// TODO enum

	@XmlAttribute(name = "desc")
	private String				description;				// TODO string or int

	@XmlAttribute(name = "gender")
	private Gender				genderPermitted		= Gender.ALL;

	@XmlAttribute(name = "option_slot_bonus")
	private int					optionSlotBonus;

	@XmlAttribute(name = "bonus_apply")
	private String				bonusApply;					// enum

	@XmlAttribute(name = "race")
	private ItemRace			race				= ItemRace.ALL;

	@XmlAttribute(name = "origRace")
	private ItemRace			origRace			= ItemRace.ALL;
	
	private int					itemId;
	
	@XmlAttribute(name = "return_world")
	private int					returnWorldId;

	@XmlAttribute(name = "return_alias")
	private String				returnAlias;

	@XmlElement(name = "godstone")
	private GodstoneInfo		godstoneInfo;

	@XmlElement(name = "stigma")
	private Stigma				stigma;

	@XmlAttribute(name = "name")
	private String				name;

	@XmlAttribute(name = "restrict")
	private String				restrict;

	@XmlTransient
	private int[]				restricts;
	
	@XmlAttribute(name = "restrict_max")
	private String				restrictMax;
	
	@XmlTransient
	private int[]				restrictsMax;
	
	@XmlAttribute(name = "quest", required = false)
	private int					itemQuestId;

	@XmlAttribute(name = "expire_mins", required = false)
	private long				expireMins;
	
	@XmlAttribute(name = "cash_minute", required = false)
	private int					cashAvailableMinute;

	@XmlAttribute(name = "exchange_mins", required = false)
	private int					temporaryExchangeMins;
	
	@XmlAttribute(name = "world_drop", required = false)
	private boolean				isWorldDrop;
	
	@XmlAttribute(name = "doping", required = false)
	private Boolean				isDoping;

	@XmlAttribute(name="arena", required=false)
	private Boolean             isArena;

	/**
	 * @return the mask
	 */
	public int getMask()
	{
		return mask;
	}

	public int getItemSlot()
	{
		return itemSlot;
	}
	
	/**
	 * 
	 * @param playerClass
	 * @return
	 */
	public boolean checkClassRestrict(PlayerClass playerClass)
	{
		boolean related = restricts[playerClass.ordinal()] > 0;
		if(!related && !playerClass.isStartingClass())
		{
			related = restricts[PlayerClass.getStartingClassFor(playerClass).ordinal()] > 0;
		}
		return related;
	}
	
	/**
	 * 
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public LevelRestrict getRectrict(PlayerClass playerClass, int level)
	{
		int restrict_from = restricts[playerClass.ordinal()];
		int restrict_to = restrictsMax[playerClass.ordinal()];
		if (restrict_to != 0)
		{
			if (restrict_from <= level && restrict_to >= level)
				return new LevelRestrict(LevelRestrictType.NONE);
			else if (restrict_from > level)
				return new LevelRestrict(LevelRestrictType.LOW, restrict_from);
			else
				return new LevelRestrict(LevelRestrictType.HIGH, restrict_to);
		}
		else if (restrict_from > level)
			return new LevelRestrict(LevelRestrictType.LOW, restrict_from);
		return new LevelRestrict(LevelRestrictType.NONE);
	}

	/**
	 * @return the modifiers
	 */
	public TreeSet<StatModifier> getModifiers()
	{
		if(modifiers != null)
		{
			return modifiers.getModifiers();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return the actions
	 */
	public ItemActions getActions()
	{
		return actions;
	}

	/**
	 * 
	 * @return the equipmentType
	 */
	public EquipType getEquipmentType()
	{
		return equipmentType;
	}

	/**
	 * @return the price
	 */
	public int getPrice()
	{
		return price;
	}

	/**
	 * @return the abyssPoints
	 */
	public int getAbyssPoints()
	{
		return abyssPoints;
	}
	
	/**
	 * @return the abyssItem
	 */
	public int getAbyssItem()
	{
		return abyssItem;
	}

	/**
	 * @return the abyssItemCount
	 */
	public int getAbyssItemCount()
	{
		return abyssItemCount;
	}
	
	/**
	 * @return the extraItem
	 */
	public int getExtraCurrencyItem()
	{
		return extraItem;
	}

	/**
	 * @return the extraItemCount
	 */
	public int getExtraCurrencyItemCount()
	{
		return extraItemCount;
	}
	
	/**
	 * @return the couponItem
	 */
	public int getCouponItem()
	{
		return couponItem;
	}

	/**
	 * @return the couponItemCount
	 */
	public int getCouponItemCount()
	{
		return couponItemCount;
	}
	
	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the quality
	 */
	public ItemQuality getItemQuality()
	{
		return itemQuality;
	}

	/**
	 * @return the itemCategory
	 */
	public ItemCategory getItemCategory()
	{
		return itemCategory;
	}

	/**
	 * @return the itemType
	 */
	public ItemType getItemType()
	{
		return itemType;
	}

	/**
	 * @return the weaponType
	 */
	public WeaponType getWeaponType()
	{
		return weaponType;
	}

	/**
	 * @return the armorType
	 */
	public ArmorType getArmorType()
	{
		return armorType;
	}

	/**
	 * @return the description
	 */
	@Override
	public int getNameId()
	{
		try
		{
			return Integer.parseInt(description);
		}
		catch(NumberFormatException nfe)
		{
			return 0;
		}
	}

	/**
	 * @return the cashItem
	 */
	public int getCashItem()
	{
		return cashItem;
	}

	/**
	 * @return the dmgDecal
	 */
	public int getDmgDecal()
	{
		return dmgDecal;
	}

	/**
	 * @return the maxStackCount
	 */
	public int getMaxStackCount()
	{
		return maxStackCount;
	}

	/**
	 * @return the attackType
	 */
	public EAttackType getAttackType()
	{
		return attackType;
	}

	/**
	 * @return the attackGap
	 */
	public float getAttackGap()
	{
		return attackGap;
	}

	/**
	 * @return Gender the gender permitted
	 */
	public Gender getGenderPermitted()
	{
		return genderPermitted;
	}

	/**
	 * @return the optionSlotBonus
	 */
	public int getOptionSlotBonus()
	{
		return optionSlotBonus;
	}

	/**
	 * @return the bonusApply
	 */
	public String getBonusApply()
	{
		return bonusApply;
	}
	
	/**
	 * @return the bonusInfo
	 */
	public ItemBonus getBonusInfo()
	{
		return bonusInfo;
	}

	
	/**
	 * @return the race
	 */
	public ItemRace getRace()
	{
		return race;
	}

	/**
	 * @return the race to which the item belongs
	 */
	public ItemRace getOriginRace()
	{
		return origRace;
	}

	/**
	 * @return the weaponBoost
	 */
	public int getWeaponBoost()
	{
		return weaponBoost;
	}

	/**
	 * @return true or false
	 */
	public boolean isWeapon()
	{
		return equipmentType == EquipType.WEAPON;
	}

	/**
	 * @return true or false
	 */
	public boolean isArmor()
	{
		return isArmor(false);
	}
	public boolean isArmor(boolean onlySet)
	{
		if (onlySet)
		{
			/**
			 * CL_
			 * RB_
			 * LT_
			 * CH_
			 * PL_
			 * 
			 * TORSO
			 * PANTS
			 * SHOULDER
			 * SHOES
			 * GLOVE
			 * 
			 * SHIELD
			 */
			if (equipmentType == EquipType.ARMOR)
			{
				if (itemCategory == ItemCategory.SHIELD)
					return true;
				
				String[] prefixes = {"CL_","RB_","LT_","CH_","PL_"};
				String[] types = {"TORSO","PANTS","SHOULDER","SHOES","GLOVE"};
				boolean isPresent = false;
				for (String prefix : prefixes)
				{
					for(String type : types)
					{
						if(itemCategory.toString().equals(prefix+type))
						{
							isPresent = true;
							break;
						}
					}
				}
			
				if (!isPresent)
					return false;
			}
			else
				return false;
		}
		
		return equipmentType == EquipType.ARMOR;
	}

	public boolean isKinah()
	{
		return itemId == ItemId.KINAH.value();
	}
	
	public boolean isStigma()
	{
		return itemId > 140000000 && itemId < 140001000;
	}
	
	void afterUnmarshal (Unmarshaller u, Object parent)
	{
        itemId = Integer.parseInt(id);
		
		restricts = new int[12];
		if (restrict == null)
		{
			for(int i = 0; i < 12; i++)
			{
				restricts[i] = level;
			}
		}
		else
		{
			String[] parts = restrict.split(",");
			for(int i = 0; i < parts.length; i++)
			{
				restricts[i] = Integer.parseInt(parts[i]);
			}
		}
		
		restrictsMax = new int[12];
		if (restrictMax != null)
		{
			String[] parts = restrictMax.split(",");
			for(int i = 0; i < parts.length; i++)
			{
				restrictsMax[i] = Integer.parseInt(parts[i]);
			}
		}
		
		if(itemQuestId != 0 && actions != null)
		{
			for (AbstractItemAction action : actions.getItemActions())
			{
				if (action instanceof QuestStartAction)
				{
					QuestStartAction qa = (QuestStartAction)action;
					if(qa.getQuestId() == 0)
						qa.setQuestId(itemQuestId);
					break;
				}
			}
		}
	}

	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return id of the associated ItemSetTemplate or null if none
	 */
	public ItemSetTemplate getItemSet()
	{
		return DataManager.ITEM_SET_DATA.getItemSetTemplateByItemId(itemId);
	}
	
	/**
	 * Checks if the ItemTemplate belongs to an item set
	 */
	public boolean isItemSet()
	{
		return getItemSet() != null;
	}
	
	/**
	 * @return the godstoneInfo
	 */
	public GodstoneInfo getGodstoneInfo()
	{
		return godstoneInfo;
	}

	@Override
	public String getName()
	{
		return name != null ? name : StringUtils.EMPTY;
	}

	@Override
	public int getTemplateId()
	{
		return itemId;
	}

	/**
	 * @return the returnWorldId
	 */
	public int getReturnWorldId()
	{
		return returnWorldId;
	}

	/**
	 * @return the returnAlias
	 */
	public String getReturnAlias()
	{
		return returnAlias;
	}
	
	/**
	 * @return the delay for item.
	 */
	public int getDelayTime()
	{
		return useDelay;
	}
	
	/**
	 * @return item delay id
	 */
	public int getDelayId()
	{
		return useDelayId;
	}

	/**
	 * @return the stigma
	 */
	public Stigma getStigma()
	{
		return stigma;
	}

	/**
	 * Itemmask
	 * 
	 * @return
	 */
	public boolean isLimitOne()
	{
		return (mask & ItemMask.LIMIT_ONE) == ItemMask.LIMIT_ONE;
	}
	/**
	 * @return
	 */
	public boolean isTradeable()
	{
		return (mask & ItemMask.TRADEABLE) == ItemMask.TRADEABLE;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isSellable()
	{
		return (mask & ItemMask.SELLABLE) == ItemMask.SELLABLE;
	}
	/**
	 * @return
	 */
	public boolean isStorableinCharWarehouse()
	{
		return (mask & ItemMask.STORABLE_IN_WH) == ItemMask.STORABLE_IN_WH;
	}
	/**
	 * @return
	 */
	public boolean isStorableinAccWarehouse()
	{
		return (mask & ItemMask.STORABLE_IN_AWH) == ItemMask.STORABLE_IN_AWH;
	}
	/**
	 * @return
	 */
	public boolean isStorableinLegionWarehouse()
	{
		return (mask & ItemMask.STORABLE_IN_LWH) == ItemMask.STORABLE_IN_LWH;
	}
	/**
	 * @return
	 * TODO: figure out what its exactly
	 */
	public boolean isBreakable()
	{
		return (mask & ItemMask.BREAKABLE) == ItemMask.BREAKABLE;
	}
	/**
	 * @return
	 */
	public boolean isSoulBound()
	{
		return (mask & ItemMask.SOUL_BOUND) == ItemMask.SOUL_BOUND;
	}
	/**
	 * @return
	 */
	public boolean isRemoveWhenLogout()
	{
		return (mask & ItemMask.REMOVE_LOGOUT) == ItemMask.REMOVE_LOGOUT;
	}
	/**
	 * @return
	 */
	public boolean isNoEnchant()
	{
		return (mask & ItemMask.NO_ENCHANT) == ItemMask.NO_ENCHANT;
	}
	/**
	 * @return
	 * TODO: figure out what its
	 */
	public boolean isCanProcEnchant()
	{
		return (mask & ItemMask.CAN_PROC_ENCHANT) == ItemMask.CAN_PROC_ENCHANT;
	}
	/**
	 * @return
	 */
	public boolean isCanFuse()
	{
		return (mask & ItemMask.CAN_COMPOSITE_WEAPON) == ItemMask.CAN_COMPOSITE_WEAPON;
	}
	/**
	 * @return
	 */
	public boolean isChangeSkinPermitted()
	{
		return (mask & ItemMask.REMODELABLE) == ItemMask.REMODELABLE;
	}
	/**
	 * @return
	 */
	public boolean isCanSplit()
	{
		return (mask & ItemMask.CAN_SPLIT) == ItemMask.CAN_SPLIT;
	}
	/**
	 * @return
	 */
	public boolean isItemDropPermitted()
	{
		return (mask & ItemMask.DELETABLE) == ItemMask.DELETABLE;
	}
	/**
	 * @return the dyePermitted
	 */
	public boolean isItemDyePermitted()
	{
		return (mask & ItemMask.DYEABLE) == ItemMask.DYEABLE;
	}

	/**
	 * @return the itemQuestId
	 */
	public int getItemQuestId()
	{
		return itemQuestId;
	}

	/**
	 * @return the expireMins
	 */
	public long getExpireMinutes()
	{
		return expireMins;
	}
	
	/**
	 * @return the cashAvailableMinute
	 */
	public int getCashAvailableMinute()
	{
		return cashAvailableMinute;
	}
	
	/**
	 * @return the temporaryExchangeMins
	 */
	public int getTempExchangeMinutes()
	{
		return temporaryExchangeMins;
	}
	
	/**
	 * @return the temporaryExchangeMins
	 */
	public boolean getIsWorldDrop()
	{
		return isWorldDrop;
	}

	/**
	 * @return the isDoping
	 */
	public Boolean IsDoping()
	{
		if (isDoping == null)
			return false;
		return isDoping;
	}

    /**
     * @return the IsArena
     */
	  public Boolean IsArena()
	  {
	    if (isArena == null)
	      return false;

          return isArena;
	  }
}
