package net.minecraft.util.concurrent;

public abstract class RecursiveEventLoop<R extends Runnable> extends ThreadTaskExecutor<R> {
   private int running;

   public RecursiveEventLoop(String p_i50401_1_) {
      super(p_i50401_1_);
   }

   protected boolean shouldDeferTasks() {
      return this.isTaskRunning() || super.shouldDeferTasks();
   }

   protected boolean isTaskRunning() {
      return this.running != 0;
   }

   protected void run(R p_213166_1_) {
      ++this.running;

      try {
         super.run(p_213166_1_);
      } finally {
         --this.running;
      }

   }
}
