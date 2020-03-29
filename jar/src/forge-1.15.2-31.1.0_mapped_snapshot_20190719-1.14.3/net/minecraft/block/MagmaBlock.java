package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagmaBlock extends Block {
   public MagmaBlock(Block.Properties p_i48366_1_) {
      super(p_i48366_1_);
   }

   public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      if (!p_176199_3_.isImmuneToFire() && p_176199_3_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_176199_3_)) {
         p_176199_3_.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_225543_m_(BlockState p_225543_1_) {
      return true;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      BubbleColumnBlock.placeBubbleColumn(p_225534_2_, p_225534_3_.up(), true);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() == Blocks.WATER) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, this.tickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void func_225542_b_(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      BlockPos lvt_5_1_ = p_225542_3_.up();
      if (p_225542_2_.getFluidState(p_225542_3_).isTagged(FluidTags.WATER)) {
         p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_225542_2_.rand.nextFloat() - p_225542_2_.rand.nextFloat()) * 0.8F);
         p_225542_2_.spawnParticle(ParticleTypes.LARGE_SMOKE, (double)lvt_5_1_.getX() + 0.5D, (double)lvt_5_1_.getY() + 0.25D, (double)lvt_5_1_.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
      }

   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 20;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      p_220082_2_.getPendingBlockTicks().scheduleTick(p_220082_3_, this, this.tickRate(p_220082_2_));
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_4_.isImmuneToFire();
   }

   public boolean needsPostProcessing(BlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
      return true;
   }
}
