package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAttributeInstance {
   IAttribute getAttribute();

   double getBaseValue();

   void setBaseValue(double var1);

   Set<AttributeModifier> func_225504_a_(AttributeModifier.Operation var1);

   Set<AttributeModifier> func_225505_c_();

   boolean hasModifier(AttributeModifier var1);

   @Nullable
   AttributeModifier getModifier(UUID var1);

   void applyModifier(AttributeModifier var1);

   void removeModifier(AttributeModifier var1);

   void removeModifier(UUID var1);

   @OnlyIn(Dist.CLIENT)
   void removeAllModifiers();

   double getValue();

   @OnlyIn(Dist.CLIENT)
   default void func_226302_a_(IAttributeInstance p_226302_1_) {
      this.setBaseValue(p_226302_1_.getBaseValue());
      Set<AttributeModifier> lvt_2_1_ = p_226302_1_.func_225505_c_();
      Set<AttributeModifier> lvt_3_1_ = this.func_225505_c_();
      ImmutableSet<AttributeModifier> lvt_4_1_ = ImmutableSet.copyOf(Sets.difference(lvt_2_1_, lvt_3_1_));
      ImmutableSet<AttributeModifier> lvt_5_1_ = ImmutableSet.copyOf(Sets.difference(lvt_3_1_, lvt_2_1_));
      lvt_4_1_.forEach(this::applyModifier);
      lvt_5_1_.forEach(this::removeModifier);
   }
}
