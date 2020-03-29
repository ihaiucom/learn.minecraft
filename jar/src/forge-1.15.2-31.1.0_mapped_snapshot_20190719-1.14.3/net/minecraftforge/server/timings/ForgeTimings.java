package net.minecraftforge.server.timings;

import java.lang.ref.WeakReference;

public class ForgeTimings<T> {
   private WeakReference<T> object;
   private int[] rawTimingData;

   public ForgeTimings(T object, int[] rawTimingData) {
      this.object = new WeakReference(object);
      this.rawTimingData = rawTimingData;
   }

   public WeakReference<T> getObject() {
      return this.object;
   }

   public double getAverageTimings() {
      double sum = 0.0D;
      int[] var3 = this.rawTimingData;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int data = var3[var5];
         sum += (double)data;
      }

      return sum / (double)this.rawTimingData.length;
   }
}
