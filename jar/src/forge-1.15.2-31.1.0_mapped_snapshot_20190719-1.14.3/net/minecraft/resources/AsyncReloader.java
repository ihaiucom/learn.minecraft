package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AsyncReloader<S> implements IAsyncReloader {
   protected final IResourceManager resourceManager;
   protected final CompletableFuture<Unit> allAsyncCompleted = new CompletableFuture();
   protected final CompletableFuture<List<S>> resultListFuture;
   private final Set<IFutureReloadListener> taskSet;
   private final int taskCount;
   private int syncScheduled;
   private int syncCompleted;
   private final AtomicInteger asyncScheduled = new AtomicInteger();
   private final AtomicInteger asyncCompleted = new AtomicInteger();

   public static AsyncReloader<Void> create(IResourceManager p_219562_0_, List<IFutureReloadListener> p_219562_1_, Executor p_219562_2_, Executor p_219562_3_, CompletableFuture<Unit> p_219562_4_) {
      return new AsyncReloader(p_219562_2_, p_219562_3_, p_219562_0_, p_219562_1_, (p_219561_1_, p_219561_2_, p_219561_3_, p_219561_4_, p_219561_5_) -> {
         return p_219561_3_.reload(p_219561_1_, p_219561_2_, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_219562_2_, p_219561_5_);
      }, p_219562_4_);
   }

   protected AsyncReloader(Executor p_i50690_1_, final Executor p_i50690_2_, IResourceManager p_i50690_3_, List<IFutureReloadListener> p_i50690_4_, AsyncReloader.IStateFactory<S> p_i50690_5_, CompletableFuture<Unit> p_i50690_6_) {
      this.resourceManager = p_i50690_3_;
      this.taskCount = p_i50690_4_.size();
      this.asyncScheduled.incrementAndGet();
      AtomicInteger var10001 = this.asyncCompleted;
      p_i50690_6_.thenRun(var10001::incrementAndGet);
      List<CompletableFuture<S>> lvt_7_1_ = Lists.newArrayList();
      final CompletableFuture<?> lvt_8_1_ = p_i50690_6_;
      this.taskSet = Sets.newHashSet(p_i50690_4_);

      CompletableFuture lvt_12_1_;
      for(Iterator var9 = p_i50690_4_.iterator(); var9.hasNext(); lvt_8_1_ = lvt_12_1_) {
         final IFutureReloadListener lvt_10_1_ = (IFutureReloadListener)var9.next();
         lvt_12_1_ = p_i50690_5_.create(new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T p_216872_1_) {
               p_i50690_2_.execute(() -> {
                  AsyncReloader.this.taskSet.remove(lvt_10_1_);
                  if (AsyncReloader.this.taskSet.isEmpty()) {
                     AsyncReloader.this.allAsyncCompleted.complete(Unit.INSTANCE);
                  }

               });
               return AsyncReloader.this.allAsyncCompleted.thenCombine(lvt_8_1_, (p_216874_1_, p_216874_2_) -> {
                  return p_216872_1_;
               });
            }
         }, p_i50690_3_, lvt_10_1_, (p_219564_2_) -> {
            this.asyncScheduled.incrementAndGet();
            p_i50690_1_.execute(() -> {
               p_219564_2_.run();
               this.asyncCompleted.incrementAndGet();
            });
         }, (p_219560_2_) -> {
            ++this.syncScheduled;
            p_i50690_2_.execute(() -> {
               p_219560_2_.run();
               ++this.syncCompleted;
            });
         });
         lvt_7_1_.add(lvt_12_1_);
      }

      this.resultListFuture = Util.gather(lvt_7_1_);
   }

   public CompletableFuture<Unit> onceDone() {
      return this.resultListFuture.thenApply((p_219558_0_) -> {
         return Unit.INSTANCE;
      });
   }

   @OnlyIn(Dist.CLIENT)
   public float estimateExecutionSpeed() {
      int lvt_1_1_ = this.taskCount - this.taskSet.size();
      float lvt_2_1_ = (float)(this.asyncCompleted.get() * 2 + this.syncCompleted * 2 + lvt_1_1_ * 1);
      float lvt_3_1_ = (float)(this.asyncScheduled.get() * 2 + this.syncScheduled * 2 + this.taskCount * 1);
      return lvt_2_1_ / lvt_3_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean asyncPartDone() {
      return this.allAsyncCompleted.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean fullyDone() {
      return this.resultListFuture.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public void join() {
      if (this.resultListFuture.isCompletedExceptionally()) {
         this.resultListFuture.join();
      }

   }

   public interface IStateFactory<S> {
      CompletableFuture<S> create(IFutureReloadListener.IStage var1, IResourceManager var2, IFutureReloadListener var3, Executor var4, Executor var5);
   }
}
