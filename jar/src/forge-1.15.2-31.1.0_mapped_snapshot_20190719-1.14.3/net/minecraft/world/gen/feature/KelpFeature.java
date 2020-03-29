package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpTopBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public KelpFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51487_1_) {
      super(p_i51487_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int lvt_6_1_ = 0;
      int lvt_7_1_ = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX(), p_212245_4_.getZ());
      BlockPos lvt_8_1_ = new BlockPos(p_212245_4_.getX(), lvt_7_1_, p_212245_4_.getZ());
      if (p_212245_1_.getBlockState(lvt_8_1_).getBlock() == Blocks.WATER) {
         BlockState lvt_9_1_ = Blocks.KELP.getDefaultState();
         BlockState lvt_10_1_ = Blocks.KELP_PLANT.getDefaultState();
         int lvt_11_1_ = 1 + p_212245_3_.nextInt(10);

         for(int lvt_12_1_ = 0; lvt_12_1_ <= lvt_11_1_; ++lvt_12_1_) {
            if (p_212245_1_.getBlockState(lvt_8_1_).getBlock() == Blocks.WATER && p_212245_1_.getBlockState(lvt_8_1_.up()).getBlock() == Blocks.WATER && lvt_10_1_.isValidPosition(p_212245_1_, lvt_8_1_)) {
               if (lvt_12_1_ == lvt_11_1_) {
                  p_212245_1_.setBlockState(lvt_8_1_, (BlockState)lvt_9_1_.with(KelpTopBlock.AGE, p_212245_3_.nextInt(4) + 20), 2);
                  ++lvt_6_1_;
               } else {
                  p_212245_1_.setBlockState(lvt_8_1_, lvt_10_1_, 2);
               }
            } else if (lvt_12_1_ > 0) {
               BlockPos lvt_13_1_ = lvt_8_1_.down();
               if (lvt_9_1_.isValidPosition(p_212245_1_, lvt_13_1_) && p_212245_1_.getBlockState(lvt_13_1_.down()).getBlock() != Blocks.KELP) {
                  p_212245_1_.setBlockState(lvt_13_1_, (BlockState)lvt_9_1_.with(KelpTopBlock.AGE, p_212245_3_.nextInt(4) + 20), 2);
                  ++lvt_6_1_;
               }
               break;
            }

            lvt_8_1_ = lvt_8_1_.up();
         }
      }

      return lvt_6_1_ > 0;
   }
}
