package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class Task<E extends LivingEntity> {
   private final Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryState;
   private Task.Status status;
   private long stopTime;
   private final int durationMin;
   private final int durationMax;

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51504_1_) {
      this(p_i51504_1_, 60);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51505_1_, int p_i51505_2_) {
      this(p_i51505_1_, p_i51505_2_, p_i51505_2_);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51506_1_, int p_i51506_2_, int p_i51506_3_) {
      this.status = Task.Status.STOPPED;
      this.durationMin = p_i51506_2_;
      this.durationMax = p_i51506_3_;
      this.requiredMemoryState = p_i51506_1_;
   }

   public Task.Status getStatus() {
      return this.status;
   }

   public final boolean start(ServerWorld p_220378_1_, E p_220378_2_, long p_220378_3_) {
      if (this.hasRequiredMemories(p_220378_2_) && this.shouldExecute(p_220378_1_, p_220378_2_)) {
         this.status = Task.Status.RUNNING;
         int lvt_5_1_ = this.durationMin + p_220378_1_.getRandom().nextInt(this.durationMax + 1 - this.durationMin);
         this.stopTime = p_220378_3_ + (long)lvt_5_1_;
         this.startExecuting(p_220378_1_, p_220378_2_, p_220378_3_);
         return true;
      } else {
         return false;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
   }

   public final void tick(ServerWorld p_220377_1_, E p_220377_2_, long p_220377_3_) {
      if (!this.isTimedOut(p_220377_3_) && this.shouldContinueExecuting(p_220377_1_, p_220377_2_, p_220377_3_)) {
         this.updateTask(p_220377_1_, p_220377_2_, p_220377_3_);
      } else {
         this.stop(p_220377_1_, p_220377_2_, p_220377_3_);
      }

   }

   protected void updateTask(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
   }

   public final void stop(ServerWorld p_220380_1_, E p_220380_2_, long p_220380_3_) {
      this.status = Task.Status.STOPPED;
      this.resetTask(p_220380_1_, p_220380_2_, p_220380_3_);
   }

   protected void resetTask(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return false;
   }

   protected boolean isTimedOut(long p_220383_1_) {
      return p_220383_1_ > this.stopTime;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, E p_212832_2_) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(E p_220382_1_) {
      return this.requiredMemoryState.entrySet().stream().allMatch((p_220379_1_) -> {
         MemoryModuleType<?> lvt_2_1_ = (MemoryModuleType)p_220379_1_.getKey();
         MemoryModuleStatus lvt_3_1_ = (MemoryModuleStatus)p_220379_1_.getValue();
         return p_220382_1_.getBrain().hasMemory(lvt_2_1_, lvt_3_1_);
      });
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}
