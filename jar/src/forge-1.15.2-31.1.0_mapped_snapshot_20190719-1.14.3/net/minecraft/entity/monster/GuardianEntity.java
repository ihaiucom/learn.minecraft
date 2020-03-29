package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GuardianEntity extends MonsterEntity {
   private static final DataParameter<Boolean> MOVING;
   private static final DataParameter<Integer> TARGET_ENTITY;
   protected float clientSideTailAnimation;
   protected float clientSideTailAnimationO;
   protected float clientSideTailAnimationSpeed;
   protected float clientSideSpikesAnimation;
   protected float clientSideSpikesAnimationO;
   private LivingEntity targetedEntity;
   private int clientSideAttackTime;
   private boolean clientSideTouchedGround;
   protected RandomWalkingGoal wander;

   public GuardianEntity(EntityType<? extends GuardianEntity> p_i48554_1_, World p_i48554_2_) {
      super(p_i48554_1_, p_i48554_2_);
      this.experienceValue = 10;
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.moveController = new GuardianEntity.MoveHelperController(this);
      this.clientSideTailAnimation = this.rand.nextFloat();
      this.clientSideTailAnimationO = this.clientSideTailAnimation;
   }

   protected void registerGoals() {
      MoveTowardsRestrictionGoal lvt_1_1_ = new MoveTowardsRestrictionGoal(this, 1.0D);
      this.wander = new RandomWalkingGoal(this, 1.0D, 80);
      this.goalSelector.addGoal(4, new GuardianEntity.AttackGoal(this));
      this.goalSelector.addGoal(5, lvt_1_1_);
      this.goalSelector.addGoal(7, this.wander);
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, GuardianEntity.class, 12.0F, 0.01F));
      this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
      this.wander.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      lvt_1_1_.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.TargetPredicate(this)));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new SwimmerPathNavigator(this, p_175447_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(MOVING, false);
      this.dataManager.register(TARGET_ENTITY, 0);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isMoving() {
      return (Boolean)this.dataManager.get(MOVING);
   }

   private void setMoving(boolean p_175476_1_) {
      this.dataManager.set(MOVING, p_175476_1_);
   }

   public int getAttackDuration() {
      return 80;
   }

   private void setTargetedEntity(int p_175463_1_) {
      this.dataManager.set(TARGET_ENTITY, p_175463_1_);
   }

   public boolean hasTargetedEntity() {
      return (Integer)this.dataManager.get(TARGET_ENTITY) != 0;
   }

   @Nullable
   public LivingEntity getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity lvt_1_1_ = this.world.getEntityByID((Integer)this.dataManager.get(TARGET_ENTITY));
            if (lvt_1_1_ instanceof LivingEntity) {
               this.targetedEntity = (LivingEntity)lvt_1_1_;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      super.notifyDataManagerChange(p_184206_1_);
      if (TARGET_ENTITY.equals(p_184206_1_)) {
         this.clientSideAttackTime = 0;
         this.targetedEntity = null;
      }

   }

   public int getTalkInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.5F;
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getFluidState(p_205022_1_).isTagged(FluidTags.WATER) ? 10.0F + p_205022_2_.getBrightness(p_205022_1_) - 0.5F : super.getBlockPathWeight(p_205022_1_, p_205022_2_);
   }

   public void livingTick() {
      if (this.isAlive()) {
         if (this.world.isRemote) {
            this.clientSideTailAnimationO = this.clientSideTailAnimation;
            Vec3d lvt_1_2_;
            if (!this.isInWater()) {
               this.clientSideTailAnimationSpeed = 2.0F;
               lvt_1_2_ = this.getMotion();
               if (lvt_1_2_.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                  this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.getFlopSound(), this.getSoundCategory(), 1.0F, 1.0F, false);
               }

               this.clientSideTouchedGround = lvt_1_2_.y < 0.0D && this.world.func_217400_a((new BlockPos(this)).down(), this);
            } else if (this.isMoving()) {
               if (this.clientSideTailAnimationSpeed < 0.5F) {
                  this.clientSideTailAnimationSpeed = 4.0F;
               } else {
                  this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
               }
            } else {
               this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
            }

            this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
            this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
            if (!this.isInWaterOrBubbleColumn()) {
               this.clientSideSpikesAnimation = this.rand.nextFloat();
            } else if (this.isMoving()) {
               this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
            } else {
               this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
            }

            if (this.isMoving() && this.isInWater()) {
               lvt_1_2_ = this.getLook(0.0F);

               for(int lvt_2_1_ = 0; lvt_2_1_ < 2; ++lvt_2_1_) {
                  this.world.addParticle(ParticleTypes.BUBBLE, this.func_226282_d_(0.5D) - lvt_1_2_.x * 1.5D, this.func_226279_cv_() - lvt_1_2_.y * 1.5D, this.func_226287_g_(0.5D) - lvt_1_2_.z * 1.5D, 0.0D, 0.0D, 0.0D);
               }
            }

            if (this.hasTargetedEntity()) {
               if (this.clientSideAttackTime < this.getAttackDuration()) {
                  ++this.clientSideAttackTime;
               }

               LivingEntity lvt_1_3_ = this.getTargetedEntity();
               if (lvt_1_3_ != null) {
                  this.getLookController().setLookPositionWithEntity(lvt_1_3_, 90.0F, 90.0F);
                  this.getLookController().tick();
                  double lvt_2_2_ = (double)this.getAttackAnimationScale(0.0F);
                  double lvt_4_1_ = lvt_1_3_.func_226277_ct_() - this.func_226277_ct_();
                  double lvt_6_1_ = lvt_1_3_.func_226283_e_(0.5D) - this.func_226280_cw_();
                  double lvt_8_1_ = lvt_1_3_.func_226281_cx_() - this.func_226281_cx_();
                  double lvt_10_1_ = Math.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_);
                  lvt_4_1_ /= lvt_10_1_;
                  lvt_6_1_ /= lvt_10_1_;
                  lvt_8_1_ /= lvt_10_1_;
                  double lvt_12_1_ = this.rand.nextDouble();

                  while(lvt_12_1_ < lvt_10_1_) {
                     lvt_12_1_ += 1.8D - lvt_2_2_ + this.rand.nextDouble() * (1.7D - lvt_2_2_);
                     this.world.addParticle(ParticleTypes.BUBBLE, this.func_226277_ct_() + lvt_4_1_ * lvt_12_1_, this.func_226280_cw_() + lvt_6_1_ * lvt_12_1_, this.func_226281_cx_() + lvt_8_1_ * lvt_12_1_, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (this.isInWaterOrBubbleColumn()) {
            this.setAir(300);
         } else if (this.onGround) {
            this.setMotion(this.getMotion().add((double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F)));
            this.rotationYaw = this.rand.nextFloat() * 360.0F;
            this.onGround = false;
            this.isAirBorne = true;
         }

         if (this.hasTargetedEntity()) {
            this.rotationYaw = this.rotationYawHead;
         }
      }

      super.livingTick();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_GUARDIAN_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAnimation(float p_175471_1_) {
      return MathHelper.lerp(p_175471_1_, this.clientSideTailAnimationO, this.clientSideTailAnimation);
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpikesAnimation(float p_175469_1_) {
      return MathHelper.lerp(p_175469_1_, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
   }

   public float getAttackAnimationScale(float p_175477_1_) {
      return ((float)this.clientSideAttackTime + p_175477_1_) / (float)this.getAttackDuration();
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return p_205019_1_.func_226668_i_(this);
   }

   public static boolean func_223329_b(EntityType<? extends GuardianEntity> p_223329_0_, IWorld p_223329_1_, SpawnReason p_223329_2_, BlockPos p_223329_3_, Random p_223329_4_) {
      return (p_223329_4_.nextInt(20) == 0 || !p_223329_1_.canBlockSeeSky(p_223329_3_)) && p_223329_1_.getDifficulty() != Difficulty.PEACEFUL && (p_223329_2_ == SpawnReason.SPAWNER || p_223329_1_.getFluidState(p_223329_3_).isTagged(FluidTags.WATER));
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.isMoving() && !p_70097_1_.isMagicDamage() && p_70097_1_.getImmediateSource() instanceof LivingEntity) {
         LivingEntity lvt_3_1_ = (LivingEntity)p_70097_1_.getImmediateSource();
         if (!p_70097_1_.isExplosion()) {
            lvt_3_1_.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
         }
      }

      if (this.wander != null) {
         this.wander.makeUpdate();
      }

      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public int getVerticalFaceSpeed() {
      return 180;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(0.1F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
         if (!this.isMoving() && this.getAttackTarget() == null) {
            this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   static {
      MOVING = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.BOOLEAN);
      TARGET_ENTITY = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.VARINT);
   }

   static class MoveHelperController extends MovementController {
      private final GuardianEntity entityGuardian;

      public MoveHelperController(GuardianEntity p_i45831_1_) {
         super(p_i45831_1_);
         this.entityGuardian = p_i45831_1_;
      }

      public void tick() {
         if (this.action == MovementController.Action.MOVE_TO && !this.entityGuardian.getNavigator().noPath()) {
            Vec3d lvt_1_1_ = new Vec3d(this.posX - this.entityGuardian.func_226277_ct_(), this.posY - this.entityGuardian.func_226278_cu_(), this.posZ - this.entityGuardian.func_226281_cx_());
            double lvt_2_1_ = lvt_1_1_.length();
            double lvt_4_1_ = lvt_1_1_.x / lvt_2_1_;
            double lvt_6_1_ = lvt_1_1_.y / lvt_2_1_;
            double lvt_8_1_ = lvt_1_1_.z / lvt_2_1_;
            float lvt_10_1_ = (float)(MathHelper.atan2(lvt_1_1_.z, lvt_1_1_.x) * 57.2957763671875D) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, lvt_10_1_, 90.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float lvt_11_1_ = (float)(this.speed * this.entityGuardian.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            float lvt_12_1_ = MathHelper.lerp(0.125F, this.entityGuardian.getAIMoveSpeed(), lvt_11_1_);
            this.entityGuardian.setAIMoveSpeed(lvt_12_1_);
            double lvt_13_1_ = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5D) * 0.05D;
            double lvt_15_1_ = Math.cos((double)(this.entityGuardian.rotationYaw * 0.017453292F));
            double lvt_17_1_ = Math.sin((double)(this.entityGuardian.rotationYaw * 0.017453292F));
            double lvt_19_1_ = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75D) * 0.05D;
            this.entityGuardian.setMotion(this.entityGuardian.getMotion().add(lvt_13_1_ * lvt_15_1_, lvt_19_1_ * (lvt_17_1_ + lvt_15_1_) * 0.25D + (double)lvt_12_1_ * lvt_6_1_ * 0.1D, lvt_13_1_ * lvt_17_1_));
            LookController lvt_21_1_ = this.entityGuardian.getLookController();
            double lvt_22_1_ = this.entityGuardian.func_226277_ct_() + lvt_4_1_ * 2.0D;
            double lvt_24_1_ = this.entityGuardian.func_226280_cw_() + lvt_6_1_ / lvt_2_1_;
            double lvt_26_1_ = this.entityGuardian.func_226281_cx_() + lvt_8_1_ * 2.0D;
            double lvt_28_1_ = lvt_21_1_.getLookPosX();
            double lvt_30_1_ = lvt_21_1_.getLookPosY();
            double lvt_32_1_ = lvt_21_1_.getLookPosZ();
            if (!lvt_21_1_.getIsLooking()) {
               lvt_28_1_ = lvt_22_1_;
               lvt_30_1_ = lvt_24_1_;
               lvt_32_1_ = lvt_26_1_;
            }

            this.entityGuardian.getLookController().setLookPosition(MathHelper.lerp(0.125D, lvt_28_1_, lvt_22_1_), MathHelper.lerp(0.125D, lvt_30_1_, lvt_24_1_), MathHelper.lerp(0.125D, lvt_32_1_, lvt_26_1_), 10.0F, 40.0F);
            this.entityGuardian.setMoving(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.setMoving(false);
         }
      }
   }

   static class AttackGoal extends Goal {
      private final GuardianEntity guardian;
      private int tickCounter;
      private final boolean isElder;

      public AttackGoal(GuardianEntity p_i45833_1_) {
         this.guardian = p_i45833_1_;
         this.isElder = p_i45833_1_ instanceof ElderGuardianEntity;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = this.guardian.getAttackTarget();
         return lvt_1_1_ != null && lvt_1_1_.isAlive();
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && (this.isElder || this.guardian.getDistanceSq(this.guardian.getAttackTarget()) > 9.0D);
      }

      public void startExecuting() {
         this.tickCounter = -10;
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookController().setLookPositionWithEntity(this.guardian.getAttackTarget(), 90.0F, 90.0F);
         this.guardian.isAirBorne = true;
      }

      public void resetTask() {
         this.guardian.setTargetedEntity(0);
         this.guardian.setAttackTarget((LivingEntity)null);
         this.guardian.wander.makeUpdate();
      }

      public void tick() {
         LivingEntity lvt_1_1_ = this.guardian.getAttackTarget();
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookController().setLookPositionWithEntity(lvt_1_1_, 90.0F, 90.0F);
         if (!this.guardian.canEntityBeSeen(lvt_1_1_)) {
            this.guardian.setAttackTarget((LivingEntity)null);
         } else {
            ++this.tickCounter;
            if (this.tickCounter == 0) {
               this.guardian.setTargetedEntity(this.guardian.getAttackTarget().getEntityId());
               this.guardian.world.setEntityState(this.guardian, (byte)21);
            } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
               float lvt_2_1_ = 1.0F;
               if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                  lvt_2_1_ += 2.0F;
               }

               if (this.isElder) {
                  lvt_2_1_ += 2.0F;
               }

               lvt_1_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.guardian, this.guardian), lvt_2_1_);
               lvt_1_1_.attackEntityFrom(DamageSource.causeMobDamage(this.guardian), (float)this.guardian.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
               this.guardian.setAttackTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   static class TargetPredicate implements Predicate<LivingEntity> {
      private final GuardianEntity parentEntity;

      public TargetPredicate(GuardianEntity p_i45832_1_) {
         this.parentEntity = p_i45832_1_;
      }

      public boolean test(@Nullable LivingEntity p_test_1_) {
         return (p_test_1_ instanceof PlayerEntity || p_test_1_ instanceof SquidEntity) && p_test_1_.getDistanceSq(this.parentEntity) > 9.0D;
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object p_test_1_) {
         return this.test((LivingEntity)p_test_1_);
      }
   }
}
