package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.NibbleArray;

public interface IWorldLightListener extends ILightListener {
   @Nullable
   NibbleArray getData(SectionPos var1);

   int getLightFor(BlockPos var1);

   public static enum Dummy implements IWorldLightListener {
      INSTANCE;

      @Nullable
      public NibbleArray getData(SectionPos p_215612_1_) {
         return null;
      }

      public int getLightFor(BlockPos p_215611_1_) {
         return 0;
      }

      public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      }
   }
}
