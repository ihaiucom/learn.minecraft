package net.minecraft.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ZombiePigmanEntity extends ZombieEntity {
   private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER;
   private int angerLevel;
   private int randomSoundDelay;
   private UUID angerTargetUUID;

   public ZombiePigmanEntity(EntityType<? extends ZombiePigmanEntity> p_i50199_1_, World p_i50199_2_) {
      super(p_i50199_1_, p_i50199_2_);
      this.setPathPriority(PathNodeType.LAVA, 8.0F);
   }

   public void setRevengeTarget(@Nullable LivingEntity p_70604_1_) {
      super.setRevengeTarget(p_70604_1_);
      if (p_70604_1_ != null) {
         this.angerTargetUUID = p_70604_1_.getUniqueID();
      }

   }

   protected void applyEntityAI() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, new ZombiePigmanEntity.HurtByAggressorGoal(this));
      this.targetSelector.addGoal(2, new ZombiePigmanEntity.TargetAggressorGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   protected boolean shouldDrown() {
      return false;
   }

   protected void updateAITasks() {
      IAttributeInstance lvt_1_1_ = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      LivingEntity lvt_2_1_ = this.getRevengeTarget();
      if (this.isAngry()) {
         if (!this.isChild() && !lvt_1_1_.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            lvt_1_1_.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
         }

         --this.angerLevel;
         LivingEntity lvt_3_1_ = lvt_2_1_ != null ? lvt_2_1_ : this.getAttackTarget();
         if (!this.isAngry() && lvt_3_1_ != null) {
            if (!this.canEntityBeSeen(lvt_3_1_)) {
               this.setRevengeTarget((LivingEntity)null);
               this.setAttackTarget((LivingEntity)null);
            } else {
               this.angerLevel = this.func_223336_ef();
            }
         }
      } else if (lvt_1_1_.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
         lvt_1_1_.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
      }

      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound(SoundEvents.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.isAngry() && this.angerTargetUUID != null && lvt_2_1_ == null) {
         PlayerEntity lvt_3_2_ = this.world.getPlayerByUuid(this.angerTargetUUID);
         this.setRevengeTarget(lvt_3_2_);
         this.attackingPlayer = lvt_3_2_;
         this.recentlyHit = this.getRevengeTimer();
      }

      super.updateAITasks();
   }

   public static boolean func_223337_b(EntityType<ZombiePigmanEntity> p_223337_0_, IWorld p_223337_1_, SpawnReason p_223337_2_, BlockPos p_223337_3_, Random p_223337_4_) {
      return p_223337_1_.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return p_205019_1_.func_226668_i_(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putShort("Anger", (short)this.angerLevel);
      if (this.angerTargetUUID != null) {
         p_213281_1_.putString("HurtBy", this.angerTargetUUID.toString());
      } else {
         p_213281_1_.putString("HurtBy", "");
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.angerLevel = p_70037_1_.getShort("Anger");
      String lvt_2_1_ = p_70037_1_.getString("HurtBy");
      if (!lvt_2_1_.isEmpty()) {
         this.angerTargetUUID = UUID.fromString(lvt_2_1_);
         PlayerEntity lvt_3_1_ = this.world.getPlayerByUuid(this.angerTargetUUID);
         this.setRevengeTarget(lvt_3_1_);
         if (lvt_3_1_ != null) {
            this.attackingPlayer = lvt_3_1_;
            this.recentlyHit = this.getRevengeTimer();
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity lvt_3_1_ = p_70097_1_.getTrueSource();
         if (lvt_3_1_ instanceof PlayerEntity && !((PlayerEntity)lvt_3_1_).isCreative() && this.canEntityBeSeen(lvt_3_1_)) {
            this.func_226547_i_((LivingEntity)lvt_3_1_);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   private boolean func_226547_i_(LivingEntity p_226547_1_) {
      this.angerLevel = this.func_223336_ef();
      this.randomSoundDelay = this.rand.nextInt(40);
      this.setRevengeTarget(p_226547_1_);
      return true;
   }

   private int func_223336_ef() {
      return 400 + this.rand.nextInt(400);
   }

   private boolean isAngry() {
      return this.angerLevel > 0;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_DEATH;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   public boolean isPreventingPlayerRest(PlayerEntity p_191990_1_) {
      return this.isAngry();
   }

   static {
      ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION)).setSaved(false);
   }

   static class TargetAggressorGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public TargetAggressorGoal(ZombiePigmanEntity p_i45829_1_) {
         super(p_i45829_1_, PlayerEntity.class, true);
      }

      public boolean shouldExecute() {
         return ((ZombiePigmanEntity)this.goalOwner).isAngry() && super.shouldExecute();
      }
   }

   static class HurtByAggressorGoal extends HurtByTargetGoal {
      public HurtByAggressorGoal(ZombiePigmanEntity p_i45828_1_) {
         super(p_i45828_1_);
         this.setCallsForHelp(new Class[]{ZombieEntity.class});
      }

      protected void setAttackTarget(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
         if (p_220793_1_ instanceof ZombiePigmanEntity && this.goalOwner.canEntityBeSeen(p_220793_2_) && ((ZombiePigmanEntity)p_220793_1_).func_226547_i_(p_220793_2_)) {
            p_220793_1_.setAttackTarget(p_220793_2_);
         }

      }
   }
}
