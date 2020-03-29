package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class FlowersFeature<U extends IFeatureConfig> extends Feature<U> {
   public FlowersFeature(Function<Dynamic<?>, ? extends U> p_i49876_1_) {
      super(p_i49876_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, U p_212245_5_) {
      BlockState blockstate = this.func_225562_b_(p_212245_3_, p_212245_4_, p_212245_5_);
      int i = 0;

      for(int j = 0; j < this.func_225560_a_(p_212245_5_); ++j) {
         BlockPos blockpos = this.func_225561_a_(p_212245_3_, p_212245_4_, p_212245_5_);
         if (p_212245_1_.isAirBlock(blockpos) && blockpos.getY() < p_212245_1_.getMaxHeight() - 1 && blockstate.isValidPosition(p_212245_1_, blockpos) && this.func_225559_a_(p_212245_1_, blockpos, p_212245_5_)) {
            p_212245_1_.setBlockState(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }

   public abstract boolean func_225559_a_(IWorld var1, BlockPos var2, U var3);

   public abstract int func_225560_a_(U var1);

   public abstract BlockPos func_225561_a_(Random var1, BlockPos var2, U var3);

   public abstract BlockState func_225562_b_(Random var1, BlockPos var2, U var3);
}
