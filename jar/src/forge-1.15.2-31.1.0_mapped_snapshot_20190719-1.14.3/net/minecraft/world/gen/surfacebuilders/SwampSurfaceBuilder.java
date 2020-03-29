package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class SwampSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public SwampSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51304_1_) {
      super(p_i51304_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double lvt_15_1_ = Biome.INFO_NOISE.func_215464_a((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D, false);
      if (lvt_15_1_ > 0.0D) {
         int lvt_17_1_ = p_205610_4_ & 15;
         int lvt_18_1_ = p_205610_5_ & 15;
         BlockPos.Mutable lvt_19_1_ = new BlockPos.Mutable();

         for(int lvt_20_1_ = p_205610_6_; lvt_20_1_ >= 0; --lvt_20_1_) {
            lvt_19_1_.setPos(lvt_17_1_, lvt_20_1_, lvt_18_1_);
            if (!p_205610_2_.getBlockState(lvt_19_1_).isAir()) {
               if (lvt_20_1_ == 62 && p_205610_2_.getBlockState(lvt_19_1_).getBlock() != p_205610_10_.getBlock()) {
                  p_205610_2_.setBlockState(lvt_19_1_, p_205610_10_, false);
               }
               break;
            }
         }
      }

      SurfaceBuilder.DEFAULT.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, p_205610_14_);
   }
}
