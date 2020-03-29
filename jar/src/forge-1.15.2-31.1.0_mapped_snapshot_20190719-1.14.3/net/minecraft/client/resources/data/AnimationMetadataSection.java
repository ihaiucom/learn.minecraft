package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final AnimationMetadataSection field_229300_b_ = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      public Pair<Integer, Integer> func_225641_a_(int p_225641_1_, int p_225641_2_) {
         return Pair.of(p_225641_1_, p_225641_2_);
      }
   };
   private final List<AnimationFrame> animationFrames;
   private final int frameWidth;
   private final int frameHeight;
   private final int frameTime;
   private final boolean interpolate;

   public AnimationMetadataSection(List<AnimationFrame> p_i46088_1_, int p_i46088_2_, int p_i46088_3_, int p_i46088_4_, boolean p_i46088_5_) {
      this.animationFrames = p_i46088_1_;
      this.frameWidth = p_i46088_2_;
      this.frameHeight = p_i46088_3_;
      this.frameTime = p_i46088_4_;
      this.interpolate = p_i46088_5_;
   }

   private static boolean func_229303_b_(int p_229303_0_, int p_229303_1_) {
      return p_229303_0_ / p_229303_1_ * p_229303_1_ == p_229303_0_;
   }

   public Pair<Integer, Integer> func_225641_a_(int p_225641_1_, int p_225641_2_) {
      Pair<Integer, Integer> lvt_3_1_ = this.func_229304_c_(p_225641_1_, p_225641_2_);
      int lvt_4_1_ = (Integer)lvt_3_1_.getFirst();
      int lvt_5_1_ = (Integer)lvt_3_1_.getSecond();
      if (func_229303_b_(p_225641_1_, lvt_4_1_) && func_229303_b_(p_225641_2_, lvt_5_1_)) {
         return lvt_3_1_;
      } else {
         throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", p_225641_1_, p_225641_2_, lvt_4_1_, lvt_5_1_));
      }
   }

   private Pair<Integer, Integer> func_229304_c_(int p_229304_1_, int p_229304_2_) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? Pair.of(this.frameWidth, this.frameHeight) : Pair.of(this.frameWidth, p_229304_2_);
      } else if (this.frameHeight != -1) {
         return Pair.of(p_229304_1_, this.frameHeight);
      } else {
         int lvt_3_1_ = Math.min(p_229304_1_, p_229304_2_);
         return Pair.of(lvt_3_1_, lvt_3_1_);
      }
   }

   public int func_229301_a_(int p_229301_1_) {
      return this.frameHeight == -1 ? p_229301_1_ : this.frameHeight;
   }

   public int func_229302_b_(int p_229302_1_) {
      return this.frameWidth == -1 ? p_229302_1_ : this.frameWidth;
   }

   public int getFrameCount() {
      return this.animationFrames.size();
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public boolean isInterpolate() {
      return this.interpolate;
   }

   private AnimationFrame getAnimationFrame(int p_130072_1_) {
      return (AnimationFrame)this.animationFrames.get(p_130072_1_);
   }

   public int getFrameTimeSingle(int p_110472_1_) {
      AnimationFrame lvt_2_1_ = this.getAnimationFrame(p_110472_1_);
      return lvt_2_1_.hasNoTime() ? this.frameTime : lvt_2_1_.getFrameTime();
   }

   public int getFrameIndex(int p_110468_1_) {
      return ((AnimationFrame)this.animationFrames.get(p_110468_1_)).getFrameIndex();
   }

   public Set<Integer> getFrameIndexSet() {
      Set<Integer> lvt_1_1_ = Sets.newHashSet();
      Iterator var2 = this.animationFrames.iterator();

      while(var2.hasNext()) {
         AnimationFrame lvt_3_1_ = (AnimationFrame)var2.next();
         lvt_1_1_.add(lvt_3_1_.getFrameIndex());
      }

      return lvt_1_1_;
   }
}
