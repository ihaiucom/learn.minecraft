package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class RedstoneDiodeBlock extends HorizontalBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED;

   protected RedstoneDiodeBlock(Block.Properties p_i48416_1_) {
      super(p_i48416_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return func_220064_c(p_196260_2_, p_196260_3_.down());
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!this.isLocked(p_225534_2_, p_225534_3_, p_225534_1_)) {
         boolean flag = (Boolean)p_225534_1_.get(POWERED);
         boolean flag1 = this.shouldBePowered(p_225534_2_, p_225534_3_, p_225534_1_);
         if (flag && !flag1) {
            p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(POWERED, false), 2);
         } else if (!flag) {
            p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(POWERED, true), 2);
            if (!flag1) {
               p_225534_2_.getPendingBlockTicks().scheduleTick(p_225534_3_, this, this.getDelay(p_225534_1_), TickPriority.VERY_HIGH);
            }
         }
      }

   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      if (!(Boolean)p_180656_1_.get(POWERED)) {
         return 0;
      } else {
         return p_180656_1_.get(HORIZONTAL_FACING) == p_180656_4_ ? this.getActiveSignal(p_180656_2_, p_180656_3_, p_180656_1_) : 0;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_1_.isValidPosition(p_220069_2_, p_220069_3_)) {
         this.updateState(p_220069_2_, p_220069_3_, p_220069_1_);
      } else {
         TileEntity tileentity = p_220069_1_.hasTileEntity() ? p_220069_2_.getTileEntity(p_220069_3_) : null;
         spawnDrops(p_220069_1_, p_220069_2_, p_220069_3_, tileentity);
         p_220069_2_.removeBlock(p_220069_3_, false);
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction direction = var8[var10];
            p_220069_2_.notifyNeighborsOfStateChange(p_220069_3_.offset(direction), this);
         }
      }

   }

   protected void updateState(World p_176398_1_, BlockPos p_176398_2_, BlockState p_176398_3_) {
      if (!this.isLocked(p_176398_1_, p_176398_2_, p_176398_3_)) {
         boolean flag = (Boolean)p_176398_3_.get(POWERED);
         boolean flag1 = this.shouldBePowered(p_176398_1_, p_176398_2_, p_176398_3_);
         if (flag != flag1 && !p_176398_1_.getPendingBlockTicks().isTickPending(p_176398_2_, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.isFacingTowardsRepeater(p_176398_1_, p_176398_2_, p_176398_3_)) {
               tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
               tickpriority = TickPriority.VERY_HIGH;
            }

            p_176398_1_.getPendingBlockTicks().scheduleTick(p_176398_2_, this, this.getDelay(p_176398_3_), tickpriority);
         }
      }

   }

   public boolean isLocked(IWorldReader p_176405_1_, BlockPos p_176405_2_, BlockState p_176405_3_) {
      return false;
   }

   protected boolean shouldBePowered(World p_176404_1_, BlockPos p_176404_2_, BlockState p_176404_3_) {
      return this.calculateInputStrength(p_176404_1_, p_176404_2_, p_176404_3_) > 0;
   }

   protected int calculateInputStrength(World p_176397_1_, BlockPos p_176397_2_, BlockState p_176397_3_) {
      Direction direction = (Direction)p_176397_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176397_2_.offset(direction);
      int i = p_176397_1_.getRedstonePower(blockpos, direction);
      if (i >= 15) {
         return i;
      } else {
         BlockState blockstate = p_176397_1_.getBlockState(blockpos);
         return Math.max(i, blockstate.getBlock() == Blocks.REDSTONE_WIRE ? (Integer)blockstate.get(RedstoneWireBlock.POWER) : 0);
      }
   }

   protected int getPowerOnSides(IWorldReader p_176407_1_, BlockPos p_176407_2_, BlockState p_176407_3_) {
      Direction direction = (Direction)p_176407_3_.get(HORIZONTAL_FACING);
      Direction direction1 = direction.rotateY();
      Direction direction2 = direction.rotateYCCW();
      return Math.max(this.getPowerOnSide(p_176407_1_, p_176407_2_.offset(direction1), direction1), this.getPowerOnSide(p_176407_1_, p_176407_2_.offset(direction2), direction2));
   }

   protected int getPowerOnSide(IWorldReader p_176401_1_, BlockPos p_176401_2_, Direction p_176401_3_) {
      BlockState blockstate = p_176401_1_.getBlockState(p_176401_2_);
      Block block = blockstate.getBlock();
      if (this.isAlternateInput(blockstate)) {
         if (block == Blocks.REDSTONE_BLOCK) {
            return 15;
         } else {
            return block == Blocks.REDSTONE_WIRE ? (Integer)blockstate.get(RedstoneWireBlock.POWER) : p_176401_1_.getStrongPower(p_176401_2_, p_176401_3_);
         }
      } else {
         return 0;
      }
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (this.shouldBePowered(p_180633_1_, p_180633_2_, p_180633_3_)) {
         p_180633_1_.getPendingBlockTicks().scheduleTick(p_180633_2_, this, 1);
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      this.notifyNeighbors(p_220082_2_, p_220082_3_, p_220082_1_);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         this.notifyNeighbors(p_196243_2_, p_196243_3_, p_196243_1_);
      }

   }

   protected void notifyNeighbors(World p_176400_1_, BlockPos p_176400_2_, BlockState p_176400_3_) {
      Direction direction = (Direction)p_176400_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176400_2_.offset(direction.getOpposite());
      if (!ForgeEventFactory.onNeighborNotify(p_176400_1_, p_176400_2_, p_176400_1_.getBlockState(p_176400_2_), EnumSet.of(direction.getOpposite()), false).isCanceled()) {
         p_176400_1_.neighborChanged(blockpos, this, p_176400_2_);
         p_176400_1_.notifyNeighborsOfStateExcept(blockpos, this, direction);
      }
   }

   protected boolean isAlternateInput(BlockState p_185545_1_) {
      return p_185545_1_.canProvidePower();
   }

   protected int getActiveSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, BlockState p_176408_3_) {
      return 15;
   }

   public static boolean isDiode(BlockState p_185546_0_) {
      return p_185546_0_.getBlock() instanceof RedstoneDiodeBlock;
   }

   public boolean isFacingTowardsRepeater(IBlockReader p_176402_1_, BlockPos p_176402_2_, BlockState p_176402_3_) {
      Direction direction = ((Direction)p_176402_3_.get(HORIZONTAL_FACING)).getOpposite();
      BlockState blockstate = p_176402_1_.getBlockState(p_176402_2_.offset(direction));
      return isDiode(blockstate) && blockstate.get(HORIZONTAL_FACING) != direction;
   }

   protected abstract int getDelay(BlockState var1);

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
