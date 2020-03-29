package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GhastEntity extends FlyingEntity implements IMob {
   private static final DataParameter<Boolean> ATTACKING;
   private int explosionStrength = 1;

   public GhastEntity(EntityType<? extends GhastEntity> p_i50206_1_, World p_i50206_2_) {
      super(p_i50206_1_, p_i50206_2_);
      this.experienceValue = 5;
      this.moveController = new GhastEntity.MoveHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new GhastEntity.RandomFlyGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.LookAroundGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.FireballAttackGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, PlayerEntity.class, 10, true, false, (p_213812_1_) -> {
         return Math.abs(p_213812_1_.func_226278_cu_() - this.func_226278_cu_()) <= 4.0D;
      }));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttacking() {
      return (Boolean)this.dataManager.get(ATTACKING);
   }

   public void setAttacking(boolean p_175454_1_) {
      this.dataManager.set(ATTACKING, p_175454_1_);
   }

   public int getFireballStrength() {
      return this.explosionStrength;
   }

   protected boolean func_225511_J_() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_.getImmediateSource() instanceof FireballEntity && p_70097_1_.getTrueSource() instanceof PlayerEntity) {
         super.attackEntityFrom(p_70097_1_, 1000.0F);
         return true;
      } else {
         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACKING, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GHAST_DEATH;
   }

   protected float getSoundVolume() {
      return 10.0F;
   }

   public static boolean func_223368_b(EntityType<GhastEntity> p_223368_0_, IWorld p_223368_1_, SpawnReason p_223368_2_, BlockPos p_223368_3_, Random p_223368_4_) {
      return p_223368_1_.getDifficulty() != Difficulty.PEACEFUL && p_223368_4_.nextInt(20) == 0 && func_223315_a(p_223368_0_, p_223368_1_, p_223368_2_, p_223368_3_, p_223368_4_);
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("ExplosionPower", this.explosionStrength);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("ExplosionPower", 99)) {
         this.explosionStrength = p_70037_1_.getInt("ExplosionPower");
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 2.6F;
   }

   static {
      ATTACKING = EntityDataManager.createKey(GhastEntity.class, DataSerializers.BOOLEAN);
   }

   static class FireballAttackGoal extends Goal {
      private final GhastEntity parentEntity;
      public int attackTimer;

      public FireballAttackGoal(GhastEntity p_i45837_1_) {
         this.parentEntity = p_i45837_1_;
      }

      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      public void startExecuting() {
         this.attackTimer = 0;
      }

      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      public void tick() {
         LivingEntity lvt_1_1_ = this.parentEntity.getAttackTarget();
         double lvt_2_1_ = 64.0D;
         if (lvt_1_1_.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(lvt_1_1_)) {
            World lvt_4_1_ = this.parentEntity.world;
            ++this.attackTimer;
            if (this.attackTimer == 10) {
               lvt_4_1_.playEvent((PlayerEntity)null, 1015, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double lvt_5_1_ = 4.0D;
               Vec3d lvt_7_1_ = this.parentEntity.getLook(1.0F);
               double lvt_8_1_ = lvt_1_1_.func_226277_ct_() - (this.parentEntity.func_226277_ct_() + lvt_7_1_.x * 4.0D);
               double lvt_10_1_ = lvt_1_1_.func_226283_e_(0.5D) - (0.5D + this.parentEntity.func_226283_e_(0.5D));
               double lvt_12_1_ = lvt_1_1_.func_226281_cx_() - (this.parentEntity.func_226281_cx_() + lvt_7_1_.z * 4.0D);
               lvt_4_1_.playEvent((PlayerEntity)null, 1016, new BlockPos(this.parentEntity), 0);
               FireballEntity lvt_14_1_ = new FireballEntity(lvt_4_1_, this.parentEntity, lvt_8_1_, lvt_10_1_, lvt_12_1_);
               lvt_14_1_.explosionPower = this.parentEntity.getFireballStrength();
               lvt_14_1_.setPosition(this.parentEntity.func_226277_ct_() + lvt_7_1_.x * 4.0D, this.parentEntity.func_226283_e_(0.5D) + 0.5D, lvt_14_1_.func_226281_cx_() + lvt_7_1_.z * 4.0D);
               lvt_4_1_.addEntity(lvt_14_1_);
               this.attackTimer = -40;
            }
         } else if (this.attackTimer > 0) {
            --this.attackTimer;
         }

         this.parentEntity.setAttacking(this.attackTimer > 10);
      }
   }

   static class LookAroundGoal extends Goal {
      private final GhastEntity parentEntity;

      public LookAroundGoal(GhastEntity p_i45839_1_) {
         this.parentEntity = p_i45839_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         return true;
      }

      public void tick() {
         if (this.parentEntity.getAttackTarget() == null) {
            Vec3d lvt_1_1_ = this.parentEntity.getMotion();
            this.parentEntity.rotationYaw = -((float)MathHelper.atan2(lvt_1_1_.x, lvt_1_1_.z)) * 57.295776F;
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
         } else {
            LivingEntity lvt_1_2_ = this.parentEntity.getAttackTarget();
            double lvt_2_1_ = 64.0D;
            if (lvt_1_2_.getDistanceSq(this.parentEntity) < 4096.0D) {
               double lvt_4_1_ = lvt_1_2_.func_226277_ct_() - this.parentEntity.func_226277_ct_();
               double lvt_6_1_ = lvt_1_2_.func_226281_cx_() - this.parentEntity.func_226281_cx_();
               this.parentEntity.rotationYaw = -((float)MathHelper.atan2(lvt_4_1_, lvt_6_1_)) * 57.295776F;
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            }
         }

      }
   }

   static class RandomFlyGoal extends Goal {
      private final GhastEntity parentEntity;

      public RandomFlyGoal(GhastEntity p_i45836_1_) {
         this.parentEntity = p_i45836_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         MovementController lvt_1_1_ = this.parentEntity.getMoveHelper();
         if (!lvt_1_1_.isUpdating()) {
            return true;
         } else {
            double lvt_2_1_ = lvt_1_1_.getX() - this.parentEntity.func_226277_ct_();
            double lvt_4_1_ = lvt_1_1_.getY() - this.parentEntity.func_226278_cu_();
            double lvt_6_1_ = lvt_1_1_.getZ() - this.parentEntity.func_226281_cx_();
            double lvt_8_1_ = lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;
            return lvt_8_1_ < 1.0D || lvt_8_1_ > 3600.0D;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void startExecuting() {
         Random lvt_1_1_ = this.parentEntity.getRNG();
         double lvt_2_1_ = this.parentEntity.func_226277_ct_() + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double lvt_4_1_ = this.parentEntity.func_226278_cu_() + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double lvt_6_1_ = this.parentEntity.func_226281_cx_() + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.parentEntity.getMoveHelper().setMoveTo(lvt_2_1_, lvt_4_1_, lvt_6_1_, 1.0D);
      }
   }

   static class MoveHelperController extends MovementController {
      private final GhastEntity parentEntity;
      private int courseChangeCooldown;

      public MoveHelperController(GhastEntity p_i45838_1_) {
         super(p_i45838_1_);
         this.parentEntity = p_i45838_1_;
      }

      public void tick() {
         if (this.action == MovementController.Action.MOVE_TO) {
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
               Vec3d lvt_1_1_ = new Vec3d(this.posX - this.parentEntity.func_226277_ct_(), this.posY - this.parentEntity.func_226278_cu_(), this.posZ - this.parentEntity.func_226281_cx_());
               double lvt_2_1_ = lvt_1_1_.length();
               lvt_1_1_ = lvt_1_1_.normalize();
               if (this.func_220673_a(lvt_1_1_, MathHelper.ceil(lvt_2_1_))) {
                  this.parentEntity.setMotion(this.parentEntity.getMotion().add(lvt_1_1_.scale(0.1D)));
               } else {
                  this.action = MovementController.Action.WAIT;
               }
            }

         }
      }

      private boolean func_220673_a(Vec3d p_220673_1_, int p_220673_2_) {
         AxisAlignedBB lvt_3_1_ = this.parentEntity.getBoundingBox();

         for(int lvt_4_1_ = 1; lvt_4_1_ < p_220673_2_; ++lvt_4_1_) {
            lvt_3_1_ = lvt_3_1_.offset(p_220673_1_);
            if (!this.parentEntity.world.func_226665_a__(this.parentEntity, lvt_3_1_)) {
               return false;
            }
         }

         return true;
      }
   }
}
