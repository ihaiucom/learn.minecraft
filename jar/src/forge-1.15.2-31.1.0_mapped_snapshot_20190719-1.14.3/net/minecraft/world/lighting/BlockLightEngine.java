package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.Mutable scratchPos = new BlockPos.Mutable();

   public BlockLightEngine(IChunkLightProvider p_i51301_1_) {
      super(p_i51301_1_, LightType.BLOCK, new BlockLightStorage(p_i51301_1_));
   }

   private int getLightValue(long p_215635_1_) {
      int i = BlockPos.unpackX(p_215635_1_);
      int j = BlockPos.unpackY(p_215635_1_);
      int k = BlockPos.unpackZ(p_215635_1_);
      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(i >> 4, k >> 4);
      return iblockreader != null ? iblockreader.getLightValue(this.scratchPos.setPos(i, j, k)) : 0;
   }

   protected int getEdgeLevel(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      if (p_215480_3_ == Long.MAX_VALUE) {
         return 15;
      } else if (p_215480_1_ == Long.MAX_VALUE) {
         return p_215480_5_ + 15 - this.getLightValue(p_215480_3_);
      } else if (p_215480_5_ >= 15) {
         return p_215480_5_;
      } else {
         int i = Integer.signum(BlockPos.unpackX(p_215480_3_) - BlockPos.unpackX(p_215480_1_));
         int j = Integer.signum(BlockPos.unpackY(p_215480_3_) - BlockPos.unpackY(p_215480_1_));
         int k = Integer.signum(BlockPos.unpackZ(p_215480_3_) - BlockPos.unpackZ(p_215480_1_));
         Direction direction = Direction.func_218383_a(i, j, k);
         if (direction == null) {
            return 15;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.func_227468_a_(p_215480_3_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               BlockState blockstate1 = this.func_227468_a_(p_215480_1_, (MutableInt)null);
               VoxelShape voxelshape = this.getVoxelShape(blockstate1, p_215480_1_, direction);
               VoxelShape voxelshape1 = this.getVoxelShape(blockstate, p_215480_3_, direction.getOpposite());
               return VoxelShapes.func_223416_b(voxelshape, voxelshape1) ? 15 : p_215480_5_ + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void notifyNeighbors(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      long i = SectionPos.worldToSection(p_215478_1_);
      Direction[] var7 = DIRECTIONS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Direction direction = var7[var9];
         long j = BlockPos.offset(p_215478_1_, direction);
         long k = SectionPos.worldToSection(j);
         if (i == k || ((BlockLightStorage)this.storage).hasSection(k)) {
            this.propagateLevel(p_215478_1_, j, p_215478_3_, p_215478_4_);
         }
      }

   }

   protected int computeLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;
      if (Long.MAX_VALUE != p_215477_3_) {
         int j = this.getEdgeLevel(Long.MAX_VALUE, p_215477_1_, 0);
         if (p_215477_5_ > j) {
            i = j;
         }

         if (i == 0) {
            return i;
         }
      }

      long j1 = SectionPos.worldToSection(p_215477_1_);
      NibbleArray nibblearray = ((BlockLightStorage)this.storage).getArray(j1, true);
      Direction[] var10 = DIRECTIONS;
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction direction = var10[var12];
         long k = BlockPos.offset(p_215477_1_, direction);
         if (k != p_215477_3_) {
            long l = SectionPos.worldToSection(k);
            NibbleArray nibblearray1;
            if (j1 == l) {
               nibblearray1 = nibblearray;
            } else {
               nibblearray1 = ((BlockLightStorage)this.storage).getArray(l, true);
            }

            if (nibblearray1 != null) {
               int i1 = this.getEdgeLevel(k, p_215477_1_, this.getLevelFromArray(nibblearray1, k));
               if (i > i1) {
                  i = i1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
      ((BlockLightStorage)this.storage).processAllLevelUpdates();
      this.scheduleUpdate(Long.MAX_VALUE, p_215623_1_.toLong(), 15 - p_215623_2_, true);
   }

   public int queuedUpdateSize() {
      return ((BlockLightStorage)this.storage).queuedUpdateSize();
   }
}
