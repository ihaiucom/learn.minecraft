package net.minecraft.item;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class Foods {
   public static final Food APPLE = (new Food.Builder()).hunger(4).saturation(0.3F).build();
   public static final Food BAKED_POTATO = (new Food.Builder()).hunger(5).saturation(0.6F).build();
   public static final Food BEEF = (new Food.Builder()).hunger(3).saturation(0.3F).meat().build();
   public static final Food BEETROOT = (new Food.Builder()).hunger(1).saturation(0.6F).build();
   public static final Food BEETROOT_SOUP = buildStew(6);
   public static final Food BREAD = (new Food.Builder()).hunger(5).saturation(0.6F).build();
   public static final Food CARROT = (new Food.Builder()).hunger(3).saturation(0.6F).build();
   public static final Food CHICKEN;
   public static final Food CHORUS_FRUIT;
   public static final Food COD;
   public static final Food COOKED_BEEF;
   public static final Food COOKED_CHICKEN;
   public static final Food COOKED_COD;
   public static final Food COOKED_MUTTON;
   public static final Food COOKED_PORKCHOP;
   public static final Food COOKED_RABBIT;
   public static final Food COOKED_SALMON;
   public static final Food COOKIE;
   public static final Food DRIED_KELP;
   public static final Food ENCHANTED_GOLDEN_APPLE;
   public static final Food GOLDEN_APPLE;
   public static final Food GOLDEN_CARROT;
   public static final Food field_226604_w_;
   public static final Food MELON_SLICE;
   public static final Food MUSHROOM_STEW;
   public static final Food MUTTON;
   public static final Food POISONOUS_POTATO;
   public static final Food PORKCHOP;
   public static final Food POTATO;
   public static final Food PUFFERFISH;
   public static final Food PUMPKIN_PIE;
   public static final Food RABBIT;
   public static final Food RABBIT_STEW;
   public static final Food ROTTEN_FLESH;
   public static final Food SALMON;
   public static final Food SPIDER_EYE;
   public static final Food SUSPICIOUS_STEW;
   public static final Food SWEET_BERRIES;
   public static final Food TROPICAL_FISH;

   private static Food buildStew(int p_221412_0_) {
      return (new Food.Builder()).hunger(p_221412_0_).saturation(0.6F).build();
   }

   static {
      CHICKEN = (new Food.Builder()).hunger(2).saturation(0.3F).effect(new EffectInstance(Effects.HUNGER, 600, 0), 0.3F).meat().build();
      CHORUS_FRUIT = (new Food.Builder()).hunger(4).saturation(0.3F).setAlwaysEdible().build();
      COD = (new Food.Builder()).hunger(2).saturation(0.1F).build();
      COOKED_BEEF = (new Food.Builder()).hunger(8).saturation(0.8F).meat().build();
      COOKED_CHICKEN = (new Food.Builder()).hunger(6).saturation(0.6F).meat().build();
      COOKED_COD = (new Food.Builder()).hunger(5).saturation(0.6F).build();
      COOKED_MUTTON = (new Food.Builder()).hunger(6).saturation(0.8F).meat().build();
      COOKED_PORKCHOP = (new Food.Builder()).hunger(8).saturation(0.8F).meat().build();
      COOKED_RABBIT = (new Food.Builder()).hunger(5).saturation(0.6F).meat().build();
      COOKED_SALMON = (new Food.Builder()).hunger(6).saturation(0.8F).build();
      COOKIE = (new Food.Builder()).hunger(2).saturation(0.1F).build();
      DRIED_KELP = (new Food.Builder()).hunger(1).saturation(0.3F).fastToEat().build();
      ENCHANTED_GOLDEN_APPLE = (new Food.Builder()).hunger(4).saturation(1.2F).effect(new EffectInstance(Effects.REGENERATION, 400, 1), 1.0F).effect(new EffectInstance(Effects.RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0), 1.0F).effect(new EffectInstance(Effects.ABSORPTION, 2400, 3), 1.0F).setAlwaysEdible().build();
      GOLDEN_APPLE = (new Food.Builder()).hunger(4).saturation(1.2F).effect(new EffectInstance(Effects.REGENERATION, 100, 1), 1.0F).effect(new EffectInstance(Effects.ABSORPTION, 2400, 0), 1.0F).setAlwaysEdible().build();
      GOLDEN_CARROT = (new Food.Builder()).hunger(6).saturation(1.2F).build();
      field_226604_w_ = (new Food.Builder()).hunger(6).saturation(0.1F).build();
      MELON_SLICE = (new Food.Builder()).hunger(2).saturation(0.3F).build();
      MUSHROOM_STEW = buildStew(6);
      MUTTON = (new Food.Builder()).hunger(2).saturation(0.3F).meat().build();
      POISONOUS_POTATO = (new Food.Builder()).hunger(2).saturation(0.3F).effect(new EffectInstance(Effects.POISON, 100, 0), 0.6F).build();
      PORKCHOP = (new Food.Builder()).hunger(3).saturation(0.3F).meat().build();
      POTATO = (new Food.Builder()).hunger(1).saturation(0.3F).build();
      PUFFERFISH = (new Food.Builder()).hunger(1).saturation(0.1F).effect(new EffectInstance(Effects.POISON, 1200, 3), 1.0F).effect(new EffectInstance(Effects.HUNGER, 300, 2), 1.0F).effect(new EffectInstance(Effects.NAUSEA, 300, 1), 1.0F).build();
      PUMPKIN_PIE = (new Food.Builder()).hunger(8).saturation(0.3F).build();
      RABBIT = (new Food.Builder()).hunger(3).saturation(0.3F).meat().build();
      RABBIT_STEW = buildStew(10);
      ROTTEN_FLESH = (new Food.Builder()).hunger(4).saturation(0.1F).effect(new EffectInstance(Effects.HUNGER, 600, 0), 0.8F).meat().build();
      SALMON = (new Food.Builder()).hunger(2).saturation(0.1F).build();
      SPIDER_EYE = (new Food.Builder()).hunger(2).saturation(0.8F).effect(new EffectInstance(Effects.POISON, 100, 0), 1.0F).build();
      SUSPICIOUS_STEW = buildStew(6);
      SWEET_BERRIES = (new Food.Builder()).hunger(2).saturation(0.1F).build();
      TROPICAL_FISH = (new Food.Builder()).hunger(1).saturation(0.1F).build();
   }
}
