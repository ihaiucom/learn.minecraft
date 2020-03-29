package net.minecraft.client.audio;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEngineExecutor extends ThreadTaskExecutor<Runnable> {
   private Thread executionThread = this.createExecutionThread();
   private volatile boolean stopped;

   public SoundEngineExecutor() {
      super("Sound executor");
   }

   private Thread createExecutionThread() {
      Thread lvt_1_1_ = new Thread(this::run);
      lvt_1_1_.setDaemon(true);
      lvt_1_1_.setName("Sound engine");
      lvt_1_1_.start();
      return lvt_1_1_;
   }

   protected Runnable wrapTask(Runnable p_212875_1_) {
      return p_212875_1_;
   }

   protected boolean canRun(Runnable p_212874_1_) {
      return !this.stopped;
   }

   protected Thread getExecutionThread() {
      return this.executionThread;
   }

   private void run() {
      while(!this.stopped) {
         this.driveUntil(() -> {
            return this.stopped;
         });
      }

   }

   protected void func_223705_bi() {
      LockSupport.park("waiting for tasks");
   }

   public void restart() {
      this.stopped = true;
      this.executionThread.interrupt();

      try {
         this.executionThread.join();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }

      this.dropTasks();
      this.stopped = false;
      this.executionThread = this.createExecutionThread();
   }
}
