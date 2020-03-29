package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(Block.Properties p_i48411_1_) {
      super(p_i48411_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      this.teleport(p_225533_1_, p_225533_2_, p_225533_3_);
      return ActionResultType.SUCCESS;
   }

   public void onBlockClicked(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      this.teleport(p_196270_1_, p_196270_2_, p_196270_3_);
   }

   private void teleport(BlockState p_196443_1_, World p_196443_2_, BlockPos p_196443_3_) {
      for(int lvt_4_1_ = 0; lvt_4_1_ < 1000; ++lvt_4_1_) {
         BlockPos lvt_5_1_ = p_196443_3_.add(p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16), p_196443_2_.rand.nextInt(8) - p_196443_2_.rand.nextInt(8), p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16));
         if (p_196443_2_.getBlockState(lvt_5_1_).isAir()) {
            if (p_196443_2_.isRemote) {
               for(int lvt_6_1_ = 0; lvt_6_1_ < 128; ++lvt_6_1_) {
                  double lvt_7_1_ = p_196443_2_.rand.nextDouble();
                  float lvt_9_1_ = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float lvt_10_1_ = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float lvt_11_1_ = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  double lvt_12_1_ = MathHelper.lerp(lvt_7_1_, (double)lvt_5_1_.getX(), (double)p_196443_3_.getX()) + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  double lvt_14_1_ = MathHelper.lerp(lvt_7_1_, (double)lvt_5_1_.getY(), (double)p_196443_3_.getY()) + p_196443_2_.rand.nextDouble() - 0.5D;
                  double lvt_16_1_ = MathHelper.lerp(lvt_7_1_, (double)lvt_5_1_.getZ(), (double)p_196443_3_.getZ()) + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  p_196443_2_.addParticle(ParticleTypes.PORTAL, lvt_12_1_, lvt_14_1_, lvt_16_1_, (double)lvt_9_1_, (double)lvt_10_1_, (double)lvt_11_1_);
               }
            } else {
               p_196443_2_.setBlockState(lvt_5_1_, p_196443_1_, 2);
               p_196443_2_.removeBlock(p_196443_3_, false);
            }

            return;
         }
      }

   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 5;
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
