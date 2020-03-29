package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class RandomPatchFeature extends Feature<BlockClusterFeatureConfig> {
   public RandomPatchFeature(Function<Dynamic<?>, ? extends BlockClusterFeatureConfig> p_i225816_1_) {
      super(p_i225816_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockClusterFeatureConfig p_212245_5_) {
      BlockState lvt_6_1_ = p_212245_5_.field_227289_a_.func_225574_a_(p_212245_3_, p_212245_4_);
      BlockPos lvt_7_2_;
      if (p_212245_5_.field_227298_k_) {
         lvt_7_2_ = p_212245_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, p_212245_4_);
      } else {
         lvt_7_2_ = p_212245_4_;
      }

      int lvt_8_1_ = 0;
      BlockPos.Mutable lvt_9_1_ = new BlockPos.Mutable();

      for(int lvt_10_1_ = 0; lvt_10_1_ < p_212245_5_.field_227293_f_; ++lvt_10_1_) {
         lvt_9_1_.setPos((Vec3i)lvt_7_2_).move(p_212245_3_.nextInt(p_212245_5_.field_227294_g_ + 1) - p_212245_3_.nextInt(p_212245_5_.field_227294_g_ + 1), p_212245_3_.nextInt(p_212245_5_.field_227295_h_ + 1) - p_212245_3_.nextInt(p_212245_5_.field_227295_h_ + 1), p_212245_3_.nextInt(p_212245_5_.field_227296_i_ + 1) - p_212245_3_.nextInt(p_212245_5_.field_227296_i_ + 1));
         BlockPos lvt_11_1_ = lvt_9_1_.down();
         BlockState lvt_12_1_ = p_212245_1_.getBlockState(lvt_11_1_);
         if ((p_212245_1_.isAirBlock(lvt_9_1_) || p_212245_5_.field_227297_j_ && p_212245_1_.getBlockState(lvt_9_1_).getMaterial().isReplaceable()) && lvt_6_1_.isValidPosition(p_212245_1_, lvt_9_1_) && (p_212245_5_.field_227291_c_.isEmpty() || p_212245_5_.field_227291_c_.contains(lvt_12_1_.getBlock())) && !p_212245_5_.field_227292_d_.contains(lvt_12_1_) && (!p_212245_5_.field_227299_l_ || p_212245_1_.getFluidState(lvt_11_1_.west()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(lvt_11_1_.east()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(lvt_11_1_.north()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(lvt_11_1_.south()).isTagged(FluidTags.WATER))) {
            p_212245_5_.field_227290_b_.func_225567_a_(p_212245_1_, lvt_9_1_, lvt_6_1_, p_212245_3_);
            ++lvt_8_1_;
         }
      }

      return lvt_8_1_ > 0;
   }
}
