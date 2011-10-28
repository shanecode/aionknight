package ru.aionknight.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author LokiReborn
 *
 */
@XmlType(name = "item_category")
@XmlEnum
public enum ItemCategory
{
	AC_HEAD,
	ACCESSORY,
	ADHESIVE,
	ARENA_REWARD,
	ARROW,
	ARTIFACTSTONE,
	AXE,
	BACKPACK,
	BADGE,
	BAR,
	BARK,
	BATTERY,
	BEAD,
	BEAK,
	BEAR_HEAD,
	BELT,
	BINDSTONE,
	BLADE,
	BLOOD,
	BOARD,
	BOLUS,
	BOMB,
	BONE,
	BOOK,
	BOTTLE,
	BOUQUET,
	BOW,
	BOX,
	BRANCH,
	BUCKET,
	BUTTON,
	CACTUS,
	CAGE,
	CANDY,
	CARAPACE,
	CASH__BODY,
	CASH__GLOVE,
	CASH__HEAD,
	CASH__PANTS,
	CASH__SHOES,
	CASH_AC_HEAD,
	CASH_ACTION,
	CASH_ACTION_BLUE,
	CASH_ACTION_CANCAN,
	CASH_ACTION_DEAD,
	CASH_ACTION_DRUM,
	CASH_ACTION_EIGHT,
	CASH_ACTION_FALSE,
	CASH_ACTION_FIVE,
	CASH_ACTION_FOUR,
	CASH_ACTION_GUM,
	CASH_ACTION_HARP,
	CASH_ACTION_HUG,
	CASH_ACTION_JUGG,
	CASH_ACTION_MOCK,
	CASH_ACTION_NINE,
	CASH_ACTION_ONE,
	CASH_ACTION_PAPERS,
	CASH_ACTION_PETAL,
	CASH_ACTION_PIPE,
	CASH_ACTION_PLANE,
	CASH_ACTION_ROCK,
	CASH_ACTION_SAXOPH,
	CASH_ACTION_SCISSOR,
	CASH_ACTION_SEVEN,
	CASH_ACTION_SING,
	CASH_ACTION_SIX,
	CASH_ACTION_SURR,
	CASH_ACTION_THREE,
	CASH_ACTION_TRUE,
	CASH_ACTION_TWO,
	CASH_ACTION_TWOFLAG,
	CASH_ACTION_WHITE,
	CASH_ACTION_ZERO,
	CASH_AIONJEWELS,
	CASH_AMERICAN_DRESS_BODY,
	CASH_BASKET,
	CASH_BINYEO_HEAD,
	CASH_BOOK,
	CASH_BOW,
	CASH_BOX,
	CASH_CARD,
	CASH_CARD_TITLE,
	CASH_CHERUBIM_HEAD,
	CASH_CHESTNUTS_HEAD,
	CASH_CHILDREN_BODY,
	CASH_CHILDREN_HEAD,
	CASH_COIN,
	CASH_COUPON_CUSTOMIZE,
	CASH_COUPON_HAIR_EXCHANGE_M,
	CASH_COUPON_HAIR_EXCHANGE_W,
	CASH_COUPON_SEX_EXCHANGE,
	CASH_DAENGGI_HEAD,
	CASH_DAGGER,
	CASH_DARU_HEAD,
	CASH_DEBRIE_BODY,
	CASH_DEBRIE_GLOVE,
	CASH_DEBRIE_HEAD,
	CASH_DEBRIE_SHOES,
	CASH_DEVA_BODY,
	CASH_DEVA_HEAD,
	CASH_DEVA_SHOES,
	CASH_DEVIL_HEAD,
	CASH_DYE_BLACK,
	CASH_DYE_DEEP_BLUE,
	CASH_DYE_DEEP_PURPLE,
	CASH_DYE_GREEN,
	CASH_DYE_YELLOW,
	CASH_DYE_OLIVE_GREEN,
	CASH_DYE_ORANGE,
	CASH_DYE_PACK,
	CASH_DYE_PINK,
	CASH_DYE_RED,
	CASH_DYE_ROMANTIC_PURPLE,
	CASH_DYE_WHITE,
	CASH_EARRING,
	CASH_EUROPEAN_DRESS_BODY,
	CASH_FEED,
	CASH_FETHLOT_HEAD,
	CASH_FIRECRACKER,
	CASH_FOOLSDAY_BODY,
	CASH_FOOLSDAY_GLOVE,
	CASH_FOOLSDAY_HEAD,
	CASH_FOOLSDAY_SHOES,
	CASH_FRILLFAIMAM_HEAD,
	CASH_FUNGY_HEAD,
	CASH_GACHAE_HEAD,
	CASH_GLASSES,
	CASH_GRAPE_HEAD,
	CASH_H2SWORD,
	CASH_HEAD,
	CASH_HIIV_HEAD,
	CASH_INIT_COOLT,
	CASH_KOREANDRESS_BODY,
	CASH_KOREANDRESS_BODY_M,
	CASH_KOREANDRESS_BODY_W,
	CASH_LINEAGE2_BODY,
	CASH_MACE,
	CASH_MANDURI_HEAD,
	CASH_MINX_HEAD,
	CASH_MISSA_BODY,
	CASH_MISSA_HEAD,
	CASH_MOTION_CUSTOMIZE,
	CASH_MUTA_HEAD,
	CASH_NAMBAWI_HEAD,
	CASH_ORB,
	CASH_PETADOPTION,
	CASH_POLEARM,
	CASH_POLYMORPH_CANDY,
	CASH_PORGUSS_BODY,
	CASH_PORGUSS_GLOVE,
	CASH_PORGUSS_HEAD,
	CASH_PORGUSS_SHOES,
	CASH_PUMPKIN_HEAD,
	CASH_RUSSIAN_DRESS_BODY,
	CASH_SANTA_BODY_M,
	CASH_SANTA_BODY_W,
	CASH_SANTA_HEAD,
	CASH_SCHOOLLOOK_BODY,
	CASH_SHEEP_HEAD,
	CASH_SHIELD,
	CASH_SHIRT_BODY,
	CASH_SOCKS,
	CASH_SPAKY_HEAD,
	CASH_SPRIGG_HEAD,
	CASH_STAFF,
	CASH_SUB,
	CASH_SWIMSUIT,
	CASH_SWORD,
	CASH_VALENTINEDAY_BODY,
	CASH_VALENTINEDAY_HEAD,
	CASH_VALENTINEDAY_SHOES,
	CASH_VICTORYDAY_HEAD,
	CASH_VIKING_HEAD,
	CASH_WEDDINGDRESS_BODY,
	CASH_WHITEDAY_BODY,
	CASH_WHITEDAY_HEAD,
	CASH_WIZARD_BODY,
	CASH_WIZARD_HEAD,
	CASH_WGCOS,
	CASH_ZAIF_HEAD,
	CASH_ZERO,
	CH_GLOVE,
	CH_HEAD,
	CH_PANTS,
	CH_SHOES,
	CH_SHOULDER,
	CH_TORSO,
	CHAIN,
	CHANGE_CHARACTER_NAME,
	CHANGE_LEGION_NAME,
	CHARCOAL,
	CHERUBIM_HEAD,
	CHINESEDRESS,
	CHOCOLATE,
	CL_GLOVE,
	CL_PANTS,
	CL_SHOES,
	CL_TORSO,
	CLAW,
	CLOAK,
	CLOTH,
	COIN,
	COMB,
	COMPASS,
	CONTROLLER,
	COOKIE,
	CRYSTAL,
	CRYSTALBALL,
	CRUST,
	CUBE,
	CUP,
	DAGGER,
	DANCE,
	DARK_WING,
	DISH,
	DYE_HAIR,
	DYE_SKIN,
	DRAGEL,
	DRAGONHIDE,
	DRAGONHORN,
	DRAGONPACK,
	DRAGONSCALE,
	DRAGONWING,
	DRANA,
	DRAZMA,
	DRINK,
	DUST,
	EARRING,
	EGG,
	EYE,
	ELEMENTALSTONE,
	ELEMENTALWATER,
	ELEMENTALWATER_EMPTY,
	ELIM_SKILLSTONE,
	EMBLEM,
	ENCHANTPACK,
	ENCHANTSTONE,
	ENHANCED,
	ETC,
	FANTA_GRAPE,
	FANTA_ORANGE,
	FEATHER,
	FERN,
	FISH,
	FLAG,
	FLASK,
	FLOUR,
	FLOWER,
	FOSSIL,
	FRUIT,
	GAS,
	GEAR,
	GEM,
	GEMPACK,
	GEMPODER,
	GEMSTONE,
	GLASSES,
	GOATEE,
	GRAIN,
	H2SWORD,
	HAIR,
	HALLOWEEN_HEAD,
	HAMMER,
	HANDKERCHIEF,
	HEAD,
	HEADGEAR,
	HEARBAND,
	HEART,
	HEART_HEAD,
	HELM,
	HERB,
	HIGHDRESSGT_DA,
	HIGHDRESSGT_LI,
	HIGHDRESSL,
	HIGHDRESSLO,
	HIGHDRESSMD__DA,
	HIGHDRESSMD__LI,
	HIGHDRESSMD_DA,
	HIGHDRESSMD_LI,
	HIGHDRESSS,
	HILT,
	HOE,
	HOLYSTONE,
	HOOD,
	HOOF,
	HORN,
	INGOT,
	INK,
	INSTRUMENT,
	ITEM,
	YARN,
	JAPANESEDRESS,
	JELLY,
	JUTE,
	KEY,
	KNIFE,
	LAMP,
	LEAF,
	LEATHER,
	LEG,
	LETTER,
	LOG,
	LT_GLOVE,
	LT_HEAD,
	LT_PANTS,
	LT_SHOES,
	LT_SHOULDER,
	LT_TORSO,
	MACE,
	MACEBODY,
	MACEHEAD,
	MAGICBALL,
	MAGICSTONE,
	MAGICSWORD,
	MANE,
	MANUFACTURE,
	MAP,
	MARBLE,
	MEAT,
	METAL,
	MIRROR,
	MOLD,
	MOSS,
	MOVE,
	MUCUS,
	MUSHROOM,
	NECKLACE,
	NIPPER,
	NODDLES,
	OD,
	ODEQUIPMENT,
	OLDCROWN,
	OLDCUP,
	OLDSEAL,
	OLDTORSO,
	ORB,
	ORB_EARTH,
	ORB_FIRE,
	ORB_WATER,
	ORB_WIND,
	ORDPACK,
	ORGAN,
	PAGE,
	PAPER,
	PATTERN,
	PETADOPTION,
	PICK,
	PINCER,
	PL_GLOVE,
	PL_HEAD,
	PL_PANTS,
	PL_SHOES,
	PL_SHOULDER,
	PL_TORSO,
	PLANEBLADE,
	PLATE,
	POLEARM,
	POLEARMBLADE,
	POLEARMBODY,
	POT,
	POTION,
	POUCH,
	POWDER,
	QINA,
	QUEST_SCROLL,
	RABBIT_HEAD,
	RANDOMPACK,
	RAWHIDE,
	RB_GLOVE,
	RB_HEAD,
	RB_PANTS,
	RB_SHOES,
	RB_SHOULDER,
	RB_TORSO,
	REPAIRSTONE,
	RIBBON,
	RING,
	ROCK,
	ROCKPACK,
	ROD,
	ROOT,
	SAC,
	SACK,
	SALVE,
	SAND,
	SANDPAPER,
	SCALE,
	SCOOP,
	SCROLL,
	SEAL,
	SEAWEED,
	SEED,
	SEEFOOD,
	SHACKLES,
	SHAKER,
	SHELL,
	SHIELD,
	SHINE,
	SHOP_PRESET_DEFAULT,
	SHOPPINGBAG,
	SIEGE,
	SKILLBOOK,
	SKULL,
	SLATE,
	SPAWN,
	SPECIALBOX,
	SPORE,
	SPOROCYST,
	SPRIGG_HEAD,
	SQUAREWOOD,
	STAFF,
	STAFFBODY,
	STIGMA,
	STING,
	STONE,
	STRAP,
	STUD,
	SUB_MATTER,
	SUNFLOWER_HEAD,
	SURKANA,
	SWORD,
	TAIL,
	TAIL_SCORPION,
	TAIWANESEDRESS,
	TEA,
	TESTTUBE,
	TEXTILE,
	THORN,
	THREAD,
	TOOL,
	TOOTH,
	TREE,
	TULIP_HEAD,
	TUNIC,
	TUSK,
	VEGETABLE,
	WATERDROP,
	WHETSTONE,
	WHIP,
	WING,
	WIRE,
	WOOD,
	WOOL,
	XPBOOST,
	XBOOST
}