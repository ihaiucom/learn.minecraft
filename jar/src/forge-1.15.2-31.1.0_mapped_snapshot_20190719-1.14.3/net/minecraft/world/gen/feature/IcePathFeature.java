package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IcePathFeature extends Feature<FeatureRadiusConfig> {
   private final Block block;

   public IcePathFeature(Function<Dynamic<?>, ? extends FeatureRadiusConfig> p_i49861_1_) {
      super(p_i49861_1_);
      this.block = Blocks.PACKED_ICE;
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, FeatureRadiusConfig p_212245_5_) {
      while(p_212245_1_.isAirBlock(p_212245_4_) && p_212245_4_.getY() > 2) {
         p_212245_4_ = p_212245_4_.down();
      }

      if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         int lvt_6_1_ = p_212245_3_.nextInt(p_212245_5_.radius) + 2;
         int lvt_7_1_ = true;

         for(int lvt_8_1_ = p_212245_4_.getX() - lvt_6_1_; lvt_8_1_ <= p_212245_4_.getX() + lvt_6_1_; ++lvt_8_1_) {
            for(int lvt_9_1_ = p_212245_4_.getZ() - lvt_6_1_; lvt_9_1_ <= p_212245_4_.getZ() + lvt_6_1_; ++lvt_9_1_) {
               int lvt_10_1_ = lvt_8_1_ - p_212245_4_.getX();
               int lvt_11_1_ = lvt_9_1_ - p_212245_4_.getZ();
               if (lvt_10_1_ * lvt_10_1_ + lvt_11_1_ * lvt_11_1_ <= lvt_6_1_ * lvt_6_1_) {
                  for(int lvt_12_1_ = p_212245_4_.getY() - 1; lvt_12_1_ <= p_212245_4_.getY() + 1; ++lvt_12_1_) {
                     BlockPos lvt_13_1_ = new BlockPos(lvt_8_1_, lvt_12_1_, lvt_9_1_);
                     Block lvt_14_1_ = p_212245_1_.getBlockState(lvt_13_1_).getBlock();
                     if (func_227250_b_(lvt_14_1_) || lvt_14_1_ == Blocks.SNOW_BLOCK || lvt_14_1_ == Blocks.ICE) {
                        p_212245_1_.setBlockState(lvt_13_1_, this.block.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
