package net.minecraft.potion;

import net.minecraft.util.registry.Registry;

public class Potions {
   public static final Potion EMPTY = register("empty", new Potion(new EffectInstance[0]));
   public static final Potion WATER = register("water", new Potion(new EffectInstance[0]));
   public static final Potion MUNDANE = register("mundane", new Potion(new EffectInstance[0]));
   public static final Potion THICK = register("thick", new Potion(new EffectInstance[0]));
   public static final Potion AWKWARD = register("awkward", new Potion(new EffectInstance[0]));
   public static final Potion NIGHT_VISION;
   public static final Potion LONG_NIGHT_VISION;
   public static final Potion INVISIBILITY;
   public static final Potion LONG_INVISIBILITY;
   public static final Potion LEAPING;
   public static final Potion LONG_LEAPING;
   public static final Potion STRONG_LEAPING;
   public static final Potion FIRE_RESISTANCE;
   public static final Potion LONG_FIRE_RESISTANCE;
   public static final Potion SWIFTNESS;
   public static final Potion LONG_SWIFTNESS;
   public static final Potion STRONG_SWIFTNESS;
   public static final Potion SLOWNESS;
   public static final Potion LONG_SLOWNESS;
   public static final Potion STRONG_SLOWNESS;
   public static final Potion TURTLE_MASTER;
   public static final Potion LONG_TURTLE_MASTER;
   public static final Potion STRONG_TURTLE_MASTER;
   public static final Potion WATER_BREATHING;
   public static final Potion LONG_WATER_BREATHING;
   public static final Potion HEALING;
   public static final Potion STRONG_HEALING;
   public static final Potion HARMING;
   public static final Potion STRONG_HARMING;
   public static final Potion POISON;
   public static final Potion LONG_POISON;
   public static final Potion STRONG_POISON;
   public static final Potion REGENERATION;
   public static final Potion LONG_REGENERATION;
   public static final Potion STRONG_REGENERATION;
   public static final Potion STRENGTH;
   public static final Potion LONG_STRENGTH;
   public static final Potion STRONG_STRENGTH;
   public static final Potion WEAKNESS;
   public static final Potion LONG_WEAKNESS;
   public static final Potion LUCK;
   public static final Potion SLOW_FALLING;
   public static final Potion LONG_SLOW_FALLING;

   private static Potion register(String p_222125_0_, Potion p_222125_1_) {
      return (Potion)Registry.register((Registry)Registry.POTION, (String)p_222125_0_, (Object)p_222125_1_);
   }

