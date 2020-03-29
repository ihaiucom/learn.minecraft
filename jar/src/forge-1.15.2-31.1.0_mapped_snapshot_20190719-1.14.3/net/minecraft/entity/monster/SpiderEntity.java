package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event.Result;

public class SpiderEntity extends MonsterEntity {
   private static final DataParameter<Byte> CLIMBING;

   public SpiderEntity(EntityType<? extends SpiderEntity> p_i48550_1_, World p_i48550_2_) {
      super(p_i48550_1_, p_i48550_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new SpiderEntity.AttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new SpiderEntity.TargetGoal(this, PlayerEntity.class));
      this.targetSelector.addGoal(3, new SpiderEntity.TargetGoal(this, IronGolemEntity.class));
   }

   public double getMountedYOffset() {
      return (double)(this.getHeight() * 0.5F);
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new ClimberPathNavigator(this, p_175447_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CLIMBING, (byte)0);
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public void setMotionMultiplier(BlockState p_213295_1_, Vec3d p_213295_2_) {
      if (p_213295_1_.getBlock() != Blocks.COBWEB) {
         super.setMotionMultiplier(p_213295_1_, p_213295_2_);
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }

   public boolean isPotionApplicable(EffectInstance p_70687_1_) {
      if (p_70687_1_.getPotion() == Effects.POISON) {
         PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, p_70687_1_);
         MinecraftForge.EVENT_BUS.post(event);
         return event.getResult() == Result.ALLOW;
      } else {
         return super.isPotionApplicable(p_70687_1_);
      }
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.dataManager.get(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean p_70839_1_) {
      byte b0 = (Byte)this.dataManager.get(CLIMBING);
      if (p_70839_1_) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 &= -2;
      }

      this.dataManager.set(CLIMBING, b0);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      ILivingEntityData p_213386_4_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (p_213386_1_.getRandom().nextInt(100) == 0) {
         SkeletonEntity skeletonentity = (SkeletonEntity)EntityType.SKELETON.create(this.world);
         skeletonentity.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, 0.0F);
         skeletonentity.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)null, (CompoundNBT)null);
         p_213386_1_.addEntity(skeletonentity);
         skeletonentity.startRiding(this);
      }

      if (p_213386_4_ == null) {
         p_213386_4_ = new SpiderEntity.GroupData();
         if (p_213386_1_.getDifficulty() == Difficulty.HARD && p_213386_1_.getRandom().nextFloat() < 0.1F * p_213386_2_.getClampedAdditionalDifficulty()) {
            ((SpiderEntity.GroupData)p_213386_4_).setRandomEffect(p_213386_1_.getRandom());
         }
      }

      if (p_213386_4_ instanceof SpiderEntity.GroupData) {
         Effect effect = ((SpiderEntity.GroupData)p_213386_4_).effect;
         if (effect != null) {
            this.addPotionEffect(new EffectInstance(effect, Integer.MAX_VALUE));
         }
      }

      return (ILivingEntityData)p_213386_4_;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.65F;
   }

   static {
      CLIMBING = EntityDataManager.createKey(SpiderEntity.class, DataSerializers.BYTE);
   }

   static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public TargetGoal(SpiderEntity p_i45818_1_, Class<T> p_i45818_2_) {
         super(p_i45818_1_, p_i45818_2_, true);
      }

      public boolean shouldExecute() {
         float f = this.goalOwner.getBrightness();
         return f >= 0.5F ? false : super.shouldExecute();
      }
   }

   public static class GroupData implements ILivingEntityData {
      public Effect effect;

      public void setRandomEffect(Random p_111104_1_) {
         int i = p_111104_1_.nextInt(5);
         if (i <= 1) {
            this.effect = Effects.SPEED;
         } else if (i <= 2) {
            this.effect = Effects.STRENGTH;
         } else if (i <= 3) {
            this.effect = Effects.REGENERATION;
         } else if (i <= 4) {
            this.effect = Effects.INVISIBILITY;
         }

      }
   }

   static class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(SpiderEntity p_i46676_1_) {
         super(p_i46676_1_, 1.0D, true);
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.attacker.isBeingRidden();
      }

      public boolean shouldContinueExecuting() {
         float f = this.attacker.getBrightness();
         if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
            this.attacker.setAttackTarget((LivingEntity)null);
            return false;
         } else {
            return super.shouldContinueExecuting();
         }
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         return (double)(4.0F + p_179512_1_.getWidth());
      }
   }
}
