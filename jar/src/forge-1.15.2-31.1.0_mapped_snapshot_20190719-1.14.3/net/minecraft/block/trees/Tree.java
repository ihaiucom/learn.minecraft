package net.minecraft.block.trees;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class Tree {
   @Nullable
   protected abstract ConfiguredFeature<TreeFeatureConfig, ?> func_225546_b_(Random var1, boolean var2);

   public boolean func_225545_a_(IWorld p_225545_1_, ChunkGenerator<?> p_225545_2_, BlockPos p_225545_3_, BlockState p_225545_4_, Random p_225545_5_) {
      ConfiguredFeature<TreeFeatureConfig, ?> lvt_6_1_ = this.func_225546_b_(p_225545_5_, this.func_230140_a_(p_225545_1_, p_225545_3_));
      if (lvt_6_1_ == null) {
         return false;
      } else {
         p_225545_1_.setBlockState(p_225545_3_, Blocks.AIR.getDefaultState(), 4);
         ((TreeFeatureConfig)lvt_6_1_.config).func_227373_a_();
         if (lvt_6_1_.place(p_225545_1_, p_225545_2_, p_225545_5_, p_225545_3_)) {
            return true;
         } else {
            p_225545_1_.setBlockState(p_225545_3_, p_225545_4_, 4);
            return false;
         }
      }
   }

   private boolean func_230140_a_(IWorld p_230140_1_, BlockPos p_230140_2_) {
      Iterator var3 = BlockPos.Mutable.getAllInBoxMutable(p_230140_2_.down().north(2).west(2), p_230140_2_.up().south(2).east(2)).iterator();

      BlockPos lvt_4_1_;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         lvt_4_1_ = (BlockPos)var3.next();
      } while(!p_230140_1_.getBlockState(lvt_4_1_).isIn(BlockTags.field_226149_I_));

      return true;
   }
}
