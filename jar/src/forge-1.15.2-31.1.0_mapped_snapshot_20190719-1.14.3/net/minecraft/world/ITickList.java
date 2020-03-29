package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public interface ITickList<T> {
   boolean isTickScheduled(BlockPos var1, T var2);

   default void scheduleTick(BlockPos p_205360_1_, T p_205360_2_, int p_205360_3_) {
      this.scheduleTick(p_205360_1_, p_205360_2_, p_205360_3_, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4);

   boolean isTickPending(BlockPos var1, T var2);

   void func_219497_a(Stream<NextTickListEntry<T>> var1);
}
