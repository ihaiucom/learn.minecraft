package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FrostedIceBlock extends IceBlock {
   public static final IntegerProperty AGE;

   public FrostedIceBlock(Block.Properties p_i48394_1_) {
      super(p_i48394_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((p_225534_4_.nextInt(3) == 0 || this.shouldMelt(p_225534_2_, p_225534_3_, 4)) && p_225534_2_.getLight(p_225534_3_) > 11 - (Integer)p_225534_1_.get(AGE) - p_225534_1_.getOpacity(p_225534_2_, p_225534_3_) && this.slightlyMelt(p_225534_1_, p_225534_2_, p_225534_3_)) {
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         Throwable var6 = null;

         try {
            Direction[] var7 = Direction.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Direction direction = var7[var9];
               blockpos$pooledmutable.setPos((Vec3i)p_225534_3_).move(direction);
               BlockState blockstate = p_225534_2_.getBlockState(blockpos$pooledmutable);
               if (blockstate.getBlock() == this && !this.slightlyMelt(blockstate, p_225534_2_, blockpos$pooledmutable)) {
                  p_225534_2_.getPendingBlockTicks().scheduleTick(blockpos$pooledmutable, this, MathHelper.nextInt(p_225534_4_, 20, 40));
               }
            }
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (blockpos$pooledmutable != null) {
               if (var6 != null) {
                  try {
                     blockpos$pooledmutable.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  blockpos$pooledmutable.close();
               }
            }

         }
      } else {
         p_225534_2_.getPendingBlockTicks().scheduleTick(p_225534_3_, this, MathHelper.nextInt(p_225534_4_, 20, 40));
      }

   }

   private boolean slightlyMelt(BlockState p_196455_1_, World p_196455_2_, BlockPos p_196455_3_) {
      int i = (Integer)p_196455_1_.get(AGE);
      if (i < 3) {
         p_196455_2_.setBlockState(p_196455_3_, (BlockState)p_196455_1_.with(AGE, i + 1), 2);
         return false;
      } else {
         this.turnIntoWater(p_196455_1_, p_196455_2_, p_196455_3_);
         return true;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_4_ == this && this.shouldMelt(p_220069_2_, p_220069_3_, 2)) {
         this.turnIntoWater(p_220069_1_, p_220069_2_, p_220069_3_);
      }

      super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
   }

   private boolean shouldMelt(IBlockReader p_196456_1_, BlockPos p_196456_2_, int p_196456_3_) {
      int i = 0;
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var6 = null;

      boolean var24;
      try {
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction direction = var7[var9];
            blockpos$pooledmutable.setPos((Vec3i)p_196456_2_).move(direction);
            if (p_196456_1_.getBlockState(blockpos$pooledmutable).getBlock() == this) {
               ++i;
               if (i >= p_196456_3_) {
                  boolean flag = false;
                  boolean var12 = flag;
                  return var12;
               }
            }
         }

         var24 = true;
      } catch (Throwable var22) {
         var6 = var22;
         throw var22;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var6 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var21) {
                  var6.addSuppressed(var21);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return var24;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   static {
      AGE = BlockStateProperties.AGE_0_3;
   }
}
