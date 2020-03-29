package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.loot.LootTables;

public class SlimeEntity extends MobEntity implements IMob {
   private static final DataParameter<Integer> SLIME_SIZE;
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private boolean wasOnGround;

   public SlimeEntity(EntityType<? extends SlimeEntity> p_i48552_1_, World p_i48552_2_) {
      super(p_i48552_1_, p_i48552_2_);
      this.moveController = new SlimeEntity.MoveHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SlimeEntity.FloatGoal(this));
      this.goalSelector.addGoal(2, new SlimeEntity.AttackGoal(this));
      this.goalSelector.addGoal(3, new SlimeEntity.FaceRandomGoal(this));
      this.goalSelector.addGoal(5, new SlimeEntity.HopGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, PlayerEntity.class, 10, true, false, (p_lambda$registerGoals$0_1_) -> {
         return Math.abs(p_lambda$registerGoals$0_1_.func_226278_cu_() - this.func_226278_cu_()) <= 4.0D;
      }));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SLIME_SIZE, 1);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void setSlimeSize(int p_70799_1_, boolean p_70799_2_) {
      this.dataManager.set(SLIME_SIZE, p_70799_1_);
      this.func_226264_Z_();
      this.recalculateSize();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(p_70799_1_ * p_70799_1_));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)p_70799_1_));
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)p_70799_1_);
      if (p_70799_2_) {
         this.setHealth(this.getMaxHealth());
      }

      this.experienceValue = p_70799_1_;
   }

   public int getSlimeSize() {
      return (Integer)this.dataManager.get(SLIME_SIZE);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Size", this.getSlimeSize() - 1);
      p_213281_1_.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      int i = p_70037_1_.getInt("Size");
      if (i < 0) {
         i = 0;
      }

      this.setSlimeSize(i + 1, false);
      super.readAdditional(p_70037_1_);
      this.wasOnGround = p_70037_1_.getBoolean("wasOnGround");
   }

   public boolean isSmallSlime() {
      return this.getSlimeSize() <= 1;
   }

   protected IParticleData getSquishParticle() {
      return ParticleTypes.ITEM_SLIME;
   }

   protected boolean func_225511_J_() {
      return this.getSlimeSize() > 0;
   }

   public void tick() {
      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int i = this.getSlimeSize();
         if (this.spawnCustomParticles()) {
            i = 0;
         }

         for(int j = 0; j < i * 8; ++j) {
            float f = this.rand.nextFloat() * 6.2831855F;
            float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
            this.world.addParticle(this.getSquishParticle(), this.func_226277_ct_() + (double)f2, this.func_226278_cu_(), this.func_226281_cx_() + (double)f3, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   public void recalculateSize() {
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226278_cu_();
      double d2 = this.func_226281_cx_();
      super.recalculateSize();
      this.setPosition(d0, d1, d2);
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (SLIME_SIZE.equals(p_184206_1_)) {
         this.recalculateSize();
         this.rotationYaw = this.rotationYawHead;
         this.renderYawOffset = this.rotationYawHead;
         if (this.isInWater() && this.rand.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public EntityType<? extends SlimeEntity> getType() {
      return super.getType();
   }

   public void remove(boolean p_remove_1_) {
      int i = this.getSlimeSize();
      if (!this.world.isRemote && i > 1 && this.getHealth() <= 0.0F && !this.removed) {
         int j = 2 + this.rand.nextInt(3);

         for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
            SlimeEntity slimeentity = (SlimeEntity)this.getType().create(this.world);
            if (this.hasCustomName()) {
               slimeentity.setCustomName(this.getCustomName());
            }

            if (this.isNoDespawnRequired()) {
               slimeentity.enablePersistence();
            }

            slimeentity.setInvulnerable(this.isInvulnerable());
            slimeentity.setSlimeSize(i / 2, true);
            slimeentity.setLocationAndAngles(this.func_226277_ct_() + (double)f, this.func_226278_cu_() + 0.5D, this.func_226281_cx_() + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
            this.world.addEntity(slimeentity);
         }
      }

      super.remove(p_remove_1_);
   }

   public void applyEntityCollision(Entity p_70108_1_) {
      super.applyEntityCollision(p_70108_1_);
      if (p_70108_1_ instanceof IronGolemEntity && this.canDamagePlayer()) {
         this.dealDamage((LivingEntity)p_70108_1_);
      }

   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      if (this.canDamagePlayer()) {
         this.dealDamage(p_70100_1_);
      }

   }

   protected void dealDamage(LivingEntity p_175451_1_) {
      if (this.isAlive()) {
         int i = this.getSlimeSize();
         if (this.getDistanceSq(p_175451_1_) < 0.6D * (double)i * 0.6D * (double)i && this.canEntityBeSeen(p_175451_1_) && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
            this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.applyEnchantments(this, p_175451_1_);
         }
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.625F * p_213348_2_.height;
   }

   protected boolean canDamagePlayer() {
      return !this.isSmallSlime() && this.isServerWorld();
   }

   protected float func_225512_er_() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
   }

   protected ResourceLocation getLootTable() {
      return this.getSlimeSize() == 1 ? this.getType().getLootTable() : LootTables.EMPTY;
   }

   public static boolean func_223366_c(EntityType<SlimeEntity> p_223366_0_, IWorld p_223366_1_, SpawnReason p_223366_2_, BlockPos p_223366_3_, Random p_223366_4_) {
      if (p_223366_1_.getWorldInfo().getGenerator().handleSlimeSpawnReduction(p_223366_4_, p_223366_1_) && p_223366_4_.nextInt(4) != 1) {
         return false;
      } else {
         if (p_223366_1_.getDifficulty() != Difficulty.PEACEFUL) {
            Biome biome = p_223366_1_.func_226691_t_(p_223366_3_);
            if (biome == Biomes.SWAMP && p_223366_3_.getY() > 50 && p_223366_3_.getY() < 70 && p_223366_4_.nextFloat() < 0.5F && p_223366_4_.nextFloat() < p_223366_1_.getCurrentMoonPhaseFactor() && p_223366_1_.getLight(p_223366_3_) <= p_223366_4_.nextInt(8)) {
               return func_223315_a(p_223366_0_, p_223366_1_, p_223366_2_, p_223366_3_, p_223366_4_);
            }

            ChunkPos chunkpos = new ChunkPos(p_223366_3_);
            boolean flag = SharedSeedRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, p_223366_1_.getSeed(), 987234911L).nextInt(10) == 0;
            if (p_223366_4_.nextInt(10) == 0 && flag && p_223366_3_.getY() < 40) {
               return func_223315_a(p_223366_0_, p_223366_1_, p_223366_2_, p_223366_3_, p_223366_4_);
            }
         }

         return false;
      }
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSlimeSize();
   }

   public int getVerticalFaceSpeed() {
      return 0;
   }

   protected boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   protected void jump() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x, (double)this.getJumpUpwardsMotion(), vec3d.z);
      this.isAirBorne = true;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      int i = this.rand.nextInt(3);
      if (i < 2 && this.rand.nextFloat() < 0.5F * p_213386_2_.getClampedAdditionalDifficulty()) {
         ++i;
      }

      int j = 1 << i;
      this.setSlimeSize(j, true);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected SoundEvent getJumpSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
   }

   public EntitySize getSize(Pose p_213305_1_) {
      return super.getSize(p_213305_1_).scale(0.255F * (float)this.getSlimeSize());
   }

   protected boolean spawnCustomParticles() {
      return false;
   }

   static {
      SLIME_SIZE = EntityDataManager.createKey(SlimeEntity.class, DataSerializers.VARINT);
   }

   static class MoveHelperController extends MovementController {
      private float yRot;
      private int jumpDelay;
      private final SlimeEntity slime;
      private boolean isAggressive;

      public MoveHelperController(SlimeEntity p_i45821_1_) {
         super(p_i45821_1_);
         this.slime = p_i45821_1_;
         this.yRot = 180.0F * p_i45821_1_.rotationYaw / 3.1415927F;
      }

      public void setDirection(float p_179920_1_, boolean p_179920_2_) {
         this.yRot = p_179920_1_;
         this.isAggressive = p_179920_2_;
      }

      public void setSpeed(double p_179921_1_) {
         this.speed = p_179921_1_;
         this.action = MovementController.Action.MOVE_TO;
      }

      public void tick() {
         this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0F);
         this.mob.rotationYawHead = this.mob.rotationYaw;
         this.mob.renderYawOffset = this.mob.rotationYaw;
         if (this.action != MovementController.Action.MOVE_TO) {
            this.mob.setMoveForward(0.0F);
         } else {
            this.action = MovementController.Action.WAIT;
            if (this.mob.onGround) {
               this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpController().setJumping();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.slime.moveStrafing = 0.0F;
                  this.slime.moveForward = 0.0F;
                  this.mob.setAIMoveSpeed(0.0F);
               }
            } else {
               this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
            }
         }

      }
   }

   static class HopGoal extends Goal {
      private final SlimeEntity slime;

      public HopGoal(SlimeEntity p_i45822_1_) {
         this.slime = p_i45822_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         return !this.slime.isPassenger();
      }

      public void tick() {
         ((SlimeEntity.MoveHelperController)this.slime.getMoveHelper()).setSpeed(1.0D);
      }
   }

   static class FloatGoal extends Goal {
      private final SlimeEntity slime;

      public FloatGoal(SlimeEntity p_i45823_1_) {
         this.slime = p_i45823_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         p_i45823_1_.getNavigator().setCanSwim(true);
      }

      public boolean shouldExecute() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveHelper() instanceof SlimeEntity.MoveHelperController;
      }

      public void tick() {
         if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpController().setJumping();
         }

         ((SlimeEntity.MoveHelperController)this.slime.getMoveHelper()).setSpeed(1.2D);
      }
   }

   static class FaceRandomGoal extends Goal {
      private final SlimeEntity slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public FaceRandomGoal(SlimeEntity p_i45820_1_) {
         this.slime = p_i45820_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(Effects.LEVITATION)) && this.slime.getMoveHelper() instanceof SlimeEntity.MoveHelperController;
      }

      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
            this.chosenDegrees = (float)this.slime.getRNG().nextInt(360);
         }

         ((SlimeEntity.MoveHelperController)this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
      }
   }

   static class AttackGoal extends Goal {
      private final SlimeEntity slime;
      private int growTieredTimer;

      public AttackGoal(SlimeEntity p_i45824_1_) {
         this.slime = p_i45824_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         LivingEntity livingentity = this.slime.getAttackTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else {
            return livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage ? false : this.slime.getMoveHelper() instanceof SlimeEntity.MoveHelperController;
         }
      }

      public void startExecuting() {
         this.growTieredTimer = 300;
         super.startExecuting();
      }

      public boolean shouldContinueExecuting() {
         LivingEntity livingentity = this.slime.getAttackTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
            return false;
         } else {
            return --this.growTieredTimer > 0;
         }
      }

      public void tick() {
         this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
         ((SlimeEntity.MoveHelperController)this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
      }
   }
}
