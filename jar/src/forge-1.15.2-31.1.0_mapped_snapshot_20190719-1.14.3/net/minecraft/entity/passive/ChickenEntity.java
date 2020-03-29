package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChickenEntity extends AnimalEntity {
   private static final Ingredient TEMPTATION_ITEMS;
   public float wingRotation;
   public float destPos;
   public float oFlapSpeed;
   public float oFlap;
   public float wingRotDelta = 1.0F;
   public int timeUntilNextEgg;
   public boolean chickenJockey;

   public ChickenEntity(EntityType<? extends ChickenEntity> p_i50282_1_, World p_i50282_2_) {
      super(p_i50282_1_, p_i50282_2_);
      this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      this.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, false, TEMPTATION_ITEMS));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isChild() ? p_213348_2_.height * 0.85F : p_213348_2_.height * 0.92F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public void livingTick() {
      super.livingTick();
      this.oFlap = this.wingRotation;
      this.oFlapSpeed = this.destPos;
      this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
      this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
      if (!this.onGround && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
      Vec3d lvt_1_1_ = this.getMotion();
      if (!this.onGround && lvt_1_1_.y < 0.0D) {
         this.setMotion(lvt_1_1_.mul(1.0D, 0.6D, 1.0D));
      }

      this.wingRotation += this.wingRotDelta * 2.0F;
      if (!this.world.isRemote && this.isAlive() && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
         this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.entityDropItem(Items.EGG);
         this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_CHICKEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_CHICKEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CHICKEN_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
   }

   public ChickenEntity createChild(AgeableEntity p_90011_1_) {
      return (ChickenEntity)EntityType.CHICKEN.create(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return TEMPTATION_ITEMS.test(p_70877_1_);
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      return this.isChickenJockey() ? 10 : super.getExperiencePoints(p_70693_1_);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.chickenJockey = p_70037_1_.getBoolean("IsChickenJockey");
      if (p_70037_1_.contains("EggLayTime")) {
         this.timeUntilNextEgg = p_70037_1_.getInt("EggLayTime");
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("IsChickenJockey", this.chickenJockey);
      p_213281_1_.putInt("EggLayTime", this.timeUntilNextEgg);
   }

   public boolean canDespawn(double p_213397_1_) {
      return this.isChickenJockey() && !this.isBeingRidden();
   }

   public void updatePassenger(Entity p_184232_1_) {
      super.updatePassenger(p_184232_1_);
      float lvt_2_1_ = MathHelper.sin(this.renderYawOffset * 0.017453292F);
      float lvt_3_1_ = MathHelper.cos(this.renderYawOffset * 0.017453292F);
      float lvt_4_1_ = 0.1F;
      float lvt_5_1_ = 0.0F;
      p_184232_1_.setPosition(this.func_226277_ct_() + (double)(0.1F * lvt_2_1_), this.func_226283_e_(0.5D) + p_184232_1_.getYOffset() + 0.0D, this.func_226281_cx_() - (double)(0.1F * lvt_3_1_));
      if (p_184232_1_ instanceof LivingEntity) {
         ((LivingEntity)p_184232_1_).renderYawOffset = this.renderYawOffset;
      }

   }

   public boolean isChickenJockey() {
      return this.chickenJockey;
   }

   public void setChickenJockey(boolean p_152117_1_) {
      this.chickenJockey = p_152117_1_;
   }

   // $FF: synthetic method
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return this.createChild(p_90011_1_);
   }

   static {
      TEMPTATION_ITEMS = Ingredient.fromItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   }
}
