package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(Block.Properties p_i48294_1_) {
      super(p_i48294_1_);
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_2_.getDimension().doesWaterVaporize()) {
         p_220082_2_.setBlockState(p_220082_3_, Blocks.SPONGE.getDefaultState(), 3);
         p_220082_2_.playEvent(2009, p_220082_3_, 0);
         p_220082_2_.playSound((PlayerEntity)null, p_220082_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + p_220082_2_.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      Direction lvt_5_1_ = Direction.random(p_180655_4_);
      if (lvt_5_1_ != Direction.UP) {
         BlockPos lvt_6_1_ = p_180655_3_.offset(lvt_5_1_);
         BlockState lvt_7_1_ = p_180655_2_.getBlockState(lvt_6_1_);
         if (!p_180655_1_.isSolid() || !lvt_7_1_.func_224755_d(p_180655_2_, lvt_6_1_, lvt_5_1_.getOpposite())) {
            double lvt_8_1_ = (double)p_180655_3_.getX();
            double lvt_10_1_ = (double)p_180655_3_.getY();
            double lvt_12_1_ = (double)p_180655_3_.getZ();
            if (lvt_5_1_ == Direction.DOWN) {
               lvt_10_1_ -= 0.05D;
               lvt_8_1_ += p_180655_4_.nextDouble();
               lvt_12_1_ += p_180655_4_.nextDouble();
            } else {
               lvt_10_1_ += p_180655_4_.nextDouble() * 0.8D;
               if (lvt_5_1_.getAxis() == Direction.Axis.X) {
                  lvt_12_1_ += p_180655_4_.nextDouble();
                  if (lvt_5_1_ == Direction.EAST) {
                     ++lvt_8_1_;
                  } else {
                     lvt_8_1_ += 0.05D;
                  }
               } else {
                  lvt_8_1_ += p_180655_4_.nextDouble();
                  if (lvt_5_1_ == Direction.SOUTH) {
                     ++lvt_12_1_;
                  } else {
                     lvt_12_1_ += 0.05D;
                  }
               }
            }

            p_180655_2_.addParticle(ParticleTypes.DRIPPING_WATER, lvt_8_1_, lvt_10_1_, lvt_12_1_, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
