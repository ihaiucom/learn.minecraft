package net.minecraftforge.common.extensions;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public interface IForgeFluid {
   default Fluid getFluid() {
      return (Fluid)this;
   }

   default boolean isEntityInside(IFluidState state, IWorldReader world, BlockPos pos, Entity entity, double yToTest, Tag<Fluid> tag, boolean testingHead) {
      return state.isTagged(tag) && yToTest < (double)((float)pos.getY() + state.func_215679_a(world, pos) + 0.11111111F);
   }

   @Nullable
   default Boolean isAABBInsideMaterial(IFluidState state, IWorldReader world, BlockPos pos, AxisAlignedBB boundingBox, Material materialIn) {
      return null;
   }

   @Nullable
   default Boolean isAABBInsideLiquid(IFluidState state, IWorldReader world, BlockPos pos, AxisAlignedBB boundingBox) {
      return null;
   }

   default float getExplosionResistance(IFluidState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return state.getExplosionResistance();
   }

   Set<ResourceLocation> getTags();

   FluidAttributes getAttributes();
}
