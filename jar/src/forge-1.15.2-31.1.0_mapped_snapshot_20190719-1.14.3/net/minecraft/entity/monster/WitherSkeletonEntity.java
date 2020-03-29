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
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WitherSkeletonEntity extends AbstractSkeletonEntity {
   public WitherSkeletonEntity(EntityType<? extends WitherSkeletonEntity> p_i50187_1_, World p_i50187_2_) {
      super(p_i50187_1_, p_i50187_2_);
      this.setPathPriority(PathNodeType.LAVA, 8.0F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
   }

   protected void dropSpecialItems(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropSpecialItems(p_213333_1_, p_213333_2_, p_213333_3_);
      Entity lvt_4_1_ = p_213333_1_.getTrueSource();
      if (lvt_4_1_ instanceof CreeperEntity) {
         CreeperEntity lvt_5_1_ = (CreeperEntity)lvt_4_1_;
         if (lvt_5_1_.ableToCauseSkullDrop()) {
            lvt_5_1_.incrementDroppedSkulls();
            this.entityDropItem(Items.WITHER_SKELETON_SKULL);
         }
      }

   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance p_180483_1_) {
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      ILivingEntityData lvt_6_1_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.setCombatTask();
      return lvt_6_1_;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 2.1F;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (!super.attackEntityAsMob(p_70652_1_)) {
         return false;
      } else {
         if (p_70652_1_ instanceof LivingEntity) {
            ((LivingEntity)p_70652_1_).addPotionEffect(new EffectInstance(Effects.WITHER, 200));
         }

         return true;
      }
   }

   protected AbstractArrowEntity func_213624_b(ItemStack p_213624_1_, float p_213624_2_) {
      AbstractArrowEntity lvt_3_1_ = super.func_213624_b(p_213624_1_, p_213624_2_);
      lvt_3_1_.setFire(100);
      return lvt_3_1_;
   }

   public boolean isPotionApplicable(EffectInstance p_70687_1_) {
      return p_70687_1_.getPotion() == Effects.WITHER ? false : super.isPotionApplicable(p_70687_1_);
   }
}
