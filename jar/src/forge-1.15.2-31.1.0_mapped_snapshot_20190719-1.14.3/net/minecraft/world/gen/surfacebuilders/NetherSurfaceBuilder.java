package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public class NetherSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   private static final BlockState CAVE_AIR;
   private static final BlockState NETHERRACK;
   private static final BlockState GRAVEL;
   private static final BlockState SOUL_SAND;
   protected long field_205552_a;
   protected OctavesNoiseGenerator field_205553_b;

   public NetherSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51308_1_) {
      super(p_i51308_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int lvt_15_1_ = p_205610_11_ + 1;
      int lvt_16_1_ = p_205610_4_ & 15;
      int lvt_17_1_ = p_205610_5_ & 15;
      double lvt_18_1_ = 0.03125D;
      boolean lvt_20_1_ = this.field_205553_b.func_205563_a((double)p_205610_4_ * 0.03125D, (double)p_205610_5_ * 0.03125D, 0.0D) * 75.0D + p_205610_1_.nextDouble() > 0.0D;
      boolean lvt_21_1_ = this.field_205553_b.func_205563_a((double)p_205610_4_ * 0.03125D, 109.0D, (double)p_205610_5_ * 0.03125D) * 75.0D + p_205610_1_.nextDouble() > 0.0D;
      int lvt_22_1_ = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      BlockPos.Mutable lvt_23_1_ = new BlockPos.Mutable();
      int lvt_24_1_ = -1;
      BlockState lvt_25_1_ = NETHERRACK;
      BlockState lvt_26_1_ = NETHERRACK;

      for(int lvt_27_1_ = 127; lvt_27_1_ >= 0; --lvt_27_1_) {
         lvt_23_1_.setPos(lvt_16_1_, lvt_27_1_, lvt_17_1_);
         BlockState lvt_28_1_ = p_205610_2_.getBlockState(lvt_23_1_);
         if (lvt_28_1_.getBlock() != null && !lvt_28_1_.isAir()) {
            if (lvt_28_1_.getBlock() == p_205610_9_.getBlock()) {
               if (lvt_24_1_ == -1) {
                  if (lvt_22_1_ <= 0) {
                     lvt_25_1_ = CAVE_AIR;
                     lvt_26_1_ = NETHERRACK;
                  } else if (lvt_27_1_ >= lvt_15_1_ - 4 && lvt_27_1_ <= lvt_15_1_ + 1) {
                     lvt_25_1_ = NETHERRACK;
                     lvt_26_1_ = NETHERRACK;
                     if (lvt_21_1_) {
                        lvt_25_1_ = GRAVEL;
                        lvt_26_1_ = NETHERRACK;
                     }

                     if (lvt_20_1_) {
                        lvt_25_1_ = SOUL_SAND;
                        lvt_26_1_ = SOUL_SAND;
                     }
                  }

                  if (lvt_27_1_ < lvt_15_1_ && (lvt_25_1_ == null || lvt_25_1_.isAir())) {
                     lvt_25_1_ = p_205610_10_;
                  }

                  lvt_24_1_ = lvt_22_1_;
                  if (lvt_27_1_ >= lvt_15_1_ - 1) {
                     p_205610_2_.setBlockState(lvt_23_1_, lvt_25_1_, false);
                  } else {
                     p_205610_2_.setBlockState(lvt_23_1_, lvt_26_1_, false);
                  }
               } else if (lvt_24_1_ > 0) {
                  --lvt_24_1_;
                  p_205610_2_.setBlockState(lvt_23_1_, lvt_26_1_, false);
               }
            }
         } else {
            lvt_24_1_ = -1;
         }
      }

   }

   public void setSeed(long p_205548_1_) {
      if (this.field_205552_a != p_205548_1_ || this.field_205553_b == null) {
         this.field_205553_b = new OctavesNoiseGenerator(new SharedSeedRandom(p_205548_1_), 3, 0);
      }

      this.field_205552_a = p_205548_1_;
   }

   static {
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
      NETHERRACK = Blocks.NETHERRACK.getDefaultState();
      GRAVEL = Blocks.GRAVEL.getDefaultState();
      SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
   }
}
