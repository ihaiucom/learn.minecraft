package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.ComparatorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ComparatorBlock extends RedstoneDiodeBlock implements ITileEntityProvider {
   public static final EnumProperty<ComparatorMode> MODE;

   public ComparatorBlock(Block.Properties p_i48424_1_) {
      super(p_i48424_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(POWERED, false)).with(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(BlockState p_196346_1_) {
      return 2;
   }

   protected int getActiveSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, BlockState p_176408_3_) {
      TileEntity tileentity = p_176408_1_.getTileEntity(p_176408_2_);
      return tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
   }

   private int calculateOutput(World p_176460_1_, BlockPos p_176460_2_, BlockState p_176460_3_) {
      return p_176460_3_.get(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.calculateInputStrength(p_176460_1_, p_176460_2_, p_176460_3_) - this.getPowerOnSides(p_176460_1_, p_176460_2_, p_176460_3_), 0) : this.calculateInputStrength(p_176460_1_, p_176460_2_, p_176460_3_);
   }

   protected boolean shouldBePowered(World p_176404_1_, BlockPos p_176404_2_, BlockState p_176404_3_) {
      int i = this.calculateInputStrength(p_176404_1_, p_176404_2_, p_176404_3_);
      if (i == 0) {
         return false;
      } else {
         int j = this.getPowerOnSides(p_176404_1_, p_176404_2_, p_176404_3_);
         if (i > j) {
            return true;
         } else {
            return i == j && p_176404_3_.get(MODE) == ComparatorMode.COMPARE;
         }
      }
   }

   protected int calculateInputStrength(World p_176397_1_, BlockPos p_176397_2_, BlockState p_176397_3_) {
      int i = super.calculateInputStrength(p_176397_1_, p_176397_2_, p_176397_3_);
      Direction direction = (Direction)p_176397_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176397_2_.offset(direction);
      BlockState blockstate = p_176397_1_.getBlockState(blockpos);
      if (blockstate.hasComparatorInputOverride()) {
         i = blockstate.getComparatorInputOverride(p_176397_1_, blockpos);
      } else if (i < 15 && blockstate.isNormalCube(p_176397_1_, blockpos)) {
         blockpos = blockpos.offset(direction);
         blockstate = p_176397_1_.getBlockState(blockpos);
         if (blockstate.hasComparatorInputOverride()) {
            i = blockstate.getComparatorInputOverride(p_176397_1_, blockpos);
         } else if (blockstate.isAir(p_176397_1_, blockpos)) {
            ItemFrameEntity itemframeentity = this.findItemFrame(p_176397_1_, direction, blockpos);
            if (itemframeentity != null) {
               i = itemframeentity.getAnalogOutput();
            }
         }
      }

      return i;
   }

   @Nullable
   private ItemFrameEntity findItemFrame(World p_176461_1_, Direction p_176461_2_, BlockPos p_176461_3_) {
      List<ItemFrameEntity> list = p_176461_1_.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB((double)p_176461_3_.getX(), (double)p_176461_3_.getY(), (double)p_176461_3_.getZ(), (double)(p_176461_3_.getX() + 1), (double)(p_176461_3_.getY() + 1), (double)(p_176461_3_.getZ() + 1)), (p_lambda$findItemFrame$0_1_) -> {
         return p_lambda$findItemFrame$0_1_ != null && p_lambda$findItemFrame$0_1_.getHorizontalFacing() == p_176461_2_;
      });
      return list.size() == 1 ? (ItemFrameEntity)list.get(0) : null;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_4_.abilities.allowEdit) {
         return ActionResultType.PASS;
      } else {
         p_225533_1_ = (BlockState)p_225533_1_.cycle(MODE);
         float f = p_225533_1_.get(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         p_225533_2_.playSound(p_225533_4_, p_225533_3_, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 2);
         this.onStateChange(p_225533_2_, p_225533_3_, p_225533_1_);
         return ActionResultType.SUCCESS;
      }
   }

   protected void updateState(World p_176398_1_, BlockPos p_176398_2_, BlockState p_176398_3_) {
      if (!p_176398_1_.getPendingBlockTicks().isTickPending(p_176398_2_, this)) {
         int i = this.calculateOutput(p_176398_1_, p_176398_2_, p_176398_3_);
         TileEntity tileentity = p_176398_1_.getTileEntity(p_176398_2_);
         int j = tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
         if (i != j || (Boolean)p_176398_3_.get(POWERED) != this.shouldBePowered(p_176398_1_, p_176398_2_, p_176398_3_)) {
            TickPriority tickpriority = this.isFacingTowardsRepeater(p_176398_1_, p_176398_2_, p_176398_3_) ? TickPriority.HIGH : TickPriority.NORMAL;
            p_176398_1_.getPendingBlockTicks().scheduleTick(p_176398_2_, this, 2, tickpriority);
         }
      }

   }

   private void onStateChange(World p_176462_1_, BlockPos p_176462_2_, BlockState p_176462_3_) {
      int i = this.calculateOutput(p_176462_1_, p_176462_2_, p_176462_3_);
      TileEntity tileentity = p_176462_1_.getTileEntity(p_176462_2_);
      int j = 0;
      if (tileentity instanceof ComparatorTileEntity) {
         ComparatorTileEntity comparatortileentity = (ComparatorTileEntity)tileentity;
         j = comparatortileentity.getOutputSignal();
         comparatortileentity.setOutputSignal(i);
      }

      if (j != i || p_176462_3_.get(MODE) == ComparatorMode.COMPARE) {
         boolean flag1 = this.shouldBePowered(p_176462_1_, p_176462_2_, p_176462_3_);
         boolean flag = (Boolean)p_176462_3_.get(POWERED);
         if (flag && !flag1) {
            p_176462_1_.setBlockState(p_176462_2_, (BlockState)p_176462_3_.with(POWERED, false), 2);
         } else if (!flag && flag1) {
            p_176462_1_.setBlockState(p_176462_2_, (BlockState)p_176462_3_.with(POWERED, true), 2);
         }

         this.notifyNeighbors(p_176462_1_, p_176462_2_, p_176462_3_);
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.onStateChange(p_225534_2_, p_225534_3_, p_225534_1_);
   }

   public boolean eventReceived(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.eventReceived(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity tileentity = p_189539_2_.getTileEntity(p_189539_3_);
      return tileentity != null && tileentity.receiveClientEvent(p_189539_4_, p_189539_5_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new ComparatorTileEntity();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, MODE, POWERED);
   }

   public boolean getWeakChanges(BlockState p_getWeakChanges_1_, IWorldReader p_getWeakChanges_2_, BlockPos p_getWeakChanges_3_) {
      return true;
   }

   public void onNeighborChange(BlockState p_onNeighborChange_1_, IWorldReader p_onNeighborChange_2_, BlockPos p_onNeighborChange_3_, BlockPos p_onNeighborChange_4_) {
      if (p_onNeighborChange_3_.getY() == p_onNeighborChange_4_.getY() && p_onNeighborChange_2_ instanceof World && !((World)p_onNeighborChange_2_).isRemote()) {
         p_onNeighborChange_1_.neighborChanged((World)p_onNeighborChange_2_, p_onNeighborChange_3_, p_onNeighborChange_2_.getBlockState(p_onNeighborChange_4_).getBlock(), p_onNeighborChange_4_, false);
      }

   }

   static {
      MODE = BlockStateProperties.COMPARATOR_MODE;
   }
}
