package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.potion.EffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class Food {
   private final int value;
   private final float saturation;
   private final boolean meat;
   private final boolean canEatWhenFull;
   private final boolean fastToEat;
   private final List<Pair<EffectInstance, Float>> effects;

   private Food(int p_i50106_1_, float p_i50106_2_, boolean p_i50106_3_, boolean p_i50106_4_, boolean p_i50106_5_, List<Pair<EffectInstance, Float>> p_i50106_6_) {
      this.value = p_i50106_1_;
      this.saturation = p_i50106_2_;
      this.meat = p_i50106_3_;
      this.canEatWhenFull = p_i50106_4_;
      this.fastToEat = p_i50106_5_;
      this.effects = p_i50106_6_;
   }

   public int getHealing() {
      return this.value;
   }

   public float getSaturation() {
      return this.saturation;
   }

   public boolean isMeat() {
      return this.meat;
   }

   public boolean canEatWhenFull() {
      return this.canEatWhenFull;
   }

   public boolean isFastEating() {
      return this.fastToEat;
   }

   public List<Pair<EffectInstance, Float>> getEffects() {
      return this.effects;
   }

   // $FF: synthetic method
   Food(int p_i50107_1_, float p_i50107_2_, boolean p_i50107_3_, boolean p_i50107_4_, boolean p_i50107_5_, List p_i50107_6_, Object p_i50107_7_) {
      this(p_i50107_1_, p_i50107_2_, p_i50107_3_, p_i50107_4_, p_i50107_5_, p_i50107_6_);
   }

   public static class Builder {
      private int value;
      private float saturation;
      private boolean meat;
      private boolean alwaysEdible;
      private boolean fastToEat;
      private final List<Pair<EffectInstance, Float>> effects = Lists.newArrayList();

      public Food.Builder hunger(int p_221456_1_) {
         this.value = p_221456_1_;
         return this;
      }

      public Food.Builder saturation(float p_221454_1_) {
         this.saturation = p_221454_1_;
         return this;
      }

      public Food.Builder meat() {
         this.meat = true;
         return this;
      }

      public Food.Builder setAlwaysEdible() {
         this.alwaysEdible = true;
         return this;
      }

      public Food.Builder fastToEat() {
         this.fastToEat = true;
         return this;
      }

      public Food.Builder effect(EffectInstance p_221452_1_, float p_221452_2_) {
         this.effects.add(Pair.of(p_221452_1_, p_221452_2_));
         return this;
      }

      public Food build() {
         return new Food(this.value, this.saturation, this.meat, this.alwaysEdible, this.fastToEat, this.effects);
      }
   }
}
