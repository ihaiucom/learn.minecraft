package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class BlockLightStorage extends SectionLightStorage<BlockLightStorage.StorageMap> {
   protected BlockLightStorage(IChunkLightProvider p_i51300_1_) {
      super(LightType.BLOCK, p_i51300_1_, new BlockLightStorage.StorageMap(new Long2ObjectOpenHashMap()));
   }

   protected int getLightOrDefault(long p_215525_1_) {
      long lvt_3_1_ = SectionPos.worldToSection(p_215525_1_);
      NibbleArray lvt_5_1_ = this.getArray(lvt_3_1_, false);
      return lvt_5_1_ == null ? 0 : lvt_5_1_.get(SectionPos.mask(BlockPos.unpackX(p_215525_1_)), SectionPos.mask(BlockPos.unpackY(p_215525_1_)), SectionPos.mask(BlockPos.unpackZ(p_215525_1_)));
   }

   public static final class StorageMap extends LightDataMap<BlockLightStorage.StorageMap> {
      public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50064_1_) {
         super(p_i50064_1_);
      }

      public BlockLightStorage.StorageMap copy() {
         return new BlockLightStorage.StorageMap(this.arrays.clone());
      }

      // $FF: synthetic method
      public LightDataMap copy() {
         return this.copy();
      }
   }
}
