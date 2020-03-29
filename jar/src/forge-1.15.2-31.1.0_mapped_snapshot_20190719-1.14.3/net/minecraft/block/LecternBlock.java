package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LecternBlock extends ContainerBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty HAS_BOOK;
   public static final VoxelShape field_220159_d;
   public static final VoxelShape field_220160_e;
   public static final VoxelShape field_220161_f;
   public static final VoxelShape field_220162_g;
   public static final VoxelShape field_220164_h;
   public static final VoxelShape field_220165_i;
   public static final VoxelShape field_220166_j;
   public static final VoxelShape field_220167_k;
   public static final VoxelShape field_220163_w;

   protected LecternBlock(Block.Properties p_i49979_1_) {
      super(p_i49979_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(HAS_BOOK, false));
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return field_220161_f;
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return field_220164_h;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.get(FACING)) {
      case NORTH:
         return field_220166_j;
      case SOUTH:
         return field_220163_w;
      case EAST:
         return field_220167_k;
      case WEST:
         return field_220165_i;
      default:
         return field_220161_f;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED, HAS_BOOK);
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new LecternTileEntity();
   }

   public static boolean tryPlaceBook(World p_220151_0_, BlockPos p_220151_1_, BlockState p_220151_2_, ItemStack p_220151_3_) {
      if (!(Boolean)p_220151_2_.get(HAS_BOOK)) {
         if (!p_220151_0_.isRemote) {
            placeBook(p_220151_0_, p_220151_1_, p_220151_2_, p_220151_3_);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(World p_220148_0_, BlockPos p_220148_1_, BlockState p_220148_2_, ItemStack p_220148_3_) {
      TileEntity lvt_4_1_ = p_220148_0_.getTileEntity(p_220148_1_);
      if (lvt_4_1_ instanceof LecternTileEntity) {
         LecternTileEntity lvt_5_1_ = (LecternTileEntity)lvt_4_1_;
         lvt_5_1_.setBook(p_220148_3_.split(1));
         setHasBook(p_220148_0_, p_220148_1_, p_220148_2_, true);
         p_220148_0_.playSound((PlayerEntity)null, p_220148_1_, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void setHasBook(World p_220155_0_, BlockPos p_220155_1_, BlockState p_220155_2_, boolean p_220155_3_) {
      p_220155_0_.setBlockState(p_220155_1_, (BlockState)((BlockState)p_220155_2_.with(POWERED, false)).with(HAS_BOOK, p_220155_3_), 3);
      notifyNeighbors(p_220155_0_, p_220155_1_, p_220155_2_);
   }

   public static void pulse(World p_220154_0_, BlockPos p_220154_1_, BlockState p_220154_2_) {
      setPowered(p_220154_0_, p_220154_1_, p_220154_2_, true);
      p_220154_0_.getPendingBlockTicks().scheduleTick(p_220154_1_, p_220154_2_.getBlock(), 2);
      p_220154_0_.playEvent(1043, p_220154_1_, 0);
   }

   private static void setPowered(World p_220149_0_, BlockPos p_220149_1_, BlockState p_220149_2_, boolean p_220149_3_) {
      p_220149_0_.setBlockState(p_220149_1_, (BlockState)p_220149_2_.with(POWERED, p_220149_3_), 3);
      notifyNeighbors(p_220149_0_, p_220149_1_, p_220149_2_);
   }

   private static void notifyNeighbors(World p_220153_0_, BlockPos p_220153_1_, BlockState p_220153_2_) {
      p_220153_0_.notifyNeighborsOfStateChange(p_220153_1_.down(), p_220153_2_.getBlock());
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      setPowered(p_225534_2_, p_225534_3_, p_225534_1_, false);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if ((Boolean)p_196243_1_.get(HAS_BOOK)) {
            this.func_220150_d(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         if ((Boolean)p_196243_1_.get(POWERED)) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.down(), this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   private void func_220150_d(BlockState p_220150_1_, World p_220150_2_, BlockPos p_220150_3_) {
      TileEntity lvt_4_1_ = p_220150_2_.getTileEntity(p_220150_3_);
      if (lvt_4_1_ instanceof LecternTileEntity) {
         LecternTileEntity lvt_5_1_ = (LecternTileEntity)lvt_4_1_;
         Direction lvt_6_1_ = (Direction)p_220150_1_.get(FACING);
         ItemStack lvt_7_1_ = lvt_5_1_.getBook().copy();
         float lvt_8_1_ = 0.25F * (float)lvt_6_1_.getXOffset();
         float lvt_9_1_ = 0.25F * (float)lvt_6_1_.getZOffset();
         ItemEntity lvt_10_1_ = new ItemEntity(p_220150_2_, (double)p_220150_3_.getX() + 0.5D + (double)lvt_8_1_, (double)(p_220150_3_.getY() + 1), (double)p_220150_3_.getZ() + 0.5D + (double)lvt_9_1_, lvt_7_1_);
         lvt_10_1_.setDefaultPickupDelay();
         p_220150_2_.addEntity(lvt_10_1_);
         lvt_5_1_.clear();
      }

   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.UP && (Boolean)p_176211_1_.get(POWERED) ? 15 : 0;
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      if ((Boolean)p_180641_1_.get(HAS_BOOK)) {
         TileEntity lvt_4_1_ = p_180641_2_.getTileEntity(p_180641_3_);
         if (lvt_4_1_ instanceof LecternTileEntity) {
            return ((LecternTileEntity)lvt_4_1_).getComparatorSignalLevel();
         }
      }

      return 0;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if ((Boolean)p_225533_1_.get(HAS_BOOK)) {
         if (!p_225533_2_.isRemote) {
            this.func_220152_a(p_225533_2_, p_225533_3_, p_225533_4_);
         }

         return ActionResultType.SUCCESS;
      } else {
         ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
         return !lvt_7_1_.isEmpty() && !lvt_7_1_.getItem().isIn(ItemTags.field_226160_P_) ? ActionResultType.CONSUME : ActionResultType.PASS;
      }
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return !(Boolean)p_220052_1_.get(HAS_BOOK) ? null : super.getContainer(p_220052_1_, p_220052_2_, p_220052_3_);
   }

   private void func_220152_a(World p_220152_1_, BlockPos p_220152_2_, PlayerEntity p_220152_3_) {
      TileEntity lvt_4_1_ = p_220152_1_.getTileEntity(p_220152_2_);
      if (lvt_4_1_ instanceof LecternTileEntity) {
         p_220152_3_.openContainer((LecternTileEntity)lvt_4_1_);
         p_220152_3_.addStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      POWERED = BlockStateProperties.POWERED;
      HAS_BOOK = BlockStateProperties.HAS_BOOK;
      field_220159_d = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
      field_220160_e = Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
      field_220161_f = VoxelShapes.or(field_220159_d, field_220160_e);
      field_220162_g = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
      field_220164_h = VoxelShapes.or(field_220161_f, field_220162_g);
      field_220165_i = VoxelShapes.or(Block.makeCuboidShape(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.makeCuboidShape(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.makeCuboidShape(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), field_220161_f);
      field_220166_j = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.makeCuboidShape(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.makeCuboidShape(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), field_220161_f);
      field_220167_k = VoxelShapes.or(Block.makeCuboidShape(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.makeCuboidShape(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.makeCuboidShape(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), field_220161_f);
      field_220163_w = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.makeCuboidShape(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.makeCuboidShape(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), field_220161_f);
   }
}
