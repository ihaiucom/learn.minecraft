package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("minecraft")
public class Enchantments {
   private static final EquipmentSlotType[] ARMOR_SLOTS;
   public static final Enchantment PROTECTION;
   public static final Enchantment FIRE_PROTECTION;
   public static final Enchantment FEATHER_FALLING;
   public static final Enchantment BLAST_PROTECTION;
   public static final Enchantment PROJECTILE_PROTECTION;
   public static final Enchantment RESPIRATION;
   public static final Enchantment AQUA_AFFINITY;
   public static final Enchantment THORNS;
   public static final Enchantment DEPTH_STRIDER;
   public static final Enchantment FROST_WALKER;
   public static final Enchantment BINDING_CURSE;
   public static final Enchantment SHARPNESS;
   public static final Enchantment SMITE;
   public static final Enchantment BANE_OF_ARTHROPODS;
   public static final Enchantment KNOCKBACK;
   public static final Enchantment FIRE_ASPECT;
   public static final Enchantment LOOTING;
   public static final Enchantment SWEEPING;
   public static final Enchantment EFFICIENCY;
   public static final Enchantment SILK_TOUCH;
   public static final Enchantment UNBREAKING;
   public static final Enchantment FORTUNE;
   public static final Enchantment POWER;
   public static final Enchantment PUNCH;
   public static final Enchantment FLAME;
   public static final Enchantment INFINITY;
   public static final Enchantment LUCK_OF_THE_SEA;
   public static final Enchantment LURE;
   public static final Enchantment LOYALTY;
   public static final Enchantment IMPALING;
   public static final Enchantment RIPTIDE;
   public static final Enchantment CHANNELING;
   public static final Enchantment MULTISHOT;
   public static final Enchantment QUICK_CHARGE;
   public static final Enchantment PIERCING;
   public static final Enchantment MENDING;
   public static final Enchantment VANISHING_CURSE;

   private static Enchantment register(String p_222191_0_, Enchantment p_222191_1_) {
      return (Enchantment)Registry.register((Registry)Registry.ENCHANTMENT, (String)p_222191_0_, (Object)p_222191_1_);
   }

   static {
      ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
      PROTECTION = register("protection", new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR_SLOTS));
      FIRE_PROTECTION = register("fire_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR_SLOTS));
      FEATHER_FALLING = register("feather_falling", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, ARMOR_SLOTS));
      BLAST_PROTECTION = register("blast_protection", new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR_SLOTS));
      PROJECTILE_PROTECTION = register("projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR_SLOTS));
      RESPIRATION = register("respiration", new RespirationEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
      AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
      THORNS = register("thorns", new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
      DEPTH_STRIDER = register("depth_strider", new DepthStriderEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
      FROST_WALKER = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.FEET}));
      BINDING_CURSE = register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
      SHARPNESS = register("sharpness", new DamageEnchantment(Enchantment.Rarity.COMMON, 0, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      SMITE = register("smite", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      BANE_OF_ARTHROPODS = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      KNOCKBACK = register("knockback", new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      FIRE_ASPECT = register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      LOOTING = register("looting", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      SWEEPING = register("sweeping", new SweepingEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      EFFICIENCY = register("efficiency", new EfficiencyEnchantment(Enchantment.Rarity.COMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      SILK_TOUCH = register("silk_touch", new SilkTouchEnchantment(Enchantment.Rarity.VERY_RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      UNBREAKING = register("unbreaking", new UnbreakingEnchantment(Enchantment.Rarity.UNCOMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      FORTUNE = register("fortune", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      POWER = register("power", new PowerEnchantment(Enchantment.Rarity.COMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      PUNCH = register("punch", new PunchEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      FLAME = register("flame", new FlameEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      INFINITY = register("infinity", new InfinityEnchantment(Enchantment.Rarity.VERY_RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      LUCK_OF_THE_SEA = register("luck_of_the_sea", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      LURE = register("lure", new LureEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      LOYALTY = register("loyalty", new LoyaltyEnchantment(Enchantment.Rarity.UNCOMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      IMPALING = register("impaling", new ImpalingEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      RIPTIDE = register("riptide", new RiptideEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      CHANNELING = register("channeling", new ChannelingEnchantment(Enchantment.Rarity.VERY_RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      MULTISHOT = register("multishot", new MultishotEnchantment(Enchantment.Rarity.RARE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      QUICK_CHARGE = register("quick_charge", new QuickChargeEnchantment(Enchantment.Rarity.UNCOMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      PIERCING = register("piercing", new PiercingEnchantment(Enchantment.Rarity.COMMON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}));
      MENDING = register("mending", new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.values()));
      VANISHING_CURSE = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.values()));
   }
}
