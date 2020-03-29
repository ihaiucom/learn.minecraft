package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooSaplingBlock extends Block implements IGrowable {
   protected static final VoxelShape field_220088_a = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(Block.Properties p_i49997_1_) {
      super(p_i49997_1_);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Vec3d vec3d = p_220053_1_.getOffset(p_220053_2_, p_220053_3_);
      return field_220088_a.withOffset(vec3d.x, vec3d.y, vec3d.z);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_4_.nextInt(3) == 0 && p_225534_2_.isAirBlock(p_225534_3_.up()) && p_225534_2_.func_226659_b_(p_225534_3_.up(), 0) >= 9) {
         this.func_220087_a(p_225534_2_, p_225534_3_);
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() == Blocks.BAMBOO) {
            p_196271_4_.setBlockState(p_196271_5_, Blocks.BAMBOO.getDefaultState(), 2);
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Items.BAMBOO);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.up()).isAir();
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.func_220087_a(p_225535_1_, p_225535_3_);
   }

   public float getPlayerRelativeBlockHardness(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      return p_180647_2_.getHeldItemMainhand().getItem() instanceof SwordItem ? 1.0F : super.getPlayerRelativeBlockHardness(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_);
   }

   protected void func_220087_a(World p_220087_1_, BlockPos p_220087_2_) {
      p_220087_1_.setBlockState(p_220087_2_.up(), (BlockState)Blocks.BAMBOO.getDefaultState().with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL), 3);
   }
}
