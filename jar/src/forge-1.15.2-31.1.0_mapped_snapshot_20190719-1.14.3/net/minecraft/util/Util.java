package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
   private static final ExecutorService SERVER_EXECUTOR = createServerExecutor();
   public static LongSupplier nanoTimeSupplier = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMapCollector() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getValueName(IProperty<T> p_200269_0_, Object p_200269_1_) {
      return p_200269_0_.getName((Comparable)p_200269_1_);
   }

   public static String makeTranslationKey(String p_200697_0_, @Nullable ResourceLocation p_200697_1_) {
      return p_200697_1_ == null ? p_200697_0_ + ".unregistered_sadface" : p_200697_0_ + '.' + p_200697_1_.getNamespace() + '.' + p_200697_1_.getPath().replace('/', '.');
   }

   public static long milliTime() {
      return nanoTime() / 1000000L;
   }

   public static long nanoTime() {
      return nanoTimeSupplier.getAsLong();
   }

   public static long millisecondsSinceEpoch() {
      return Instant.now().toEpochMilli();
   }

   private static ExecutorService createServerExecutor() {
      int lvt_0_1_ = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      Object lvt_1_2_;
      if (lvt_0_1_ <= 0) {
         lvt_1_2_ = MoreExecutors.newDirectExecutorService();
      } else {
         lvt_1_2_ = new ForkJoinPool(lvt_0_1_, (p_215073_0_) -> {
            ForkJoinWorkerThread lvt_1_1_ = new ForkJoinWorkerThread(p_215073_0_) {
               protected void onTermination(Throwable p_onTermination_1_) {
                  if (p_onTermination_1_ != null) {
                     Util.LOGGER.warn("{} died", this.getName(), p_onTermination_1_);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(p_onTermination_1_);
               }
            };
            lvt_1_1_.setName("Server-Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            return lvt_1_1_;
         }, (p_215086_0_, p_215086_1_) -> {
            func_229757_c_(p_215086_1_);
            if (p_215086_1_ instanceof CompletionException) {
               p_215086_1_ = p_215086_1_.getCause();
            }

            if (p_215086_1_ instanceof ReportedException) {
               Bootstrap.printToSYSOUT(((ReportedException)p_215086_1_).getCrashReport().getCompleteReport());
               System.exit(-1);
            }

            LOGGER.error(String.format("Caught exception in thread %s", p_215086_0_), p_215086_1_);
         }, true);
      }

      return (ExecutorService)lvt_1_2_;
   }

   public static Executor getServerExecutor() {
      return SERVER_EXECUTOR;
   }

   public static void shutdownServerExecutor() {
      SERVER_EXECUTOR.shutdown();

      boolean lvt_0_2_;
      try {
         lvt_0_2_ = SERVER_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         lvt_0_2_ = false;
      }

      if (!lvt_0_2_) {
         SERVER_EXECUTOR.shutdownNow();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static <T> CompletableFuture<T> completedExceptionallyFuture(Throwable p_215087_0_) {
      CompletableFuture<T> lvt_1_1_ = new CompletableFuture();
      lvt_1_1_.completeExceptionally(p_215087_0_);
      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static void func_229756_b_(Throwable p_229756_0_) {
      throw p_229756_0_ instanceof RuntimeException ? (RuntimeException)p_229756_0_ : new RuntimeException(p_229756_0_);
   }

   public static Util.OS getOSType() {
      String lvt_0_1_ = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (lvt_0_1_.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (lvt_0_1_.contains("mac")) {
         return Util.OS.OSX;
      } else if (lvt_0_1_.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (lvt_0_1_.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (lvt_0_1_.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return lvt_0_1_.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getJvmFlags() {
      RuntimeMXBean lvt_0_1_ = ManagementFactory.getRuntimeMXBean();
      return lvt_0_1_.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static <T> T func_223378_a(List<T> p_223378_0_) {
      return p_223378_0_.get(p_223378_0_.size() - 1);
   }

   public static <T> T getElementAfter(Iterable<T> p_195647_0_, @Nullable T p_195647_1_) {
      Iterator<T> lvt_2_1_ = p_195647_0_.iterator();
      T lvt_3_1_ = lvt_2_1_.next();
      if (p_195647_1_ != null) {
         Object lvt_4_1_ = lvt_3_1_;

         while(lvt_4_1_ != p_195647_1_) {
            if (lvt_2_1_.hasNext()) {
               lvt_4_1_ = lvt_2_1_.next();
            }
         }

         if (lvt_2_1_.hasNext()) {
            return lvt_2_1_.next();
         }
      }

      return lvt_3_1_;
   }

   public static <T> T getElementBefore(Iterable<T> p_195648_0_, @Nullable T p_195648_1_) {
      Iterator<T> lvt_2_1_ = p_195648_0_.iterator();

      Object lvt_3_1_;
      Object lvt_4_1_;
      for(lvt_3_1_ = null; lvt_2_1_.hasNext(); lvt_3_1_ = lvt_4_1_) {
         lvt_4_1_ = lvt_2_1_.next();
         if (lvt_4_1_ == p_195648_1_) {
            if (lvt_3_1_ == null) {
               lvt_3_1_ = lvt_2_1_.hasNext() ? Iterators.getLast(lvt_2_1_) : p_195648_1_;
            }
            break;
         }
      }

      return lvt_3_1_;
   }

   public static <T> T make(Supplier<T> p_199748_0_) {
      return p_199748_0_.get();
   }

   public static <T> T make(T p_200696_0_, Consumer<T> p_200696_1_) {
      p_200696_1_.accept(p_200696_0_);
      return p_200696_0_;
   }

   public static <K> Strategy<K> identityHashStrategy() {
      return Util.IdentityStrategy.INSTANCE;
   }

   public static <V> CompletableFuture<List<V>> gather(List<? extends CompletableFuture<? extends V>> p_215079_0_) {
      List<V> lvt_1_1_ = Lists.newArrayListWithCapacity(p_215079_0_.size());
      CompletableFuture<?>[] lvt_2_1_ = new CompletableFuture[p_215079_0_.size()];
      CompletableFuture<Void> lvt_3_1_ = new CompletableFuture();
      p_215079_0_.forEach((p_215083_3_) -> {
         int lvt_4_1_ = lvt_1_1_.size();
         lvt_1_1_.add((Object)null);
         lvt_2_1_[lvt_4_1_] = p_215083_3_.whenComplete((p_215085_3_, p_215085_4_) -> {
            if (p_215085_4_ != null) {
               lvt_3_1_.completeExceptionally(p_215085_4_);
            } else {
               lvt_1_1_.set(lvt_4_1_, p_215085_3_);
            }

         });
      });
      return CompletableFuture.allOf(lvt_2_1_).applyToEither(lvt_3_1_, (p_215089_1_) -> {
         return lvt_1_1_;
      });
   }

   public static <T> Stream<T> streamOptional(Optional<? extends T> p_215081_0_) {
      return (Stream)DataFixUtils.orElseGet(p_215081_0_.map(Stream::of), Stream::empty);
   }

   public static <T> Optional<T> acceptOrElse(Optional<T> p_215077_0_, Consumer<T> p_215077_1_, Runnable p_215077_2_) {
      if (p_215077_0_.isPresent()) {
         p_215077_1_.accept(p_215077_0_.get());
      } else {
         p_215077_2_.run();
      }

      return p_215077_0_;
   }

   public static Runnable namedRunnable(Runnable p_215075_0_, Supplier<String> p_215075_1_) {
      return p_215075_0_;
   }

   public static Optional<UUID> readUUID(String p_215074_0_, Dynamic<?> p_215074_1_) {
      return p_215074_1_.get(p_215074_0_ + "Most").asNumber().flatMap((p_215076_2_) -> {
         return p_215074_1_.get(p_215074_0_ + "Least").asNumber().map((p_215080_1_) -> {
            return new UUID(p_215076_2_.longValue(), p_215080_1_.longValue());
         });
      });
   }

   public static <T> Dynamic<T> writeUUID(String p_215084_0_, UUID p_215084_1_, Dynamic<T> p_215084_2_) {
      return p_215084_2_.set(p_215084_0_ + "Most", p_215084_2_.createLong(p_215084_1_.getMostSignificantBits())).set(p_215084_0_ + "Least", p_215084_2_.createLong(p_215084_1_.getLeastSignificantBits()));
   }

   public static <T extends Throwable> T func_229757_c_(T p_229757_0_) {
      if (SharedConstants.developmentMode) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", p_229757_0_);

         while(true) {
            try {
               Thread.sleep(1000L);
               LOGGER.error("paused");
            } catch (InterruptedException var2) {
               return p_229757_0_;
            }
         }
      } else {
         return p_229757_0_;
      }
   }

   public static String func_229758_d_(Throwable p_229758_0_) {
      if (p_229758_0_.getCause() != null) {
         return func_229758_d_(p_229758_0_.getCause());
      } else {
         return p_229758_0_.getMessage() != null ? p_229758_0_.getMessage() : p_229758_0_.toString();
      }
   }

   static enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      public int hashCode(Object p_hashCode_1_) {
         return System.identityHashCode(p_hashCode_1_);
      }

      public boolean equals(Object p_equals_1_, Object p_equals_2_) {
         return p_equals_1_ == p_equals_2_;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL p_195643_1_) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", p_195643_1_.toString()};
         }
      },
      OSX {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenCommandLine(URL p_195643_1_) {
            return new String[]{"open", p_195643_1_.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      @OnlyIn(Dist.CLIENT)
      public void openURL(URL p_195639_1_) {
         try {
            Process lvt_2_1_ = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.getOpenCommandLine(p_195639_1_));
            });
            Iterator var3 = IOUtils.readLines(lvt_2_1_.getErrorStream()).iterator();

            while(var3.hasNext()) {
               String lvt_4_1_ = (String)var3.next();
               Util.LOGGER.error(lvt_4_1_);
            }

            lvt_2_1_.getInputStream().close();
            lvt_2_1_.getErrorStream().close();
            lvt_2_1_.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var5) {
            Util.LOGGER.error("Couldn't open url '{}'", p_195639_1_, var5);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(URI p_195642_1_) {
         try {
            this.openURL(p_195642_1_.toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195642_1_, var3);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openFile(File p_195641_1_) {
         try {
            this.openURL(p_195641_1_.toURI().toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn't open file '{}'", p_195641_1_, var3);
         }

      }

      @OnlyIn(Dist.CLIENT)
      protected String[] getOpenCommandLine(URL p_195643_1_) {
         String lvt_2_1_ = p_195643_1_.toString();
         if ("file".equals(p_195643_1_.getProtocol())) {
            lvt_2_1_ = lvt_2_1_.replace("file:", "file://");
         }

         return new String[]{"xdg-open", lvt_2_1_};
      }

      @OnlyIn(Dist.CLIENT)
      public void openURI(String p_195640_1_) {
         try {
            this.openURL((new URI(p_195640_1_)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195640_1_, var3);
         }

      }

      // $FF: synthetic method
      OS(Object p_i47979_3_) {
         this();
      }
   }
}
