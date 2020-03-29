package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PolarBearEntity extends AnimalEntity {
   private static final DataParameter<Boolean> IS_STANDING;
   private float clientSideStandAnimation0;
   private float clientSideStandAnimation;
   private int warningSoundTicks;

   public PolarBearEntity(EntityType<? extends PolarBearEntity> p_i50249_1_, World p_i50249_2_) {
      super(p_i50249_1_, p_i50249_2_);
   }

   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return (AgeableEntity)EntityType.POLAR_BEAR.create(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PolarBearEntity.MeleeAttackGoal());
      this.goalSelector.addGoal(1, new PolarBearEntity.PanicGoal());
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new PolarBearEntity.HurtByTargetGoal());
      this.targetSelector.addGoal(2, new PolarBearEntity.AttackPlayerGoal());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, FoxEntity.class, 10, true, true, (Predicate)null));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public static boolean func_223320_c(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason p_223320_2_, BlockPos p_223320_3_, Random p_223320_4_) {
      Biome lvt_5_1_ = p_223320_1_.func_226691_t_(p_223320_3_);
      if (lvt_5_1_ != Biomes.FROZEN_OCEAN && lvt_5_1_ != Biomes.DEEP_FROZEN_OCEAN) {
         return func_223316_b(p_223320_0_, p_223320_1_, p_223320_2_, p_223320_3_, p_223320_4_);
      } else {
         return p_223320_1_.func_226659_b_(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.down()).getBlock() == Blocks.ICE;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_POLAR_BEAR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
   }

   protected void playWarningSound() {
      if (this.warningSoundTicks <= 0) {
         this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
         this.warningSoundTicks = 40;
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(IS_STANDING, false);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideStandAnimation != this.clientSideStandAnimation0) {
            this.recalculateSize();
         }

         this.clientSideStandAnimation0 = this.clientSideStandAnimation;
         if (this.isStanding()) {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
         } else {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
         }
      }

      if (this.warningSoundTicks > 0) {
         --this.warningSoundTicks;
      }

   }

   public EntitySize getSize(Pose p_213305_1_) {
      if (this.clientSideStandAnimation > 0.0F) {
         float lvt_2_1_ = this.clientSideStandAnimation / 6.0F;
         float lvt_3_1_ = 1.0F + lvt_2_1_;
         return super.getSize(p_213305_1_).scale(1.0F, lvt_3_1_);
      } else {
         return super.getSize(p_213305_1_);
      }
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean lvt_2_1_ = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (lvt_2_1_) {
         this.applyEnchantments(this, p_70652_1_);
      }

      return lvt_2_1_;
   }

   public boolean isStanding() {
      return (Boolean)this.dataManager.get(IS_STANDING);
   }

   public void setStanding(boolean p_189794_1_) {
      this.dataManager.set(IS_STANDING, p_189794_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStandingAnimationScale(float p_189795_1_) {
      return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimation0, this.clientSideStandAnimation) / 6.0F;
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226258_a_(1.0F);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   static {
      IS_STANDING = EntityDataManager.createKey(PolarBearEntity.class, DataSerializers.BOOLEAN);
   }

   class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      public PanicGoal() {
         super(PolarBearEntity.this, 2.0D);
      }

      public boolean shouldExecute() {
         return !PolarBearEntity.this.isChild() && !PolarBearEntity.this.isBurning() ? false : super.shouldExecute();
      }
   }

   class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
      public MeleeAttackGoal() {
         super(PolarBearEntity.this, 1.25D, true);
      }

      protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
         double lvt_4_1_ = this.getAttackReachSqr(p_190102_1_);
         if (p_190102_2_ <= lvt_4_1_ && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.attackEntityAsMob(p_190102_1_);
            PolarBearEntity.this.setStanding(false);
         } else if (p_190102_2_ <= lvt_4_1_ * 2.0D) {
            if (this.attackTick <= 0) {
               PolarBearEntity.this.setStanding(false);
               this.attackTick = 20;
            }

            if (this.attackTick <= 10) {
               PolarBearEntity.this.setStanding(true);
               PolarBearEntity.this.playWarningSound();
            }
         } else {
            this.attackTick = 20;
            PolarBearEntity.this.setStanding(false);
         }

      }

      public void resetTask() {
         PolarBearEntity.this.setStanding(false);
         super.resetTask();
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         return (double)(4.0F + p_179512_1_.getWidth());
      }
   }

   class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public AttackPlayerGoal() {
         super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate)null);
      }

      public boolean shouldExecute() {
         if (PolarBearEntity.this.isChild()) {
            return false;
         } else {
            if (super.shouldExecute()) {
               List<PolarBearEntity> lvt_1_1_ = PolarBearEntity.this.world.getEntitiesWithinAABB(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
               Iterator var2 = lvt_1_1_.iterator();

               while(var2.hasNext()) {
                  PolarBearEntity lvt_3_1_ = (PolarBearEntity)var2.next();
                  if (lvt_3_1_.isChild()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.5D;
      }
   }

   class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(PolarBearEntity.this);
      }

      public void startExecuting() {
         super.startExecuting();
         if (PolarBearEntity.this.isChild()) {
            this.alertOthers();
            this.resetTask();
         }

      }

      protected void setAttackTarget(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
         if (p_220793_1_ instanceof PolarBearEntity && !p_220793_1_.isChild()) {
            super.setAttackTarget(p_220793_1_, p_220793_2_);
         }

      }
   }
}
