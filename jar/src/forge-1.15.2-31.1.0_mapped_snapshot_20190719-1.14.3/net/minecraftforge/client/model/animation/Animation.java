package net.minecraftforge.client.model.animation;

import java.lang.ref.WeakReference;
import net.minecraft.world.World;

public enum Animation {
   INSTANCE;

   private float clientPartialTickTime;
   private static long epochTime;
   private static WeakReference<World> worldRef;

   public static float getWorldTime(World world) {
      return getWorldTime(world, 0.0F);
   }

   public static float getWorldTime(World world, float tickProgress) {
      if (worldRef == null || worldRef.get() != world) {
         epochTime = world.getGameTime();
         worldRef = new WeakReference(world);
      }

      return ((float)(world.getGameTime() - epochTime) + tickProgress) / 20.0F;
   }

   public static float getPartialTickTime() {
      return INSTANCE.clientPartialTickTime;
   }

   public static void setClientPartialTickTime(float clientPartialTickTime) {
      INSTANCE.clientPartialTickTime = clientPartialTickTime;
   }
}
