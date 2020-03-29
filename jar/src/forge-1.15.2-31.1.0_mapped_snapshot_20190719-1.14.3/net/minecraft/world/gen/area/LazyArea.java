package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea {
   private final IPixelTransformer pixelTransformer;
   private final Long2IntLinkedOpenHashMap cachedValues;
   private final int maxCacheSize;

   public LazyArea(Long2IntLinkedOpenHashMap p_i51286_1_, int p_i51286_2_, IPixelTransformer p_i51286_3_) {
      this.cachedValues = p_i51286_1_;
      this.maxCacheSize = p_i51286_2_;
      this.pixelTransformer = p_i51286_3_;
   }

   public int getValue(int p_202678_1_, int p_202678_2_) {
      long lvt_3_1_ = ChunkPos.asLong(p_202678_1_, p_202678_2_);
      synchronized(this.cachedValues) {
         int lvt_6_1_ = this.cachedValues.get(lvt_3_1_);
         if (lvt_6_1_ != Integer.MIN_VALUE) {
            return lvt_6_1_;
         } else {
            int lvt_7_1_ = this.pixelTransformer.apply(p_202678_1_, p_202678_2_);
            this.cachedValues.put(lvt_3_1_, lvt_7_1_);
            if (this.cachedValues.size() > this.maxCacheSize) {
               for(int lvt_8_1_ = 0; lvt_8_1_ < this.maxCacheSize / 16; ++lvt_8_1_) {
                  this.cachedValues.removeFirstInt();
               }
            }

            return lvt_7_1_;
         }
      }
   }

   public int getmaxCacheSize() {
      return this.maxCacheSize;
   }
}
