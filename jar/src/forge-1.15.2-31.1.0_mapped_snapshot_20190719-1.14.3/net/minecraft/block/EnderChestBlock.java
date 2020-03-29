package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderChestBlock extends AbstractChestBlock<EnderChestTileEntity> implements IWaterLoggable {
   public static final DirectionProperty FACING;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE;
   public static final TranslationTextComponent field_220115_d;

   protected EnderChestBlock(Block.Properties p_i48403_1_) {
      super(p_i48403_1_, () -> {
         return TileEntityType.ENDER_CHEST;
      });
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
   }

   @OnlyIn(Dist.CLIENT)
   public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> func_225536_a_(BlockState p_225536_1_, World p_225536_2_, BlockPos p_225536_3_, boolean p_225536_4_) {
      return TileEntityMerger.ICallback::func_225537_b_;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState lvt_2_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite())).with(WATERLOGGED, lvt_2_1_.getFluid() == Fluids.WATER);
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      EnderChestInventory lvt_7_1_ = p_225533_4_.getInventoryEnderChest();
      TileEntity lvt_8_1_ = p_225533_2_.getTileEntity(p_225533_3_);
      if (lvt_7_1_ != null && lvt_8_1_ instanceof EnderChestTileEntity) {
         BlockPos lvt_9_1_ = p_225533_3_.up();
         if (p_225533_2_.getBlockState(lvt_9_1_).isNormalCube(p_225533_2_, lvt_9_1_)) {
            return ActionResultType.SUCCESS;
         } else if (p_225533_2_.isRemote) {
            return ActionResultType.SUCCESS;
         } else {
            EnderChestTileEntity lvt_10_1_ = (EnderChestTileEntity)lvt_8_1_;
            lvt_7_1_.setChestTileEntity(lvt_10_1_);
            p_225533_4_.openContainer(new SimpleNamedContainerProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> {
               return ChestContainer.createGeneric9X3(p_226928_1_, p_226928_2_, lvt_7_1_);
            }, field_220115_d));
            p_225533_4_.addStat(Stats.OPEN_ENDERCHEST);
            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new EnderChestTileEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_) {
         int lvt_6_1_ = p_180655_4_.nextInt(2) * 2 - 1;
         int lvt_7_1_ = p_180655_4_.nextInt(2) * 2 - 1;
         double lvt_8_1_ = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)lvt_6_1_;
         double lvt_10_1_ = (double)((float)p_180655_3_.getY() + p_180655_4_.nextFloat());
         double lvt_12_1_ = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)lvt_7_1_;
         double lvt_14_1_ = (double)(p_180655_4_.nextFloat() * (float)lvt_6_1_);
         double lvt_16_1_ = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.125D;
         double lvt_18_1_ = (double)(p_180655_4_.nextFloat() * (float)lvt_7_1_);
         p_180655_2_.addParticle(ParticleTypes.PORTAL, lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_14_1_, lvt_16_1_, lvt_18_1_);
      }

   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      field_220115_d = new TranslationTextComponent("container.enderchest", new Object[0]);
   }
}
