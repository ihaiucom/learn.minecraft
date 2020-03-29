package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TameableEntity extends AnimalEntity {
   protected static final DataParameter<Byte> TAMED;
   protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID;
   protected SitGoal sitGoal;

   protected TameableEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
      super(p_i48574_1_, p_i48574_2_);
      this.setupTamedAI();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TAMED, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.getOwnerId() == null) {
         p_213281_1_.putString("OwnerUUID", "");
      } else {
         p_213281_1_.putString("OwnerUUID", this.getOwnerId().toString());
      }

      p_213281_1_.putBoolean("Sitting", this.isSitting());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      String lvt_2_2_;
      if (p_70037_1_.contains("OwnerUUID", 8)) {
         lvt_2_2_ = p_70037_1_.getString("OwnerUUID");
      } else {
         String lvt_3_1_ = p_70037_1_.getString("Owner");
         lvt_2_2_ = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), lvt_3_1_);
      }

      if (!lvt_2_2_.isEmpty()) {
         try {
            this.setOwnerId(UUID.fromString(lvt_2_2_));
            this.setTamed(true);
         } catch (Throwable var4) {
            this.setTamed(false);
         }
      }

      if (this.sitGoal != null) {
         this.sitGoal.setSitting(p_70037_1_.getBoolean("Sitting"));
      }

      this.setSitting(p_70037_1_.getBoolean("Sitting"));
   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return !this.getLeashed();
   }

   @OnlyIn(Dist.CLIENT)
   protected void playTameEffect(boolean p_70908_1_) {
      IParticleData lvt_2_1_ = ParticleTypes.HEART;
      if (!p_70908_1_) {
         lvt_2_1_ = ParticleTypes.SMOKE;
      }

      for(int lvt_3_1_ = 0; lvt_3_1_ < 7; ++lvt_3_1_) {
         double lvt_4_1_ = this.rand.nextGaussian() * 0.02D;
         double lvt_6_1_ = this.rand.nextGaussian() * 0.02D;
         double lvt_8_1_ = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(lvt_2_1_, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), lvt_4_1_, lvt_6_1_, lvt_8_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.playTameEffect(true);
      } else if (p_70103_1_ == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public boolean isTamed() {
      return ((Byte)this.dataManager.get(TAMED) & 4) != 0;
   }

   public void setTamed(boolean p_70903_1_) {
      byte lvt_2_1_ = (Byte)this.dataManager.get(TAMED);
      if (p_70903_1_) {
         this.dataManager.set(TAMED, (byte)(lvt_2_1_ | 4));
      } else {
         this.dataManager.set(TAMED, (byte)(lvt_2_1_ & -5));
      }

      this.setupTamedAI();
   }

   protected void setupTamedAI() {
   }

   public boolean isSitting() {
      return ((Byte)this.dataManager.get(TAMED) & 1) != 0;
   }

   public void setSitting(boolean p_70904_1_) {
      byte lvt_2_1_ = (Byte)this.dataManager.get(TAMED);
      if (p_70904_1_) {
         this.dataManager.set(TAMED, (byte)(lvt_2_1_ | 1));
      } else {
         this.dataManager.set(TAMED, (byte)(lvt_2_1_ & -2));
      }

   }

   @Nullable
   public UUID getOwnerId() {
      return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orElse((Object)null);
   }

   public void setOwnerId(@Nullable UUID p_184754_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
   }

   public void setTamedBy(PlayerEntity p_193101_1_) {
      this.setTamed(true);
      this.setOwnerId(p_193101_1_.getUniqueID());
      if (p_193101_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)p_193101_1_, this);
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID lvt_1_1_ = this.getOwnerId();
         return lvt_1_1_ == null ? null : this.world.getPlayerByUuid(lvt_1_1_);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean canAttack(LivingEntity p_213336_1_) {
      return this.isOwner(p_213336_1_) ? false : super.canAttack(p_213336_1_);
   }

   public boolean isOwner(LivingEntity p_152114_1_) {
      return p_152114_1_ == this.getOwner();
   }

   public SitGoal getAISit() {
      return this.sitGoal;
   }

   public boolean shouldAttackEntity(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
      return true;
   }

   public Team getTeam() {
      if (this.isTamed()) {
         LivingEntity lvt_1_1_ = this.getOwner();
         if (lvt_1_1_ != null) {
            return lvt_1_1_.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (this.isTamed()) {
         LivingEntity lvt_2_1_ = this.getOwner();
         if (p_184191_1_ == lvt_2_1_) {
            return true;
         }

         if (lvt_2_1_ != null) {
            return lvt_2_1_.isOnSameTeam(p_184191_1_);
         }
      }

      return super.isOnSameTeam(p_184191_1_);
   }

   public void onDeath(DamageSource p_70645_1_) {
      if (!this.world.isRemote && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(p_70645_1_);
   }

   static {
      TAMED = EntityDataManager.createKey(TameableEntity.class, DataSerializers.BYTE);
      OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   }
}
