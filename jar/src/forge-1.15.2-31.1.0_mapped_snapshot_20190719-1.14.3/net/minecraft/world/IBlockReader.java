package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;

public interface IBlockReader {
   @Nullable
   TileEntity getTileEntity(BlockPos var1);

   BlockState getBlockState(BlockPos var1);

   IFluidState getFluidState(BlockPos var1);

   default int getLightValue(BlockPos p_217298_1_) {
      return this.getBlockState(p_217298_1_).getLightValue(this, p_217298_1_);
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default int getHeight() {
      return 256;
   }

   default BlockRayTraceResult rayTraceBlocks(RayTraceContext p_217299_1_) {
      return (BlockRayTraceResult)func_217300_a(p_217299_1_, (p_lambda$rayTraceBlocks$0_1_, p_lambda$rayTraceBlocks$0_2_) -> {
         BlockState blockstate = this.getBlockState(p_lambda$rayTraceBlocks$0_2_);
         IFluidState ifluidstate = this.getFluidState(p_lambda$rayTraceBlocks$0_2_);
         Vec3d vec3d = p_lambda$rayTraceBlocks$0_1_.func_222253_b();
         Vec3d vec3d1 = p_lambda$rayTraceBlocks$0_1_.func_222250_a();
         VoxelShape voxelshape = p_lambda$rayTraceBlocks$0_1_.getBlockShape(blockstate, this, p_lambda$rayTraceBlocks$0_2_);
         BlockRayTraceResult blockraytraceresult = this.func_217296_a(vec3d, vec3d1, p_lambda$rayTraceBlocks$0_2_, voxelshape, blockstate);
         VoxelShape voxelshape1 = p_lambda$rayTraceBlocks$0_1_.getFluidShape(ifluidstate, this, p_lambda$rayTraceBlocks$0_2_);
         BlockRayTraceResult blockraytraceresult1 = voxelshape1.rayTrace(vec3d, vec3d1, p_lambda$rayTraceBlocks$0_2_);
         double d0 = blockraytraceresult == null ? Double.MAX_VALUE : p_lambda$rayTraceBlocks$0_1_.func_222253_b().squareDistanceTo(blockraytraceresult.getHitVec());
         double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : p_lambda$rayTraceBlocks$0_1_.func_222253_b().squareDistanceTo(blockraytraceresult1.getHitVec());
         return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
      }, (p_lambda$rayTraceBlocks$1_0_) -> {
         Vec3d vec3d = p_lambda$rayTraceBlocks$1_0_.func_222253_b().subtract(p_lambda$rayTraceBlocks$1_0_.func_222250_a());
         return BlockRayTraceResult.createMiss(p_lambda$rayTraceBlocks$1_0_.func_222250_a(), Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(p_lambda$rayTraceBlocks$1_0_.func_222250_a()));
      });
   }

   @Nullable
   default BlockRayTraceResult func_217296_a(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_, VoxelShape p_217296_4_, BlockState p_217296_5_) {
      BlockRayTraceResult blockraytraceresult = p_217296_4_.rayTrace(p_217296_1_, p_217296_2_, p_217296_3_);
      if (blockraytraceresult != null) {
         BlockRayTraceResult blockraytraceresult1 = p_217296_5_.getRaytraceShape(this, p_217296_3_).rayTrace(p_217296_1_, p_217296_2_, p_217296_3_);
         if (blockraytraceresult1 != null && blockraytraceresult1.getHitVec().subtract(p_217296_1_).lengthSquared() < blockraytraceresult.getHitVec().subtract(p_217296_1_).lengthSquared()) {
            return blockraytraceresult.withFace(blockraytraceresult1.getFace());
         }
      }

      return blockraytraceresult;
   }

   static <T> T func_217300_a(RayTraceContext p_217300_0_, BiFunction<RayTraceContext, BlockPos, T> p_217300_1_, Function<RayTraceContext, T> p_217300_2_) {
      Vec3d vec3d = p_217300_0_.func_222253_b();
      Vec3d vec3d1 = p_217300_0_.func_222250_a();
      if (vec3d.equals(vec3d1)) {
         return p_217300_2_.apply(p_217300_0_);
      } else {
         double d0 = MathHelper.lerp(-1.0E-7D, vec3d1.x, vec3d.x);
         double d1 = MathHelper.lerp(-1.0E-7D, vec3d1.y, vec3d.y);
         double d2 = MathHelper.lerp(-1.0E-7D, vec3d1.z, vec3d.z);
         double d3 = MathHelper.lerp(-1.0E-7D, vec3d.x, vec3d1.x);
         double d4 = MathHelper.lerp(-1.0E-7D, vec3d.y, vec3d1.y);
         double d5 = MathHelper.lerp(-1.0E-7D, vec3d.z, vec3d1.z);
         int i = MathHelper.floor(d3);
         int j = MathHelper.floor(d4);
         int k = MathHelper.floor(d5);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i, j, k);
         T t = p_217300_1_.apply(p_217300_0_, blockpos$mutable);
         if (t != null) {
            return t;
         } else {
            double d6 = d0 - d3;
            double d7 = d1 - d4;
            double d8 = d2 - d5;
            int l = MathHelper.signum(d6);
            int i1 = MathHelper.signum(d7);
            int j1 = MathHelper.signum(d8);
            double d9 = l == 0 ? Double.MAX_VALUE : (double)l / d6;
            double d10 = i1 == 0 ? Double.MAX_VALUE : (double)i1 / d7;
            double d11 = j1 == 0 ? Double.MAX_VALUE : (double)j1 / d8;
            double d12 = d9 * (l > 0 ? 1.0D - MathHelper.frac(d3) : MathHelper.frac(d3));
            double d13 = d10 * (i1 > 0 ? 1.0D - MathHelper.frac(d4) : MathHelper.frac(d4));
            double d14 = d11 * (j1 > 0 ? 1.0D - MathHelper.frac(d5) : MathHelper.frac(d5));

            Object t1;
            do {
               if (d12 > 1.0D && d13 > 1.0D && d14 > 1.0D) {
                  return p_217300_2_.apply(p_217300_0_);
               }

               if (d12 < d13) {
                  if (d12 < d14) {
                     i += l;
                     d12 += d9;
                  } else {
                     k += j1;
                     d14 += d11;
                  }
               } else if (d13 < d14) {
                  j += i1;
                  d13 += d10;
               } else {
                  k += j1;
                  d14 += d11;
               }

               t1 = p_217300_1_.apply(p_217300_0_, blockpos$mutable.setPos(i, j, k));
            } while(t1 == null);

            return t1;
         }
      }
   }
}
