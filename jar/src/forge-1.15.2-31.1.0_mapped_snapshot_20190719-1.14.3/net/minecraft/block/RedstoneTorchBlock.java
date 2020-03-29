package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneTorchBlock extends TorchBlock {
   public static final BooleanProperty LIT;
   private static final Map<IBlockReader, List<RedstoneTorchBlock.Toggle>> BURNED_TORCHES;

   protected RedstoneTorchBlock(Block.Properties p_i48342_1_) {
      super(p_i48342_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LIT, true));
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 2;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      Direction[] var6 = Direction.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction lvt_9_1_ = var6[var8];
         p_220082_2_.notifyNeighborsOfStateChange(p_220082_3_.offset(lvt_9_1_), this);
      }

   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction lvt_9_1_ = var6[var8];
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(lvt_9_1_), this);
         }

      }
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(LIT) && Direction.UP != p_180656_4_ ? 15 : 0;
   }

   protected boolean shouldBeOff(World p_176597_1_, BlockPos p_176597_2_, BlockState p_176597_3_) {
      return p_176597_1_.isSidePowered(p_176597_2_.down(), Direction.DOWN);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      update(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_, this.shouldBeOff(p_225534_2_, p_225534_3_, p_225534_1_));
   }

   public static void update(BlockState p_196527_0_, World p_196527_1_, BlockPos p_196527_2_, Random p_196527_3_, boolean p_196527_4_) {
      List lvt_5_1_ = (List)BURNED_TORCHES.get(p_196527_1_);

      while(lvt_5_1_ != null && !lvt_5_1_.isEmpty() && p_196527_1_.getGameTime() - ((RedstoneTorchBlock.Toggle)lvt_5_1_.get(0)).time > 60L) {
         lvt_5_1_.remove(0);
      }

      if ((Boolean)p_196527_0_.get(LIT)) {
         if (p_196527_4_) {
            p_196527_1_.setBlockState(p_196527_2_, (BlockState)p_196527_0_.with(LIT, false), 3);
            if (isBurnedOut(p_196527_1_, p_196527_2_, true)) {
               p_196527_1_.playEvent(1502, p_196527_2_, 0);
               p_196527_1_.getPendingBlockTicks().scheduleTick(p_196527_2_, p_196527_1_.getBlockState(p_196527_2_).getBlock(), 160);
            }
         }
      } else if (!p_196527_4_ && !isBurnedOut(p_196527_1_, p_196527_2_, false)) {
         p_196527_1_.setBlockState(p_196527_2_, (BlockState)p_196527_0_.with(LIT, true), 3);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if ((Boolean)p_220069_1_.get(LIT) == this.shouldBeOff(p_220069_2_, p_220069_3_, p_220069_1_) && !p_220069_2_.getPendingBlockTicks().isTickPending(p_220069_3_, this)) {
         p_220069_2_.getPendingBlockTicks().scheduleTick(p_220069_3_, this, this.tickRate(p_220069_2_));
      }

   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.DOWN ? p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(LIT)) {
         double lvt_5_1_ = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double lvt_7_1_ = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double lvt_9_1_ = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         p_180655_2_.addParticle(RedstoneParticleData.REDSTONE_DUST, lvt_5_1_, lvt_7_1_, lvt_9_1_, 0.0D, 0.0D, 0.0D);
      }
   }

   public int getLightValue(BlockState p_149750_1_) {
      return (Boolean)p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }

   private static boolean isBurnedOut(World p_176598_0_, BlockPos p_176598_1_, boolean p_176598_2_) {
      List<RedstoneTorchBlock.Toggle> lvt_3_1_ = (List)BURNED_TORCHES.computeIfAbsent(p_176598_0_, (p_220288_0_) -> {
         return Lists.newArrayList();
      });
      if (p_176598_2_) {
         lvt_3_1_.add(new RedstoneTorchBlock.Toggle(p_176598_1_.toImmutable(), p_176598_0_.getGameTime()));
      }

      int lvt_4_1_ = 0;

      for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_.size(); ++lvt_5_1_) {
         RedstoneTorchBlock.Toggle lvt_6_1_ = (RedstoneTorchBlock.Toggle)lvt_3_1_.get(lvt_5_1_);
         if (lvt_6_1_.pos.equals(p_176598_1_)) {
            ++lvt_4_1_;
            if (lvt_4_1_ >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   static {
      LIT = BlockStateProperties.LIT;
      BURNED_TORCHES = new WeakHashMap();
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long time;

      public Toggle(BlockPos p_i45688_1_, long p_i45688_2_) {
         this.pos = p_i45688_1_;
         this.time = p_i45688_2_;
      }
   }
}
