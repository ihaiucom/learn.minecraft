package net.minecraft.particles;

import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("minecraft")
public class ParticleTypes {
   public static final BasicParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
   public static final BasicParticleType ANGRY_VILLAGER = register("angry_villager", false);
   public static final BasicParticleType BARRIER = register("barrier", false);
   public static final ParticleType<BlockParticleData> BLOCK;
   public static final BasicParticleType BUBBLE;
   public static final BasicParticleType CLOUD;
   public static final BasicParticleType CRIT;
   public static final BasicParticleType DAMAGE_INDICATOR;
   public static final BasicParticleType DRAGON_BREATH;
   public static final BasicParticleType DRIPPING_LAVA;
   public static final BasicParticleType FALLING_LAVA;
   public static final BasicParticleType LANDING_LAVA;
   public static final BasicParticleType DRIPPING_WATER;
   public static final BasicParticleType FALLING_WATER;
   public static final ParticleType<RedstoneParticleData> DUST;
   public static final BasicParticleType EFFECT;
   public static final BasicParticleType ELDER_GUARDIAN;
   public static final BasicParticleType ENCHANTED_HIT;
   public static final BasicParticleType ENCHANT;
   public static final BasicParticleType END_ROD;
   public static final BasicParticleType ENTITY_EFFECT;
   public static final BasicParticleType EXPLOSION_EMITTER;
   public static final BasicParticleType EXPLOSION;
   public static final ParticleType<BlockParticleData> FALLING_DUST;
   public static final BasicParticleType FIREWORK;
   public static final BasicParticleType FISHING;
   public static final BasicParticleType FLAME;
   public static final BasicParticleType FLASH;
   public static final BasicParticleType HAPPY_VILLAGER;
   public static final BasicParticleType COMPOSTER;
   public static final BasicParticleType HEART;
   public static final BasicParticleType INSTANT_EFFECT;
   public static final ParticleType<ItemParticleData> ITEM;
   public static final BasicParticleType ITEM_SLIME;
   public static final BasicParticleType ITEM_SNOWBALL;
   public static final BasicParticleType LARGE_SMOKE;
   public static final BasicParticleType LAVA;
   public static final BasicParticleType MYCELIUM;
   public static final BasicParticleType NOTE;
   public static final BasicParticleType POOF;
   public static final BasicParticleType PORTAL;
   public static final BasicParticleType RAIN;
   public static final BasicParticleType SMOKE;
   public static final BasicParticleType SNEEZE;
   public static final BasicParticleType SPIT;
   public static final BasicParticleType SQUID_INK;
   public static final BasicParticleType SWEEP_ATTACK;
   public static final BasicParticleType TOTEM_OF_UNDYING;
   public static final BasicParticleType UNDERWATER;
   public static final BasicParticleType SPLASH;
   public static final BasicParticleType WITCH;
   public static final BasicParticleType BUBBLE_POP;
   public static final BasicParticleType CURRENT_DOWN;
   public static final BasicParticleType BUBBLE_COLUMN_UP;
   public static final BasicParticleType NAUTILUS;
   public static final BasicParticleType DOLPHIN;
   public static final BasicParticleType CAMPFIRE_COSY_SMOKE;
   public static final BasicParticleType CAMPFIRE_SIGNAL_SMOKE;
   public static final BasicParticleType field_229427_ag_;
   public static final BasicParticleType field_229428_ah_;
   public static final BasicParticleType field_229429_ai_;
   public static final BasicParticleType field_229430_aj_;

   private static BasicParticleType register(String p_218415_0_, boolean p_218415_1_) {
      return (BasicParticleType)Registry.register((Registry)Registry.PARTICLE_TYPE, (String)p_218415_0_, (Object)(new BasicParticleType(p_218415_1_)));
   }

   private static <T extends IParticleData> ParticleType<T> register(String p_218416_0_, IParticleData.IDeserializer<T> p_218416_1_) {
      return (ParticleType)Registry.register((Registry)Registry.PARTICLE_TYPE, (String)p_218416_0_, (Object)(new ParticleType(false, p_218416_1_)));
   }

   static {
      BLOCK = register("block", BlockParticleData.DESERIALIZER);
      BUBBLE = register("bubble", false);
      CLOUD = register("cloud", false);
      CRIT = register("crit", false);
      DAMAGE_INDICATOR = register("damage_indicator", true);
      DRAGON_BREATH = register("dragon_breath", false);
      DRIPPING_LAVA = register("dripping_lava", false);
      FALLING_LAVA = register("falling_lava", false);
      LANDING_LAVA = register("landing_lava", false);
      DRIPPING_WATER = register("dripping_water", false);
      FALLING_WATER = register("falling_water", false);
      DUST = register("dust", RedstoneParticleData.DESERIALIZER);
      EFFECT = register("effect", false);
      ELDER_GUARDIAN = register("elder_guardian", true);
      ENCHANTED_HIT = register("enchanted_hit", false);
      ENCHANT = register("enchant", false);
      END_ROD = register("end_rod", false);
      ENTITY_EFFECT = register("entity_effect", false);
      EXPLOSION_EMITTER = register("explosion_emitter", true);
      EXPLOSION = register("explosion", true);
      FALLING_DUST = register("falling_dust", BlockParticleData.DESERIALIZER);
      FIREWORK = register("firework", false);
      FISHING = register("fishing", false);
      FLAME = register("flame", false);
      FLASH = register("flash", false);
      HAPPY_VILLAGER = register("happy_villager", false);
      COMPOSTER = register("composter", false);
      HEART = register("heart", false);
      INSTANT_EFFECT = register("instant_effect", false);
      ITEM = register("item", ItemParticleData.DESERIALIZER);
      ITEM_SLIME = register("item_slime", false);
      ITEM_SNOWBALL = register("item_snowball", false);
      LARGE_SMOKE = register("large_smoke", false);
      LAVA = register("lava", false);
      MYCELIUM = register("mycelium", false);
      NOTE = register("note", false);
      POOF = register("poof", true);
      PORTAL = register("portal", false);
      RAIN = register("rain", false);
      SMOKE = register("smoke", false);
      SNEEZE = register("sneeze", false);
      SPIT = register("spit", true);
      SQUID_INK = register("squid_ink", true);
      SWEEP_ATTACK = register("sweep_attack", true);
      TOTEM_OF_UNDYING = register("totem_of_undying", false);
      UNDERWATER = register("underwater", false);
      SPLASH = register("splash", false);
      WITCH = register("witch", false);
      BUBBLE_POP = register("bubble_pop", false);
      CURRENT_DOWN = register("current_down", false);
      BUBBLE_COLUMN_UP = register("bubble_column_up", false);
      NAUTILUS = register("nautilus", false);
      DOLPHIN = register("dolphin", false);
      CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
      CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
      field_229427_ag_ = register("dripping_honey", false);
      field_229428_ah_ = register("falling_honey", false);
      field_229429_ai_ = register("landing_honey", false);
      field_229430_aj_ = register("falling_nectar", false);
   }
}
