package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PrioritizedGoal extends Goal {
   private final Goal inner;
   private final int priority;
   private boolean running;

   public PrioritizedGoal(int p_i50318_1_, Goal p_i50318_2_) {
      this.priority = p_i50318_1_;
      this.inner = p_i50318_2_;
   }

   public boolean isPreemptedBy(PrioritizedGoal p_220771_1_) {
      return this.isPreemptible() && p_220771_1_.getPriority() < this.getPriority();
   }

   public boolean shouldExecute() {
      return this.inner.shouldExecute();
   }

   public boolean shouldContinueExecuting() {
      return this.inner.shouldContinueExecuting();
   }

   public boolean isPreemptible() {
      return this.inner.isPreemptible();
   }

   public void startExecuting() {
      if (!this.running) {
         this.running = true;
         this.inner.startExecuting();
      }
   }

   public void resetTask() {
      if (this.running) {
         this.running = false;
         this.inner.resetTask();
      }
   }

   public void tick() {
      this.inner.tick();
   }

   public void setMutexFlags(EnumSet<Goal.Flag> p_220684_1_) {
      this.inner.setMutexFlags(p_220684_1_);
   }

   public EnumSet<Goal.Flag> getMutexFlags() {
      return this.inner.getMutexFlags();
   }

   public boolean isRunning() {
      return this.running;
   }

   public int getPriority() {
      return this.priority;
   }

   public Goal getGoal() {
      return this.inner;
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? this.inner.equals(((PrioritizedGoal)p_equals_1_).inner) : false;
      }
   }

   public int hashCode() {
      return this.inner.hashCode();
   }
}
