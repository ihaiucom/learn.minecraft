package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final CatEntity cat;

   public CatLieOnBedGoal(CatEntity p_i50331_1_, double p_i50331_2_, int p_i50331_4_) {
      super(p_i50331_1_, p_i50331_2_, p_i50331_4_, 6);
      this.cat = p_i50331_1_;
      this.field_203112_e = -2;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.func_213416_eg() && super.shouldExecute();
   }

   public void startExecuting() {
      super.startExecuting();
      this.cat.getAISit().setSitting(false);
   }

   protected int getRunDelay(CreatureEntity p_203109_1_) {
      return 40;
   }

   public void resetTask() {
      super.resetTask();
      this.cat.func_213419_u(false);
   }

   public void tick() {
      super.tick();
      this.cat.getAISit().setSitting(false);
      if (!this.getIsAboveDestination()) {
         this.cat.func_213419_u(false);
      } else if (!this.cat.func_213416_eg()) {
         this.cat.func_213419_u(true);
      }

   }

   protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
      return p_179488_1_.isAirBlock(p_179488_2_.up()) && p_179488_1_.getBlockState(p_179488_2_).getBlock().isIn(BlockTags.BEDS);
   }
}
