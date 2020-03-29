package net.minecraftforge.fml;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class WorldPersistenceHooks {
   private static List<WorldPersistenceHooks.WorldPersistenceHook> worldPersistenceHooks = new ArrayList();

   public static void addHook(WorldPersistenceHooks.WorldPersistenceHook hook) {
      worldPersistenceHooks.add(hook);
   }

   public static void handleWorldDataSave(SaveHandler handler, WorldInfo worldInfo, CompoundNBT tagCompound) {
      worldPersistenceHooks.forEach((wac) -> {
         tagCompound.put(wac.getModId(), wac.getDataForWriting(handler, worldInfo));
      });
   }

   public static void handleWorldDataLoad(SaveHandler handler, WorldInfo worldInfo, CompoundNBT tagCompound) {
      if (EffectiveSide.get() == LogicalSide.SERVER) {
         worldPersistenceHooks.forEach((wac) -> {
            wac.readData(handler, worldInfo, tagCompound.getCompound(wac.getModId()));
         });
      }

   }

   public static void confirmBackupLevelDatUse(SaveHandler handler) {
   }

   public interface WorldPersistenceHook {
      String getModId();

      CompoundNBT getDataForWriting(SaveHandler var1, WorldInfo var2);

      void readData(SaveHandler var1, WorldInfo var2, CompoundNBT var3);
   }
}
