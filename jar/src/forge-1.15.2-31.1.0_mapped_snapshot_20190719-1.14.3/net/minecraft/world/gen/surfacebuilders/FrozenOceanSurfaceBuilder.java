package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   protected static final BlockState PACKED_ICE;
   protected static final BlockState SNOW_BLOCK;
   private static final BlockState AIR;
   private static final BlockState GRAVEL;
   private static final BlockState ICE;
   private PerlinNoiseGenerator field_205199_h;
   private PerlinNoiseGenerator field_205200_i;
   private long seed;

   public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51313_1_) {
      super(p_i51313_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double lvt_15_1_ = 0.0D;
      double lvt_17_1_ = 0.0D;
      BlockPos.Mutable lvt_19_1_ = new BlockPos.Mutable();
      float lvt_20_1_ = p_205610_3_.func_225486_c(lvt_19_1_.setPos(p_205610_4_, 63, p_205610_5_));
      double lvt_21_1_ = Math.min(Math.abs(p_205610_7_), this.field_205199_h.func_215464_a((double)p_205610_4_ * 0.1D, (double)p_205610_5_ * 0.1D, false) * 15.0D);
      if (lvt_21_1_ > 1.8D) {
         double lvt_23_1_ = 0.09765625D;
         double lvt_25_1_ = Math.abs(this.field_205200_i.func_215464_a((double)p_205610_4_ * 0.09765625D, (double)p_205610_5_ * 0.09765625D, false));
         lvt_15_1_ = lvt_21_1_ * lvt_21_1_ * 1.2D;
         double lvt_27_1_ = Math.ceil(lvt_25_1_ * 40.0D) + 14.0D;
         if (lvt_15_1_ > lvt_27_1_) {
            lvt_15_1_ = lvt_27_1_;
         }

         if (lvt_20_1_ > 0.1F) {
            lvt_15_1_ -= 2.0D;
         }

         if (lvt_15_1_ > 2.0D) {
            lvt_17_1_ = (double)p_205610_11_ - lvt_15_1_ - 7.0D;
            lvt_15_1_ += (double)p_205610_11_;
         } else {
            lvt_15_1_ = 0.0D;
         }
      }

      int lvt_23_2_ = p_205610_4_ & 15;
      int lvt_24_1_ = p_205610_5_ & 15;
      BlockState lvt_25_2_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
      BlockState lvt_26_1_ = p_205610_3_.getSurfaceBuilderConfig().getTop();
      int lvt_27_2_ = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      int lvt_28_1_ = -1;
      int lvt_29_1_ = 0;
      int lvt_30_1_ = 2 + p_205610_1_.nextInt(4);
      int lvt_31_1_ = p_205610_11_ + 18 + p_205610_1_.nextInt(10);

      for(int lvt_32_1_ = Math.max(p_205610_6_, (int)lvt_15_1_ + 1); lvt_32_1_ >= 0; --lvt_32_1_) {
         lvt_19_1_.setPos(lvt_23_2_, lvt_32_1_, lvt_24_1_);
         if (p_205610_2_.getBlockState(lvt_19_1_).isAir() && lvt_32_1_ < (int)lvt_15_1_ && p_205610_1_.nextDouble() > 0.01D) {
            p_205610_2_.setBlockState(lvt_19_1_, PACKED_ICE, false);
         } else if (p_205610_2_.getBlockState(lvt_19_1_).getMaterial() == Material.WATER && lvt_32_1_ > (int)lvt_17_1_ && lvt_32_1_ < p_205610_11_ && lvt_17_1_ != 0.0D && p_205610_1_.nextDouble() > 0.15D) {
            p_205610_2_.setBlockState(lvt_19_1_, PACKED_ICE, false);
         }

         BlockState lvt_33_1_ = p_205610_2_.getBlockState(lvt_19_1_);
         if (lvt_33_1_.isAir()) {
            lvt_28_1_ = -1;
         } else if (lvt_33_1_.getBlock() != p_205610_9_.getBlock()) {
            if (lvt_33_1_.getBlock() == Blocks.PACKED_ICE && lvt_29_1_ <= lvt_30_1_ && lvt_32_1_ > lvt_31_1_) {
               p_205610_2_.setBlockState(lvt_19_1_, SNOW_BLOCK, false);
               ++lvt_29_1_;
            }
         } else if (lvt_28_1_ == -1) {
            if (lvt_27_2_ <= 0) {
               lvt_26_1_ = AIR;
               lvt_25_2_ = p_205610_9_;
            } else if (lvt_32_1_ >= p_205610_11_ - 4 && lvt_32_1_ <= p_205610_11_ + 1) {
               lvt_26_1_ = p_205610_3_.getSurfaceBuilderConfig().getTop();
               lvt_25_2_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
            }

            if (lvt_32_1_ < p_205610_11_ && (lvt_26_1_ == null || lvt_26_1_.isAir())) {
               if (p_205610_3_.func_225486_c(lvt_19_1_.setPos(p_205610_4_, lvt_32_1_, p_205610_5_)) < 0.15F) {
                  lvt_26_1_ = ICE;
               } else {
                  lvt_26_1_ = p_205610_10_;
               }
            }

            lvt_28_1_ = lvt_27_2_;
            if (lvt_32_1_ >= p_205610_11_ - 1) {
               p_205610_2_.setBlockState(lvt_19_1_, lvt_26_1_, false);
            } else if (lvt_32_1_ < p_205610_11_ - 7 - lvt_27_2_) {
               lvt_26_1_ = AIR;
               lvt_25_2_ = p_205610_9_;
               p_205610_2_.setBlockState(lvt_19_1_, GRAVEL, false);
            } else {
               p_205610_2_.setBlockState(lvt_19_1_, lvt_25_2_, false);
            }
         } else if (lvt_28_1_ > 0) {
            --lvt_28_1_;
            p_205610_2_.setBlockState(lvt_19_1_, lvt_25_2_, false);
            if (lvt_28_1_ == 0 && lvt_25_2_.getBlock() == Blocks.SAND && lvt_27_2_ > 1) {
               lvt_28_1_ = p_205610_1_.nextInt(4) + Math.max(0, lvt_32_1_ - 63);
               lvt_25_2_ = lvt_25_2_.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
         }
      }

   }

   public void setSeed(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.field_205199_h == null || this.field_205200_i == null) {
         SharedSeedRandom lvt_3_1_ = new SharedSeedRandom(p_205548_1_);
         this.field_205199_h = new PerlinNoiseGenerator(lvt_3_1_, 3, 0);
         this.field_205200_i = new PerlinNoiseGenerator(lvt_3_1_, 0, 0);
      }

      this.seed = p_205548_1_;
   }

   static {
      PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
      SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
      AIR = Blocks.AIR.getDefaultState();
      GRAVEL = Blocks.GRAVEL.getDefaultState();
      ICE = Blocks.ICE.getDefaultState();
   }
}
