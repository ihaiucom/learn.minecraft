package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestBlock extends AbstractChestBlock<ChestTileEntity> implements IWaterLoggable {
   public static final DirectionProperty FACING;
   public static final EnumProperty<ChestType> TYPE;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE_NORTH;
   protected static final VoxelShape SHAPE_SOUTH;
   protected static final VoxelShape SHAPE_WEST;
   protected static final VoxelShape SHAPE_EAST;
   protected static final VoxelShape field_196315_B;
   private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>> field_220109_i;
   private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> field_220110_j;

   protected ChestBlock(Block.Properties p_i225757_1_, Supplier<TileEntityType<? extends ChestTileEntity>> p_i225757_2_) {
      super(p_i225757_1_, p_i225757_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
   }

   public static TileEntityMerger.Type func_226919_h_(BlockState p_226919_0_) {
      ChestType lvt_1_1_ = (ChestType)p_226919_0_.get(TYPE);
      if (lvt_1_1_ == ChestType.SINGLE) {
         return TileEntityMerger.Type.SINGLE;
      } else {
         return lvt_1_1_ == ChestType.RIGHT ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
      }
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      if (p_196271_3_.getBlock() == this && p_196271_2_.getAxis().isHorizontal()) {
         ChestType lvt_7_1_ = (ChestType)p_196271_3_.get(TYPE);
         if (p_196271_1_.get(TYPE) == ChestType.SINGLE && lvt_7_1_ != ChestType.SINGLE && p_196271_1_.get(FACING) == p_196271_3_.get(FACING) && getDirectionToAttached(p_196271_3_) == p_196271_2_.getOpposite()) {
            return (BlockState)p_196271_1_.with(TYPE, lvt_7_1_.opposite());
         }
      } else if (getDirectionToAttached(p_196271_1_) == p_196271_2_) {
         return (BlockState)p_196271_1_.with(TYPE, ChestType.SINGLE);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (p_220053_1_.get(TYPE) == ChestType.SINGLE) {
         return field_196315_B;
      } else {
         switch(getDirectionToAttached(p_220053_1_)) {
         case NORTH:
         default:
            return SHAPE_NORTH;
         case SOUTH:
            return SHAPE_SOUTH;
         case WEST:
            return SHAPE_WEST;
         case EAST:
            return SHAPE_EAST;
         }
      }
   }

   public static Direction getDirectionToAttached(BlockState p_196311_0_) {
      Direction lvt_1_1_ = (Direction)p_196311_0_.get(FACING);
      return p_196311_0_.get(TYPE) == ChestType.LEFT ? lvt_1_1_.rotateY() : lvt_1_1_.rotateYCCW();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      ChestType lvt_2_1_ = ChestType.SINGLE;
      Direction lvt_3_1_ = p_196258_1_.getPlacementHorizontalFacing().getOpposite();
      IFluidState lvt_4_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      boolean lvt_5_1_ = p_196258_1_.func_225518_g_();
      Direction lvt_6_1_ = p_196258_1_.getFace();
      if (lvt_6_1_.getAxis().isHorizontal() && lvt_5_1_) {
         Direction lvt_7_1_ = this.getDirectionToAttach(p_196258_1_, lvt_6_1_.getOpposite());
         if (lvt_7_1_ != null && lvt_7_1_.getAxis() != lvt_6_1_.getAxis()) {
            lvt_3_1_ = lvt_7_1_;
            lvt_2_1_ = lvt_7_1_.rotateYCCW() == lvt_6_1_.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (lvt_2_1_ == ChestType.SINGLE && !lvt_5_1_) {
         if (lvt_3_1_ == this.getDirectionToAttach(p_196258_1_, lvt_3_1_.rotateY())) {
            lvt_2_1_ = ChestType.LEFT;
         } else if (lvt_3_1_ == this.getDirectionToAttach(p_196258_1_, lvt_3_1_.rotateYCCW())) {
            lvt_2_1_ = ChestType.RIGHT;
         }
      }

      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, lvt_3_1_)).with(TYPE, lvt_2_1_)).with(WATERLOGGED, lvt_4_1_.getFluid() == Fluids.WATER);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   @Nullable
   private Direction getDirectionToAttach(BlockItemUseContext p_196312_1_, Direction p_196312_2_) {
      BlockState lvt_3_1_ = p_196312_1_.getWorld().getBlockState(p_196312_1_.getPos().offset(p_196312_2_));
      return lvt_3_1_.getBlock() == this && lvt_3_1_.get(TYPE) == ChestType.SINGLE ? (Direction)lvt_3_1_.get(FACING) : null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof ChestTileEntity) {
            ((ChestTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
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

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         INamedContainerProvider lvt_7_1_ = this.getContainer(p_225533_1_, p_225533_2_, p_225533_3_);
         if (lvt_7_1_ != null) {
            p_225533_4_.openContainer(lvt_7_1_);
            p_225533_4_.addStat(this.getOpenStat());
         }

         return ActionResultType.SUCCESS;
      }
   }

   protected Stat<ResourceLocation> getOpenStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   @Nullable
   public static IInventory func_226916_a_(ChestBlock p_226916_0_, BlockState p_226916_1_, World p_226916_2_, BlockPos p_226916_3_, boolean p_226916_4_) {
      return (IInventory)((Optional)p_226916_0_.func_225536_a_(p_226916_1_, p_226916_2_, p_226916_3_, p_226916_4_).apply(field_220109_i)).orElse((Object)null);
   }

   public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> func_225536_a_(BlockState p_225536_1_, World p_225536_2_, BlockPos p_225536_3_, boolean p_225536_4_) {
      BiPredicate lvt_5_2_;
      if (p_225536_4_) {
         lvt_5_2_ = (p_226918_0_, p_226918_1_) -> {
            return false;
         };
      } else {
         lvt_5_2_ = ChestBlock::isBlocked;
      }

      return TileEntityMerger.func_226924_a_((TileEntityType)this.field_226859_a_.get(), ChestBlock::func_226919_h_, ChestBlock::getDirectionToAttached, FACING, p_225536_1_, p_225536_2_, p_225536_3_, lvt_5_2_);
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return (INamedContainerProvider)((Optional)this.func_225536_a_(p_220052_1_, p_220052_2_, p_220052_3_, false).apply(field_220110_j)).orElse((Object)null);
   }

   @OnlyIn(Dist.CLIENT)
   public static TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction> func_226917_a_(final IChestLid p_226917_0_) {
      return new TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction>() {
         public Float2FloatFunction func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
            return (p_226921_2_) -> {
               return Math.max(p_225539_1_.getLidAngle(p_226921_2_), p_225539_2_.getLidAngle(p_226921_2_));
            };
         }

         public Float2FloatFunction func_225538_a_(ChestTileEntity p_225538_1_) {
            return p_225538_1_::getLidAngle;
         }

         public Float2FloatFunction func_225537_b_() {
            IChestLid var10000 = p_226917_0_;
            return var10000::getLidAngle;
         }

         // $FF: synthetic method
         public Object func_225537_b_() {
            return this.func_225537_b_();
         }
      };
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new ChestTileEntity();
   }

   public static boolean isBlocked(IWorld p_220108_0_, BlockPos p_220108_1_) {
      return isBelowSolidBlock(p_220108_0_, p_220108_1_) || isCatSittingOn(p_220108_0_, p_220108_1_);
   }

   private static boolean isBelowSolidBlock(IBlockReader p_176456_0_, BlockPos p_176456_1_) {
      BlockPos lvt_2_1_ = p_176456_1_.up();
      return p_176456_0_.getBlockState(lvt_2_1_).isNormalCube(p_176456_0_, lvt_2_1_);
   }

   private static boolean isCatSittingOn(IWorld p_220107_0_, BlockPos p_220107_1_) {
      List<CatEntity> lvt_2_1_ = p_220107_0_.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB((double)p_220107_1_.getX(), (double)(p_220107_1_.getY() + 1), (double)p_220107_1_.getZ(), (double)(p_220107_1_.getX() + 1), (double)(p_220107_1_.getY() + 2), (double)(p_220107_1_.getZ() + 1)));
      if (!lvt_2_1_.isEmpty()) {
         Iterator var3 = lvt_2_1_.iterator();

         while(var3.hasNext()) {
            CatEntity lvt_4_1_ = (CatEntity)var3.next();
            if (lvt_4_1_.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstoneFromInventory(func_226916_a_(this, p_180641_1_, p_180641_2_, p_180641_3_, false));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE, WATERLOGGED);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      TYPE = BlockStateProperties.CHEST_TYPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
      SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
      SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      SHAPE_EAST = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
      field_196315_B = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      field_220109_i = new TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>>() {
         public Optional<IInventory> func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
            return Optional.of(new DoubleSidedInventory(p_225539_1_, p_225539_2_));
         }

         public Optional<IInventory> func_225538_a_(ChestTileEntity p_225538_1_) {
            return Optional.of(p_225538_1_);
         }

         public Optional<IInventory> func_225537_b_() {
            return Optional.empty();
         }

         // $FF: synthetic method
         public Object func_225537_b_() {
            return this.func_225537_b_();
         }
      };
      field_220110_j = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>() {
         public Optional<INamedContainerProvider> func_225539_a_(final ChestTileEntity p_225539_1_, final ChestTileEntity p_225539_2_) {
            final IInventory lvt_3_1_ = new DoubleSidedInventory(p_225539_1_, p_225539_2_);
            return Optional.of(new INamedContainerProvider() {
               @Nullable
               public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                  if (p_225539_1_.canOpen(p_createMenu_3_) && p_225539_2_.canOpen(p_createMenu_3_)) {
                     p_225539_1_.fillWithLoot(p_createMenu_2_.player);
                     p_225539_2_.fillWithLoot(p_createMenu_2_.player);
                     return ChestContainer.createGeneric9X6(p_createMenu_1_, p_createMenu_2_, lvt_3_1_);
                  } else {
                     return null;
                  }
               }

               public ITextComponent getDisplayName() {
                  if (p_225539_1_.hasCustomName()) {
                     return p_225539_1_.getDisplayName();
                  } else {
                     return (ITextComponent)(p_225539_2_.hasCustomName() ? p_225539_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble", new Object[0]));
                  }
               }
            });
         }

         public Optional<INamedContainerProvider> func_225538_a_(ChestTileEntity p_225538_1_) {
            return Optional.of(p_225538_1_);
         }

         public Optional<INamedContainerProvider> func_225537_b_() {
            return Optional.empty();
         }

         // $FF: synthetic method
         public Object func_225537_b_() {
            return this.func_225537_b_();
         }
      };
   }
}
