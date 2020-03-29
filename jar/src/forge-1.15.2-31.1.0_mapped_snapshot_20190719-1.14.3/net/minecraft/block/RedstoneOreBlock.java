package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneOreBlock extends Block {
   public static final BooleanProperty LIT;

   public RedstoneOreBlock(Block.Properties p_i48345_1_) {
      super(p_i48345_1_);
      this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
   }

   public int getLightValue(BlockState p_149750_1_) {
      return (Boolean)p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public void onBlockClicked(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      activate(p_196270_1_, p_196270_2_, p_196270_3_);
      super.onBlockClicked(p_196270_1_, p_196270_2_, p_196270_3_, p_196270_4_);
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      activate(p_176199_1_.getBlockState(p_176199_2_), p_176199_1_, p_176199_2_);
      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         spawnParticles(p_225533_2_, p_225533_3_);
         return ActionResultType.SUCCESS;
      } else {
         activate(p_225533_1_, p_225533_2_, p_225533_3_);
         return ActionResultType.PASS;
      }
   }

   private static void activate(BlockState p_196500_0_, World p_196500_1_, BlockPos p_196500_2_) {
      spawnParticles(p_196500_1_, p_196500_2_);
      if (!(Boolean)p_196500_0_.get(LIT)) {
         p_196500_1_.setBlockState(p_196500_2_, (BlockState)p_196500_0_.with(LIT, true), 3);
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_1_.get(LIT)) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(LIT, false), 3);
      }

   }

   public void spawnAdditionalDrops(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAdditionalDrops(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
   }

   public int getExpDrop(BlockState p_getExpDrop_1_, IWorldReader p_getExpDrop_2_, BlockPos p_getExpDrop_3_, int p_getExpDrop_4_, int p_getExpDrop_5_) {
      return p_getExpDrop_5_ == 0 ? 1 + this.RANDOM.nextInt(5) : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(LIT)) {
         spawnParticles(p_180655_2_, p_180655_3_);
      }

   }

   private static void spawnParticles(World p_180691_0_, BlockPos p_180691_1_) {
      double d0 = 0.5625D;
      Random random = p_180691_0_.rand;
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         BlockPos blockpos = p_180691_1_.offset(direction);
         if (!p_180691_0_.getBlockState(blockpos).isOpaqueCube(p_180691_0_, blockpos)) {
            Direction.Axis direction$axis = direction.getAxis();
            double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getXOffset() : (double)random.nextFloat();
            double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getYOffset() : (double)random.nextFloat();
            double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getZOffset() : (double)random.nextFloat();
            p_180691_0_.addParticle(RedstoneParticleData.REDSTONE_DUST, (double)p_180691_1_.getX() + d1, (double)p_180691_1_.getY() + d2, (double)p_180691_1_.getZ() + d3, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
