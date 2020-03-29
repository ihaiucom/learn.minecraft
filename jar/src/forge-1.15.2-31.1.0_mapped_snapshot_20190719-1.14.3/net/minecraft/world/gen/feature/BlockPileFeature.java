package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockPileFeature extends Feature<BlockStateProvidingFeatureConfig> {
   public BlockPileFeature(Function<Dynamic<?>, ? extends BlockStateProvidingFeatureConfig> p_i49914_1_) {
      super(p_i49914_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockStateProvidingFeatureConfig p_212245_5_) {
      if (p_212245_4_.getY() < 5) {
         return false;
      } else {
         int lvt_6_1_ = 2 + p_212245_3_.nextInt(2);
         int lvt_7_1_ = 2 + p_212245_3_.nextInt(2);
         Iterator var8 = BlockPos.getAllInBoxMutable(p_212245_4_.add(-lvt_6_1_, 0, -lvt_7_1_), p_212245_4_.add(lvt_6_1_, 1, lvt_7_1_)).iterator();

         while(var8.hasNext()) {
            BlockPos lvt_9_1_ = (BlockPos)var8.next();
            int lvt_10_1_ = p_212245_4_.getX() - lvt_9_1_.getX();
            int lvt_11_1_ = p_212245_4_.getZ() - lvt_9_1_.getZ();
            if ((float)(lvt_10_1_ * lvt_10_1_ + lvt_11_1_ * lvt_11_1_) <= p_212245_3_.nextFloat() * 10.0F - p_212245_3_.nextFloat() * 6.0F) {
               this.func_227225_a_(p_212245_1_, lvt_9_1_, p_212245_3_, p_212245_5_);
            } else if ((double)p_212245_3_.nextFloat() < 0.031D) {
               this.func_227225_a_(p_212245_1_, lvt_9_1_, p_212245_3_, p_212245_5_);
            }
         }

         return true;
      }
   }

   private boolean canPlaceOn(IWorld p_214621_1_, BlockPos p_214621_2_, Random p_214621_3_) {
      BlockPos lvt_4_1_ = p_214621_2_.down();
      BlockState lvt_5_1_ = p_214621_1_.getBlockState(lvt_4_1_);
      return lvt_5_1_.getBlock() == Blocks.GRASS_PATH ? p_214621_3_.nextBoolean() : lvt_5_1_.func_224755_d(p_214621_1_, lvt_4_1_, Direction.UP);
   }

   private void func_227225_a_(IWorld p_227225_1_, BlockPos p_227225_2_, Random p_227225_3_, BlockStateProvidingFeatureConfig p_227225_4_) {
      if (p_227225_1_.isAirBlock(p_227225_2_) && this.canPlaceOn(p_227225_1_, p_227225_2_, p_227225_3_)) {
         p_227225_1_.setBlockState(p_227225_2_, p_227225_4_.field_227268_a_.func_225574_a_(p_227225_3_, p_227225_2_), 4);
      }

   }
}
