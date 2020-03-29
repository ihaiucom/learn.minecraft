package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity extends MonsterEntity {
   private int lifetime;
   private boolean playerSpawned;

   public EndermiteEntity(EntityType<? extends EndermiteEntity> p_i50209_1_, World p_i50209_2_) {
      super(p_i50209_1_, p_i50209_2_);
      this.experienceValue = 3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.lifetime = p_70037_1_.getInt("Lifetime");
      this.playerSpawned = p_70037_1_.getBoolean("PlayerSpawned");
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Lifetime", this.lifetime);
      p_213281_1_.putBoolean("PlayerSpawned", this.playerSpawned);
   }

   public void tick() {
      this.renderYawOffset = this.rotationYaw;
      super.tick();
   }

   public void setRenderYawOffset(float p_181013_1_) {
      this.rotationYaw = p_181013_1_;
      super.setRenderYawOffset(p_181013_1_);
   }

   public double getYOffset() {
      return 0.1D;
   }

   public boolean isSpawnedByPlayer() {
      return this.playerSpawned;
   }

   public void setSpawnedByPlayer(boolean p_175496_1_) {
      this.playerSpawned = p_175496_1_;
   }

   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote) {
         for(int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_) {
            this.world.addParticle(ParticleTypes.PORTAL, this.func_226282_d_(0.5D), this.func_226279_cv_(), this.func_226287_g_(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if (!this.isNoDespawnRequired()) {
            ++this.lifetime;
         }

         if (this.lifetime >= 2400) {
            this.remove();
         }
      }

   }

   public static boolean func_223328_b(EntityType<EndermiteEntity> p_223328_0_, IWorld p_223328_1_, SpawnReason p_223328_2_, BlockPos p_223328_3_, Random p_223328_4_) {
      if (func_223324_d(p_223328_0_, p_223328_1_, p_223328_2_, p_223328_3_, p_223328_4_)) {
         PlayerEntity lvt_5_1_ = p_223328_1_.getClosestPlayer((double)p_223328_3_.getX() + 0.5D, (double)p_223328_3_.getY() + 0.5D, (double)p_223328_3_.getZ() + 0.5D, 5.0D, true);
         return lvt_5_1_ == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }
}