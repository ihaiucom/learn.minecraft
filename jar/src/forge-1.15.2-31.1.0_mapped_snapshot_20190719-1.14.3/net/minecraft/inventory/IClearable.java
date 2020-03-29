package net.minecraft.inventory;

import javax.annotation.Nullable;

public interface IClearable {
   void clear();

   static void clearObj(@Nullable Object p_213131_0_) {
      if (p_213131_0_ instanceof IClearable) {
         ((IClearable)p_213131_0_).clear();
      }

   }
}
