package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {
   private static final Logger field_227080_a_ = LogManager.getLogger();
   private final Thread field_227081_b_;
   private final AtomicBoolean field_227082_c_ = new AtomicBoolean();
   private final Queue<Runnable> field_227083_d_ = Queues.newConcurrentLinkedQueue();
   private final RegionFileCache field_227084_e_;
   private final Map<ChunkPos, IOWorker.Entry> field_227085_f_ = Maps.newLinkedHashMap();
   private boolean field_227086_g_ = true;
   private CompletableFuture<Void> field_227087_h_ = new CompletableFuture();

   IOWorker(RegionFileCache p_i225782_1_, String p_i225782_2_) {
      this.field_227084_e_ = p_i225782_1_;
      this.field_227081_b_ = new Thread(this::func_227107_d_);
      this.field_227081_b_.setName(p_i225782_2_ + " IO worker");
      this.field_227081_b_.start();
   }

   public CompletableFuture<Void> func_227093_a_(ChunkPos p_227093_1_, CompoundNBT p_227093_2_) {
      return this.func_227099_a_((p_227094_3_) -> {
         return () -> {
            IOWorker.Entry lvt_4_1_ = (IOWorker.Entry)this.field_227085_f_.computeIfAbsent(p_227093_1_, (p_227101_0_) -> {
               return new IOWorker.Entry();
            });
            lvt_4_1_.field_227113_a_ = p_227093_2_;
            lvt_4_1_.field_227114_b_.whenComplete((p_227098_1_, p_227098_2_) -> {
               if (p_227098_2_ != null) {
                  p_227094_3_.completeExceptionally(p_227098_2_);
               } else {
                  p_227094_3_.complete((Object)null);
               }

            });
         };
      });
   }

   @Nullable
   public CompoundNBT func_227090_a_(ChunkPos p_227090_1_) throws IOException {
      CompletableFuture lvt_2_1_ = this.func_227099_a_((p_227092_2_) -> {
         return () -> {
            IOWorker.Entry lvt_3_1_ = (IOWorker.Entry)this.field_227085_f_.get(p_227090_1_);
            if (lvt_3_1_ != null) {
               p_227092_2_.complete(lvt_3_1_.field_227113_a_);
            } else {
               try {
                  CompoundNBT lvt_4_1_ = this.field_227084_e_.readChunk(p_227090_1_);
                  p_227092_2_.complete(lvt_4_1_);
               } catch (Exception var5) {
                  field_227080_a_.warn("Failed to read chunk {}", p_227090_1_, var5);
                  p_227092_2_.completeExceptionally(var5);
               }
            }

         };
      });

      try {
         return (CompoundNBT)lvt_2_1_.join();
      } catch (CompletionException var4) {
         if (var4.getCause() instanceof IOException) {
            throw (IOException)var4.getCause();
         } else {
            throw var4;
         }
      }
   }

   private CompletableFuture<Void> func_227100_b_() {
      return this.func_227099_a_((p_227106_1_) -> {
         return () -> {
            this.field_227086_g_ = false;
            this.field_227087_h_ = p_227106_1_;
         };
      });
   }

   public CompletableFuture<Void> func_227088_a_() {
      return this.func_227099_a_((p_227096_1_) -> {
         return () -> {
            CompletableFuture<?> lvt_2_1_ = CompletableFuture.allOf((CompletableFuture[])this.field_227085_f_.values().stream().map((p_227095_0_) -> {
               return p_227095_0_.field_227114_b_;
            }).toArray((p_227089_0_) -> {
               return new CompletableFuture[p_227089_0_];
            }));
            lvt_2_1_.whenComplete((p_227097_1_, p_227097_2_) -> {
               p_227096_1_.complete((Object)null);
            });
         };
      });
   }

   private <T> CompletableFuture<T> func_227099_a_(Function<CompletableFuture<T>, Runnable> p_227099_1_) {
      CompletableFuture<T> lvt_2_1_ = new CompletableFuture();
      this.field_227083_d_.add(p_227099_1_.apply(lvt_2_1_));
      LockSupport.unpark(this.field_227081_b_);
      return lvt_2_1_;
   }

   private void func_227105_c_() {
      LockSupport.park("waiting for tasks");
   }

   private void func_227107_d_() {
      try {
         while(this.field_227086_g_) {
            boolean lvt_1_1_ = this.func_227112_h_();
            boolean lvt_2_1_ = this.func_227109_e_();
            if (!lvt_1_1_ && !lvt_2_1_) {
               this.func_227105_c_();
            }
         }

         this.func_227112_h_();
         this.func_227110_f_();
      } finally {
         this.func_227111_g_();
      }

   }

   private boolean func_227109_e_() {
      Iterator<java.util.Map.Entry<ChunkPos, IOWorker.Entry>> lvt_1_1_ = this.field_227085_f_.entrySet().iterator();
      if (!lvt_1_1_.hasNext()) {
         return false;
      } else {
         java.util.Map.Entry<ChunkPos, IOWorker.Entry> lvt_2_1_ = (java.util.Map.Entry)lvt_1_1_.next();
         lvt_1_1_.remove();
         this.func_227091_a_((ChunkPos)lvt_2_1_.getKey(), (IOWorker.Entry)lvt_2_1_.getValue());
         return true;
      }
   }

   private void func_227110_f_() {
      this.field_227085_f_.forEach(this::func_227091_a_);
      this.field_227085_f_.clear();
   }

   private void func_227091_a_(ChunkPos p_227091_1_, IOWorker.Entry p_227091_2_) {
      try {
         this.field_227084_e_.writeChunk(p_227091_1_, p_227091_2_.field_227113_a_);
         p_227091_2_.field_227114_b_.complete((Object)null);
      } catch (Exception var4) {
         field_227080_a_.error("Failed to store chunk {}", p_227091_1_, var4);
         p_227091_2_.field_227114_b_.completeExceptionally(var4);
      }

   }

   private void func_227111_g_() {
      try {
         this.field_227084_e_.close();
         this.field_227087_h_.complete((Object)null);
      } catch (Exception var2) {
         field_227080_a_.error("Failed to close storage", var2);
         this.field_227087_h_.completeExceptionally(var2);
      }

   }

   private boolean func_227112_h_() {
      boolean lvt_1_1_ = false;

      Runnable lvt_2_1_;
      while((lvt_2_1_ = (Runnable)this.field_227083_d_.poll()) != null) {
         lvt_1_1_ = true;
         lvt_2_1_.run();
      }

      return lvt_1_1_;
   }

   public void close() throws IOException {
      if (this.field_227082_c_.compareAndSet(false, true)) {
         try {
            this.func_227100_b_().join();
         } catch (CompletionException var2) {
            if (var2.getCause() instanceof IOException) {
               throw (IOException)var2.getCause();
            } else {
               throw var2;
            }
         }
      }
   }

   static class Entry {
      private CompoundNBT field_227113_a_;
      private final CompletableFuture<Void> field_227114_b_;

      private Entry() {
         this.field_227114_b_ = new CompletableFuture();
      }

      // $FF: synthetic method
      Entry(Object p_i225783_1_) {
         this();
      }
   }
}
