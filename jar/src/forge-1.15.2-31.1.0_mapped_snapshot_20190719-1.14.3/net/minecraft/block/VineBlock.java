package net.minecraft.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IShearable;

public class VineBlock extends Block implements IShearable {
   public static final BooleanProperty UP;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP;
   protected static final VoxelShape UP_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape SOUTH_AABB;

   public VineBlock(Block.Properties p_i48303_1_) {
      super(p_i48303_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(UP, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if ((Boolean)p_220053_1_.get(UP)) {
         voxelshape = VoxelShapes.or(voxelshape, UP_AABB);
      }

      if ((Boolean)p_220053_1_.get(NORTH)) {
         voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
      }

      if ((Boolean)p_220053_1_.get(EAST)) {
         voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
      }

      if ((Boolean)p_220053_1_.get(SOUTH)) {
         voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
      }

      if ((Boolean)p_220053_1_.get(WEST)) {
         voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return this.func_196543_i(this.func_196545_h(p_196260_1_, p_196260_2_, p_196260_3_));
   }

   private boolean func_196543_i(BlockState p_196543_1_) {
      return this.func_208496_w(p_196543_1_) > 0;
   }

   private int func_208496_w(BlockState p_208496_1_) {
      int i = 0;
      Iterator var3 = FACING_TO_PROPERTY_MAP.values().iterator();

      while(var3.hasNext()) {
         BooleanProperty booleanproperty = (BooleanProperty)var3.next();
         if ((Boolean)p_208496_1_.get(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean func_196541_a(IBlockReader p_196541_1_, BlockPos p_196541_2_, Direction p_196541_3_) {
      if (p_196541_3_ == Direction.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_196541_2_.offset(p_196541_3_);
         if (canAttachTo(p_196541_1_, blockpos, p_196541_3_)) {
            return true;
         } else if (p_196541_3_.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = (BooleanProperty)FACING_TO_PROPERTY_MAP.get(p_196541_3_);
            BlockState blockstate = p_196541_1_.getBlockState(p_196541_2_.up());
            return blockstate.getBlock() == this && (Boolean)blockstate.get(booleanproperty);
         }
      }
   }

   public static boolean canAttachTo(IBlockReader p_196542_0_, BlockPos p_196542_1_, Direction p_196542_2_) {
      BlockState blockstate = p_196542_0_.getBlockState(p_196542_1_);
      return Block.doesSideFillSquare(blockstate.getCollisionShape(p_196542_0_, p_196542_1_), p_196542_2_.getOpposite());
   }

   private BlockState func_196545_h(BlockState p_196545_1_, IBlockReader p_196545_2_, BlockPos p_196545_3_) {
      BlockPos blockpos = p_196545_3_.up();
      if ((Boolean)p_196545_1_.get(UP)) {
         p_196545_1_ = (BlockState)p_196545_1_.with(UP, canAttachTo(p_196545_2_, blockpos, Direction.DOWN));
      }

      BlockState blockstate = null;
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      while(true) {
         Direction direction;
         BooleanProperty booleanproperty;
         do {
            if (!var6.hasNext()) {
               return p_196545_1_;
            }

            direction = (Direction)var6.next();
            booleanproperty = getPropertyFor(direction);
         } while(!(Boolean)p_196545_1_.get(booleanproperty));

         boolean flag = this.func_196541_a(p_196545_2_, p_196545_3_, direction);
         if (!flag) {
            if (blockstate == null) {
               blockstate = p_196545_2_.getBlockState(blockpos);
            }

            flag = blockstate.getBlock() == this && (Boolean)blockstate.get(booleanproperty);
         }

         p_196545_1_ = (BlockState)p_196545_1_.with(booleanproperty, flag);
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         BlockState blockstate = this.func_196545_h(p_196271_1_, p_196271_4_, p_196271_5_);
         return !this.func_196543_i(blockstate) ? Blocks.AIR.getDefaultState() : blockstate;
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      BlockState blockstate = this.func_196545_h(p_225534_1_, p_225534_2_, p_225534_3_);
      if (blockstate != p_225534_1_) {
         if (this.func_196543_i(blockstate)) {
            p_225534_2_.setBlockState(p_225534_3_, blockstate, 2);
         } else {
            spawnDrops(p_225534_1_, p_225534_2_, p_225534_3_);
            p_225534_2_.removeBlock(p_225534_3_, false);
         }
      } else if (p_225534_2_.rand.nextInt(4) == 0 && p_225534_2_.isAreaLoaded(p_225534_3_, 4)) {
         Direction direction = Direction.random(p_225534_4_);
         BlockPos blockpos = p_225534_3_.up();
         BlockPos blockpos4;
         BlockState blockstate1;
         Direction direction2;
         if (direction.getAxis().isHorizontal() && !(Boolean)p_225534_1_.get(getPropertyFor(direction))) {
            if (this.func_196539_a(p_225534_2_, p_225534_3_)) {
               blockpos4 = p_225534_3_.offset(direction);
               blockstate1 = p_225534_2_.getBlockState(blockpos4);
               if (blockstate1.isAir(p_225534_2_, blockpos4)) {
                  direction2 = direction.rotateY();
                  Direction direction4 = direction.rotateYCCW();
                  boolean flag = (Boolean)p_225534_1_.get(getPropertyFor(direction2));
                  boolean flag1 = (Boolean)p_225534_1_.get(getPropertyFor(direction4));
                  BlockPos blockpos2 = blockpos4.offset(direction2);
                  BlockPos blockpos3 = blockpos4.offset(direction4);
                  if (flag && canAttachTo(p_225534_2_, blockpos2, direction2)) {
                     p_225534_2_.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(getPropertyFor(direction2), true), 2);
                  } else if (flag1 && canAttachTo(p_225534_2_, blockpos3, direction4)) {
                     p_225534_2_.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(getPropertyFor(direction4), true), 2);
                  } else {
                     Direction direction1 = direction.getOpposite();
                     if (flag && p_225534_2_.isAirBlock(blockpos2) && canAttachTo(p_225534_2_, p_225534_3_.offset(direction2), direction1)) {
                        p_225534_2_.setBlockState(blockpos2, (BlockState)this.getDefaultState().with(getPropertyFor(direction1), true), 2);
                     } else if (flag1 && p_225534_2_.isAirBlock(blockpos3) && canAttachTo(p_225534_2_, p_225534_3_.offset(direction4), direction1)) {
                        p_225534_2_.setBlockState(blockpos3, (BlockState)this.getDefaultState().with(getPropertyFor(direction1), true), 2);
                     } else if ((double)p_225534_2_.rand.nextFloat() < 0.05D && canAttachTo(p_225534_2_, blockpos4.up(), Direction.UP)) {
                        p_225534_2_.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(UP, true), 2);
                     }
                  }
               } else if (canAttachTo(p_225534_2_, blockpos4, direction)) {
                  p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(getPropertyFor(direction), true), 2);
               }
            }
         } else {
            if (direction == Direction.UP && p_225534_3_.getY() < 255) {
               if (this.func_196541_a(p_225534_2_, p_225534_3_, direction)) {
                  p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(UP, true), 2);
                  return;
               }

               if (p_225534_2_.isAirBlock(blockpos)) {
                  if (!this.func_196539_a(p_225534_2_, p_225534_3_)) {
                     return;
                  }

                  BlockState blockstate4 = p_225534_1_;
                  Iterator var18 = Direction.Plane.HORIZONTAL.iterator();

                  while(true) {
                     do {
                        if (!var18.hasNext()) {
                           if (this.func_196540_x(blockstate4)) {
                              p_225534_2_.setBlockState(blockpos, blockstate4, 2);
                           }

                           return;
                        }

                        direction2 = (Direction)var18.next();
                     } while(!p_225534_4_.nextBoolean() && canAttachTo(p_225534_2_, blockpos.offset(direction2), Direction.UP));

                     blockstate4 = (BlockState)blockstate4.with(getPropertyFor(direction2), false);
                  }
               }
            }

            if (p_225534_3_.getY() > 0) {
               blockpos4 = p_225534_3_.down();
               blockstate1 = p_225534_2_.getBlockState(blockpos4);
               if (blockstate1.isAir(p_225534_2_, blockpos4) || blockstate1.getBlock() == this) {
                  BlockState blockstate2 = blockstate1.isAir(p_225534_2_, blockpos4) ? this.getDefaultState() : blockstate1;
                  BlockState blockstate3 = this.func_196544_a(p_225534_1_, blockstate2, p_225534_4_);
                  if (blockstate2 != blockstate3 && this.func_196540_x(blockstate3)) {
                     p_225534_2_.setBlockState(blockpos4, blockstate3, 2);
                  }
               }
            }
         }
      }

   }

   private BlockState func_196544_a(BlockState p_196544_1_, BlockState p_196544_2_, Random p_196544_3_) {
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction direction = (Direction)var4.next();
         if (p_196544_3_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            if ((Boolean)p_196544_1_.get(booleanproperty)) {
               p_196544_2_ = (BlockState)p_196544_2_.with(booleanproperty, true);
            }
         }
      }

      return p_196544_2_;
   }

   private boolean func_196540_x(BlockState p_196540_1_) {
      return (Boolean)p_196540_1_.get(NORTH) || (Boolean)p_196540_1_.get(EAST) || (Boolean)p_196540_1_.get(SOUTH) || (Boolean)p_196540_1_.get(WEST);
   }

   private boolean func_196539_a(IBlockReader p_196539_1_, BlockPos p_196539_2_) {
      int i = true;
      Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(p_196539_2_.getX() - 4, p_196539_2_.getY() - 1, p_196539_2_.getZ() - 4, p_196539_2_.getX() + 4, p_196539_2_.getY() + 1, p_196539_2_.getZ() + 4);
      int j = 5;
      Iterator var6 = iterable.iterator();

      while(var6.hasNext()) {
         BlockPos blockpos = (BlockPos)var6.next();
         if (p_196539_1_.getBlockState(blockpos).getBlock() == this) {
            --j;
            if (j <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      BlockState blockstate = p_196253_2_.getWorld().getBlockState(p_196253_2_.getPos());
      if (blockstate.getBlock() == this) {
         return this.func_208496_w(blockstate) < FACING_TO_PROPERTY_MAP.size();
      } else {
         return super.isReplaceable(p_196253_1_, p_196253_2_);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      boolean flag = blockstate.getBlock() == this;
      BlockState blockstate1 = flag ? blockstate : this.getDefaultState();
      Direction[] var5 = p_196258_1_.getNearestLookingDirections();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         if (direction != Direction.DOWN) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            boolean flag1 = flag && (Boolean)blockstate.get(booleanproperty);
            if (!flag1 && this.func_196541_a(p_196258_1_.getWorld(), p_196258_1_.getPos(), direction)) {
               return (BlockState)blockstate1.with(booleanproperty, true);
            }
         }
      }

      return flag ? blockstate1 : null;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(SOUTH))).with(EAST, p_185499_1_.get(WEST))).with(SOUTH, p_185499_1_.get(NORTH))).with(WEST, p_185499_1_.get(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(EAST))).with(EAST, p_185499_1_.get(SOUTH))).with(SOUTH, p_185499_1_.get(WEST))).with(WEST, p_185499_1_.get(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(WEST))).with(EAST, p_185499_1_.get(NORTH))).with(SOUTH, p_185499_1_.get(EAST))).with(WEST, p_185499_1_.get(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)p_185471_1_.with(NORTH, p_185471_1_.get(SOUTH))).with(SOUTH, p_185471_1_.get(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)p_185471_1_.with(EAST, p_185471_1_.get(WEST))).with(WEST, p_185471_1_.get(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   public static BooleanProperty getPropertyFor(Direction p_176267_0_) {
      return (BooleanProperty)FACING_TO_PROPERTY_MAP.get(p_176267_0_);
   }

   public boolean isLadder(BlockState p_isLadder_1_, IWorldReader p_isLadder_2_, BlockPos p_isLadder_3_, LivingEntity p_isLadder_4_) {
      return true;
   }

   static {
      UP = SixWayBlock.UP;
      NORTH = SixWayBlock.NORTH;
      EAST = SixWayBlock.EAST;
      SOUTH = SixWayBlock.SOUTH;
      WEST = SixWayBlock.WEST;
      FACING_TO_PROPERTY_MAP = (Map)SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_.getKey() != Direction.DOWN;
      }).collect(Util.toMapCollector());
      UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
      EAST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
      SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   }
}
