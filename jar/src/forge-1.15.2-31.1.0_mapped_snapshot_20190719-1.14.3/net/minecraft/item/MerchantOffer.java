package net.minecraft.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.MathHelper;

public class MerchantOffer {
   private final ItemStack buyingStackFirst;
   private final ItemStack buyingStackSecond;
   private final ItemStack sellingStack;
   private int uses;
   private final int maxUses;
   private boolean doesRewardEXP;
   private int specialPrice;
   private int demand;
   private float priceMultiplier;
   private int givenEXP;

   public MerchantOffer(CompoundNBT p_i50012_1_) {
      this.doesRewardEXP = true;
      this.givenEXP = 1;
      this.buyingStackFirst = ItemStack.read(p_i50012_1_.getCompound("buy"));
      this.buyingStackSecond = ItemStack.read(p_i50012_1_.getCompound("buyB"));
      this.sellingStack = ItemStack.read(p_i50012_1_.getCompound("sell"));
      this.uses = p_i50012_1_.getInt("uses");
      if (p_i50012_1_.contains("maxUses", 99)) {
         this.maxUses = p_i50012_1_.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (p_i50012_1_.contains("rewardExp", 1)) {
         this.doesRewardEXP = p_i50012_1_.getBoolean("rewardExp");
      }

      if (p_i50012_1_.contains("xp", 3)) {
         this.givenEXP = p_i50012_1_.getInt("xp");
      }

      if (p_i50012_1_.contains("priceMultiplier", 5)) {
         this.priceMultiplier = p_i50012_1_.getFloat("priceMultiplier");
      }

      this.specialPrice = p_i50012_1_.getInt("specialPrice");
      this.demand = p_i50012_1_.getInt("demand");
   }

   public MerchantOffer(ItemStack p_i50013_1_, ItemStack p_i50013_2_, int p_i50013_3_, int p_i50013_4_, float p_i50013_5_) {
      this(p_i50013_1_, ItemStack.EMPTY, p_i50013_2_, p_i50013_3_, p_i50013_4_, p_i50013_5_);
   }

   public MerchantOffer(ItemStack p_i50014_1_, ItemStack p_i50014_2_, ItemStack p_i50014_3_, int p_i50014_4_, int p_i50014_5_, float p_i50014_6_) {
      this(p_i50014_1_, p_i50014_2_, p_i50014_3_, 0, p_i50014_4_, p_i50014_5_, p_i50014_6_);
   }

   public MerchantOffer(ItemStack p_i50015_1_, ItemStack p_i50015_2_, ItemStack p_i50015_3_, int p_i50015_4_, int p_i50015_5_, int p_i50015_6_, float p_i50015_7_) {
      this(p_i50015_1_, p_i50015_2_, p_i50015_3_, p_i50015_4_, p_i50015_5_, p_i50015_6_, p_i50015_7_, 0);
   }

   public MerchantOffer(ItemStack p_i51550_1_, ItemStack p_i51550_2_, ItemStack p_i51550_3_, int p_i51550_4_, int p_i51550_5_, int p_i51550_6_, float p_i51550_7_, int p_i51550_8_) {
      this.doesRewardEXP = true;
      this.givenEXP = 1;
      this.buyingStackFirst = p_i51550_1_;
      this.buyingStackSecond = p_i51550_2_;
      this.sellingStack = p_i51550_3_;
      this.uses = p_i51550_4_;
      this.maxUses = p_i51550_5_;
      this.givenEXP = p_i51550_6_;
      this.priceMultiplier = p_i51550_7_;
      this.demand = p_i51550_8_;
   }

   public ItemStack func_222218_a() {
      return this.buyingStackFirst;
   }

   public ItemStack func_222205_b() {
      int lvt_1_1_ = this.buyingStackFirst.getCount();
      ItemStack lvt_2_1_ = this.buyingStackFirst.copy();
      int lvt_3_1_ = Math.max(0, MathHelper.floor((float)(lvt_1_1_ * this.demand) * this.priceMultiplier));
      lvt_2_1_.setCount(MathHelper.clamp(lvt_1_1_ + lvt_3_1_ + this.specialPrice, 1, this.buyingStackFirst.getItem().getMaxStackSize()));
      return lvt_2_1_;
   }

   public ItemStack func_222202_c() {
      return this.buyingStackSecond;
   }

   public ItemStack func_222200_d() {
      return this.sellingStack;
   }

   public void func_222222_e() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack func_222206_f() {
      return this.sellingStack.copy();
   }

   public int func_222213_g() {
      return this.uses;
   }

   public void func_222203_h() {
      this.uses = 0;
   }

   public int func_222214_i() {
      return this.maxUses;
   }

   public void func_222219_j() {
      ++this.uses;
   }

   public int func_225482_k() {
      return this.demand;
   }

   public void func_222207_a(int p_222207_1_) {
      this.specialPrice += p_222207_1_;
   }

   public void func_222220_k() {
      this.specialPrice = 0;
   }

   public int func_222212_l() {
      return this.specialPrice;
   }

   public void func_222209_b(int p_222209_1_) {
      this.specialPrice = p_222209_1_;
   }

   public float func_222211_m() {
      return this.priceMultiplier;
   }

   public int func_222210_n() {
      return this.givenEXP;
   }

   public boolean func_222217_o() {
      return this.uses >= this.maxUses;
   }

   public void func_222216_p() {
      this.uses = this.maxUses;
   }

   public boolean func_226654_r_() {
      return this.uses > 0;
   }

   public boolean func_222221_q() {
      return this.doesRewardEXP;
   }

   public CompoundNBT func_222208_r() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.put("buy", this.buyingStackFirst.write(new CompoundNBT()));
      lvt_1_1_.put("sell", this.sellingStack.write(new CompoundNBT()));
      lvt_1_1_.put("buyB", this.buyingStackSecond.write(new CompoundNBT()));
      lvt_1_1_.putInt("uses", this.uses);
      lvt_1_1_.putInt("maxUses", this.maxUses);
      lvt_1_1_.putBoolean("rewardExp", this.doesRewardEXP);
      lvt_1_1_.putInt("xp", this.givenEXP);
      lvt_1_1_.putFloat("priceMultiplier", this.priceMultiplier);
      lvt_1_1_.putInt("specialPrice", this.specialPrice);
      lvt_1_1_.putInt("demand", this.demand);
      return lvt_1_1_;
   }

