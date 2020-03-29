package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeEffectInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Comparable<EffectInstance>, IForgeEffectInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Effect potion;
   private int duration;
   private int amplifier;
   private boolean isSplashPotion;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private boolean showIcon;
   @Nullable
   private EffectInstance field_230115_j_;
   private List<ItemStack> curativeItems;

   public EffectInstance(Effect p_i46811_1_) {
      this(p_i46811_1_, 0, 0);
   }

   public EffectInstance(Effect p_i46812_1_, int p_i46812_2_) {
      this(p_i46812_1_, p_i46812_2_, 0);
   }

   public EffectInstance(Effect p_i46813_1_, int p_i46813_2_, int p_i46813_3_) {
      this(p_i46813_1_, p_i46813_2_, p_i46813_3_, false, true);
   }

   public EffectInstance(Effect p_i46814_1_, int p_i46814_2_, int p_i46814_3_, boolean p_i46814_4_, boolean p_i46814_5_) {
      this(p_i46814_1_, p_i46814_2_, p_i46814_3_, p_i46814_4_, p_i46814_5_, p_i46814_5_);
   }

   public EffectInstance(Effect p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this(p_i48980_1_, p_i48980_2_, p_i48980_3_, p_i48980_4_, p_i48980_5_, p_i48980_6_, (EffectInstance)null);
   }

   public EffectInstance(Effect p_i230050_1_, int p_i230050_2_, int p_i230050_3_, boolean p_i230050_4_, boolean p_i230050_5_, boolean p_i230050_6_, @Nullable EffectInstance p_i230050_7_) {
      this.potion = p_i230050_1_;
      this.duration = p_i230050_2_;
      this.amplifier = p_i230050_3_;
      this.ambient = p_i230050_4_;
      this.showParticles = p_i230050_5_;
      this.showIcon = p_i230050_6_;
      this.field_230115_j_ = p_i230050_7_;
   }

   public EffectInstance(EffectInstance p_i1577_1_) {
      this.potion = p_i1577_1_.potion;
      this.func_230117_a_(p_i1577_1_);
   }

   void func_230117_a_(EffectInstance p_230117_1_) {
      this.duration = p_230117_1_.duration;
      this.amplifier = p_230117_1_.amplifier;
      this.ambient = p_230117_1_.ambient;
      this.showParticles = p_230117_1_.showParticles;
      this.showIcon = p_230117_1_.showIcon;
      this.curativeItems = p_230117_1_.curativeItems == null ? null : new ArrayList(p_230117_1_.curativeItems);
   }

   public boolean combine(EffectInstance p_199308_1_) {
      if (this.potion != p_199308_1_.potion) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_199308_1_.amplifier > this.amplifier) {
         if (p_199308_1_.duration < this.duration) {
            EffectInstance effectinstance = this.field_230115_j_;
            this.field_230115_j_ = new EffectInstance(this);
            this.field_230115_j_.field_230115_j_ = effectinstance;
         }

         this.amplifier = p_199308_1_.amplifier;
         this.duration = p_199308_1_.duration;
         flag = true;
      } else if (p_199308_1_.duration > this.duration) {
         if (p_199308_1_.amplifier == this.amplifier) {
            this.duration = p_199308_1_.duration;
            flag = true;
         } else if (this.field_230115_j_ == null) {
            this.field_230115_j_ = new EffectInstance(p_199308_1_);
         } else {
            this.field_230115_j_.combine(p_199308_1_);
         }
      }

      if (!p_199308_1_.ambient && this.ambient || flag) {
         this.ambient = p_199308_1_.ambient;
         flag = true;
      }

      if (p_199308_1_.showParticles != this.showParticles) {
         this.showParticles = p_199308_1_.showParticles;
         flag = true;
      }

      if (p_199308_1_.showIcon != this.showIcon) {
         this.showIcon = p_199308_1_.showIcon;
         flag = true;
      }

      return flag;
   }

   public Effect getPotion() {
      return this.potion == null ? null : (Effect)this.potion.delegate.get();
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean doesShowParticles() {
      return this.showParticles;
   }

   public boolean isShowIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity p_76455_1_, Runnable p_76455_2_) {
      if (this.duration > 0) {
         if (this.potion.isReady(this.duration, this.amplifier)) {
            this.performEffect(p_76455_1_);
         }

         this.deincrementDuration();
         if (this.duration == 0 && this.field_230115_j_ != null) {
            this.func_230117_a_(this.field_230115_j_);
            this.field_230115_j_ = this.field_230115_j_.field_230115_j_;
            p_76455_2_.run();
         }
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      if (this.field_230115_j_ != null) {
         this.field_230115_j_.deincrementDuration();
      }

      return --this.duration;
   }

   public void performEffect(LivingEntity p_76457_1_) {
      if (this.duration > 0) {
         this.potion.performEffect(p_76457_1_, this.amplifier);
      }

   }

   public String getEffectName() {
      return this.potion.getName();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getEffectName() + ", Duration: " + this.duration;
      }

      if (this.isSplashPotion) {
         s = s + ", Splash: true";
      }

      if (!this.showParticles) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof EffectInstance)) {
         return false;
      } else {
         EffectInstance effectinstance = (EffectInstance)p_equals_1_;
         return this.duration == effectinstance.duration && this.amplifier == effectinstance.amplifier && this.isSplashPotion == effectinstance.isSplashPotion && this.ambient == effectinstance.ambient && this.potion.equals(effectinstance.potion);
      }
   }

   public int hashCode() {
      int i = this.potion.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.isSplashPotion ? 1 : 0);
      i = 31 * i + (this.ambient ? 1 : 0);
      return i;
   }

   public CompoundNBT write(CompoundNBT p_82719_1_) {
      p_82719_1_.putByte("Id", (byte)Effect.getId(this.getPotion()));
      this.func_230119_c_(p_82719_1_);
      return p_82719_1_;
   }

   private void func_230119_c_(CompoundNBT p_230119_1_) {
      p_230119_1_.putByte("Amplifier", (byte)this.getAmplifier());
      p_230119_1_.putInt("Duration", this.getDuration());
      p_230119_1_.putBoolean("Ambient", this.isAmbient());
      p_230119_1_.putBoolean("ShowParticles", this.doesShowParticles());
      p_230119_1_.putBoolean("ShowIcon", this.isShowIcon());
      if (this.field_230115_j_ != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.field_230115_j_.write(compoundnbt);
         p_230119_1_.put("HiddenEffect", compoundnbt);
      }

      this.writeCurativeItems(p_230119_1_);
   }

   public static EffectInstance read(CompoundNBT p_82722_0_) {
      int i = p_82722_0_.getByte("Id") & 255;
      Effect effect = Effect.get(i);
      return effect == null ? null : func_230116_a_(effect, p_82722_0_);
   }

   private static EffectInstance func_230116_a_(Effect p_230116_0_, CompoundNBT p_230116_1_) {
      int i = p_230116_1_.getByte("Amplifier");
      int j = p_230116_1_.getInt("Duration");
      boolean flag = p_230116_1_.getBoolean("Ambient");
      boolean flag1 = true;
      if (p_230116_1_.contains("ShowParticles", 1)) {
         flag1 = p_230116_1_.getBoolean("ShowParticles");
      }

      boolean flag2 = flag1;
      if (p_230116_1_.contains("ShowIcon", 1)) {
         flag2 = p_230116_1_.getBoolean("ShowIcon");
      }

      EffectInstance effectinstance = null;
      if (p_230116_1_.contains("HiddenEffect", 10)) {
         effectinstance = func_230116_a_(p_230116_0_, p_230116_1_.getCompound("HiddenEffect"));
      }

      return readCurativeItems(new EffectInstance(p_230116_0_, j, i < 0 ? 0 : i, flag, flag1, flag2, effectinstance), p_230116_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPotionDurationMax(boolean p_100012_1_) {
      this.isPotionDurationMax = p_100012_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public int compareTo(EffectInstance p_compareTo_1_) {
      int i = true;
      return this.getDuration() > 32147 && p_compareTo_1_.getDuration() > 32147 || this.isAmbient() && p_compareTo_1_.isAmbient() ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result();
   }

   public List<ItemStack> getCurativeItems() {
      if (this.curativeItems == null) {
         this.curativeItems = this.getPotion().getCurativeItems();
      }

      return this.curativeItems;
   }

   public void setCurativeItems(List<ItemStack> p_setCurativeItems_1_) {
      this.curativeItems = p_setCurativeItems_1_;
   }

   private static EffectInstance readCurativeItems(EffectInstance p_readCurativeItems_0_, CompoundNBT p_readCurativeItems_1_) {
      if (p_readCurativeItems_1_.contains("CurativeItems", 9)) {
         List<ItemStack> items = new ArrayList();
         ListNBT list = p_readCurativeItems_1_.getList("CurativeItems", 10);

         for(int i = 0; i < list.size(); ++i) {
            items.add(ItemStack.read(list.getCompound(i)));
         }

         p_readCurativeItems_0_.setCurativeItems(items);
      }

      return p_readCurativeItems_0_;
   }
}
