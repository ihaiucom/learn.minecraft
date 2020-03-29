package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDamageSource extends DamageSource {
   @Nullable
   protected final Entity damageSourceEntity;
   private boolean isThornsDamage;

   public EntityDamageSource(String p_i1567_1_, @Nullable Entity p_i1567_2_) {
      super(p_i1567_1_);
      this.damageSourceEntity = p_i1567_2_;
   }

   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   @Nullable
   public Entity getTrueSource() {
      return this.damageSourceEntity;
   }

   public ITextComponent getDeathMessage(LivingEntity p_151519_1_) {
      ItemStack lvt_2_1_ = this.damageSourceEntity instanceof LivingEntity ? ((LivingEntity)this.damageSourceEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String lvt_3_1_ = "death.attack." + this.damageType;
      return !lvt_2_1_.isEmpty() && lvt_2_1_.hasDisplayName() ? new TranslationTextComponent(lvt_3_1_ + ".item", new Object[]{p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName(), lvt_2_1_.getTextComponent()}) : new TranslationTextComponent(lvt_3_1_, new Object[]{p_151519_1_.getDisplayName(), this.damageSourceEntity.getDisplayName()});
   }

   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof LivingEntity && !(this.damageSourceEntity instanceof PlayerEntity);
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return this.damageSourceEntity != null ? this.damageSourceEntity.getPositionVec() : null;
   }
}
