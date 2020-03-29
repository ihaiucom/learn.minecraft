package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DetectorRailBlock extends AbstractRailBlock {
   public static final EnumProperty<RailShape> SHAPE;
   public static final BooleanProperty POWERED;

   public DetectorRailBlock(Block.Properties p_i48417_1_) {
      super(true, p_i48417_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWERED, false)).with(SHAPE, RailShape.NORTH_SOUTH));
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 20;
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote && !(Boolean)p_196262_1_.get(POWERED)) {
         this.updatePoweredState(p_196262_2_, p_196262_3_, p_196262_1_);
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_1_.get(POWERED)) {
         this.updatePoweredState(p_225534_2_, p_225534_3_, p_225534_1_);
      }

   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      if (!(Boolean)p_176211_1_.get(POWERED)) {
         return 0;
      } else {
         return p_176211_4_ == Direction.UP ? 15 : 0;
      }
   }

   private void updatePoweredState(World p_176570_1_, BlockPos p_176570_2_, BlockState p_176570_3_) {
      boolean flag = (Boolean)p_176570_3_.get(POWERED);
      boolean flag1 = false;
      List<AbstractMinecartEntity> list = this.findMinecarts(p_176570_1_, p_176570_2_, AbstractMinecartEntity.class, (Predicate)null);
      if (!list.isEmpty()) {
         flag1 = true;
      }

      BlockState blockstate1;
      if (flag1 && !flag) {
         blockstate1 = (BlockState)p_176570_3_.with(POWERED, true);
         p_176570_1_.setBlockState(p_176570_2_, blockstate1, 3);
         this.updateConnectedRails(p_176570_1_, p_176570_2_, blockstate1, true);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_, this);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_.down(), this);
         p_176570_1_.func_225319_b(p_176570_2_, p_176570_3_, blockstate1);
      }

      if (!flag1 && flag) {
         blockstate1 = (BlockState)p_176570_3_.with(POWERED, false);
         p_176570_1_.setBlockState(p_176570_2_, blockstate1, 3);
         this.updateConnectedRails(p_176570_1_, p_176570_2_, blockstate1, false);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_, this);
         p_176570_1_.notifyNeighborsOfStateChange(p_176570_2_.down(), this);
         p_176570_1_.func_225319_b(p_176570_2_, p_176570_3_, blockstate1);
      }

      if (flag1) {
         p_176570_1_.getPendingBlockTicks().scheduleTick(p_176570_2_, this, this.tickRate(p_176570_1_));
      }

      p_176570_1_.updateComparatorOutputLevel(p_176570_2_, this);
   }

   protected void updateConnectedRails(World p_185592_1_, BlockPos p_185592_2_, BlockState p_185592_3_, boolean p_185592_4_) {
      RailState railstate = new RailState(p_185592_1_, p_185592_2_, p_185592_3_);
      Iterator var6 = railstate.getConnectedRails().iterator();

      while(var6.hasNext()) {
         BlockPos blockpos = (BlockPos)var6.next();
         BlockState blockstate = p_185592_1_.getBlockState(blockpos);
         blockstate.neighborChanged(p_185592_1_, blockpos, blockstate.getBlock(), p_185592_2_, false);
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         super.onBlockAdded(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_4_, p_220082_5_);
         this.updatePoweredState(p_220082_2_, p_220082_3_, p_220082_1_);
      }

   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      if ((Boolean)p_180641_1_.get(POWERED)) {
         List<AbstractMinecartEntity> carts = this.findMinecarts(p_180641_2_, p_180641_3_, AbstractMinecartEntity.class, (Predicate)null);
         if (!carts.isEmpty() && ((AbstractMinecartEntity)carts.get(0)).getComparatorLevel() > -1) {
            return ((AbstractMinecartEntity)carts.get(0)).getComparatorLevel();
         }

         List<MinecartCommandBlockEntity> list = this.findMinecarts(p_180641_2_, p_180641_3_, MinecartCommandBlockEntity.class, (Predicate)null);
         if (!list.isEmpty()) {
            return ((MinecartCommandBlockEntity)list.get(0)).getCommandBlockLogic().getSuccessCount();
         }

         List<AbstractMinecartEntity> list1 = this.findMinecarts(p_180641_2_, p_180641_3_, AbstractMinecartEntity.class, EntityPredicates.HAS_INVENTORY);
         if (!list1.isEmpty()) {
            return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
         }
      }

      return 0;
   }

   protected <T extends AbstractMinecartEntity> List<T> findMinecarts(World p_200878_1_, BlockPos p_200878_2_, Class<T> p_200878_3_, @Nullable Predicate<Entity> p_200878_4_) {
      return p_200878_1_.getEntitiesWithinAABB(p_200878_3_, this.getDectectionBox(p_200878_2_), p_200878_4_);
   }

   private AxisAlignedBB getDectectionBox(BlockPos p_176572_1_) {
      float f = 0.2F;
      return new AxisAlignedBB((double)((float)p_176572_1_.getX() + 0.2F), (double)p_176572_1_.getY(), (double)((float)p_176572_1_.getZ() + 0.2F), (double)((float)(p_176572_1_.getX() + 1) - 0.2F), (double)((float)(p_176572_1_.getY() + 1) - 0.2F), (double)((float)(p_176572_1_.getZ() + 1) - 0.2F));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      RailShape railshape = (RailShape)p_185471_1_.get(SHAPE);
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_185471_1_, p_185471_2_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         }
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(SHAPE, POWERED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
      POWERED = BlockStateProperties.POWERED;
   }
}
