package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockStructureHelper {
   private final World world;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos blockToMove;
   private final Direction moveDirection;
   private final List<BlockPos> toMove = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();
   private final Direction facing;

   public PistonBlockStructureHelper(World p_i45664_1_, BlockPos p_i45664_2_, Direction p_i45664_3_, boolean p_i45664_4_) {
      this.world = p_i45664_1_;
      this.pistonPos = p_i45664_2_;
      this.facing = p_i45664_3_;
      this.extending = p_i45664_4_;
      if (p_i45664_4_) {
         this.moveDirection = p_i45664_3_;
         this.blockToMove = p_i45664_2_.offset(p_i45664_3_);
      } else {
         this.moveDirection = p_i45664_3_.getOpposite();
         this.blockToMove = p_i45664_2_.offset(p_i45664_3_, 2);
      }

   }

   public boolean canMove() {
      this.toMove.clear();
      this.toDestroy.clear();
      BlockState blockstate = this.world.getBlockState(this.blockToMove);
      if (!PistonBlock.canPush(blockstate, this.world, this.blockToMove, this.moveDirection, false, this.facing)) {
         if (this.extending && blockstate.getPushReaction() == PushReaction.DESTROY) {
            this.toDestroy.add(this.blockToMove);
            return true;
         } else {
            return false;
         }
      } else if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
         return false;
      } else {
         for(int i = 0; i < this.toMove.size(); ++i) {
            BlockPos blockpos = (BlockPos)this.toMove.get(i);
            if (this.world.getBlockState(blockpos).isStickyBlock() && !this.addBranchingBlocks(blockpos)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean addBlockLine(BlockPos p_177251_1_, Direction p_177251_2_) {
      BlockState blockstate = this.world.getBlockState(p_177251_1_);
      if (this.world.isAirBlock(p_177251_1_)) {
         return true;
      } else if (!PistonBlock.canPush(blockstate, this.world, p_177251_1_, this.moveDirection, false, p_177251_2_)) {
         return true;
      } else if (p_177251_1_.equals(this.pistonPos)) {
         return true;
      } else if (this.toMove.contains(p_177251_1_)) {
         return true;
      } else {
         int i = 1;
         if (i + this.toMove.size() > 12) {
            return false;
         } else {
            while(blockstate.isStickyBlock()) {
               BlockPos blockpos = p_177251_1_.offset(this.moveDirection.getOpposite(), i);
               BlockState oldState = blockstate;
               blockstate = this.world.getBlockState(blockpos);
               if (blockstate.isAir(this.world, blockpos) || !oldState.canStickTo(blockstate) || !PistonBlock.canPush(blockstate, this.world, blockpos, this.moveDirection, false, this.moveDirection.getOpposite()) || blockpos.equals(this.pistonPos)) {
                  break;
               }

               ++i;
               if (i + this.toMove.size() > 12) {
                  return false;
               }
            }

            int l = 0;

            int j1;
            for(j1 = i - 1; j1 >= 0; --j1) {
               this.toMove.add(p_177251_1_.offset(this.moveDirection.getOpposite(), j1));
               ++l;
            }

            j1 = 1;

            while(true) {
               BlockPos blockpos1 = p_177251_1_.offset(this.moveDirection, j1);
               int j = this.toMove.indexOf(blockpos1);
               if (j > -1) {
                  this.reorderListAtCollision(l, j);

                  for(int k = 0; k <= j + l; ++k) {
                     BlockPos blockpos2 = (BlockPos)this.toMove.get(k);
                     if (this.world.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
                        return false;
                     }
                  }

                  return true;
               }

               blockstate = this.world.getBlockState(blockpos1);
               if (blockstate.isAir(this.world, blockpos1)) {
                  return true;
               }

               if (!PistonBlock.canPush(blockstate, this.world, blockpos1, this.moveDirection, true, this.moveDirection) || blockpos1.equals(this.pistonPos)) {
                  return false;
               }

               if (blockstate.getPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(blockpos1);
                  return true;
               }

               if (this.toMove.size() >= 12) {
                  return false;
               }

               this.toMove.add(blockpos1);
               ++l;
               ++j1;
            }
         }
      }
   }

   private void reorderListAtCollision(int p_177255_1_, int p_177255_2_) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list1 = Lists.newArrayList();
      List<BlockPos> list2 = Lists.newArrayList();
      list.addAll(this.toMove.subList(0, p_177255_2_));
      list1.addAll(this.toMove.subList(this.toMove.size() - p_177255_1_, this.toMove.size()));
      list2.addAll(this.toMove.subList(p_177255_2_, this.toMove.size() - p_177255_1_));
      this.toMove.clear();
      this.toMove.addAll(list);
      this.toMove.addAll(list1);
      this.toMove.addAll(list2);
   }

   private boolean addBranchingBlocks(BlockPos p_177250_1_) {
      BlockState blockstate = this.world.getBlockState(p_177250_1_);
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction direction = var3[var5];
         if (direction.getAxis() != this.moveDirection.getAxis()) {
            BlockPos blockpos = p_177250_1_.offset(direction);
            BlockState blockstate1 = this.world.getBlockState(blockpos);
            if (blockstate1.canStickTo(blockstate) && !this.addBlockLine(blockpos, direction)) {
               return false;
            }
         }
      }

      return true;
   }

   public List<BlockPos> getBlocksToMove() {
      return this.toMove;
   }

   public List<BlockPos> getBlocksToDestroy() {
      return this.toDestroy;
   }
}
