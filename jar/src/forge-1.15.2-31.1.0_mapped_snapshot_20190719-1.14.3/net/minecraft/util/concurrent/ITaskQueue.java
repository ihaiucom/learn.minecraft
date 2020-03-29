package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface ITaskQueue<T, F> {
   @Nullable
   F poll();

   boolean enqueue(T var1);

   boolean isEmpty();

   public static final class Priority implements ITaskQueue<ITaskQueue.RunnableWithPriority, Runnable> {
      private final List<Queue<Runnable>> queues;

      public Priority(int p_i50964_1_) {
         this.queues = (List)IntStream.range(0, p_i50964_1_).mapToObj((p_219948_0_) -> {
            return Queues.newConcurrentLinkedQueue();
         }).collect(Collectors.toList());
      }

      @Nullable
      public Runnable poll() {
         Iterator var1 = this.queues.iterator();

         Runnable lvt_3_1_;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            Queue<Runnable> lvt_2_1_ = (Queue)var1.next();
            lvt_3_1_ = (Runnable)lvt_2_1_.poll();
         } while(lvt_3_1_ == null);

         return lvt_3_1_;
      }

      public boolean enqueue(ITaskQueue.RunnableWithPriority p_212828_1_) {
         int lvt_2_1_ = p_212828_1_.getPriority();
         ((Queue)this.queues.get(lvt_2_1_)).add(p_212828_1_);
         return true;
      }

      public boolean isEmpty() {
         return this.queues.stream().allMatch(Collection::isEmpty);
      }

      // $FF: synthetic method
      @Nullable
      public Object poll() {
         return this.poll();
      }
   }

   public static final class RunnableWithPriority implements Runnable {
      private final int priority;
      private final Runnable runnable;

      public RunnableWithPriority(int p_i50963_1_, Runnable p_i50963_2_) {
         this.priority = p_i50963_1_;
         this.runnable = p_i50963_2_;
      }

      public void run() {
         this.runnable.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class Single<T> implements ITaskQueue<T, T> {
      private final Queue<T> queue;

      public Single(Queue<T> p_i50962_1_) {
         this.queue = p_i50962_1_;
      }

      @Nullable
      public T poll() {
         return this.queue.poll();
      }

      public boolean enqueue(T p_212828_1_) {
         return this.queue.add(p_212828_1_);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }
   }
}
