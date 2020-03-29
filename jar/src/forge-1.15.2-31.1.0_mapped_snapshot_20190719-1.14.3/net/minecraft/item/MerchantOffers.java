package net.minecraft.item;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public MerchantOffers() {
   }

   public MerchantOffers(CompoundNBT p_i50011_1_) {
      ListNBT lvt_2_1_ = p_i50011_1_.getList("Recipes", 10);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         this.add(new MerchantOffer(lvt_2_1_.getCompound(lvt_3_1_)));
      }

   }

   @Nullable
   public MerchantOffer func_222197_a(ItemStack p_222197_1_, ItemStack p_222197_2_, int p_222197_3_) {
      if (p_222197_3_ > 0 && p_222197_3_ < this.size()) {
         MerchantOffer lvt_4_1_ = (MerchantOffer)this.get(p_222197_3_);
         return lvt_4_1_.func_222204_a(p_222197_1_, p_222197_2_) ? lvt_4_1_ : null;
      } else {
         for(int lvt_4_2_ = 0; lvt_4_2_ < this.size(); ++lvt_4_2_) {
            MerchantOffer lvt_5_1_ = (MerchantOffer)this.get(lvt_4_2_);
            if (lvt_5_1_.func_222204_a(p_222197_1_, p_222197_2_)) {
               return lvt_5_1_;
            }
         }

         return null;
      }
   }

   public void func_222196_a(PacketBuffer p_222196_1_) {
      p_222196_1_.writeByte((byte)(this.size() & 255));

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.size(); ++lvt_2_1_) {
         MerchantOffer lvt_3_1_ = (MerchantOffer)this.get(lvt_2_1_);
         p_222196_1_.writeItemStack(lvt_3_1_.func_222218_a());
         p_222196_1_.writeItemStack(lvt_3_1_.func_222200_d());
         ItemStack lvt_4_1_ = lvt_3_1_.func_222202_c();
         p_222196_1_.writeBoolean(!lvt_4_1_.isEmpty());
         if (!lvt_4_1_.isEmpty()) {
            p_222196_1_.writeItemStack(lvt_4_1_);
         }

         p_222196_1_.writeBoolean(lvt_3_1_.func_222217_o());
         p_222196_1_.writeInt(lvt_3_1_.func_222213_g());
         p_222196_1_.writeInt(lvt_3_1_.func_222214_i());
         p_222196_1_.writeInt(lvt_3_1_.func_222210_n());
         p_222196_1_.writeInt(lvt_3_1_.func_222212_l());
         p_222196_1_.writeFloat(lvt_3_1_.func_222211_m());
         p_222196_1_.writeInt(lvt_3_1_.func_225482_k());
      }

   }

   public static MerchantOffers func_222198_b(PacketBuffer p_222198_0_) {
      MerchantOffers lvt_1_1_ = new MerchantOffers();
      int lvt_2_1_ = p_222198_0_.readByte() & 255;

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         ItemStack lvt_4_1_ = p_222198_0_.readItemStack();
         ItemStack lvt_5_1_ = p_222198_0_.readItemStack();
         ItemStack lvt_6_1_ = ItemStack.EMPTY;
         if (p_222198_0_.readBoolean()) {
            lvt_6_1_ = p_222198_0_.readItemStack();
         }

         boolean lvt_7_1_ = p_222198_0_.readBoolean();
         int lvt_8_1_ = p_222198_0_.readInt();
         int lvt_9_1_ = p_222198_0_.readInt();
         int lvt_10_1_ = p_222198_0_.readInt();
         int lvt_11_1_ = p_222198_0_.readInt();
         float lvt_12_1_ = p_222198_0_.readFloat();
         int lvt_13_1_ = p_222198_0_.readInt();
         MerchantOffer lvt_14_1_ = new MerchantOffer(lvt_4_1_, lvt_6_1_, lvt_5_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_, lvt_12_1_, lvt_13_1_);
         if (lvt_7_1_) {
            lvt_14_1_.func_222216_p();
         }

         lvt_14_1_.func_222209_b(lvt_11_1_);
         lvt_1_1_.add(lvt_14_1_);
      }

      return lvt_1_1_;
   }

   public CompoundNBT func_222199_a() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      ListNBT lvt_2_1_ = new ListNBT();

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.size(); ++lvt_3_1_) {
         MerchantOffer lvt_4_1_ = (MerchantOffer)this.get(lvt_3_1_);
         lvt_2_1_.add(lvt_4_1_.func_222208_r());
      }

      lvt_1_1_.put("Recipes", lvt_2_1_);
      return lvt_1_1_;
   }
}
