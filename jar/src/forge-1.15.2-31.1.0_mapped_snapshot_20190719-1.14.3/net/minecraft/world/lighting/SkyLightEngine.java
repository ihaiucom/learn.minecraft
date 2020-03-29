package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine extends LightEngine<SkyLightStorage.StorageMap, SkyLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Direction[] CARDINALS;

   public SkyLightEngine(IChunkLightProvider p_i51289_1_) {
      super(p_i51289_1_, LightType.SKY, new SkyLightStorage(p_i51289_1_));
   }

   protected int getEdgeLevel(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      if (p_215480_3_ == Long.MAX_VALUE) {
         return 15;
      } else {
         if (p_215480_1_ == Long.MAX_VALUE) {
            if (!((SkyLightStorage)this.storage).func_215551_l(p_215480_3_)) {
               return 15;
            }

            p_215480_5_ = 0;
         }

         if (p_215480_5_ >= 15) {
            return p_215480_5_;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.func_227468_a_(p_215480_3_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               int i = BlockPos.unpackX(p_215480_1_);
               int j = BlockPos.unpackY(p_215480_1_);
               int k = BlockPos.unpackZ(p_215480_1_);
               int l = BlockPos.unpackX(p_215480_3_);
               int i1 = BlockPos.unpackY(p_215480_3_);
               int j1 = BlockPos.unpackZ(p_215480_3_);
               boolean flag = i == l && k == j1;
               int k1 = Integer.signum(l - i);
               int l1 = Integer.signum(i1 - j);
               int i2 = Integer.signum(j1 - k);
               Direction direction;
               if (p_215480_1_ == Long.MAX_VALUE) {
                  direction = Direction.DOWN;
               } else {
                  direction = Direction.func_218383_a(k1, l1, i2);
               }

               BlockState blockstate1 = this.func_227468_a_(p_215480_1_, (MutableInt)null);
               VoxelShape voxelshape;
               if (direction != null) {
                  voxelshape = this.getVoxelShape(blockstate1, p_215480_1_, direction);
                  VoxelShape voxelshape1 = this.getVoxelShape(blockstate, p_215480_3_, direction.getOpposite());
                  if (VoxelShapes.func_223416_b(voxelshape, voxelshape1)) {
                     return 15;
                  }
               } else {
                  voxelshape = this.getVoxelShape(blockstate1, p_215480_1_, Direction.DOWN);
                  if (VoxelShapes.func_223416_b(voxelshape, VoxelShapes.empty())) {
                     return 15;
                  }

                  int j2 = flag ? -1 : 0;
                  Direction direction1 = Direction.func_218383_a(k1, j2, i2);
                  if (direction1 == null) {
                     return 15;
                  }

                  VoxelShape voxelshape2 = this.getVoxelShape(blockstate, p_215480_3_, direction1.getOpposite());
                  if (VoxelShapes.func_223416_b(VoxelShapes.empty(), voxelshape2)) {
                     return 15;
                  }
               }

               boolean flag1 = p_215480_1_ == Long.MAX_VALUE || flag && j > i1;
               return flag1 && p_215480_5_ == 0 && mutableint.getValue() == 0 ? 0 : p_215480_5_ + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void notifyNeighbors(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      long i = SectionPos.worldToSection(p_215478_1_);
      int j = BlockPos.unpackY(p_215478_1_);
      int k = SectionPos.mask(j);
      int l = SectionPos.toChunk(j);
      int i1;
      if (k != 0) {
         i1 = 0;
      } else {
         int j1;
         for(j1 = 0; !((SkyLightStorage)this.storage).hasSection(SectionPos.withOffset(i, 0, -j1 - 1, 0)) && ((SkyLightStorage)this.storage).func_215550_a(l - j1 - 1); ++j1) {
         }

         i1 = j1;
      }

      long i3 = BlockPos.offset(p_215478_1_, 0, -1 - i1 * 16, 0);
      long k1 = SectionPos.worldToSection(i3);
      if (i == k1 || ((SkyLightStorage)this.storage).hasSection(k1)) {
         this.propagateLevel(p_215478_1_, i3, p_215478_3_, p_215478_4_);
      }

      long l1 = BlockPos.offset(p_215478_1_, Direction.UP);
      long i2 = SectionPos.worldToSection(l1);
      if (i == i2 || ((SkyLightStorage)this.storage).hasSection(i2)) {
         this.propagateLevel(p_215478_1_, l1, p_215478_3_, p_215478_4_);
      }

      Direction[] var19 = CARDINALS;
      int var20 = var19.length;

      for(int var21 = 0; var21 < var20; ++var21) {
         Direction direction = var19[var21];
         int j2 = 0;

         while(true) {
            long k2 = BlockPos.offset(p_215478_1_, direction.getXOffset(), -j2, direction.getZOffset());
            long l2 = SectionPos.worldToSection(k2);
            if (i == l2) {
               this.propagateLevel(p_215478_1_, k2, p_215478_3_, p_215478_4_);
               break;
            }

            if (((SkyLightStorage)this.storage).hasSection(l2)) {
               this.propagateLevel(p_215478_1_, k2, p_215478_3_, p_215478_4_);
            }

            ++j2;
            if (j2 > i1 * 16) {
               break;
            }
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
      NibbleArray nibblearray = ((SkyLightStorage)this.storage).getArray(j1, true);
      Direction[] var10 = DIRECTIONS;
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction direction = var10[var12];
         long k = BlockPos.offset(p_215477_1_, direction);
         long l = SectionPos.worldToSection(k);
         NibbleArray nibblearray1;
         if (j1 == l) {
            nibblearray1 = nibblearray;
         } else {
            nibblearray1 = ((SkyLightStorage)this.storage).getArray(l, true);
         }

         if (nibblearray1 != null) {
            if (k != p_215477_3_) {
               int k1 = this.getEdgeLevel(k, p_215477_1_, this.getLevelFromArray(nibblearray1, k));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         } else if (direction != Direction.DOWN) {
            for(k = BlockPos.func_218288_f(k); !((SkyLightStorage)this.storage).hasSection(l) && !((SkyLightStorage)this.storage).func_215549_m(l); k = BlockPos.offset(k, 0, 16, 0)) {
               l = SectionPos.withOffset(l, Direction.UP);
            }

            NibbleArray nibblearray2 = ((SkyLightStorage)this.storage).getArray(l, true);
            if (k != p_215477_3_) {
               int i1;
               if (nibblearray2 != null) {
                  i1 = this.getEdgeLevel(k, p_215477_1_, this.getLevelFromArray(nibblearray2, k));
               } else {
                  i1 = ((SkyLightStorage)this.storage).func_215548_n(l) ? 0 : 15;
               }

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

   protected void scheduleUpdate(long p_215473_1_) {
      ((SkyLightStorage)this.storage).processAllLevelUpdates();
      long i = SectionPos.worldToSection(p_215473_1_);
      if (((SkyLightStorage)this.storage).hasSection(i)) {
         super.scheduleUpdate(p_215473_1_);
      } else {
         for(p_215473_1_ = BlockPos.func_218288_f(p_215473_1_); !((SkyLightStorage)this.storage).hasSection(i) && !((SkyLightStorage)this.storage).func_215549_m(i); p_215473_1_ = BlockPos.offset(p_215473_1_, 0, 16, 0)) {
            i = SectionPos.withOffset(i, Direction.UP);
         }

         if (((SkyLightStorage)this.storage).hasSection(i)) {
            super.scheduleUpdate(p_215473_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugString(long p_215614_1_) {
      return super.getDebugString(p_215614_1_) + (((SkyLightStorage)this.storage).func_215549_m(p_215614_1_) ? "*" : "");
   }

   public int queuedUpdateSize() {
      return 0;
   }

   static {
      CARDINALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   }
}
