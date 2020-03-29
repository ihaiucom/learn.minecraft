package net.minecraft.resources;

import com.google.common.base.Stopwatch;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugAsyncReloader extends AsyncReloader<DebugAsyncReloader.DataPoint> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Stopwatch timer = Stopwatch.createUnstarted();

   public DebugAsyncReloader(IResourceManager p_i50694_1_, List<IFutureReloadListener> p_i50694_2_, Executor p_i50694_3_, Executor p_i50694_4_, CompletableFuture<Unit> p_i50694_5_) {
      super(p_i50694_3_, p_i50694_4_, p_i50694_1_, p_i50694_2_, (p_219578_1_, p_219578_2_, p_219578_3_, p_219578_4_, p_219578_5_) -> {
         AtomicLong lvt_6_1_ = new AtomicLong();
         AtomicLong lvt_7_1_ = new AtomicLong();
         Profiler lvt_8_1_ = new Profiler(Util.nanoTime(), () -> {
            return 0;
         }, false);
         Profiler lvt_9_1_ = new Profiler(Util.nanoTime(), () -> {
            return 0;
         }, false);
         CompletableFuture<Void> lvt_10_1_ = p_219578_3_.reload(p_219578_1_, p_219578_2_, lvt_8_1_, lvt_9_1_, (p_219577_2_) -> {
            p_219578_4_.execute(() -> {
               long lvt_2_1_ = Util.nanoTime();
               p_219577_2_.run();
               lvt_6_1_.addAndGet(Util.nanoTime() - lvt_2_1_);
            });
         }, (p_219574_2_) -> {
            p_219578_5_.execute(() -> {
               long lvt_2_1_ = Util.nanoTime();
               p_219574_2_.run();
               lvt_7_1_.addAndGet(Util.nanoTime() - lvt_2_1_);
            });
         });
         return lvt_10_1_.thenApplyAsync((p_219576_5_) -> {
            return new DebugAsyncReloader.DataPoint(p_219578_3_.func_225594_i_(), lvt_8_1_.getResults(), lvt_9_1_.getResults(), lvt_6_1_, lvt_7_1_);
         }, p_i50694_4_);
      }, p_i50694_5_);
      this.timer.start();
      this.resultListFuture.thenAcceptAsync(this::logStatistics, p_i50694_4_);
   }

   private void logStatistics(List<DebugAsyncReloader.DataPoint> p_219575_1_) {
      this.timer.stop();
      int lvt_2_1_ = 0;
      LOGGER.info("Resource reload finished after " + this.timer.elapsed(TimeUnit.MILLISECONDS) + " ms");

      int lvt_8_1_;
      for(Iterator var3 = p_219575_1_.iterator(); var3.hasNext(); lvt_2_1_ += lvt_8_1_) {
         DebugAsyncReloader.DataPoint lvt_4_1_ = (DebugAsyncReloader.DataPoint)var3.next();
         IProfileResult lvt_5_1_ = lvt_4_1_.prepareProfilerResult;
         IProfileResult lvt_6_1_ = lvt_4_1_.applyProfilerResult;
         int lvt_7_1_ = (int)((double)lvt_4_1_.prepareDuration.get() / 1000000.0D);
         lvt_8_1_ = (int)((double)lvt_4_1_.applyDuration.get() / 1000000.0D);
         int lvt_9_1_ = lvt_7_1_ + lvt_8_1_;
         String lvt_10_1_ = lvt_4_1_.className;
         LOGGER.info(lvt_10_1_ + " took approximately " + lvt_9_1_ + " ms (" + lvt_7_1_ + " ms preparing, " + lvt_8_1_ + " ms applying)");
      }

      LOGGER.info("Total blocking time: " + lvt_2_1_ + " ms");
   }

   public static class DataPoint {
      private final String className;
      private final IProfileResult prepareProfilerResult;
      private final IProfileResult applyProfilerResult;
      private final AtomicLong prepareDuration;
      private final AtomicLong applyDuration;

      private DataPoint(String p_i50542_1_, IProfileResult p_i50542_2_, IProfileResult p_i50542_3_, AtomicLong p_i50542_4_, AtomicLong p_i50542_5_) {
         this.className = p_i50542_1_;
         this.prepareProfilerResult = p_i50542_2_;
         this.applyProfilerResult = p_i50542_3_;
         this.prepareDuration = p_i50542_4_;
         this.applyDuration = p_i50542_5_;
      }

      // $FF: synthetic method
      DataPoint(String p_i50543_1_, IProfileResult p_i50543_2_, IProfileResult p_i50543_3_, AtomicLong p_i50543_4_, AtomicLong p_i50543_5_, Object p_i50543_6_) {
         this(p_i50543_1_, p_i50543_2_, p_i50543_3_, p_i50543_4_, p_i50543_5_);
      }
   }
}
