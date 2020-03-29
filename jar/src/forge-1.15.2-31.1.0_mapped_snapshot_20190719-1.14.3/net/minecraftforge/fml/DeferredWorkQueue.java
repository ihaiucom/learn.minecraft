package net.minecraftforge.fml;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** @deprecated */
@Deprecated
public class DeferredWorkQueue {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ThreadLocal<ModContainer> currentOwner = new ThreadLocal();
   private static List<ModLoadingException> raisedExceptions = new ArrayList();
   private static final ConcurrentLinkedDeque<DeferredWorkQueue.TaskInfo> taskQueue = new ConcurrentLinkedDeque();
   private static final Executor deferredExecutor = (r) -> {
      taskQueue.add(new DeferredWorkQueue.TaskInfo(((ModContainer)currentOwner.get()).getModInfo(), r));
   };

   private static <T> Function<Throwable, T> handleException() {
      ModContainer owner = (ModContainer)currentOwner.get();
      return (t) -> {
         LogManager.getLogger(DeferredWorkQueue.class).error("Encountered exception executing deferred work", t);
         raisedExceptions.add(new ModLoadingException(owner.getModInfo(), owner.getCurrentState(), "fml.modloading.failedtoprocesswork", t, new Object[0]));
         return null;
      };
   }

   public static CompletableFuture<Void> runLater(Runnable workToEnqueue) {
      currentOwner.set(ModLoadingContext.get().getActiveContainer());
      return CompletableFuture.runAsync(workToEnqueue, deferredExecutor).exceptionally(handleException());
   }

   public static CompletableFuture<Void> runLaterChecked(DeferredWorkQueue.CheckedRunnable workToEnqueue) {
      return runLater(() -> {
         try {
            workToEnqueue.run();
         } catch (Throwable var2) {
            throw new CompletionException(var2);
         }
      });
   }

   public static <T> CompletableFuture<T> getLater(Supplier<T> workToEnqueue) {
      currentOwner.set(ModLoadingContext.get().getActiveContainer());
      return CompletableFuture.supplyAsync(workToEnqueue, deferredExecutor).exceptionally(handleException());
   }

   public static <T> CompletableFuture<T> getLaterChecked(Callable<T> workToEnqueue) {
      return getLater(() -> {
         try {
            return workToEnqueue.call();
         } catch (Throwable var2) {
            throw new CompletionException(var2);
         }
      });
   }

   static void clear() {
      taskQueue.clear();
   }

   static void runTasks(ModLoadingStage fromStage, Consumer<List<ModLoadingException>> errorHandler, Executor executor) {
      raisedExceptions.clear();
      if (!taskQueue.isEmpty()) {
         LOGGER.info(Logging.LOADING, "Dispatching synchronous work after {}: {} jobs", fromStage, taskQueue.size());
         StopWatch globalTimer = StopWatch.createStarted();
         CompletableFuture<Void> tasks = CompletableFuture.allOf((CompletableFuture[])taskQueue.stream().map((ti) -> {
            return makeRunnable(ti, executor);
         }).toArray((x$0) -> {
            return new CompletableFuture[x$0];
         }));
         tasks.join();
         LOGGER.info(Logging.LOADING, "Synchronous work queue completed in {}", globalTimer);
         errorHandler.accept(raisedExceptions);
      }
   }

   private static CompletableFuture<?> makeRunnable(DeferredWorkQueue.TaskInfo ti, Executor executor) {
      return CompletableFuture.runAsync(() -> {
         Stopwatch timer = Stopwatch.createStarted();
         ti.task.run();
         timer.stop();
         if (timer.elapsed(TimeUnit.SECONDS) >= 1L) {
            LOGGER.warn(Logging.LOADING, "Mod '{}' took {} to run a deferred task.", ti.owner.getModId(), timer);
         }

      }, executor);
   }

   @FunctionalInterface
   public interface CheckedRunnable {
      void run() throws Exception;
   }

   private static class TaskInfo {
      public final IModInfo owner;
      public final Runnable task;

      TaskInfo(IModInfo owner, Runnable task) {
         this.owner = owner;
         this.task = task;
      }
   }
}
