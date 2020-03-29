package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class LootingLevelEvent extends LivingEvent {
   private final DamageSource damageSource;
   private int lootingLevel;

   public LootingLevelEvent(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
      super(entity);
      this.damageSource = damageSource;
      this.lootingLevel = lootingLevel;
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public int getLootingLevel() {
      return this.lootingLevel;
   }

   public void setLootingLevel(int lootingLevel) {
      this.lootingLevel = lootingLevel;
   }
}
