package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class ItemStackHelper {
   public static ItemStack getAndSplit(List<ItemStack> p_188382_0_, int p_188382_1_, int p_188382_2_) {
      return p_188382_1_ >= 0 && p_188382_1_ < p_188382_0_.size() && !((ItemStack)p_188382_0_.get(p_188382_1_)).isEmpty() && p_188382_2_ > 0 ? ((ItemStack)p_188382_0_.get(p_188382_1_)).split(p_188382_2_) : ItemStack.EMPTY;
   }

   public static ItemStack getAndRemove(List<ItemStack> p_188383_0_, int p_188383_1_) {
      return p_188383_1_ >= 0 && p_188383_1_ < p_188383_0_.size() ? (ItemStack)p_188383_0_.set(p_188383_1_, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static CompoundNBT saveAllItems(CompoundNBT p_191282_0_, NonNullList<ItemStack> p_191282_1_) {
      return saveAllItems(p_191282_0_, p_191282_1_, true);
   }

   public static CompoundNBT saveAllItems(CompoundNBT p_191281_0_, NonNullList<ItemStack> p_191281_1_, boolean p_191281_2_) {
      ListNBT lvt_3_1_ = new ListNBT();

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_191281_1_.size(); ++lvt_4_1_) {
         ItemStack lvt_5_1_ = (ItemStack)p_191281_1_.get(lvt_4_1_);
         if (!lvt_5_1_.isEmpty()) {
            CompoundNBT lvt_6_1_ = new CompoundNBT();
            lvt_6_1_.putByte("Slot", (byte)lvt_4_1_);
            lvt_5_1_.write(lvt_6_1_);
            lvt_3_1_.add(lvt_6_1_);
         }
      }

      if (!lvt_3_1_.isEmpty() || p_191281_2_) {
         p_191281_0_.put("Items", lvt_3_1_);
      }

      return p_191281_0_;
   }

   public static void loadAllItems(CompoundNBT p_191283_0_, NonNullList<ItemStack> p_191283_1_) {
      ListNBT lvt_2_1_ = p_191283_0_.getList("Items", 10);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
         int lvt_5_1_ = lvt_4_1_.getByte("Slot") & 255;
         if (lvt_5_1_ >= 0 && lvt_5_1_ < p_191283_1_.size()) {
            p_191283_1_.set(lvt_5_1_, ItemStack.read(lvt_4_1_));
         }
      }

   }
}
