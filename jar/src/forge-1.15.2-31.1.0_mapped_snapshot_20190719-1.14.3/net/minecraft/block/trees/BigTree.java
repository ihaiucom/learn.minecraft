package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;

public abstract class BigTree extends Tree {
   public boolean func_225545_a_(IWorld p_225545_1_, ChunkGenerator<?> p_225545_2_, BlockPos p_225545_3_, BlockState p_225545_4_, Random p_225545_5_) {
      for(int lvt_6_1_ = 0; lvt_6_1_ >= -1; --lvt_6_1_) {
         for(int lvt_7_1_ = 0; lvt_7_1_ >= -1; --lvt_7_1_) {
            if (canBigTreeSpawnAt(p_225545_4_, p_225545_1_, p_225545_3_, lvt_6_1_, lvt_7_1_)) {
               return this.func_227017_a_(p_225545_1_, p_225545_2_, p_225545_3_, p_225545_4_, p_225545_5_, lvt_6_1_, lvt_7_1_);
            }
         }
      }

      return super.func_225545_a_(p_225545_1_, p_225545_2_, p_225545_3_, p_225545_4_, p_225545_5_);
   }

   @Nullable
   protected abstract ConfiguredFeature<HugeTreeFeatureConfig, ?> func_225547_a_(Random var1);

   public boolean func_227017_a_(IWorld p_227017_1_, ChunkGenerator<?> p_227017_2_, BlockPos p_227017_3_, BlockState p_227017_4_, Random p_227017_5_, int p_227017_6_, int p_227017_7_) {
      ConfiguredFeature<HugeTreeFeatureConfig, ?> lvt_8_1_ = this.func_225547_a_(p_227017_5_);
      if (lvt_8_1_ == null) {
         return false;
      } else {
         BlockState lvt_9_1_ = Blocks.AIR.getDefaultState();
         p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_, 0, p_227017_7_), lvt_9_1_, 4);
         p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_ + 1, 0, p_227017_7_), lvt_9_1_, 4);
         p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_, 0, p_227017_7_ + 1), lvt_9_1_, 4);
         p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_ + 1, 0, p_227017_7_ + 1), lvt_9_1_, 4);
         if (lvt_8_1_.place(p_227017_1_, p_227017_2_, p_227017_5_, p_227017_3_.add(p_227017_6_, 0, p_227017_7_))) {
            return true;
         } else {
            p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_, 0, p_227017_7_), p_227017_4_, 4);
            p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_ + 1, 0, p_227017_7_), p_227017_4_, 4);
            p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_, 0, p_227017_7_ + 1), p_227017_4_, 4);
            p_227017_1_.setBlockState(p_227017_3_.add(p_227017_6_ + 1, 0, p_227017_7_ + 1), p_227017_4_, 4);
            return false;
         }
      }
   }

   public static boolean canBigTreeSpawnAt(BlockState p_196937_0_, IBlockReader p_196937_1_, BlockPos p_196937_2_, int p_196937_3_, int p_196937_4_) {
      Block lvt_5_1_ = p_196937_0_.getBlock();
      return lvt_5_1_ == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_, 0, p_196937_4_)).getBlock() && lvt_5_1_ == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_ + 1, 0, p_196937_4_)).getBlock() && lvt_5_1_ == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_, 0, p_196937_4_ + 1)).getBlock() && lvt_5_1_ == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_ + 1, 0, p_196937_4_ + 1)).getBlock();
   }
}
