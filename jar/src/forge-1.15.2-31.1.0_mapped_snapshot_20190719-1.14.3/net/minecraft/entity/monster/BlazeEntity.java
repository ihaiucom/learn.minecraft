package net.minecraft.entity.monster;

import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeEntity extends MonsterEntity {
   private float heightOffset = 0.5F;
   private int heightOffsetUpdateTime;
   private static final DataParameter<Byte> ON_FIRE;

   public BlazeEntity(EntityType<? extends BlazeEntity> p_i50215_1_, World p_i50215_2_) {
      super(p_i50215_1_, p_i50215_2_);
      this.setPathPriority(PathNodeType.WATER, -1.0F);
      this.setPathPriority(PathNodeType.LAVA, 8.0F);
      this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.experienceValue = 10;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new BlazeEntity.FireballAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ON_FIRE, (byte)0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_BLAZE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_BLAZE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BLAZE_DEATH;
   }

   public float getBrightness() {
      return 1.0F;
   }

   public void livingTick() {
      if (!this.onGround && this.getMotion().y < 0.0D) {
         this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
      }

      if (this.world.isRemote) {
         if (this.rand.nextInt(24) == 0 && !this.isSilent()) {
            this.world.playSound(this.func_226277_ct_() + 0.5D, this.func_226278_cu_() + 0.5D, this.func_226281_cx_() + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_) {
            this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.func_226282_d_(0.5D), this.func_226279_cv_(), this.func_226287_g_(0.5D), 0.0D, 0.0D, 0.0D);
         }
      }

      super.livingTick();
   }

   protected void updateAITasks() {
      if (this.isInWaterRainOrBubbleColumn()) {
         this.attackEntityFrom(DamageSource.DROWN, 1.0F);
      }

      --this.heightOffsetUpdateTime;
      if (this.heightOffsetUpdateTime <= 0) {
         this.heightOffsetUpdateTime = 100;
         this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
      }

      LivingEntity lvt_1_1_ = this.getAttackTarget();
      if (lvt_1_1_ != null && lvt_1_1_.func_226280_cw_() > this.func_226280_cw_() + (double)this.heightOffset && this.canAttack(lvt_1_1_)) {
         Vec3d lvt_2_1_ = this.getMotion();
         this.setMotion(this.getMotion().add(0.0D, (0.30000001192092896D - lvt_2_1_.y) * 0.30000001192092896D, 0.0D));
         this.isAirBorne = true;
      }

      super.updateAITasks();
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   public boolean isBurning() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return ((Byte)this.dataManager.get(ON_FIRE) & 1) != 0;
   }

   private void setOnFire(boolean p_70844_1_) {
      byte lvt_2_1_ = (Byte)this.dataManager.get(ON_FIRE);
      if (p_70844_1_) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 1);
      } else {
         lvt_2_1_ &= -2;
      }

      this.dataManager.set(ON_FIRE, lvt_2_1_);
   }

   static {
      ON_FIRE = EntityDataManager.createKey(BlazeEntity.class, DataSerializers.BYTE);
   }

   static class FireballAttackGoal extends Goal {
      private final BlazeEntity blaze;
      private int attackStep;
      private int attackTime;
      private int field_223527_d;

      public FireballAttackGoal(BlazeEntity p_i45846_1_) {
         this.blaze = p_i45846_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = this.blaze.getAttackTarget();
         return lvt_1_1_ != null && lvt_1_1_.isAlive() && this.blaze.canAttack(lvt_1_1_);
      }

      public void startExecuting() {
         this.attackStep = 0;
      }

      public void resetTask() {
         this.blaze.setOnFire(false);
         this.field_223527_d = 0;
      }

      public void tick() {
         --this.attackTime;
         LivingEntity lvt_1_1_ = this.blaze.getAttackTarget();
         if (lvt_1_1_ != null) {
            boolean lvt_2_1_ = this.blaze.getEntitySenses().canSee(lvt_1_1_);
            if (lvt_2_1_) {
               this.field_223527_d = 0;
            } else {
               ++this.field_223527_d;
            }

            double lvt_3_1_ = this.blaze.getDistanceSq(lvt_1_1_);
            if (lvt_3_1_ < 4.0D) {
               if (!lvt_2_1_) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.blaze.attackEntityAsMob(lvt_1_1_);
               }

               this.blaze.getMoveHelper().setMoveTo(lvt_1_1_.func_226277_ct_(), lvt_1_1_.func_226278_cu_(), lvt_1_1_.func_226281_cx_(), 1.0D);
            } else if (lvt_3_1_ < this.getFollowDistance() * this.getFollowDistance() && lvt_2_1_) {
               double lvt_5_1_ = lvt_1_1_.func_226277_ct_() - this.blaze.func_226277_ct_();
               double lvt_7_1_ = lvt_1_1_.func_226283_e_(0.5D) - this.blaze.func_226283_e_(0.5D);
               double lvt_9_1_ = lvt_1_1_.func_226281_cx_() - this.blaze.func_226281_cx_();
               if (this.attackTime <= 0) {
                  ++this.attackStep;
                  if (this.attackStep == 1) {
                     this.attackTime = 60;
                     this.blaze.setOnFire(true);
                  } else if (this.attackStep <= 4) {
                     this.attackTime = 6;
                  } else {
                     this.attackTime = 100;
                     this.attackStep = 0;
                     this.blaze.setOnFire(false);
                  }

                  if (this.attackStep > 1) {
                     float lvt_11_1_ = MathHelper.sqrt(MathHelper.sqrt(lvt_3_1_)) * 0.5F;
                     this.blaze.world.playEvent((PlayerEntity)null, 1018, new BlockPos(this.blaze), 0);

                     for(int lvt_12_1_ = 0; lvt_12_1_ < 1; ++lvt_12_1_) {
                        SmallFireballEntity lvt_13_1_ = new SmallFireballEntity(this.blaze.world, this.blaze, lvt_5_1_ + this.blaze.getRNG().nextGaussian() * (double)lvt_11_1_, lvt_7_1_, lvt_9_1_ + this.blaze.getRNG().nextGaussian() * (double)lvt_11_1_);
                        lvt_13_1_.setPosition(lvt_13_1_.func_226277_ct_(), this.blaze.func_226283_e_(0.5D) + 0.5D, lvt_13_1_.func_226281_cx_());
                        this.blaze.world.addEntity(lvt_13_1_);
                     }
                  }
               }

               this.blaze.getLookController().setLookPositionWithEntity(lvt_1_1_, 10.0F, 10.0F);
            } else if (this.field_223527_d < 5) {
               this.blaze.getMoveHelper().setMoveTo(lvt_1_1_.func_226277_ct_(), lvt_1_1_.func_226278_cu_(), lvt_1_1_.func_226281_cx_(), 1.0D);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.blaze.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue();
      }
   }
}
