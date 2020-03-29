package com.mojang.realmsclient.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ValueObject {
   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder("{");
      Field[] var2 = this.getClass().getFields();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Field lvt_5_1_ = var2[var4];
         if (!isStatic(lvt_5_1_)) {
            try {
               lvt_1_1_.append(lvt_5_1_.getName()).append("=").append(lvt_5_1_.get(this)).append(" ");
            } catch (IllegalAccessException var7) {
            }
         }
      }

      lvt_1_1_.deleteCharAt(lvt_1_1_.length() - 1);
      lvt_1_1_.append('}');
      return lvt_1_1_.toString();
   }

   private static boolean isStatic(Field p_isStatic_0_) {
      return Modifier.isStatic(p_isStatic_0_.getModifiers());
   }
}
