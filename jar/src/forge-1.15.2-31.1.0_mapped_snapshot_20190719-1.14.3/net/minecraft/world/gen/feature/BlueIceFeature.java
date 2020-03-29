package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlueIceFeature extends Feature<NoFeatureConfig> {
   public BlueIceFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49912_1_) {
      super(p_i49912_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (p_212245_4_.getY() > p_212245_1_.getSeaLevel() - 1) {
         return false;
      } else if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.WATER && p_212245_1_.getBlockState(p_212245_4_.down()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean lvt_6_1_ = false;
         Direction[] var7 = Direction.values();
         int lvt_8_1_ = var7.length;

         int lvt_9_1_;
         for(lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
            Direction lvt_10_1_ = var7[lvt_9_1_];
            if (lvt_10_1_ != Direction.DOWN && p_212245_1_.getBlockState(p_212245_4_.offset(lvt_10_1_)).getBlock() == Blocks.PACKED_ICE) {
               lvt_6_1_ = true;
               break;
            }
         }

         if (!lvt_6_1_) {
            return false;
         } else {
            p_212245_1_.setBlockState(p_212245_4_, Blocks.BLUE_ICE.getDefaultState(), 2);

            for(int lvt_7_1_ = 0; lvt_7_1_ < 200; ++lvt_7_1_) {
               lvt_8_1_ = p_212245_3_.nextInt(5) - p_212245_3_.nextInt(6);
               lvt_9_1_ = 3;
               if (lvt_8_1_ < 2) {
                  lvt_9_1_ += lvt_8_1_ / 2;
               }

               if (lvt_9_1_ >= 1) {
                  BlockPos lvt_10_2_ = p_212245_4_.add(p_212245_3_.nextInt(lvt_9_1_) - p_212245_3_.nextInt(lvt_9_1_), lvt_8_1_, p_212245_3_.nextInt(lvt_9_1_) - p_212245_3_.nextInt(lvt_9_1_));
                  BlockState lvt_11_1_ = p_212245_1_.getBlockState(lvt_10_2_);
                  Block lvt_12_1_ = lvt_11_1_.getBlock();
                  if (lvt_11_1_.getMaterial() == Material.AIR || lvt_12_1_ == Blocks.WATER || lvt_12_1_ == Blocks.PACKED_ICE || lvt_12_1_ == Blocks.ICE) {
                     Direction[] var13 = Direction.values();
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        Direction lvt_16_1_ = var13[var15];
                        Block lvt_17_1_ = p_212245_1_.getBlockState(lvt_10_2_.offset(lvt_16_1_)).getBlock();
                        if (lvt_17_1_ == Blocks.BLUE_ICE) {
                           p_212245_1_.setBlockState(lvt_10_2_, Blocks.BLUE_ICE.getDefaultState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
