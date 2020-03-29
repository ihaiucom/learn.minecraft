package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
   private final ResourceLocation defaultValueKey;
   private T defaultValue;

   public DefaultedRegistry(String p_i50797_1_) {
      this.defaultValueKey = new ResourceLocation(p_i50797_1_);
   }

   public <V extends T> V register(int p_218382_1_, ResourceLocation p_218382_2_, V p_218382_3_) {
      if (this.defaultValueKey.equals(p_218382_2_)) {
         this.defaultValue = p_218382_3_;
      }

      return super.register(p_218382_1_, p_218382_2_, p_218382_3_);
   }

   public int getId(@Nullable T p_148757_1_) {
      int lvt_2_1_ = super.getId(p_148757_1_);
      return lvt_2_1_ == -1 ? super.getId(this.defaultValue) : lvt_2_1_;
   }

   @Nonnull
   public ResourceLocation getKey(T p_177774_1_) {
      ResourceLocation lvt_2_1_ = super.getKey(p_177774_1_);
      return lvt_2_1_ == null ? this.defaultValueKey : lvt_2_1_;
   }

   @Nonnull
   public T getOrDefault(@Nullable ResourceLocation p_82594_1_) {
      T lvt_2_1_ = super.getOrDefault(p_82594_1_);
      return lvt_2_1_ == null ? this.defaultValue : lvt_2_1_;
   }

   @Nonnull
   public T getByValue(int p_148745_1_) {
      T lvt_2_1_ = super.getByValue(p_148745_1_);
      return lvt_2_1_ == null ? this.defaultValue : lvt_2_1_;
   }

   @Nonnull
   public T getRandom(Random p_186801_1_) {
      T lvt_2_1_ = super.getRandom(p_186801_1_);
      return lvt_2_1_ == null ? this.defaultValue : lvt_2_1_;
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultValueKey;
   }
}
