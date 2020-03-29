package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.NibbleArray;

public abstract class LightDataMap<M extends LightDataMap<M>> {
   private final long[] recentPositions = new long[2];
   private final NibbleArray[] recentArrays = new NibbleArray[2];
   private boolean useCaching;
   protected final Long2ObjectOpenHashMap<NibbleArray> arrays;

   protected LightDataMap(Long2ObjectOpenHashMap<NibbleArray> p_i51299_1_) {
      this.arrays = p_i51299_1_;
      this.invalidateCaches();
      this.useCaching = true;
   }

   public abstract M copy();

   public void copyArray(long p_215641_1_) {
      this.arrays.put(p_215641_1_, ((NibbleArray)this.arrays.get(p_215641_1_)).copy());
      this.invalidateCaches();
   }

   public boolean hasArray(long p_215642_1_) {
      return this.arrays.containsKey(p_215642_1_);
   }

   @Nullable
   public NibbleArray getArray(long p_215638_1_) {
      if (this.useCaching) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < 2; ++lvt_3_1_) {
            if (p_215638_1_ == this.recentPositions[lvt_3_1_]) {
               return this.recentArrays[lvt_3_1_];
            }
         }
      }

      NibbleArray lvt_3_2_ = (NibbleArray)this.arrays.get(p_215638_1_);
      if (lvt_3_2_ == null) {
         return null;
      } else {
         if (this.useCaching) {
            for(int lvt_4_1_ = 1; lvt_4_1_ > 0; --lvt_4_1_) {
               this.recentPositions[lvt_4_1_] = this.recentPositions[lvt_4_1_ - 1];
               this.recentArrays[lvt_4_1_] = this.recentArrays[lvt_4_1_ - 1];
            }

            this.recentPositions[0] = p_215638_1_;
            this.recentArrays[0] = lvt_3_2_;
         }

         return lvt_3_2_;
      }
   }

   @Nullable
   public NibbleArray removeArray(long p_223130_1_) {
      return (NibbleArray)this.arrays.remove(p_223130_1_);
   }

   public void setArray(long p_215640_1_, NibbleArray p_215640_3_) {
      this.arrays.put(p_215640_1_, p_215640_3_);
   }

   public void invalidateCaches() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_) {
         this.recentPositions[lvt_1_1_] = Long.MAX_VALUE;
         this.recentArrays[lvt_1_1_] = null;
      }

   }

   public void disableCaching() {
      this.useCaching = false;
   }
}
