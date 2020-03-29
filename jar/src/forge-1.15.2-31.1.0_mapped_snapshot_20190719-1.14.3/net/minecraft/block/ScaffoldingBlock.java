package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ScaffoldingBlock extends Block implements IWaterLoggable {
   private static final VoxelShape field_220121_d;
   private static final VoxelShape field_220122_e;
   private static final VoxelShape field_220123_f = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   private static final VoxelShape field_220124_g = VoxelShapes.fullCube().withOffset(0.0D, -1.0D, 0.0D);
   public static final IntegerProperty field_220118_a;
   public static final BooleanProperty field_220119_b;
   public static final BooleanProperty field_220120_c;

   protected ScaffoldingBlock(Block.Properties p_i49976_1_) {
      super(p_i49976_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(field_220118_a, 7)).with(field_220119_b, false)).with(field_220120_c, false));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_220118_a, field_220119_b, field_220120_c);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (!p_220053_4_.hasItem(p_220053_1_.getBlock().asItem())) {
         return (Boolean)p_220053_1_.get(field_220120_c) ? field_220122_e : field_220121_d;
      } else {
         return VoxelShapes.fullCube();
      }
   }

   public VoxelShape getRaytraceShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return VoxelShapes.fullCube();
   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_2_.getItem().getItem() == this.asItem();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos blockpos = p_196258_1_.getPos();
      World world = p_196258_1_.getWorld();
      int i = func_220117_a(world, blockpos);
      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(field_220119_b, world.getFluidState(blockpos).getFluid() == Fluids.WATER)).with(field_220118_a, i)).with(field_220120_c, this.func_220116_a(world, blockpos, i));
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_2_.isRemote) {
         p_220082_2_.getPendingBlockTicks().scheduleTick(p_220082_3_, this, 1);
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(field_220119_b)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      if (!p_196271_4_.isRemote()) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return p_196271_1_;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      int i = func_220117_a(p_225534_2_, p_225534_3_);
      BlockState blockstate = (BlockState)((BlockState)p_225534_1_.with(field_220118_a, i)).with(field_220120_c, this.func_220116_a(p_225534_2_, p_225534_3_, i));
      if ((Integer)blockstate.get(field_220118_a) == 7) {
         if ((Integer)p_225534_1_.get(field_220118_a) == 7) {
            p_225534_2_.addEntity(new FallingBlockEntity(p_225534_2_, (double)p_225534_3_.getX() + 0.5D, (double)p_225534_3_.getY(), (double)p_225534_3_.getZ() + 0.5D, (BlockState)blockstate.with(field_220119_b, false)));
         } else {
            p_225534_2_.destroyBlock(p_225534_3_, true);
         }
      } else if (p_225534_1_ != blockstate) {
         p_225534_2_.setBlockState(p_225534_3_, blockstate, 3);
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return func_220117_a(p_196260_2_, p_196260_3_) < 7;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      if (p_220071_4_.func_216378_a(VoxelShapes.fullCube(), p_220071_3_, true) && !p_220071_4_.func_225581_b_()) {
         return field_220121_d;
      } else {
         return (Integer)p_220071_1_.get(field_220118_a) != 0 && (Boolean)p_220071_1_.get(field_220120_c) && p_220071_4_.func_216378_a(field_220124_g, p_220071_3_, true) ? field_220123_f : VoxelShapes.empty();
      }
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(field_220119_b) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   private boolean func_220116_a(IBlockReader p_220116_1_, BlockPos p_220116_2_, int p_220116_3_) {
      return p_220116_3_ > 0 && p_220116_1_.getBlockState(p_220116_2_.down()).getBlock() != this;
   }

   public static int func_220117_a(IBlockReader p_220117_0_, BlockPos p_220117_1_) {
      BlockPos.Mutable blockpos$mutable = (new BlockPos.Mutable(p_220117_1_)).move(Direction.DOWN);
      BlockState blockstate = p_220117_0_.getBlockState(blockpos$mutable);
      int i = 7;
      if (blockstate.getBlock() == Blocks.SCAFFOLDING) {
         i = (Integer)blockstate.get(field_220118_a);
      } else if (blockstate.func_224755_d(p_220117_0_, blockpos$mutable, Direction.UP)) {
         return 0;
      }

      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction direction = (Direction)var5.next();
         BlockState blockstate1 = p_220117_0_.getBlockState(blockpos$mutable.setPos((Vec3i)p_220117_1_).move(direction));
         if (blockstate1.getBlock() == Blocks.SCAFFOLDING) {
            i = Math.min(i, (Integer)blockstate1.get(field_220118_a) + 1);
            if (i == 1) {
               break;
            }
         }
      }

      return i;
   }

   public boolean isLadder(BlockState p_isLadder_1_, IWorldReader p_isLadder_2_, BlockPos p_isLadder_3_, LivingEntity p_isLadder_4_) {
      return true;
   }

   static {
      field_220118_a = BlockStateProperties.DISTANCE_0_7;
      field_220119_b = BlockStateProperties.WATERLOGGED;
      field_220120_c = BlockStateProperties.BOTTOM;
      VoxelShape voxelshape = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      VoxelShape voxelshape1 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
      VoxelShape voxelshape2 = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
      VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
      VoxelShape voxelshape4 = Block.makeCuboidShape(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
      field_220121_d = VoxelShapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
      VoxelShape voxelshape5 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
      VoxelShape voxelshape6 = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
      VoxelShape voxelshape7 = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
      VoxelShape voxelshape8 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
      field_220122_e = VoxelShapes.or(field_220123_f, field_220121_d, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
   }
}
