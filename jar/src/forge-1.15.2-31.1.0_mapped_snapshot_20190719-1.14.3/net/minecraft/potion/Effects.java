package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("minecraft")
public class Effects {
   public static final Effect SPEED;
   public static final Effect SLOWNESS;
   public static final Effect HASTE;
   public static final Effect MINING_FATIGUE;
   public static final Effect STRENGTH;
   public static final Effect INSTANT_HEALTH;
   public static final Effect INSTANT_DAMAGE;
   public static final Effect JUMP_BOOST;
   public static final Effect NAUSEA;
   public static final Effect REGENERATION;
   public static final Effect RESISTANCE;
   public static final Effect FIRE_RESISTANCE;
   public static final Effect WATER_BREATHING;
   public static final Effect INVISIBILITY;
   public static final Effect BLINDNESS;
   public static final Effect NIGHT_VISION;
   public static final Effect HUNGER;
   public static final Effect WEAKNESS;
   public static final Effect POISON;
   public static final Effect WITHER;
   public static final Effect HEALTH_BOOST;
   public static final Effect ABSORPTION;
   public static final Effect SATURATION;
   public static final Effect GLOWING;
   public static final Effect LEVITATION;
   public static final Effect LUCK;
   public static final Effect UNLUCK;
   public static final Effect SLOW_FALLING;
   public static final Effect CONDUIT_POWER;
   public static final Effect DOLPHINS_GRACE;
   public static final Effect BAD_OMEN;
   public static final Effect HERO_OF_THE_VILLAGE;

   private static Effect register(int p_220308_0_, String p_220308_1_, Effect p_220308_2_) {
      return (Effect)Registry.register(Registry.EFFECTS, p_220308_0_, p_220308_1_, p_220308_2_);
   }

   static {
      SPEED = register(1, "speed", (new Effect(EffectType.BENEFICIAL, 8171462)).addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      SLOWNESS = register(2, "slowness", (new Effect(EffectType.HARMFUL, 5926017)).addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      HASTE = register(3, "haste", (new Effect(EffectType.BENEFICIAL, 14270531)).addAttributesModifier(SharedMonsterAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      MINING_FATIGUE = register(4, "mining_fatigue", (new Effect(EffectType.HARMFUL, 4866583)).addAttributesModifier(SharedMonsterAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      STRENGTH = register(5, "strength", (new AttackDamageEffect(EffectType.BENEFICIAL, 9643043, 3.0D)).addAttributesModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.ADDITION));
      INSTANT_HEALTH = register(6, "instant_health", new InstantEffect(EffectType.BENEFICIAL, 16262179));
      INSTANT_DAMAGE = register(7, "instant_damage", new InstantEffect(EffectType.HARMFUL, 4393481));
      JUMP_BOOST = register(8, "jump_boost", new Effect(EffectType.BENEFICIAL, 2293580));
      NAUSEA = register(9, "nausea", new Effect(EffectType.HARMFUL, 5578058));
      REGENERATION = register(10, "regeneration", new Effect(EffectType.BENEFICIAL, 13458603));
      RESISTANCE = register(11, "resistance", new Effect(EffectType.BENEFICIAL, 10044730));
      FIRE_RESISTANCE = register(12, "fire_resistance", new Effect(EffectType.BENEFICIAL, 14981690));
      WATER_BREATHING = register(13, "water_breathing", new Effect(EffectType.BENEFICIAL, 3035801));
      INVISIBILITY = register(14, "invisibility", new Effect(EffectType.BENEFICIAL, 8356754));
      BLINDNESS = register(15, "blindness", new Effect(EffectType.HARMFUL, 2039587));
      NIGHT_VISION = register(16, "night_vision", new Effect(EffectType.BENEFICIAL, 2039713));
      HUNGER = register(17, "hunger", new Effect(EffectType.HARMFUL, 5797459));
      WEAKNESS = register(18, "weakness", (new AttackDamageEffect(EffectType.HARMFUL, 4738376, -4.0D)).addAttributesModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION));
      POISON = register(19, "poison", new Effect(EffectType.HARMFUL, 5149489));
      WITHER = register(20, "wither", new Effect(EffectType.HARMFUL, 3484199));
      HEALTH_BOOST = register(21, "health_boost", (new HealthBoostEffect(EffectType.BENEFICIAL, 16284963)).addAttributesModifier(SharedMonsterAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, AttributeModifier.Operation.ADDITION));
      ABSORPTION = register(22, "absorption", new AbsorptionEffect(EffectType.BENEFICIAL, 2445989));
      SATURATION = register(23, "saturation", new InstantEffect(EffectType.BENEFICIAL, 16262179));
      GLOWING = register(24, "glowing", new Effect(EffectType.NEUTRAL, 9740385));
      LEVITATION = register(25, "levitation", new Effect(EffectType.HARMFUL, 13565951));
      LUCK = register(26, "luck", (new Effect(EffectType.BENEFICIAL, 3381504)).addAttributesModifier(SharedMonsterAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, AttributeModifier.Operation.ADDITION));
      UNLUCK = register(27, "unluck", (new Effect(EffectType.HARMFUL, 12624973)).addAttributesModifier(SharedMonsterAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, AttributeModifier.Operation.ADDITION));
      SLOW_FALLING = register(28, "slow_falling", new Effect(EffectType.BENEFICIAL, 16773073));
      CONDUIT_POWER = register(29, "conduit_power", new Effect(EffectType.BENEFICIAL, 1950417));
      DOLPHINS_GRACE = register(30, "dolphins_grace", new Effect(EffectType.BENEFICIAL, 8954814));
      BAD_OMEN = register(31, "bad_omen", new Effect(EffectType.NEUTRAL, 745784) {
         public boolean isReady(int p_76397_1_, int p_76397_2_) {
            return true;
         }

         public void performEffect(LivingEntity p_76394_1_, int p_76394_2_) {
            if (p_76394_1_ instanceof ServerPlayerEntity && !p_76394_1_.isSpectator()) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_76394_1_;
               ServerWorld serverworld = serverplayerentity.getServerWorld();
               if (serverworld.getDifficulty() == Difficulty.PEACEFUL) {
                  return;
               }

               if (serverworld.func_217483_b_(new BlockPos(p_76394_1_))) {
                  serverworld.getRaids().badOmenTick(serverplayerentity);
               }
            }

         }
      });
      HERO_OF_THE_VILLAGE = register(32, "hero_of_the_village", new Effect(EffectType.BENEFICIAL, 4521796));
   }
}
