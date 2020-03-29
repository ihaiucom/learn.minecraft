package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadTaskExecutor<R extends Runnable> implements ITaskExecutor<R>, Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue<R> queue = Queues.newConcurrentLinkedQueue();
   private int drivers;

   protected ThreadTaskExecutor(String p_i50403_1_) {
      this.name = p_i50403_1_;
   }

   protected abstract R wrapTask(Runnable var1);

   protected abstract boolean canRun(R var1);

   public boolean isOnExecutionThread() {
      return Thread.currentThread() == this.getExecutionThread();
   }

   protected abstract Thread getExecutionThread();

   protected boolean shouldDeferTasks() {
      return !this.isOnExecutionThread();
   }

   public int func_223704_be() {
      return this.queue.size();
   }

   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public <V> CompletableFuture<V> supplyAsync(Supplier<V> p_213169_1_) {
      return this.shouldDeferTasks() ? CompletableFuture.supplyAsync(p_213169_1_, this) : CompletableFuture.completedFuture(p_213169_1_.get());
   }

   public CompletableFuture<Void> deferTask(Runnable p_213165_1_) {
      return CompletableFuture.supplyAsync(() -> {
         p_213165_1_.run();
         return null;
      }, this);
   }

   public CompletableFuture<Void> runAsync(Runnable p_222817_1_) {
      if (this.shouldDeferTasks()) {
         return this.deferTask(p_222817_1_);
      } else {
         p_222817_1_.run();
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void runImmediately(Runnable p_213167_1_) {
      if (!this.isOnExecutionThread()) {
         this.deferTask(p_213167_1_).join();
      } else {
         p_213167_1_.run();
      }

   }

   public void enqueue(R p_212871_1_) {
      this.queue.add(p_212871_1_);
      LockSupport.unpark(this.getExecutionThread());
   }

   public void execute(Runnable p_execute_1_) {
      if (this.shouldDeferTasks()) {
         this.enqueue(this.wrapTask(p_execute_1_));
      } else {
         p_execute_1_.run();
      }

   }

   @OnlyIn(Dist.CLIENT)
   protected void dropTasks() {
      this.queue.clear();
   }

   protected void drainTasks() {
      while(this.driveOne()) {
      }

   }

   protected boolean driveOne() {
      R lvt_1_1_ = (Runnable)this.queue.peek();
      if (lvt_1_1_ == null) {
         return false;
      } else if (this.drivers == 0 && !this.canRun(lvt_1_1_)) {
         return false;
      } else {
         this.run((Runnable)this.queue.remove());
         return true;
      }
   }

   public void driveUntil(BooleanSupplier p_213161_1_) {
      ++this.drivers;

      try {
         while(!p_213161_1_.getAsBoolean()) {
            if (!this.driveOne()) {
               this.func_223705_bi();
            }
         }
      } finally {
         --this.drivers;
      }

   }

   protected void func_223705_bi() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void run(R p_213166_1_) {
      try {
         p_213166_1_.run();
      } catch (Exception var3) {
         LOGGER.fatal("Error executing task on {}", this.getName(), var3);
      }

   }

   // $FF: synthetic method
   public void enqueue(Object p_212871_1_) {
      this.enqueue((Runnable)p_212871_1_);
   }
}
