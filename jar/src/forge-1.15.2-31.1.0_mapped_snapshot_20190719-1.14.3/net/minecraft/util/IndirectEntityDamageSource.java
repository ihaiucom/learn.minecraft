package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IndirectEntityDamageSource extends EntityDamageSource {
   private final Entity indirectEntity;

   public IndirectEntityDamageSource(String p_i1568_1_, Entity p_i1568_2_, @Nullable Entity p_i1568_3_) {
      super(p_i1568_1_, p_i1568_2_);
      this.indirectEntity = p_i1568_3_;
   }

   @Nullable
   public Entity getImmediateSource() {
      return this.damageSourceEntity;
   }

   @Nullable
   public Entity getTrueSource() {
      return this.indirectEntity;
   }

   public ITextComponent getDeathMessage(LivingEntity p_151519_1_) {
      ITextComponent lvt_2_1_ = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack lvt_3_1_ = this.indirectEntity instanceof LivingEntity ? ((LivingEntity)this.indirectEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String lvt_4_1_ = "death.attack." + this.damageType;
      String lvt_5_1_ = lvt_4_1_ + ".item";
      return !lvt_3_1_.isEmpty() && lvt_3_1_.hasDisplayName() ? new TranslationTextComponent(lvt_5_1_, new Object[]{p_151519_1_.getDisplayName(), lvt_2_1_, lvt_3_1_.getTextComponent()}) : new TranslationTextComponent(lvt_4_1_, new Object[]{p_151519_1_.getDisplayName(), lvt_2_1_});
   }
}
