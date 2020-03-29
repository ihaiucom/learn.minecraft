package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class SweetBerryBushBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty AGE;
   private static final VoxelShape field_220126_b;
   private static final VoxelShape field_220127_c;

   public SweetBerryBushBlock(Block.Properties p_i49971_1_) {
      super(p_i49971_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if ((Integer)p_220053_1_.get(AGE) == 0) {
         return field_220126_b;
      } else {
         return (Integer)p_220053_1_.get(AGE) < 3 ? field_220127_c : super.getShape(p_220053_1_, p_220053_2_, p_220053_3_, p_220053_4_);
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
      int i = (Integer)p_225534_1_.get(AGE);
      if (i < 3 && p_225534_2_.func_226659_b_(p_225534_3_.up(), 0) >= 9 && ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_.nextInt(5) == 0)) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, i + 1), 2);
         ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
      }

   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (p_196262_4_ instanceof LivingEntity && p_196262_4_.getType() != EntityType.FOX && p_196262_4_.getType() != EntityType.field_226289_e_) {
         p_196262_4_.setMotionMultiplier(p_196262_1_, new Vec3d(0.800000011920929D, 0.75D, 0.800000011920929D));
         if (!p_196262_2_.isRemote && (Integer)p_196262_1_.get(AGE) > 0 && (p_196262_4_.lastTickPosX != p_196262_4_.func_226277_ct_() || p_196262_4_.lastTickPosZ != p_196262_4_.func_226281_cx_())) {
            double d0 = Math.abs(p_196262_4_.func_226277_ct_() - p_196262_4_.lastTickPosX);
            double d1 = Math.abs(p_196262_4_.func_226281_cx_() - p_196262_4_.lastTickPosZ);
            if (d0 >= 0.003000000026077032D || d1 >= 0.003000000026077032D) {
               p_196262_4_.attackEntityFrom(DamageSource.SWEET_BERRY_BUSH, 1.0F);
            }
         }
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      int i = (Integer)p_225533_1_.get(AGE);
      boolean flag = i == 3;
      if (!flag && p_225533_4_.getHeldItem(p_225533_5_).getItem() == Items.BONE_MEAL) {
         return ActionResultType.PASS;
      } else if (i > 1) {
         int j = 1 + p_225533_2_.rand.nextInt(2);
         spawnAsEntity(p_225533_2_, p_225533_3_, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
         p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + p_225533_2_.rand.nextFloat() * 0.4F);
         p_225533_2_.setBlockState(p_225533_3_, (BlockState)p_225533_1_.with(AGE, 1), 2);
         return ActionResultType.SUCCESS;
      } else {
         return super.func_225533_a_(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return (Integer)p_176473_3_.get(AGE) < 3;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      int i = Math.min(3, (Integer)p_225535_4_.get(AGE) + 1);
      p_225535_1_.setBlockState(p_225535_3_, (BlockState)p_225535_4_.with(AGE, i), 2);
   }

   static {
      AGE = BlockStateProperties.AGE_0_3;
      field_220126_b = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
      field_220127_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   }
}
