package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class CoralFeature extends Feature<NoFeatureConfig> {
   public CoralFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49898_1_) {
      super(p_i49898_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockState lvt_6_1_ = ((Block)BlockTags.CORAL_BLOCKS.getRandomElement(p_212245_3_)).getDefaultState();
      return this.func_204623_a(p_212245_1_, p_212245_3_, p_212245_4_, lvt_6_1_);
   }

   protected abstract boolean func_204623_a(IWorld var1, Random var2, BlockPos var3, BlockState var4);

   protected boolean func_204624_b(IWorld p_204624_1_, Random p_204624_2_, BlockPos p_204624_3_, BlockState p_204624_4_) {
      BlockPos lvt_5_1_ = p_204624_3_.up();
      BlockState lvt_6_1_ = p_204624_1_.getBlockState(p_204624_3_);
      if ((lvt_6_1_.getBlock() == Blocks.WATER || lvt_6_1_.isIn(BlockTags.CORALS)) && p_204624_1_.getBlockState(lvt_5_1_).getBlock() == Blocks.WATER) {
         p_204624_1_.setBlockState(p_204624_3_, p_204624_4_, 3);
         if (p_204624_2_.nextFloat() < 0.25F) {
            p_204624_1_.setBlockState(lvt_5_1_, ((Block)BlockTags.CORALS.getRandomElement(p_204624_2_)).getDefaultState(), 2);
         } else if (p_204624_2_.nextFloat() < 0.05F) {
            p_204624_1_.setBlockState(lvt_5_1_, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, p_204624_2_.nextInt(4) + 1), 2);
         }

         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction lvt_8_1_ = (Direction)var7.next();
            if (p_204624_2_.nextFloat() < 0.2F) {
               BlockPos lvt_9_1_ = p_204624_3_.offset(lvt_8_1_);
               if (p_204624_1_.getBlockState(lvt_9_1_).getBlock() == Blocks.WATER) {
                  BlockState lvt_10_1_ = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(p_204624_2_)).getDefaultState().with(DeadCoralWallFanBlock.FACING, lvt_8_1_);
                  p_204624_1_.setBlockState(lvt_9_1_, lvt_10_1_, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
