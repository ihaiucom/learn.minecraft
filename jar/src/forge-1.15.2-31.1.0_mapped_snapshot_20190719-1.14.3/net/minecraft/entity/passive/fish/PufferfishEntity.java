package net.minecraft.entity.passive.fish;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends AbstractFishEntity {
   private static final DataParameter<Integer> PUFF_STATE;
   private int puffTimer;
   private int deflateTimer;
   private static final Predicate<LivingEntity> ENEMY_MATCHER;

   public PufferfishEntity(EntityType<? extends PufferfishEntity> p_i50248_1_, World p_i50248_2_) {
      super(p_i50248_1_, p_i50248_2_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return (Integer)this.dataManager.get(PUFF_STATE);
   }

   public void setPuffState(int p_203714_1_) {
      this.dataManager.set(PUFF_STATE, p_203714_1_);
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (PUFF_STATE.equals(p_184206_1_)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("PuffState", this.getPuffState());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setPuffState(p_70037_1_.getInt("PuffState"));
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PufferfishEntity.PuffGoal(this));
   }

   public void tick() {
      if (!this.world.isRemote && this.isAlive() && this.isServerWorld()) {
         if (this.puffTimer > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.puffTimer > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(2);
            }

            ++this.puffTimer;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void livingTick() {
      super.livingTick();
      if (this.isAlive() && this.getPuffState() > 0) {
         List<MobEntity> lvt_1_1_ = this.world.getEntitiesWithinAABB(MobEntity.class, this.getBoundingBox().grow(0.3D), ENEMY_MATCHER);
         Iterator var2 = lvt_1_1_.iterator();

         while(var2.hasNext()) {
            MobEntity lvt_3_1_ = (MobEntity)var2.next();
            if (lvt_3_1_.isAlive()) {
               this.attack(lvt_3_1_);
            }
         }
      }

   }

   private void attack(MobEntity p_205719_1_) {
      int lvt_2_1_ = this.getPuffState();
      if (p_205719_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + lvt_2_1_))) {
         p_205719_1_.addPotionEffect(new EffectInstance(Effects.POISON, 60 * lvt_2_1_, 0));
         this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      int lvt_2_1_ = this.getPuffState();
      if (p_70100_1_ instanceof ServerPlayerEntity && lvt_2_1_ > 0 && p_70100_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + lvt_2_1_))) {
         ((ServerPlayerEntity)p_70100_1_).connection.sendPacket(new SChangeGameStatePacket(9, 0.0F));
         p_70100_1_.addPotionEffect(new EffectInstance(Effects.POISON, 60 * lvt_2_1_, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
   }

   public EntitySize getSize(Pose p_213305_1_) {
      return super.getSize(p_213305_1_).scale(getPuffSize(this.getPuffState()));
   }

   private static float getPuffSize(int p_213806_0_) {
      switch(p_213806_0_) {
      case 0:
         return 0.5F;
      case 1:
         return 0.7F;
      default:
         return 1.0F;
      }
   }

   static {
      PUFF_STATE = EntityDataManager.createKey(PufferfishEntity.class, DataSerializers.VARINT);
      ENEMY_MATCHER = (p_210139_0_) -> {
         if (p_210139_0_ == null) {
            return false;
         } else if (!(p_210139_0_ instanceof PlayerEntity) || !p_210139_0_.isSpectator() && !((PlayerEntity)p_210139_0_).isCreative()) {
            return p_210139_0_.getCreatureAttribute() != CreatureAttribute.WATER;
         } else {
            return false;
         }
      };
   }

   static class PuffGoal extends Goal {
      private final PufferfishEntity fish;

      public PuffGoal(PufferfishEntity p_i48861_1_) {
         this.fish = p_i48861_1_;
      }

      public boolean shouldExecute() {
         List<LivingEntity> lvt_1_1_ = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0D), PufferfishEntity.ENEMY_MATCHER);
         return !lvt_1_1_.isEmpty();
      }

      public void startExecuting() {
         this.fish.puffTimer = 1;
         this.fish.deflateTimer = 0;
      }

      public void resetTask() {
         this.fish.puffTimer = 0;
      }

      public boolean shouldContinueExecuting() {
         List<LivingEntity> lvt_1_1_ = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0D), PufferfishEntity.ENEMY_MATCHER);
         return !lvt_1_1_.isEmpty();
      }
   }
}
