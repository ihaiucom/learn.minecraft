package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BarrelBlock extends ContainerBlock {
   public static final DirectionProperty PROPERTY_FACING;
   public static final BooleanProperty PROPERTY_OPEN;

   public BarrelBlock(Block.Properties p_i49996_1_) {
      super(p_i49996_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PROPERTY_FACING, Direction.NORTH)).with(PROPERTY_OPEN, false));
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_7_1_ instanceof BarrelTileEntity) {
            p_225533_4_.openContainer((BarrelTileEntity)lvt_7_1_);
            p_225533_4_.addStat(Stats.OPEN_BARREL);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity lvt_6_1_ = p_196243_2_.getTileEntity(p_196243_3_);
         if (lvt_6_1_ instanceof IInventory) {
            InventoryHelper.dropInventoryItems(p_196243_2_, p_196243_3_, (IInventory)lvt_6_1_);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      TileEntity lvt_5_1_ = p_225534_2_.getTileEntity(p_225534_3_);
      if (lvt_5_1_ instanceof BarrelTileEntity) {
         ((BarrelTileEntity)lvt_5_1_).func_213962_h();
      }

   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BarrelTileEntity();
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof BarrelTileEntity) {
            ((BarrelTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(PROPERTY_FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(PROPERTY_FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(PROPERTY_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(PROPERTY_FACING, PROPERTY_OPEN);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(PROPERTY_FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }

   static {
      PROPERTY_FACING = BlockStateProperties.FACING;
      PROPERTY_OPEN = BlockStateProperties.OPEN;
   }
}
