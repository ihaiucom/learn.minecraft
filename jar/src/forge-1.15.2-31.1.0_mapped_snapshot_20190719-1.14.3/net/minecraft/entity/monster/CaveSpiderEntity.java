package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
   public CaveSpiderEntity(EntityType<? extends CaveSpiderEntity> p_i50214_1_, World p_i50214_2_) {
      super(p_i50214_1_, p_i50214_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (super.attackEntityAsMob(p_70652_1_)) {
         if (p_70652_1_ instanceof LivingEntity) {
            int lvt_2_1_ = 0;
            if (this.world.getDifficulty() == Difficulty.NORMAL) {
               lvt_2_1_ = 7;
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
               lvt_2_1_ = 15;
            }

            if (lvt_2_1_ > 0) {
               ((LivingEntity)p_70652_1_).addPotionEffect(new EffectInstance(Effects.POISON, lvt_2_1_ * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      return p_213386_4_;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.45F;
   }
}
