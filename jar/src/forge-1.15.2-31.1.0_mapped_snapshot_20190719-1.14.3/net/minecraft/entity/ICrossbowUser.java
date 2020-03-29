package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface ICrossbowUser {
   void setCharging(boolean var1);

   void shoot(LivingEntity var1, ItemStack var2, IProjectile var3, float var4);

   @Nullable
   LivingEntity getAttackTarget();
}
