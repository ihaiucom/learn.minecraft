package net.minecraft.util.math;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class RayTraceContext {
   private final Vec3d startVec;
   private final Vec3d endVec;
   private final RayTraceContext.BlockMode blockMode;
   private final RayTraceContext.FluidMode fluidMode;
   private final ISelectionContext context;

   public RayTraceContext(Vec3d p_i50009_1_, Vec3d p_i50009_2_, RayTraceContext.BlockMode p_i50009_3_, RayTraceContext.FluidMode p_i50009_4_, Entity p_i50009_5_) {
      this.startVec = p_i50009_1_;
      this.endVec = p_i50009_2_;
      this.blockMode = p_i50009_3_;
      this.fluidMode = p_i50009_4_;
      this.context = ISelectionContext.forEntity(p_i50009_5_);
   }

   public Vec3d func_222250_a() {
      return this.endVec;
   }

   public Vec3d func_222253_b() {
      return this.startVec;
   }

   public VoxelShape getBlockShape(BlockState p_222251_1_, IBlockReader p_222251_2_, BlockPos p_222251_3_) {
      return this.blockMode.get(p_222251_1_, p_222251_2_, p_222251_3_, this.context);
   }

   public VoxelShape getFluidShape(IFluidState p_222252_1_, IBlockReader p_222252_2_, BlockPos p_222252_3_) {
      return this.fluidMode.test(p_222252_1_) ? p_222252_1_.getShape(p_222252_2_, p_222252_3_) : VoxelShapes.empty();
   }

   public static enum FluidMode {
      NONE((p_222247_0_) -> {
         return false;
      }),
      SOURCE_ONLY(IFluidState::isSource),
      ANY((p_222246_0_) -> {
         return !p_222246_0_.isEmpty();
      });

      private final Predicate<IFluidState> fluidTest;

      private FluidMode(Predicate<IFluidState> p_i49923_3_) {
         this.fluidTest = p_i49923_3_;
      }

      public boolean test(IFluidState p_222248_1_) {
         return this.fluidTest.test(p_222248_1_);
      }
   }

   public interface IVoxelProvider {
      VoxelShape get(BlockState var1, IBlockReader var2, BlockPos var3, ISelectionContext var4);
   }

   public static enum BlockMode implements RayTraceContext.IVoxelProvider {
      COLLIDER(BlockState::getCollisionShape),
      OUTLINE(BlockState::getShape);

      private final RayTraceContext.IVoxelProvider provider;

      private BlockMode(RayTraceContext.IVoxelProvider p_i49926_3_) {
         this.provider = p_i49926_3_;
      }

      public VoxelShape get(BlockState p_get_1_, IBlockReader p_get_2_, BlockPos p_get_3_, ISelectionContext p_get_4_) {
         return this.provider.get(p_get_1_, p_get_2_, p_get_3_, p_get_4_);
      }
   }
}
