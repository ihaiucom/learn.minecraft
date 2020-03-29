package net.minecraft.entity.ai.brain.schedule;

import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Activity extends ForgeRegistryEntry<Activity> {
   public static final Activity CORE = register("core");
   public static final Activity IDLE = register("idle");
   public static final Activity WORK = register("work");
   public static final Activity PLAY = register("play");
   public static final Activity REST = register("rest");
   public static final Activity MEET = register("meet");
   public static final Activity PANIC = register("panic");
   public static final Activity RAID = register("raid");
   public static final Activity PRE_RAID = register("pre_raid");
   public static final Activity HIDE = register("hide");
   private final String id;

   public Activity(String p_i50141_1_) {
      this.id = p_i50141_1_;
   }

   public String func_221364_a() {
      return this.id;
   }

   private static Activity register(String p_221363_0_) {
      return (Activity)Registry.register((Registry)Registry.ACTIVITY, (String)p_221363_0_, (Object)(new Activity(p_221363_0_)));
   }

   public String toString() {
      return this.func_221364_a();
   }
}
