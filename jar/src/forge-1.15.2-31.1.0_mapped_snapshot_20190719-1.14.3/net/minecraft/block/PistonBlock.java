package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

public class PistonBlock extends DirectionalBlock {
   public static final BooleanProperty EXTENDED;
   protected static final VoxelShape PISTON_BASE_EAST_AABB;
   protected static final VoxelShape PISTON_BASE_WEST_AABB;
   protected static final VoxelShape PISTON_BASE_SOUTH_AABB;
   protected static final VoxelShape PISTON_BASE_NORTH_AABB;
   protected static final VoxelShape PISTON_BASE_UP_AABB;
   protected static final VoxelShape PISTON_BASE_DOWN_AABB;
   private final boolean isSticky;

   public PistonBlock(boolean p_i48281_1_, Block.Properties p_i48281_2_) {
      super(p_i48281_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
      this.isSticky = p_i48281_1_;
   }

   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return !(Boolean)p_229869_1_.get(EXTENDED);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if ((Boolean)p_220053_1_.get(EXTENDED)) {
         switch((Direction)p_220053_1_.get(FACING)) {
         case DOWN:
            return PISTON_BASE_DOWN_AABB;
         case UP:
         default:
            return PISTON_BASE_UP_AABB;
         case NORTH:
            return PISTON_BASE_NORTH_AABB;
         case SOUTH:
            return PISTON_BASE_SOUTH_AABB;
         case WEST:
            return PISTON_BASE_WEST_AABB;
         case EAST:
            return PISTON_BASE_EAST_AABB;
         }
      } else {
         return VoxelShapes.fullCube();
      }
   }

   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return false;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isRemote) {
         this.checkForMove(p_180633_1_, p_180633_2_, p_180633_3_);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         this.checkForMove(p_220069_2_, p_220069_3_, p_220069_1_);
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && !p_220082_2_.isRemote && p_220082_2_.getTileEntity(p_220082_3_) == null) {
         this.checkForMove(p_220082_2_, p_220082_3_, p_220082_1_);
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, p_196258_1_.getNearestLookingDirection().getOpposite())).with(EXTENDED, false);
   }

   private void checkForMove(World p_176316_1_, BlockPos p_176316_2_, BlockState p_176316_3_) {
      Direction direction = (Direction)p_176316_3_.get(FACING);
      boolean flag = this.shouldBeExtended(p_176316_1_, p_176316_2_, direction);
      if (flag && !(Boolean)p_176316_3_.get(EXTENDED)) {
         if ((new PistonBlockStructureHelper(p_176316_1_, p_176316_2_, direction, true)).canMove()) {
            p_176316_1_.addBlockEvent(p_176316_2_, this, 0, direction.getIndex());
         }
      } else if (!flag && (Boolean)p_176316_3_.get(EXTENDED)) {
         BlockPos blockpos = p_176316_2_.offset(direction, 2);
         BlockState blockstate = p_176316_1_.getBlockState(blockpos);
         int i = 1;
         if (blockstate.getBlock() == Blocks.MOVING_PISTON && blockstate.get(FACING) == direction) {
            TileEntity tileentity = p_176316_1_.getTileEntity(blockpos);
            if (tileentity instanceof PistonTileEntity) {
               PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
               if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || p_176316_1_.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld)p_176316_1_).isInsideTick())) {
                  i = 2;
               }
            }
         }

         p_176316_1_.addBlockEvent(p_176316_2_, this, i, direction.getIndex());
      }

   }

   private boolean shouldBeExtended(World p_176318_1_, BlockPos p_176318_2_, Direction p_176318_3_) {
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         if (direction != p_176318_3_ && p_176318_1_.isSidePowered(p_176318_2_.offset(direction), direction)) {
            return true;
         }
      }

      if (p_176318_1_.isSidePowered(p_176318_2_, Direction.DOWN)) {
         return true;
      } else {
         BlockPos blockpos = p_176318_2_.up();
         Direction[] var10 = Direction.values();
         var6 = var10.length;

         for(int var11 = 0; var11 < var6; ++var11) {
            Direction direction1 = var10[var11];
            if (direction1 != Direction.DOWN && p_176318_1_.isSidePowered(blockpos.offset(direction1), direction1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean eventReceived(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      Direction direction = (Direction)p_189539_1_.get(FACING);
      if (!p_189539_2_.isRemote) {
         boolean flag = this.shouldBeExtended(p_189539_2_, p_189539_3_, direction);
         if (flag && (p_189539_4_ == 1 || p_189539_4_ == 2)) {
            p_189539_2_.setBlockState(p_189539_3_, (BlockState)p_189539_1_.with(EXTENDED, true), 2);
            return false;
         }

         if (!flag && p_189539_4_ == 0) {
            return false;
         }
      }

      if (p_189539_4_ == 0) {
         if (ForgeEventFactory.onPistonMovePre(p_189539_2_, p_189539_3_, direction, true)) {
            return false;
         }

         if (!this.doMove(p_189539_2_, p_189539_3_, direction, true)) {
            return false;
         }

         p_189539_2_.setBlockState(p_189539_3_, (BlockState)p_189539_1_.with(EXTENDED, true), 67);
         p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, p_189539_2_.rand.nextFloat() * 0.25F + 0.6F);
      } else if (p_189539_4_ == 1 || p_189539_4_ == 2) {
         if (ForgeEventFactory.onPistonMovePre(p_189539_2_, p_189539_3_, direction, false)) {
            return false;
         }

         TileEntity tileentity1 = p_189539_2_.getTileEntity(p_189539_3_.offset(direction));
         if (tileentity1 instanceof PistonTileEntity) {
            ((PistonTileEntity)tileentity1).clearPistonTileEntity();
         }

         p_189539_2_.setBlockState(p_189539_3_, (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(MovingPistonBlock.FACING, direction)).with(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
         p_189539_2_.setTileEntity(p_189539_3_, MovingPistonBlock.createTilePiston((BlockState)this.getDefaultState().with(FACING, Direction.byIndex(p_189539_5_ & 7)), direction, false, true));
         if (this.isSticky) {
            BlockPos blockpos = p_189539_3_.add(direction.getXOffset() * 2, direction.getYOffset() * 2, direction.getZOffset() * 2);
            BlockState blockstate = p_189539_2_.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            boolean flag1 = false;
            if (block == Blocks.MOVING_PISTON) {
               TileEntity tileentity = p_189539_2_.getTileEntity(blockpos);
               if (tileentity instanceof PistonTileEntity) {
                  PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
                  if (pistontileentity.getFacing() == direction && pistontileentity.isExtending()) {
                     pistontileentity.clearPistonTileEntity();
                     flag1 = true;
                  }
               }
            }

            if (!flag1) {
               if (p_189539_4_ == 1 && !blockstate.isAir(p_189539_2_, blockpos) && canPush(blockstate, p_189539_2_, blockpos, direction.getOpposite(), false, direction) && (blockstate.getPushReaction() == PushReaction.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
                  this.doMove(p_189539_2_, p_189539_3_, direction, false);
               } else {
                  p_189539_2_.removeBlock(p_189539_3_.offset(direction), false);
               }
            }
         } else {
            p_189539_2_.removeBlock(p_189539_3_.offset(direction), false);
         }

         p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, p_189539_2_.rand.nextFloat() * 0.15F + 0.6F);
      }

      ForgeEventFactory.onPistonMovePost(p_189539_2_, p_189539_3_, direction, p_189539_4_ == 0);
      return true;
   }

   public static boolean canPush(BlockState p_185646_0_, World p_185646_1_, BlockPos p_185646_2_, Direction p_185646_3_, boolean p_185646_4_, Direction p_185646_5_) {
      Block block = p_185646_0_.getBlock();
      if (block == Blocks.OBSIDIAN) {
         return false;
      } else if (!p_185646_1_.getWorldBorder().contains(p_185646_2_)) {
         return false;
      } else if (p_185646_2_.getY() < 0 || p_185646_3_ == Direction.DOWN && p_185646_2_.getY() == 0) {
         return false;
      } else if (p_185646_2_.getY() > p_185646_1_.getHeight() - 1 || p_185646_3_ == Direction.UP && p_185646_2_.getY() == p_185646_1_.getHeight() - 1) {
         return false;
      } else {
         if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
            if (p_185646_0_.getBlockHardness(p_185646_1_, p_185646_2_) == -1.0F) {
               return false;
            }

            switch(p_185646_0_.getPushReaction()) {
            case BLOCK:
               return false;
            case DESTROY:
               return p_185646_4_;
            case PUSH_ONLY:
               return p_185646_3_ == p_185646_5_;
            }
         } else if ((Boolean)p_185646_0_.get(EXTENDED)) {
            return false;
         }

         return !p_185646_0_.hasTileEntity();
      }
   }

   private boolean doMove(World p_176319_1_, BlockPos p_176319_2_, Direction p_176319_3_, boolean p_176319_4_) {
      BlockPos blockpos = p_176319_2_.offset(p_176319_3_);
      if (!p_176319_4_ && p_176319_1_.getBlockState(blockpos).getBlock() == Blocks.PISTON_HEAD) {
         p_176319_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 20);
      }

      PistonBlockStructureHelper pistonblockstructurehelper = new PistonBlockStructureHelper(p_176319_1_, p_176319_2_, p_176319_3_, p_176319_4_);
      if (!pistonblockstructurehelper.canMove()) {
         return false;
      } else {
         Map<BlockPos, BlockState> map = Maps.newHashMap();
         List<BlockPos> list = pistonblockstructurehelper.getBlocksToMove();
         List<BlockState> list1 = Lists.newArrayList();

         for(int i = 0; i < list.size(); ++i) {
            BlockPos blockpos1 = (BlockPos)list.get(i);
            BlockState blockstate = p_176319_1_.getBlockState(blockpos1);
            list1.add(blockstate);
            map.put(blockpos1, blockstate);
         }

         List<BlockPos> list2 = pistonblockstructurehelper.getBlocksToDestroy();
         int k = list.size() + list2.size();
         BlockState[] ablockstate = new BlockState[k];
         Direction direction = p_176319_4_ ? p_176319_3_ : p_176319_3_.getOpposite();

         int l;
         BlockPos blockpos3;
         BlockState blockstate7;
         for(l = list2.size() - 1; l >= 0; --l) {
            blockpos3 = (BlockPos)list2.get(l);
            blockstate7 = p_176319_1_.getBlockState(blockpos3);
            TileEntity tileentity = blockstate7.hasTileEntity() ? p_176319_1_.getTileEntity(blockpos3) : null;
            spawnDrops(blockstate7, p_176319_1_, blockpos3, tileentity);
            p_176319_1_.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 18);
            --k;
            ablockstate[k] = blockstate7;
         }

         for(l = list.size() - 1; l >= 0; --l) {
            blockpos3 = (BlockPos)list.get(l);
            blockstate7 = p_176319_1_.getBlockState(blockpos3);
            blockpos3 = blockpos3.offset(direction);
            map.remove(blockpos3);
            p_176319_1_.setBlockState(blockpos3, (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, p_176319_3_), 68);
            p_176319_1_.setTileEntity(blockpos3, MovingPistonBlock.createTilePiston((BlockState)list1.get(l), p_176319_3_, p_176319_4_, false));
            --k;
            ablockstate[k] = blockstate7;
         }

         if (p_176319_4_) {
            PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockstate4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, p_176319_3_)).with(PistonHeadBlock.TYPE, pistontype);
            blockstate7 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(MovingPistonBlock.FACING, p_176319_3_)).with(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockpos);
            p_176319_1_.setBlockState(blockpos, blockstate7, 68);
            p_176319_1_.setTileEntity(blockpos, MovingPistonBlock.createTilePiston(blockstate4, p_176319_3_, true, true));
         }

         BlockState blockstate3 = Blocks.AIR.getDefaultState();
         Iterator var25 = map.keySet().iterator();

         while(var25.hasNext()) {
            BlockPos blockpos4 = (BlockPos)var25.next();
            p_176319_1_.setBlockState(blockpos4, blockstate3, 82);
         }

         var25 = map.entrySet().iterator();

         BlockPos blockpos6;
         while(var25.hasNext()) {
            Entry<BlockPos, BlockState> entry = (Entry)var25.next();
            blockpos6 = (BlockPos)entry.getKey();
            BlockState blockstate2 = (BlockState)entry.getValue();
            blockstate2.updateDiagonalNeighbors(p_176319_1_, blockpos6, 2);
            blockstate3.updateNeighbors(p_176319_1_, blockpos6, 2);
            blockstate3.updateDiagonalNeighbors(p_176319_1_, blockpos6, 2);
         }

         int j1;
         for(j1 = list2.size() - 1; j1 >= 0; --j1) {
            blockstate7 = ablockstate[k++];
            blockpos6 = (BlockPos)list2.get(j1);
            blockstate7.updateDiagonalNeighbors(p_176319_1_, blockpos6, 2);
            p_176319_1_.notifyNeighborsOfStateChange(blockpos6, blockstate7.getBlock());
         }

         for(j1 = list.size() - 1; j1 >= 0; --j1) {
            p_176319_1_.notifyNeighborsOfStateChange((BlockPos)list.get(j1), ablockstate[k++].getBlock());
         }

         if (p_176319_4_) {
            p_176319_1_.notifyNeighborsOfStateChange(blockpos, Blocks.PISTON_HEAD);
         }

         return true;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState rotate(BlockState p_rotate_1_, IWorld p_rotate_2_, BlockPos p_rotate_3_, Rotation p_rotate_4_) {
      return (Boolean)p_rotate_1_.get(EXTENDED) ? p_rotate_1_ : super.rotate(p_rotate_1_, p_rotate_2_, p_rotate_3_, p_rotate_4_);
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, EXTENDED);
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return (Boolean)p_220074_1_.get(EXTENDED);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      EXTENDED = BlockStateProperties.EXTENDED;
      PISTON_BASE_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      PISTON_BASE_WEST_AABB = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      PISTON_BASE_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
      PISTON_BASE_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
      PISTON_BASE_UP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
      PISTON_BASE_DOWN_AABB = Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
