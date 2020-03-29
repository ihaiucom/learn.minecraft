package net.minecraft.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ITaskExecutor<Msg> extends AutoCloseable {
   String getName();

   void enqueue(Msg var1);

   default void close() {
   }

   default <Source> CompletableFuture<Source> func_213141_a(Function<? super ITaskExecutor<Source>, ? extends Msg> p_213141_1_) {
      CompletableFuture<Source> lvt_2_1_ = new CompletableFuture();
      lvt_2_1_.getClass();
      Msg lvt_3_1_ = p_213141_1_.apply(inline("ask future procesor handle", lvt_2_1_::complete));
      this.enqueue(lvt_3_1_);
      return lvt_2_1_;
   }

   static <Msg> ITaskExecutor<Msg> inline(final String p_213140_0_, final Consumer<Msg> p_213140_1_) {
      return new ITaskExecutor<Msg>() {
         public String getName() {
            return p_213140_0_;
         }

         public void enqueue(Msg p_212871_1_) {
            p_213140_1_.accept(p_212871_1_);
         }

         public String toString() {
            return p_213140_0_;
         }
      };
   }
}
