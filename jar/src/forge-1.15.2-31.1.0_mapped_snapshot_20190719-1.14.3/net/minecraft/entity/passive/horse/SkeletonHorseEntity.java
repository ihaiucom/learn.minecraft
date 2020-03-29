package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.TriggerSkeletonTrapGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends AbstractHorseEntity {
   private final TriggerSkeletonTrapGoal skeletonTrapAI = new TriggerSkeletonTrapGoal(this);
   private boolean skeletonTrap;
   private int skeletonTrapTime;

   public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> p_i50235_1_, World p_i50235_2_) {
      super(p_i50235_1_, p_i50235_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   protected void initExtraAI() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.areEyesInFluid(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround) {
         if (!this.isBeingRidden()) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopTime;
         if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
            return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopTime <= 5) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float p_203006_1_) {
      if (this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, p_203006_1_ * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.1875D;
   }

   public void livingTick() {
      super.livingTick();
      if (this.isTrap() && this.skeletonTrapTime++ >= 18000) {
         this.remove();
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("SkeletonTrap", this.isTrap());
      p_213281_1_.putInt("SkeletonTrapTime", this.skeletonTrapTime);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setTrap(p_70037_1_.getBoolean("SkeletonTrap"));
      this.skeletonTrapTime = p_70037_1_.getInt("SkeletonTrapTime");
   }

   public boolean canBeRiddenInWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.skeletonTrap;
   }

   public void setTrap(boolean p_190691_1_) {
      if (p_190691_1_ != this.skeletonTrap) {
         this.skeletonTrap = p_190691_1_;
         if (p_190691_1_) {
            this.goalSelector.addGoal(1, this.skeletonTrapAI);
         } else {
            this.goalSelector.removeGoal(this.skeletonTrapAI);
         }

      }
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return (AgeableEntity)EntityType.SKELETON_HORSE.create(this.world);
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
            if (lvt_3_1_.getItem() == Items.SADDLE && !this.isHorseSaddled()) {
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
}
