package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT;

   public RedstoneLampBlock(Block.Properties p_i48343_1_) {
      super(p_i48343_1_);
      this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
   }

   public int getLightValue(BlockState p_149750_1_) {
      return (Boolean)p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      super.onBlockAdded(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_4_, p_220082_5_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(LIT, p_196258_1_.getWorld().isBlockPowered(p_196258_1_.getPos()));
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         boolean lvt_7_1_ = (Boolean)p_220069_1_.get(LIT);
         if (lvt_7_1_ != p_220069_2_.isBlockPowered(p_220069_3_)) {
            if (lvt_7_1_) {
               p_220069_2_.getPendingBlockTicks().scheduleTick(p_220069_3_, this, 4);
            } else {
               p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.cycle(LIT), 2);
            }
         }

      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_1_.get(LIT) && !p_225534_2_.isBlockPowered(p_225534_3_)) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.cycle(LIT), 2);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return true;
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