   public boolean func_222204_a(ItemStack p_222204_1_, ItemStack p_222204_2_) {
      return this.func_222201_c(p_222204_1_, this.func_222205_b()) && p_222204_1_.getCount() >= this.func_222205_b().getCount() && this.func_222201_c(p_222204_2_, this.buyingStackSecond) && p_222204_2_.getCount() >= this.buyingStackSecond.getCount();
   }

   private boolean func_222201_c(ItemStack p_222201_1_, ItemStack p_222201_2_) {
      if (p_222201_2_.isEmpty() && p_222201_1_.isEmpty()) {
         return true;
      } else {
         ItemStack lvt_3_1_ = p_222201_1_.copy();
         if (lvt_3_1_.getItem().isDamageable()) {
            lvt_3_1_.setDamage(lvt_3_1_.getDamage());
         }

         return ItemStack.areItemsEqual(lvt_3_1_, p_222201_2_) && (!p_222201_2_.hasTag() || lvt_3_1_.hasTag() && NBTUtil.areNBTEquals(p_222201_2_.getTag(), lvt_3_1_.getTag(), false));
      }
   }

   public boolean func_222215_b(ItemStack p_222215_1_, ItemStack p_222215_2_) {
      if (!this.func_222204_a(p_222215_1_, p_222215_2_)) {
         return false;
      } else {
         p_222215_1_.shrink(this.func_222205_b().getCount());
         if (!this.func_222202_c().isEmpty()) {
            p_222215_2_.shrink(this.func_222202_c().getCount());
         }

         return true;
      }
   }
}
