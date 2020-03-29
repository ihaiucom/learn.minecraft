package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IDoubleListMerger {
   DoubleList func_212435_a();

   boolean forMergedIndexes(IDoubleListMerger.IConsumer var1);

   public interface IConsumer {
      boolean merge(int var1, int var2, int var3);
   }
}
