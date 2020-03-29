package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class ChorusFlowerBlock extends Block {
   public static final IntegerProperty AGE;
   private final ChorusPlantBlock field_196405_b;

   protected ChorusFlowerBlock(ChorusPlantBlock p_i48429_1_, Block.Properties p_i48429_2_) {
      super(p_i48429_2_);
      this.field_196405_b = p_i48429_1_;
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      } else {
         BlockPos blockpos = p_225534_3_.up();
         if (p_225534_2_.isAirBlock(blockpos) && blockpos.getY() < p_225534_2_.getDimension().getHeight()) {
            int i = (Integer)p_225534_1_.get(AGE);
            if (i < 5 && ForgeHooks.onCropsGrowPre(p_225534_2_, blockpos, p_225534_1_, true)) {
               boolean flag = false;
               boolean flag1 = false;
               BlockState blockstate = p_225534_2_.getBlockState(p_225534_3_.down());
               Block block = blockstate.getBlock();
               int l;
               if (block == Blocks.END_STONE) {
                  flag = true;
               } else if (block != this.field_196405_b) {
                  if (blockstate.isAir(p_225534_2_, p_225534_3_.down())) {
                     flag = true;
                  }
               } else {
                  l = 1;

                  for(int k = 0; k < 4; ++k) {
                     Block block1 = p_225534_2_.getBlockState(p_225534_3_.down(l + 1)).getBlock();
                     if (block1 != this.field_196405_b) {
                        if (block1 == Blocks.END_STONE) {
                           flag1 = true;
                        }
                        break;
                     }

                     ++l;
                  }

                  if (l < 2 || l <= p_225534_4_.nextInt(flag1 ? 5 : 4)) {
                     flag = true;
                  }
               }

               if (flag && areAllNeighborsEmpty(p_225534_2_, blockpos, (Direction)null) && p_225534_2_.isAirBlock(p_225534_3_.up(2))) {
                  p_225534_2_.setBlockState(p_225534_3_, this.field_196405_b.makeConnections(p_225534_2_, p_225534_3_), 2);
                  this.placeGrownFlower(p_225534_2_, blockpos, i);
               } else if (i >= 4) {
                  this.placeDeadFlower(p_225534_2_, p_225534_3_);
               } else {
                  l = p_225534_4_.nextInt(4);
                  if (flag1) {
                     ++l;
                  }

                  boolean flag2 = false;

                  for(int i1 = 0; i1 < l; ++i1) {
                     Direction direction = Direction.Plane.HORIZONTAL.random(p_225534_4_);
                     BlockPos blockpos1 = p_225534_3_.offset(direction);
                     if (p_225534_2_.isAirBlock(blockpos1) && p_225534_2_.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(p_225534_2_, blockpos1, direction.getOpposite())) {
                        this.placeGrownFlower(p_225534_2_, blockpos1, i + 1);
                        flag2 = true;
                     }
                  }

                  if (flag2) {
                     p_225534_2_.setBlockState(p_225534_3_, this.field_196405_b.makeConnections(p_225534_2_, p_225534_3_), 2);
                  } else {
                     this.placeDeadFlower(p_225534_2_, p_225534_3_);
                  }
               }

               ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
            }
         }
      }

   }

   private void placeGrownFlower(World p_185602_1_, BlockPos p_185602_2_, int p_185602_3_) {
      p_185602_1_.setBlockState(p_185602_2_, (BlockState)this.getDefaultState().with(AGE, p_185602_3_), 2);
      p_185602_1_.playEvent(1033, p_185602_2_, 0);
   }

   private void placeDeadFlower(World p_185605_1_, BlockPos p_185605_2_) {
      p_185605_1_.setBlockState(p_185605_2_, (BlockState)this.getDefaultState().with(AGE, 5), 2);
      p_185605_1_.playEvent(1034, p_185605_2_, 0);
   }

   private static boolean areAllNeighborsEmpty(IWorldReader p_185604_0_, BlockPos p_185604_1_, @Nullable Direction p_185604_2_) {
      Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

      Direction direction;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         direction = (Direction)var3.next();
      } while(direction == p_185604_2_ || p_185604_0_.isAirBlock(p_185604_1_.offset(direction)));

      return false;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != Direction.UP && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      Block block = blockstate.getBlock();
      if (block != this.field_196405_b && block != Blocks.END_STONE) {
         if (!blockstate.isAir(p_196260_2_, p_196260_3_.down())) {
            return false;
         } else {
            boolean flag = false;
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction direction = (Direction)var7.next();
               BlockState blockstate1 = p_196260_2_.getBlockState(p_196260_3_.offset(direction));
               if (blockstate1.getBlock() == this.field_196405_b) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (!blockstate1.isAir(p_196260_2_, p_196260_3_.offset(direction))) {
                  return false;
               }
            }

            return flag;
         }
      } else {
         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public static void generatePlant(IWorld p_185603_0_, BlockPos p_185603_1_, Random p_185603_2_, int p_185603_3_) {
      p_185603_0_.setBlockState(p_185603_1_, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).makeConnections(p_185603_0_, p_185603_1_), 2);
      growTreeRecursive(p_185603_0_, p_185603_1_, p_185603_2_, p_185603_1_, p_185603_3_, 0);
   }

   private static void growTreeRecursive(IWorld p_185601_0_, BlockPos p_185601_1_, Random p_185601_2_, BlockPos p_185601_3_, int p_185601_4_, int p_185601_5_) {
      ChorusPlantBlock chorusplantblock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int i = p_185601_2_.nextInt(4) + 1;
      if (p_185601_5_ == 0) {
         ++i;
      }

      for(int j = 0; j < i; ++j) {
         BlockPos blockpos = p_185601_1_.up(j + 1);
         if (!areAllNeighborsEmpty(p_185601_0_, blockpos, (Direction)null)) {
            return;
         }

         p_185601_0_.setBlockState(blockpos, chorusplantblock.makeConnections(p_185601_0_, blockpos), 2);
         p_185601_0_.setBlockState(blockpos.down(), chorusplantblock.makeConnections(p_185601_0_, blockpos.down()), 2);
      }

      boolean flag = false;
      if (p_185601_5_ < 4) {
         int l = p_185601_2_.nextInt(4);
         if (p_185601_5_ == 0) {
            ++l;
         }

         for(int k = 0; k < l; ++k) {
            Direction direction = Direction.Plane.HORIZONTAL.random(p_185601_2_);
            BlockPos blockpos1 = p_185601_1_.up(i).offset(direction);
            if (Math.abs(blockpos1.getX() - p_185601_3_.getX()) < p_185601_4_ && Math.abs(blockpos1.getZ() - p_185601_3_.getZ()) < p_185601_4_ && p_185601_0_.isAirBlock(blockpos1) && p_185601_0_.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(p_185601_0_, blockpos1, direction.getOpposite())) {
               flag = true;
               p_185601_0_.setBlockState(blockpos1, chorusplantblock.makeConnections(p_185601_0_, blockpos1), 2);
               p_185601_0_.setBlockState(blockpos1.offset(direction.getOpposite()), chorusplantblock.makeConnections(p_185601_0_, blockpos1.offset(direction.getOpposite())), 2);
               growTreeRecursive(p_185601_0_, blockpos1, p_185601_2_, p_185601_3_, p_185601_4_, p_185601_5_ + 1);
            }
         }
      }

      if (!flag) {
         p_185601_0_.setBlockState(p_185601_1_.up(i), (BlockState)Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, 5), 2);
      }

   }

   public void onProjectileCollision(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
      BlockPos blockpos = p_220066_3_.getPos();
      p_220066_1_.func_225521_a_(blockpos, true, p_220066_4_);
   }

   static {
      AGE = BlockStateProperties.AGE_0_5;
   }
}