   static {
      NIGHT_VISION = register("night_vision", new Potion(new EffectInstance[]{new EffectInstance(Effects.NIGHT_VISION, 3600)}));
      LONG_NIGHT_VISION = register("long_night_vision", new Potion("night_vision", new EffectInstance[]{new EffectInstance(Effects.NIGHT_VISION, 9600)}));
      INVISIBILITY = register("invisibility", new Potion(new EffectInstance[]{new EffectInstance(Effects.INVISIBILITY, 3600)}));
      LONG_INVISIBILITY = register("long_invisibility", new Potion("invisibility", new EffectInstance[]{new EffectInstance(Effects.INVISIBILITY, 9600)}));
      LEAPING = register("leaping", new Potion(new EffectInstance[]{new EffectInstance(Effects.JUMP_BOOST, 3600)}));
      LONG_LEAPING = register("long_leaping", new Potion("leaping", new EffectInstance[]{new EffectInstance(Effects.JUMP_BOOST, 9600)}));
      STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new EffectInstance[]{new EffectInstance(Effects.JUMP_BOOST, 1800, 1)}));
      FIRE_RESISTANCE = register("fire_resistance", new Potion(new EffectInstance[]{new EffectInstance(Effects.FIRE_RESISTANCE, 3600)}));
      LONG_FIRE_RESISTANCE = register("long_fire_resistance", new Potion("fire_resistance", new EffectInstance[]{new EffectInstance(Effects.FIRE_RESISTANCE, 9600)}));
      SWIFTNESS = register("swiftness", new Potion(new EffectInstance[]{new EffectInstance(Effects.SPEED, 3600)}));
      LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new EffectInstance[]{new EffectInstance(Effects.SPEED, 9600)}));
      STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new EffectInstance[]{new EffectInstance(Effects.SPEED, 1800, 1)}));
      SLOWNESS = register("slowness", new Potion(new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 1800)}));
      LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 4800)}));
      STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 400, 3)}));
      TURTLE_MASTER = register("turtle_master", new Potion("turtle_master", new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 400, 3), new EffectInstance(Effects.RESISTANCE, 400, 2)}));
      LONG_TURTLE_MASTER = register("long_turtle_master", new Potion("turtle_master", new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 800, 3), new EffectInstance(Effects.RESISTANCE, 800, 2)}));
      STRONG_TURTLE_MASTER = register("strong_turtle_master", new Potion("turtle_master", new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 400, 5), new EffectInstance(Effects.RESISTANCE, 400, 3)}));
      WATER_BREATHING = register("water_breathing", new Potion(new EffectInstance[]{new EffectInstance(Effects.WATER_BREATHING, 3600)}));
      LONG_WATER_BREATHING = register("long_water_breathing", new Potion("water_breathing", new EffectInstance[]{new EffectInstance(Effects.WATER_BREATHING, 9600)}));
      HEALING = register("healing", new Potion(new EffectInstance[]{new EffectInstance(Effects.INSTANT_HEALTH, 1)}));
      STRONG_HEALING = register("strong_healing", new Potion("healing", new EffectInstance[]{new EffectInstance(Effects.INSTANT_HEALTH, 1, 1)}));
      HARMING = register("harming", new Potion(new EffectInstance[]{new EffectInstance(Effects.INSTANT_DAMAGE, 1)}));
      STRONG_HARMING = register("strong_harming", new Potion("harming", new EffectInstance[]{new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1)}));
      POISON = register("poison", new Potion(new EffectInstance[]{new EffectInstance(Effects.POISON, 900)}));
      LONG_POISON = register("long_poison", new Potion("poison", new EffectInstance[]{new EffectInstance(Effects.POISON, 1800)}));
      STRONG_POISON = register("strong_poison", new Potion("poison", new EffectInstance[]{new EffectInstance(Effects.POISON, 432, 1)}));
      REGENERATION = register("regeneration", new Potion(new EffectInstance[]{new EffectInstance(Effects.REGENERATION, 900)}));
      LONG_REGENERATION = register("long_regeneration", new Potion("regeneration", new EffectInstance[]{new EffectInstance(Effects.REGENERATION, 1800)}));
      STRONG_REGENERATION = register("strong_regeneration", new Potion("regeneration", new EffectInstance[]{new EffectInstance(Effects.REGENERATION, 450, 1)}));
      STRENGTH = register("strength", new Potion(new EffectInstance[]{new EffectInstance(Effects.STRENGTH, 3600)}));
      LONG_STRENGTH = register("long_strength", new Potion("strength", new EffectInstance[]{new EffectInstance(Effects.STRENGTH, 9600)}));
      STRONG_STRENGTH = register("strong_strength", new Potion("strength", new EffectInstance[]{new EffectInstance(Effects.STRENGTH, 1800, 1)}));
      WEAKNESS = register("weakness", new Potion(new EffectInstance[]{new EffectInstance(Effects.WEAKNESS, 1800)}));
      LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new EffectInstance[]{new EffectInstance(Effects.WEAKNESS, 4800)}));
      LUCK = register("luck", new Potion("luck", new EffectInstance[]{new EffectInstance(Effects.LUCK, 6000)}));
      SLOW_FALLING = register("slow_falling", new Potion(new EffectInstance[]{new EffectInstance(Effects.SLOW_FALLING, 1800)}));
      LONG_SLOW_FALLING = register("long_slow_falling", new Potion("slow_falling", new EffectInstance[]{new EffectInstance(Effects.SLOW_FALLING, 4800)}));
   }
}
