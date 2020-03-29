package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DispenserBlock extends ContainerBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty TRIGGERED;
   private static final Map<Item, IDispenseItemBehavior> DISPENSE_BEHAVIOR_REGISTRY;

   public static void registerDispenseBehavior(IItemProvider p_199774_0_, IDispenseItemBehavior p_199774_1_) {
      DISPENSE_BEHAVIOR_REGISTRY.put(p_199774_0_.asItem(), p_199774_1_);
   }

   protected DispenserBlock(Block.Properties p_i48414_1_) {
      super(p_i48414_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 4;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_7_1_ instanceof DispenserTileEntity) {
            p_225533_4_.openContainer((DispenserTileEntity)lvt_7_1_);
            if (lvt_7_1_ instanceof DropperTileEntity) {
               p_225533_4_.addStat(Stats.INSPECT_DROPPER);
            } else {
               p_225533_4_.addStat(Stats.INSPECT_DISPENSER);
            }
         }

         return ActionResultType.SUCCESS;
      }
   }

   protected void dispense(World p_176439_1_, BlockPos p_176439_2_) {
      ProxyBlockSource lvt_3_1_ = new ProxyBlockSource(p_176439_1_, p_176439_2_);
      DispenserTileEntity lvt_4_1_ = (DispenserTileEntity)lvt_3_1_.getBlockTileEntity();
      int lvt_5_1_ = lvt_4_1_.getDispenseSlot();
      if (lvt_5_1_ < 0) {
         p_176439_1_.playEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack lvt_6_1_ = lvt_4_1_.getStackInSlot(lvt_5_1_);
         IDispenseItemBehavior lvt_7_1_ = this.getBehavior(lvt_6_1_);
         if (lvt_7_1_ != IDispenseItemBehavior.NOOP) {
            lvt_4_1_.setInventorySlotContents(lvt_5_1_, lvt_7_1_.dispense(lvt_3_1_, lvt_6_1_));
         }

      }
   }

   protected IDispenseItemBehavior getBehavior(ItemStack p_149940_1_) {
      return (IDispenseItemBehavior)DISPENSE_BEHAVIOR_REGISTRY.get(p_149940_1_.getItem());
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean lvt_7_1_ = p_220069_2_.isBlockPowered(p_220069_3_) || p_220069_2_.isBlockPowered(p_220069_3_.up());
      boolean lvt_8_1_ = (Boolean)p_220069_1_.get(TRIGGERED);
      if (lvt_7_1_ && !lvt_8_1_) {
         p_220069_2_.getPendingBlockTicks().scheduleTick(p_220069_3_, this, this.tickRate(p_220069_2_));
         p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.with(TRIGGERED, true), 4);
      } else if (!lvt_7_1_ && lvt_8_1_) {
         p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.with(TRIGGERED, false), 4);
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.dispense(p_225534_2_, p_225534_3_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new DispenserTileEntity();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof DispenserTileEntity) {
            ((DispenserTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity lvt_6_1_ = p_196243_2_.getTileEntity(p_196243_3_);
         if (lvt_6_1_ instanceof DispenserTileEntity) {
            InventoryHelper.dropInventoryItems(p_196243_2_, (BlockPos)p_196243_3_, (DispenserTileEntity)lvt_6_1_);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public static IPosition getDispensePosition(IBlockSource p_149939_0_) {
      Direction lvt_1_1_ = (Direction)p_149939_0_.getBlockState().get(FACING);
      double lvt_2_1_ = p_149939_0_.getX() + 0.7D * (double)lvt_1_1_.getXOffset();
      double lvt_4_1_ = p_149939_0_.getY() + 0.7D * (double)lvt_1_1_.getYOffset();
      double lvt_6_1_ = p_149939_0_.getZ() + 0.7D * (double)lvt_1_1_.getZOffset();
      return new Position(lvt_2_1_, lvt_4_1_, lvt_6_1_);
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TRIGGERED);
   }

   static {
      FACING = DirectionalBlock.FACING;
      TRIGGERED = BlockStateProperties.TRIGGERED;
      DISPENSE_BEHAVIOR_REGISTRY = (Map)Util.make(new Object2ObjectOpenHashMap(), (p_212564_0_) -> {
         p_212564_0_.defaultReturnValue(new DefaultDispenseItemBehavior());
      });
   }
}
