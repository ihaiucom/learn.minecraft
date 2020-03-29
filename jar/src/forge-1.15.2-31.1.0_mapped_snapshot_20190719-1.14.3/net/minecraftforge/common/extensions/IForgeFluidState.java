package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;

public interface IForgeFluidState {
   default IFluidState getFluidState() {
      return (IFluidState)this;
   }

   default boolean isEntityInside(IWorldReader world, BlockPos pos, Entity entity, double yToTest, Tag<Fluid> tag, boolean testingHead) {
      return this.getFluidState().getFluid().isEntityInside(this.getFluidState(), world, pos, entity, yToTest, tag, testingHead);
   }

   default float getExplosionResistance(IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return this.getFluidState().getFluid().getExplosionResistance(this.getFluidState(), world, pos, exploder, explosion);
   }
}
