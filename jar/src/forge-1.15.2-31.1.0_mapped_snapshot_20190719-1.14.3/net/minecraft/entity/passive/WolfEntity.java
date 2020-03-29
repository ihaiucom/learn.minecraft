package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BegGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class WolfEntity extends TameableEntity {
   private static final DataParameter<Boolean> BEGGING;
   private static final DataParameter<Integer> COLLAR_COLOR;
   public static final Predicate<LivingEntity> field_213441_bD;
   private float headRotationCourse;
   private float headRotationCourseOld;
   private boolean isWet;
   private boolean isShaking;
   private float timeWolfIsShaking;
   private float prevTimeWolfIsShaking;

   public WolfEntity(EntityType<? extends WolfEntity> p_i50240_1_, World p_i50240_2_) {
      super(p_i50240_1_, p_i50240_2_);
      this.setTamed(false);
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, new WolfEntity.AvoidEntityGoal(this, LlamaEntity.class, 24.0F, 1.5D, 1.5D));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp());
      this.targetSelector.addGoal(4, new NonTamedTargetGoal(this, AnimalEntity.class, false, field_213441_bD));
      this.targetSelector.addGoal(4, new NonTamedTargetGoal(this, TurtleEntity.class, false, TurtleEntity.TARGET_DRY_BABY));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, AbstractSkeletonEntity.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      if (this.isTamed()) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   public void setAttackTarget(@Nullable LivingEntity p_70624_1_) {
      super.setAttackTarget(p_70624_1_);
      if (p_70624_1_ == null) {
         this.setAngry(false);
      } else if (!this.isTamed()) {
         this.setAngry(true);
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(BEGGING, false);
      this.dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("Angry", this.isAngry());
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setAngry(p_70037_1_.getBoolean("Angry"));
      if (p_70037_1_.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(p_70037_1_.getInt("CollarColor")));
      }

   }

   protected SoundEvent getAmbientSound() {
      if (this.isAngry()) {
         return SoundEvents.ENTITY_WOLF_GROWL;
      } else if (this.rand.nextInt(3) != 0) {
         return SoundEvents.ENTITY_WOLF_AMBIENT;
      } else {
         return this.isTamed() && this.getHealth() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WOLF_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
         this.world.setEntityState(this, (byte)8);
      }

      if (!this.world.isRemote && this.getAttackTarget() == null && this.isAngry()) {
         this.setAngry(false);
      }

   }

   public void tick() {
      super.tick();
      if (this.isAlive()) {
         this.headRotationCourseOld = this.headRotationCourse;
         if (this.isBegging()) {
            this.headRotationCourse += (1.0F - this.headRotationCourse) * 0.4F;
         } else {
            this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;
         }

         if (this.isInWaterRainOrBubbleColumn()) {
            this.isWet = true;
            this.isShaking = false;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
         } else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.timeWolfIsShaking == 0.0F) {
               this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
            this.timeWolfIsShaking += 0.05F;
            if (this.prevTimeWolfIsShaking >= 2.0F) {
               this.isWet = false;
               this.isShaking = false;
               this.prevTimeWolfIsShaking = 0.0F;
               this.timeWolfIsShaking = 0.0F;
            }

            if (this.timeWolfIsShaking > 0.4F) {
               float f = (float)this.func_226278_cu_();
               int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * 3.1415927F) * 7.0F);
               Vec3d vec3d = this.getMotion();

               for(int j = 0; j < i; ++j) {
                  float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
                  float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
                  this.world.addParticle(ParticleTypes.SPLASH, this.func_226277_ct_() + (double)f1, (double)(f + 0.8F), this.func_226281_cx_() + (double)f2, vec3d.x, vec3d.y, vec3d.z);
               }
            }
         }
      }

   }

   public void onDeath(DamageSource p_70645_1_) {
      this.isWet = false;
      this.isShaking = false;
      this.prevTimeWolfIsShaking = 0.0F;
      this.timeWolfIsShaking = 0.0F;
      super.onDeath(p_70645_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWolfWet() {
      return this.isWet;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadingWhileWet(float p_70915_1_) {
      return 0.75F + MathHelper.lerp(p_70915_1_, this.prevTimeWolfIsShaking, this.timeWolfIsShaking) / 2.0F * 0.25F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShakeAngle(float p_70923_1_, float p_70923_2_) {
      float f = (MathHelper.lerp(p_70923_1_, this.prevTimeWolfIsShaking, this.timeWolfIsShaking) + p_70923_2_) / 1.8F;
      if (f < 0.0F) {
         f = 0.0F;
      } else if (f > 1.0F) {
         f = 1.0F;
      }

      return MathHelper.sin(f * 3.1415927F) * MathHelper.sin(f * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getInterestedAngle(float p_70917_1_) {
      return MathHelper.lerp(p_70917_1_, this.headRotationCourseOld, this.headRotationCourse) * 0.15F * 3.1415927F;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.8F;
   }

   public int getVerticalFaceSpeed() {
      return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getTrueSource();
         if (this.sitGoal != null) {
            this.sitGoal.setSitting(false);
         }

         if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
            p_70097_2_ = (p_70097_2_ + 1.0F) / 2.0F;
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (flag) {
         this.applyEnchantments(this, p_70652_1_);
      }

      return flag;
   }

   public void setTamed(boolean p_70903_1_) {
      super.setTamed(p_70903_1_);
      if (p_70903_1_) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
         this.setHealth(20.0F);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      Item item = itemstack.getItem();
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (this.world.isRemote) {
         return this.isOwner(p_184645_1_) || item == Items.BONE && !this.isAngry();
      } else {
         if (this.isTamed()) {
            if (item.isFood() && item.getFood().isMeat() && this.getHealth() < this.getMaxHealth()) {
               if (!p_184645_1_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               this.heal((float)item.getFood().getHealing());
               return true;
            }

            if (!(item instanceof DyeItem)) {
               boolean flag = super.processInteract(p_184645_1_, p_184645_2_);
               if (!flag || this.isChild()) {
                  this.sitGoal.setSitting(!this.isSitting());
               }

               return flag;
            }

            DyeColor dyecolor = ((DyeItem)item).getDyeColor();
            if (dyecolor != this.getCollarColor()) {
               this.setCollarColor(dyecolor);
               if (!p_184645_1_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (this.isOwner(p_184645_1_) && !this.isBreedingItem(itemstack)) {
               this.sitGoal.setSitting(!this.isSitting());
               this.isJumping = false;
               this.navigator.clearPath();
               this.setAttackTarget((LivingEntity)null);
            }
         } else if (item == Items.BONE && !this.isAngry()) {
            if (!p_184645_1_.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }

            if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, p_184645_1_)) {
               this.setTamedBy(p_184645_1_);
               this.navigator.clearPath();
               this.setAttackTarget((LivingEntity)null);
               this.sitGoal.setSitting(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.world.setEntityState(this, (byte)6);
            }

            return true;
         }

         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 8) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getTailRotation() {
      if (this.isAngry()) {
         return 1.5393804F;
      } else {
         return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * 3.1415927F : 0.62831855F;
      }
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      Item item = p_70877_1_.getItem();
      return item.isFood() && item.getFood().isMeat();
   }

   public int getMaxSpawnedInChunk() {
      return 8;
   }

   public boolean isAngry() {
      return ((Byte)this.dataManager.get(TAMED) & 2) != 0;
   }

   public void setAngry(boolean p_70916_1_) {
      byte b0 = (Byte)this.dataManager.get(TAMED);
      if (p_70916_1_) {
         this.dataManager.set(TAMED, (byte)(b0 | 2));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -3));
      }

   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.dataManager.get(COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_175547_1_) {
      this.dataManager.set(COLLAR_COLOR, p_175547_1_.getId());
   }

   public WolfEntity createChild(AgeableEntity p_90011_1_) {
      WolfEntity wolfentity = (WolfEntity)EntityType.WOLF.create(this.world);
      UUID uuid = this.getOwnerId();
      if (uuid != null) {
         wolfentity.setOwnerId(uuid);
         wolfentity.setTamed(true);
      }

      return wolfentity;
   }

   public void setBegging(boolean p_70918_1_) {
      this.dataManager.set(BEGGING, p_70918_1_);
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(p_70878_1_ instanceof WolfEntity)) {
         return false;
      } else {
         WolfEntity wolfentity = (WolfEntity)p_70878_1_;
         if (!wolfentity.isTamed()) {
            return false;
         } else if (wolfentity.isSitting()) {
            return false;
         } else {
            return this.isInLove() && wolfentity.isInLove();
         }
      }
   }

   public boolean isBegging() {
      return (Boolean)this.dataManager.get(BEGGING);
   }

   public boolean shouldAttackEntity(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
      if (!(p_142018_1_ instanceof CreeperEntity) && !(p_142018_1_ instanceof GhastEntity)) {
         if (p_142018_1_ instanceof WolfEntity) {
            WolfEntity wolfentity = (WolfEntity)p_142018_1_;
            return !wolfentity.isTamed() || wolfentity.getOwner() != p_142018_2_;
         } else if (p_142018_1_ instanceof PlayerEntity && p_142018_2_ instanceof PlayerEntity && !((PlayerEntity)p_142018_2_).canAttackPlayer((PlayerEntity)p_142018_1_)) {
            return false;
         } else if (p_142018_1_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_142018_1_).isTame()) {
            return false;
         } else {
            return !(p_142018_1_ instanceof TameableEntity) || !((TameableEntity)p_142018_1_).isTamed();
         }
      } else {
         return false;
      }
   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return !this.isAngry() && super.canBeLeashedTo(p_184652_1_);
   }

   static {
      BEGGING = EntityDataManager.createKey(WolfEntity.class, DataSerializers.BOOLEAN);
      COLLAR_COLOR = EntityDataManager.createKey(WolfEntity.class, DataSerializers.VARINT);
      field_213441_bD = (p_lambda$static$0_0_) -> {
         EntityType<?> entitytype = p_lambda$static$0_0_.getType();
         return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
      };
   }

   class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final WolfEntity wolf;

      public AvoidEntityGoal(WolfEntity p_i47251_2_, Class<T> p_i47251_3_, float p_i47251_4_, double p_i47251_5_, double p_i47251_7_) {
         super(p_i47251_2_, p_i47251_3_, p_i47251_4_, p_i47251_5_, p_i47251_7_);
         this.wolf = p_i47251_2_;
      }

      public boolean shouldExecute() {
         if (super.shouldExecute() && this.field_75376_d instanceof LlamaEntity) {
            return !this.wolf.isTamed() && this.avoidLlama((LlamaEntity)this.field_75376_d);
         } else {
            return false;
         }
      }

      private boolean avoidLlama(LlamaEntity p_190854_1_) {
         return p_190854_1_.getStrength() >= WolfEntity.this.rand.nextInt(5);
      }

      public void startExecuting() {
         WolfEntity.this.setAttackTarget((LivingEntity)null);
         super.startExecuting();
      }

      public void tick() {
         WolfEntity.this.setAttackTarget((LivingEntity)null);
         super.tick();
      }
   }
}
