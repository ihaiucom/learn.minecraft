package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
   private static final DataParameter<Boolean> SADDLED;
   private static final DataParameter<Integer> BOOST_TIME;
   private static final Ingredient TEMPTATION_ITEMS;
   private boolean boosting;
   private int boostTime;
   private int totalBoostTime;

   public PigEntity(EntityType<? extends PigEntity> p_i50250_1_, World p_i50250_2_) {
      super(p_i50250_1_, p_i50250_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.fromItems(Items.CARROT_ON_A_STICK), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, TEMPTATION_ITEMS));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   public boolean canBeSteered() {
      Entity lvt_1_1_ = this.getControllingPassenger();
      if (!(lvt_1_1_ instanceof PlayerEntity)) {
         return false;
      } else {
         PlayerEntity lvt_2_1_ = (PlayerEntity)lvt_1_1_;
         return lvt_2_1_.getHeldItemMainhand().getItem() == Items.CARROT_ON_A_STICK || lvt_2_1_.getHeldItemOffhand().getItem() == Items.CARROT_ON_A_STICK;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (BOOST_TIME.equals(p_184206_1_) && this.world.isRemote) {
         this.boosting = true;
         this.boostTime = 0;
         this.totalBoostTime = (Integer)this.dataManager.get(BOOST_TIME);
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SADDLED, false);
      this.dataManager.register(BOOST_TIME, 0);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("Saddle", this.getSaddled());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setSaddled(p_70037_1_.getBoolean("Saddle"));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PIG_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      if (super.processInteract(p_184645_1_, p_184645_2_)) {
         return true;
      } else {
         ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
         if (lvt_3_1_.getItem() == Items.NAME_TAG) {
            lvt_3_1_.interactWithEntity(p_184645_1_, this, p_184645_2_);
            return true;
         } else if (this.getSaddled() && !this.isBeingRidden()) {
            if (!this.world.isRemote) {
               p_184645_1_.startRiding(this);
            }

            return true;
         } else {
            return lvt_3_1_.getItem() == Items.SADDLE && lvt_3_1_.interactWithEntity(p_184645_1_, this, p_184645_2_);
         }
      }
   }

   protected void dropInventory() {
      super.dropInventory();
      if (this.getSaddled()) {
         this.entityDropItem(Items.SADDLE);
      }

   }

   public boolean getSaddled() {
      return (Boolean)this.dataManager.get(SADDLED);
   }

   public void setSaddled(boolean p_70900_1_) {
      if (p_70900_1_) {
         this.dataManager.set(SADDLED, true);
      } else {
         this.dataManager.set(SADDLED, false);
      }

   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
      ZombiePigmanEntity lvt_2_1_ = (ZombiePigmanEntity)EntityType.ZOMBIE_PIGMAN.create(this.world);
      lvt_2_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
      lvt_2_1_.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
      lvt_2_1_.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         lvt_2_1_.setCustomName(this.getCustomName());
         lvt_2_1_.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.world.addEntity(lvt_2_1_);
      this.remove();
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isAlive()) {
         Entity lvt_2_1_ = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
         if (this.isBeingRidden() && this.canBeSteered()) {
            this.rotationYaw = lvt_2_1_.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = lvt_2_1_.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.rotationYaw;
            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
            if (this.boosting && this.boostTime++ > this.totalBoostTime) {
               this.boosting = false;
            }

            if (this.canPassengerSteer()) {
               float lvt_3_1_ = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
               if (this.boosting) {
                  lvt_3_1_ += lvt_3_1_ * 1.15F * MathHelper.sin((float)this.boostTime / (float)this.totalBoostTime * 3.1415927F);
               }

               this.setAIMoveSpeed(lvt_3_1_);
               super.travel(new Vec3d(0.0D, 0.0D, 1.0D));
               this.newPosRotationIncrements = 0;
            } else {
               this.setMotion(Vec3d.ZERO);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double lvt_3_2_ = this.func_226277_ct_() - this.prevPosX;
            double lvt_5_1_ = this.func_226281_cx_() - this.prevPosZ;
            float lvt_7_1_ = MathHelper.sqrt(lvt_3_2_ * lvt_3_2_ + lvt_5_1_ * lvt_5_1_) * 4.0F;
            if (lvt_7_1_ > 1.0F) {
               lvt_7_1_ = 1.0F;
            }

            this.limbSwingAmount += (lvt_7_1_ - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
         } else {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.travel(p_213352_1_);
         }
      }
   }

   public boolean boost() {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.totalBoostTime = this.getRNG().nextInt(841) + 140;
         this.getDataManager().set(BOOST_TIME, this.totalBoostTime);
         return true;
      }
   }

   public PigEntity createChild(AgeableEntity p_90011_1_) {
      return (PigEntity)EntityType.PIG.create(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return TEMPTATION_ITEMS.test(p_70877_1_);
   }

   // $FF: synthetic method
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return this.createChild(p_90011_1_);
   }

   static {
      SADDLED = EntityDataManager.createKey(PigEntity.class, DataSerializers.BOOLEAN);
      BOOST_TIME = EntityDataManager.createKey(PigEntity.class, DataSerializers.VARINT);
      TEMPTATION_ITEMS = Ingredient.fromItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
   }
}
