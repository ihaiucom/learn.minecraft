package com.mojang.realmsclient.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ListButton {
   public final int field_225125_a;
   public final int field_225126_b;
   public final int field_225127_c;
   public final int field_225128_d;

   public ListButton(int p_i51779_1_, int p_i51779_2_, int p_i51779_3_, int p_i51779_4_) {
      this.field_225125_a = p_i51779_1_;
      this.field_225126_b = p_i51779_2_;
      this.field_225127_c = p_i51779_3_;
      this.field_225128_d = p_i51779_4_;
   }

   public void func_225118_a(int p_225118_1_, int p_225118_2_, int p_225118_3_, int p_225118_4_) {
      int lvt_5_1_ = p_225118_1_ + this.field_225127_c;
      int lvt_6_1_ = p_225118_2_ + this.field_225128_d;
      boolean lvt_7_1_ = false;
      if (p_225118_3_ >= lvt_5_1_ && p_225118_3_ <= lvt_5_1_ + this.field_225125_a && p_225118_4_ >= lvt_6_1_ && p_225118_4_ <= lvt_6_1_ + this.field_225126_b) {
         lvt_7_1_ = true;
      }

      this.func_225120_a(lvt_5_1_, lvt_6_1_, lvt_7_1_);
   }

   protected abstract void func_225120_a(int var1, int var2, boolean var3);

   public int func_225122_a() {
      return this.field_225127_c + this.field_225125_a;
   }

   public int func_225123_b() {
      return this.field_225128_d + this.field_225126_b;
   }

   public abstract void func_225121_a(int var1);

   public static void func_225124_a(List<ListButton> p_225124_0_, RealmsObjectSelectionList p_225124_1_, int p_225124_2_, int p_225124_3_, int p_225124_4_, int p_225124_5_) {
      Iterator var6 = p_225124_0_.iterator();

      while(var6.hasNext()) {
         ListButton lvt_7_1_ = (ListButton)var6.next();
         if (p_225124_1_.getRowWidth() > lvt_7_1_.func_225122_a()) {
            lvt_7_1_.func_225118_a(p_225124_2_, p_225124_3_, p_225124_4_, p_225124_5_);
         }
      }

   }

   public static void func_225119_a(RealmsObjectSelectionList p_225119_0_, RealmListEntry p_225119_1_, List<ListButton> p_225119_2_, int p_225119_3_, double p_225119_4_, double p_225119_6_) {
      if (p_225119_3_ == 0) {
         int lvt_8_1_ = p_225119_0_.children().indexOf(p_225119_1_);
         if (lvt_8_1_ > -1) {
            p_225119_0_.selectItem(lvt_8_1_);
            int lvt_9_1_ = p_225119_0_.getRowLeft();
            int lvt_10_1_ = p_225119_0_.getRowTop(lvt_8_1_);
            int lvt_11_1_ = (int)(p_225119_4_ - (double)lvt_9_1_);
            int lvt_12_1_ = (int)(p_225119_6_ - (double)lvt_10_1_);
            Iterator var13 = p_225119_2_.iterator();

            while(var13.hasNext()) {
               ListButton lvt_14_1_ = (ListButton)var13.next();
               if (lvt_11_1_ >= lvt_14_1_.field_225127_c && lvt_11_1_ <= lvt_14_1_.func_225122_a() && lvt_12_1_ >= lvt_14_1_.field_225128_d && lvt_12_1_ <= lvt_14_1_.func_225123_b()) {
                  lvt_14_1_.func_225121_a(lvt_8_1_);
               }
            }
         }
      }

   }
}
