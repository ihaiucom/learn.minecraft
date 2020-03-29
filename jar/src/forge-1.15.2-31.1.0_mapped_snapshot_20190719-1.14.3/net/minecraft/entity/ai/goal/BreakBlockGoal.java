package net.minecraft.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class BreakBlockGoal extends MoveToBlockGoal {
   private final Block block;
   private final MobEntity entity;
   private int breakingTime;

   public BreakBlockGoal(Block p_i48795_1_, CreatureEntity p_i48795_2_, double p_i48795_3_, int p_i48795_5_) {
      super(p_i48795_2_, p_i48795_3_, 24, p_i48795_5_);
      this.block = p_i48795_1_;
      this.entity = p_i48795_2_;
   }

   public boolean shouldExecute() {
      if (!ForgeHooks.canEntityDestroy(this.entity.world, this.destinationBlock, this.entity)) {
         return false;
      } else if (this.runDelay > 0) {
         --this.runDelay;
         return false;
      } else if (this.func_220729_m()) {
         this.runDelay = 20;
         return true;
      } else {
         this.runDelay = this.getRunDelay(this.creature);
         return false;
      }
   }

   private boolean func_220729_m() {
      return this.destinationBlock != null && this.shouldMoveTo(this.creature.world, this.destinationBlock) ? true : this.searchForDestination();
   }

   public void resetTask() {
      super.resetTask();
      this.entity.fallDistance = 1.0F;
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
   }

   public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
   }

   public void tick() {
      super.tick();
      World world = this.entity.world;
      BlockPos blockpos = new BlockPos(this.entity);
      BlockPos blockpos1 = this.findTarget(blockpos, world);
      Random random = this.entity.getRNG();
      if (this.getIsAboveDestination() && blockpos1 != null) {
         Vec3d vec3d1;
         double d3;
         if (this.breakingTime > 0) {
            vec3d1 = this.entity.getMotion();
            this.entity.setMotion(vec3d1.x, 0.3D, vec3d1.z);
            if (!world.isRemote) {
               d3 = 0.08D;
               ((ServerWorld)world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.7D, (double)blockpos1.getZ() + 0.5D, 3, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, 0.15000000596046448D);
            }
         }

         if (this.breakingTime % 2 == 0) {
            vec3d1 = this.entity.getMotion();
            this.entity.setMotion(vec3d1.x, -0.3D, vec3d1.z);
            if (this.breakingTime % 6 == 0) {
               this.playBreakingSound(world, this.destinationBlock);
            }
         }

         if (this.breakingTime > 60) {
            world.removeBlock(blockpos1, false);
            if (!world.isRemote) {
               for(int i = 0; i < 20; ++i) {
                  d3 = random.nextGaussian() * 0.02D;
                  double d1 = random.nextGaussian() * 0.02D;
                  double d2 = random.nextGaussian() * 0.02D;
                  ((ServerWorld)world).spawnParticle(ParticleTypes.POOF, (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D, 1, d3, d1, d2, 0.15000000596046448D);
               }

               this.playBrokenSound(world, blockpos1);
            }
         }

         ++this.breakingTime;
      }

   }

   @Nullable
   private BlockPos findTarget(BlockPos p_203115_1_, IBlockReader p_203115_2_) {
      if (p_203115_2_.getBlockState(p_203115_1_).getBlock() == this.block) {
         return p_203115_1_;
      } else {
         BlockPos[] ablockpos = new BlockPos[]{p_203115_1_.down(), p_203115_1_.west(), p_203115_1_.east(), p_203115_1_.north(), p_203115_1_.south(), p_203115_1_.down().down()};
         BlockPos[] var4 = ablockpos;
         int var5 = ablockpos.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockPos blockpos = var4[var6];
            if (p_203115_2_.getBlockState(blockpos).getBlock() == this.block) {
               return blockpos;
            }
         }

         return null;
      }
   }

   protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
      IChunk ichunk = p_179488_1_.getChunk(p_179488_2_.getX() >> 4, p_179488_2_.getZ() >> 4, ChunkStatus.FULL, false);
      if (ichunk == null) {
         return false;
      } else {
         return ichunk.getBlockState(p_179488_2_).getBlock() == this.block && ichunk.getBlockState(p_179488_2_.up()).isAir() && ichunk.getBlockState(p_179488_2_.up(2)).isAir();
      }
   }
}
