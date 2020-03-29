package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IceSpikeFeature extends Feature<NoFeatureConfig> {
   public IceSpikeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51493_1_) {
      super(p_i51493_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      while(p_212245_1_.isAirBlock(p_212245_4_) && p_212245_4_.getY() > 2) {
         p_212245_4_ = p_212245_4_.down();
      }

      if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         p_212245_4_ = p_212245_4_.up(p_212245_3_.nextInt(4));
         int i = p_212245_3_.nextInt(4) + 7;
         int j = i / 4 + p_212245_3_.nextInt(2);
         if (j > 1 && p_212245_3_.nextInt(60) == 0) {
            p_212245_4_ = p_212245_4_.up(10 + p_212245_3_.nextInt(30));
         }

         int k;
         int l;
         for(k = 0; k < i; ++k) {
            float f = (1.0F - (float)k / (float)i) * (float)j;
            l = MathHelper.ceil(f);

            for(int i1 = -l; i1 <= l; ++i1) {
               float f1 = (float)MathHelper.abs(i1) - 0.25F;

               for(int j1 = -l; j1 <= l; ++j1) {
                  float f2 = (float)MathHelper.abs(j1) - 0.25F;
                  if ((i1 == 0 && j1 == 0 || f1 * f1 + f2 * f2 <= f * f) && (i1 != -l && i1 != l && j1 != -l && j1 != l || p_212245_3_.nextFloat() <= 0.75F)) {
                     BlockState blockstate = p_212245_1_.getBlockState(p_212245_4_.add(i1, k, j1));
                     Block block = blockstate.getBlock();
                     if (blockstate.isAir(p_212245_1_, p_212245_4_.add(i1, k, j1)) || func_227250_b_(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        this.setBlockState(p_212245_1_, p_212245_4_.add(i1, k, j1), Blocks.PACKED_ICE.getDefaultState());
                     }

                     if (k != 0 && l > 1) {
                        blockstate = p_212245_1_.getBlockState(p_212245_4_.add(i1, -k, j1));
                        block = blockstate.getBlock();
                        if (blockstate.isAir(p_212245_1_, p_212245_4_.add(i1, -k, j1)) || func_227250_b_(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                           this.setBlockState(p_212245_1_, p_212245_4_.add(i1, -k, j1), Blocks.PACKED_ICE.getDefaultState());
                        }
                     }
                  }
               }
            }
         }

         k = j - 1;
         if (k < 0) {
            k = 0;
         } else if (k > 1) {
            k = 1;
         }

         for(int l1 = -k; l1 <= k; ++l1) {
            for(l = -k; l <= k; ++l) {
               BlockPos blockpos = p_212245_4_.add(l1, -1, l);
               int j2 = 50;
               if (Math.abs(l1) == 1 && Math.abs(l) == 1) {
                  j2 = p_212245_3_.nextInt(5);
               }

               while(blockpos.getY() > 50) {
                  BlockState blockstate1 = p_212245_1_.getBlockState(blockpos);
                  Block block1 = blockstate1.getBlock();
                  if (!blockstate1.isAir(p_212245_1_, blockpos) && !func_227250_b_(block1) && block1 != Blocks.SNOW_BLOCK && block1 != Blocks.ICE && block1 != Blocks.PACKED_ICE) {
                     break;
                  }

                  this.setBlockState(p_212245_1_, blockpos, Blocks.PACKED_ICE.getDefaultState());
                  blockpos = blockpos.down();
                  --j2;
                  if (j2 <= 0) {
                     blockpos = blockpos.down(p_212245_3_.nextInt(5) + 1);
                     j2 = p_212245_3_.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
