package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SpringFeature extends Feature<LiquidsConfig> {
   public SpringFeature(Function<Dynamic<?>, ? extends LiquidsConfig> p_i51430_1_) {
      super(p_i51430_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, LiquidsConfig p_212245_5_) {
      if (!p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.up()).getBlock())) {
         return false;
      } else if (p_212245_5_.field_227363_b_ && !p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.down()).getBlock())) {
         return false;
      } else {
         BlockState blockstate = p_212245_1_.getBlockState(p_212245_4_);
         if (!blockstate.isAir(p_212245_1_, p_212245_4_) && !p_212245_5_.field_227366_f_.contains(blockstate.getBlock())) {
            return false;
         } else {
            int i = 0;
            int j = 0;
            if (p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.west()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.east()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.north()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.south()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.field_227366_f_.contains(p_212245_1_.getBlockState(p_212245_4_.down()).getBlock())) {
               ++j;
            }

            int k = 0;
            if (p_212245_1_.isAirBlock(p_212245_4_.west())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.east())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.north())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.south())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.down())) {
               ++k;
            }

            if (j == p_212245_5_.field_227364_c_ && k == p_212245_5_.field_227365_d_) {
               p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.state.getBlockState(), 2);
               p_212245_1_.getPendingFluidTicks().scheduleTick(p_212245_4_, p_212245_5_.state.getFluid(), 0);
               ++i;
            }

            return i > 0;
         }
      }
   }
}
