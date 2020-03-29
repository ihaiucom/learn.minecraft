package net.minecraft.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelegatedTaskExecutor<T> implements ITaskExecutor<T>, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AtomicInteger flags = new AtomicInteger(0);
   public final ITaskQueue<? super T, ? extends Runnable> queue;
   private final Executor delegate;
   private final String name;

   public static DelegatedTaskExecutor<Runnable> create(Executor p_213144_0_, String p_213144_1_) {
      return new DelegatedTaskExecutor(new ITaskQueue.Single(new ConcurrentLinkedQueue()), p_213144_0_, p_213144_1_);
   }

   public DelegatedTaskExecutor(ITaskQueue<? super T, ? extends Runnable> p_i50402_1_, Executor p_i50402_2_, String p_i50402_3_) {
      this.delegate = p_i50402_2_;
      this.queue = p_i50402_1_;
      this.name = p_i50402_3_;
   }

   private boolean setActive() {
      int lvt_1_1_;
      do {
         lvt_1_1_ = this.flags.get();
         if ((lvt_1_1_ & 3) != 0) {
            return false;
         }
      } while(!this.flags.compareAndSet(lvt_1_1_, lvt_1_1_ | 2));

      return true;
   }

   private void clearActive() {
      int lvt_1_1_;
      do {
         lvt_1_1_ = this.flags.get();
      } while(!this.flags.compareAndSet(lvt_1_1_, lvt_1_1_ & -3));

   }

   private boolean shouldSchedule() {
      if ((this.flags.get() & 1) != 0) {
         return false;
      } else {
         return !this.queue.isEmpty();
      }
   }

   public void close() {
      int lvt_1_1_;
      do {
         lvt_1_1_ = this.flags.get();
      } while(!this.flags.compareAndSet(lvt_1_1_, lvt_1_1_ | 1));

   }

   private boolean isActive() {
      return (this.flags.get() & 2) != 0;
   }

   private boolean driveOne() {
      if (!this.isActive()) {
         return false;
      } else {
         Runnable lvt_1_1_ = (Runnable)this.queue.poll();
         if (lvt_1_1_ == null) {
            return false;
         } else {
            lvt_1_1_.run();
            return true;
         }
      }
   }

   public void run() {
      try {
         this.driveWhile((p_213147_0_) -> {
            return p_213147_0_ == 0;
         });
      } finally {
         this.clearActive();
         this.reschedule();
      }

   }

   public void enqueue(T p_212871_1_) {
      this.queue.enqueue(p_212871_1_);
      this.reschedule();
   }

   private void reschedule() {
      if (this.shouldSchedule() && this.setActive()) {
         try {
            this.delegate.execute(this);
         } catch (RejectedExecutionException var4) {
            try {
               this.delegate.execute(this);
            } catch (RejectedExecutionException var3) {
               LOGGER.error("Cound not schedule mailbox", var3);
            }
         }
      }

   }

   private int driveWhile(Int2BooleanFunction p_213145_1_) {
      int lvt_2_1_;
      for(lvt_2_1_ = 0; p_213145_1_.get(lvt_2_1_) && this.driveOne(); ++lvt_2_1_) {
      }

      return lvt_2_1_;
   }

   public String toString() {
      return this.name + " " + this.flags.get() + " " + this.queue.isEmpty();
   }

   public String getName() {
      return this.name;
   }
}
