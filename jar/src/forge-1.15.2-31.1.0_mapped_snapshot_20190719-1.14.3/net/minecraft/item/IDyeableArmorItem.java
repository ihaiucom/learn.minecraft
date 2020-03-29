package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;

public interface IDyeableArmorItem {
   default boolean hasColor(ItemStack p_200883_1_) {
      CompoundNBT lvt_2_1_ = p_200883_1_.getChildTag("display");
      return lvt_2_1_ != null && lvt_2_1_.contains("color", 99);
   }

   default int getColor(ItemStack p_200886_1_) {
      CompoundNBT lvt_2_1_ = p_200886_1_.getChildTag("display");
      return lvt_2_1_ != null && lvt_2_1_.contains("color", 99) ? lvt_2_1_.getInt("color") : 10511680;
   }

   default void removeColor(ItemStack p_200884_1_) {
      CompoundNBT lvt_2_1_ = p_200884_1_.getChildTag("display");
      if (lvt_2_1_ != null && lvt_2_1_.contains("color")) {
         lvt_2_1_.remove("color");
      }

   }

   default void setColor(ItemStack p_200885_1_, int p_200885_2_) {
      p_200885_1_.getOrCreateChildTag("display").putInt("color", p_200885_2_);
   }

   static ItemStack func_219975_a(ItemStack p_219975_0_, List<DyeItem> p_219975_1_) {
      ItemStack lvt_2_1_ = ItemStack.EMPTY;
      int[] lvt_3_1_ = new int[3];
      int lvt_4_1_ = 0;
      int lvt_5_1_ = 0;
      IDyeableArmorItem lvt_6_1_ = null;
      Item lvt_7_1_ = p_219975_0_.getItem();
      int lvt_8_2_;
      float lvt_11_3_;
      int lvt_13_1_;
      if (lvt_7_1_ instanceof IDyeableArmorItem) {
         lvt_6_1_ = (IDyeableArmorItem)lvt_7_1_;
         lvt_2_1_ = p_219975_0_.copy();
         lvt_2_1_.setCount(1);
         if (lvt_6_1_.hasColor(p_219975_0_)) {
            lvt_8_2_ = lvt_6_1_.getColor(lvt_2_1_);
            float lvt_9_1_ = (float)(lvt_8_2_ >> 16 & 255) / 255.0F;
            float lvt_10_1_ = (float)(lvt_8_2_ >> 8 & 255) / 255.0F;
            lvt_11_3_ = (float)(lvt_8_2_ & 255) / 255.0F;
            lvt_4_1_ = (int)((float)lvt_4_1_ + Math.max(lvt_9_1_, Math.max(lvt_10_1_, lvt_11_3_)) * 255.0F);
            lvt_3_1_[0] = (int)((float)lvt_3_1_[0] + lvt_9_1_ * 255.0F);
            lvt_3_1_[1] = (int)((float)lvt_3_1_[1] + lvt_10_1_ * 255.0F);
            lvt_3_1_[2] = (int)((float)lvt_3_1_[2] + lvt_11_3_ * 255.0F);
            ++lvt_5_1_;
         }

         for(Iterator var14 = p_219975_1_.iterator(); var14.hasNext(); ++lvt_5_1_) {
            DyeItem lvt_9_2_ = (DyeItem)var14.next();
            float[] lvt_10_2_ = lvt_9_2_.getDyeColor().getColorComponentValues();
            int lvt_11_2_ = (int)(lvt_10_2_[0] * 255.0F);
            int lvt_12_1_ = (int)(lvt_10_2_[1] * 255.0F);
            lvt_13_1_ = (int)(lvt_10_2_[2] * 255.0F);
            lvt_4_1_ += Math.max(lvt_11_2_, Math.max(lvt_12_1_, lvt_13_1_));
            lvt_3_1_[0] += lvt_11_2_;
            lvt_3_1_[1] += lvt_12_1_;
            lvt_3_1_[2] += lvt_13_1_;
         }
      }

      if (lvt_6_1_ == null) {
         return ItemStack.EMPTY;
      } else {
         lvt_8_2_ = lvt_3_1_[0] / lvt_5_1_;
         int lvt_9_3_ = lvt_3_1_[1] / lvt_5_1_;
         int lvt_10_3_ = lvt_3_1_[2] / lvt_5_1_;
         lvt_11_3_ = (float)lvt_4_1_ / (float)lvt_5_1_;
         float lvt_12_2_ = (float)Math.max(lvt_8_2_, Math.max(lvt_9_3_, lvt_10_3_));
         lvt_8_2_ = (int)((float)lvt_8_2_ * lvt_11_3_ / lvt_12_2_);
         lvt_9_3_ = (int)((float)lvt_9_3_ * lvt_11_3_ / lvt_12_2_);
         lvt_10_3_ = (int)((float)lvt_10_3_ * lvt_11_3_ / lvt_12_2_);
         lvt_13_1_ = (lvt_8_2_ << 8) + lvt_9_3_;
         lvt_13_1_ = (lvt_13_1_ << 8) + lvt_10_3_;
         lvt_6_1_.setColor(lvt_2_1_, lvt_13_1_);
         return lvt_2_1_;
      }
   }
}
