package net.minecraft.entity.passive.fish;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractFishEntity extends WaterMobEntity {
   private static final DataParameter<Boolean> FROM_BUCKET;

   public AbstractFishEntity(EntityType<? extends AbstractFishEntity> p_i48855_1_, World p_i48855_2_) {
      super(p_i48855_1_, p_i48855_2_);
      this.moveController = new AbstractFishEntity.MoveHelperController(this);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.65F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
   }

   public boolean preventDespawn() {
      return this.isFromBucket();
   }

   public static boolean func_223363_b(EntityType<? extends AbstractFishEntity> p_223363_0_, IWorld p_223363_1_, SpawnReason p_223363_2_, BlockPos p_223363_3_, Random p_223363_4_) {
      return p_223363_1_.getBlockState(p_223363_3_).getBlock() == Blocks.WATER && p_223363_1_.getBlockState(p_223363_3_.up()).getBlock() == Blocks.WATER;
   }

   public boolean canDespawn(double p_213397_1_) {
      return !this.isFromBucket() && !this.hasCustomName();
   }

   public int getMaxSpawnedInChunk() {
      return 8;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(FROM_BUCKET, false);
   }

   private boolean isFromBucket() {
      return (Boolean)this.dataManager.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean p_203706_1_) {
      this.dataManager.set(FROM_BUCKET, p_203706_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("FromBucket", this.isFromBucket());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setFromBucket(p_70037_1_.getBoolean("FromBucket"));
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      GoalSelector var10000 = this.goalSelector;
      Predicate var10009 = EntityPredicates.NOT_SPECTATING;
      var10009.getClass();
      var10000.addGoal(2, new AvoidEntityGoal(this, PlayerEntity.class, 8.0F, 1.6D, 1.4D, var10009::test));
      this.goalSelector.addGoal(4, new AbstractFishEntity.SwimGoal(this));
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new SwimmerPathNavigator(this, p_175447_1_);
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(0.01F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
         if (this.getAttackTarget() == null) {
            this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public void livingTick() {
      if (!this.isInWater() && this.onGround && this.collidedVertically) {
         this.setMotion(this.getMotion().add((double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645D, (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.05F)));
         this.onGround = false;
         this.isAirBorne = true;
         this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getSoundPitch());
      }

      super.livingTick();
   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (lvt_3_1_.getItem() == Items.WATER_BUCKET && this.isAlive()) {
         this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
         lvt_3_1_.shrink(1);
         ItemStack lvt_4_1_ = this.getFishBucket();
         this.setBucketData(lvt_4_1_);
         if (!this.world.isRemote) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_184645_1_, lvt_4_1_);
         }

         if (lvt_3_1_.isEmpty()) {
            p_184645_1_.setHeldItem(p_184645_2_, lvt_4_1_);
         } else if (!p_184645_1_.inventory.addItemStackToInventory(lvt_4_1_)) {
            p_184645_1_.dropItem(lvt_4_1_, false);
         }

         this.remove();
         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   protected void setBucketData(ItemStack p_204211_1_) {
      if (this.hasCustomName()) {
         p_204211_1_.setDisplayName(this.getCustomName());
      }

   }

   protected abstract ItemStack getFishBucket();

   protected boolean func_212800_dy() {
      return true;
   }

   protected abstract SoundEvent getFlopSound();

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_FISH_SWIM;
   }

   static {
      FROM_BUCKET = EntityDataManager.createKey(AbstractFishEntity.class, DataSerializers.BOOLEAN);
   }

   static class MoveHelperController extends MovementController {
      private final AbstractFishEntity fish;

      MoveHelperController(AbstractFishEntity p_i48857_1_) {
         super(p_i48857_1_);
         this.fish = p_i48857_1_;
      }

      public void tick() {
         if (this.fish.areEyesInFluid(FluidTags.WATER)) {
            this.fish.setMotion(this.fish.getMotion().add(0.0D, 0.005D, 0.0D));
         }

         if (this.action == MovementController.Action.MOVE_TO && !this.fish.getNavigator().noPath()) {
            double lvt_1_1_ = this.posX - this.fish.func_226277_ct_();
            double lvt_3_1_ = this.posY - this.fish.func_226278_cu_();
            double lvt_5_1_ = this.posZ - this.fish.func_226281_cx_();
            double lvt_7_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_);
            lvt_3_1_ /= lvt_7_1_;
            float lvt_9_1_ = (float)(MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
            this.fish.rotationYaw = this.limitAngle(this.fish.rotationYaw, lvt_9_1_, 90.0F);
            this.fish.renderYawOffset = this.fish.rotationYaw;
            float lvt_10_1_ = (float)(this.speed * this.fish.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.fish.setAIMoveSpeed(MathHelper.lerp(0.125F, this.fish.getAIMoveSpeed(), lvt_10_1_));
            this.fish.setMotion(this.fish.getMotion().add(0.0D, (double)this.fish.getAIMoveSpeed() * lvt_3_1_ * 0.1D, 0.0D));
         } else {
            this.fish.setAIMoveSpeed(0.0F);
         }
      }
   }

   static class SwimGoal extends RandomSwimmingGoal {
      private final AbstractFishEntity fish;

      public SwimGoal(AbstractFishEntity p_i48856_1_) {
         super(p_i48856_1_, 1.0D, 40);
         this.fish = p_i48856_1_;
      }

      public boolean shouldExecute() {
         return this.fish.func_212800_dy() && super.shouldExecute();
      }
   }
}
