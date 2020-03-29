package net.minecraft.entity.passive.horse;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity extends AbstractChestedHorseEntity {
   public DonkeyEntity(EntityType<? extends DonkeyEntity> p_i50239_1_, World p_i50239_2_) {
      super(p_i50239_1_, p_i50239_2_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_DONKEY_HURT;
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof DonkeyEntity) && !(p_70878_1_ instanceof HorseEntity)) {
         return false;
      } else {
         return this.canMate() && ((AbstractHorseEntity)p_70878_1_).canMate();
      }
   }

   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      EntityType<? extends AbstractHorseEntity> lvt_2_1_ = p_90011_1_ instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorseEntity lvt_3_1_ = (AbstractHorseEntity)lvt_2_1_.create(this.world);
      this.setOffspringAttributes(p_90011_1_, lvt_3_1_);
      return lvt_3_1_;
   }
}
