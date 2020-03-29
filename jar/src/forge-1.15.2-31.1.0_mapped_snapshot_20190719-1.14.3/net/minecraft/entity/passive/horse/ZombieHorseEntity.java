package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ZombieHorseEntity extends AbstractHorseEntity {
   public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> p_i50233_1_, World p_i50233_2_) {
      super(p_i50233_1_, p_i50233_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return (AgeableEntity)EntityType.ZOMBIE_HORSE.create(this.world);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (lvt_3_1_.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (!this.isTame()) {
         return false;
      } else if (this.isChild()) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (p_184645_1_.func_226563_dT_()) {
         this.openGUI(p_184645_1_);
         return true;
      } else if (this.isBeingRidden()) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else {
         if (!lvt_3_1_.isEmpty()) {
            if (!this.isHorseSaddled() && lvt_3_1_.getItem() == Items.SADDLE) {
               this.openGUI(p_184645_1_);
               return true;
            }

            if (lvt_3_1_.interactWithEntity(p_184645_1_, this, p_184645_2_)) {
               return true;
            }
         }

         this.mountTo(p_184645_1_);
         return true;
      }
   }

   protected void initExtraAI() {
   }
}
