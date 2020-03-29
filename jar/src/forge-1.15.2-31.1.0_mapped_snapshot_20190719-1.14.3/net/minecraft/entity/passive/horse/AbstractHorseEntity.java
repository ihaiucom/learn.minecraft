package net.minecraft.entity.passive.horse;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractHorseEntity extends AnimalEntity implements IInventoryChangedListener, IJumpingMount {
   private static final Predicate<LivingEntity> IS_HORSE_BREEDING = (p_lambda$static$0_0_) -> {
      return p_lambda$static$0_0_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_lambda$static$0_0_).isBreeding();
   };
   private static final EntityPredicate field_213618_bK;
   protected static final IAttribute JUMP_STRENGTH;
   private static final DataParameter<Byte> STATUS;
   private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID;
   private int eatingCounter;
   private int openMouthCounter;
   private int jumpRearingCounter;
   public int tailCounter;
   public int sprintCounter;
   protected boolean horseJumping;
   protected Inventory horseChest;
   protected int temper;
   protected float jumpPower;
   private boolean allowStandSliding;
   private float headLean;
   private float prevHeadLean;
   private float rearingAmount;
   private float prevRearingAmount;
   private float mouthOpenness;
   private float prevMouthOpenness;
   protected boolean canGallop = true;
   protected int gallopTime;
   private LazyOptional<?> itemHandler = null;

   protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> p_i48563_1_, World p_i48563_2_) {
      super(p_i48563_1_, p_i48563_2_);
      this.stepHeight = 1.0F;
      this.initHorseChest();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.initExtraAI();
   }

   protected void initExtraAI() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   protected boolean getHorseWatchableBoolean(int p_110233_1_) {
      return ((Byte)this.dataManager.get(STATUS) & p_110233_1_) != 0;
   }

   protected void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
      byte b0 = (Byte)this.dataManager.get(STATUS);
      if (p_110208_2_) {
         this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
      } else {
         this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
      }

   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   @Nullable
   public UUID getOwnerUniqueId() {
      return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orElse((UUID)null);
   }

   public void setOwnerUniqueId(@Nullable UUID p_184779_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184779_1_));
   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean p_110234_1_) {
      this.setHorseWatchableBoolean(2, p_110234_1_);
   }

   public void setHorseJumping(boolean p_110255_1_) {
      this.horseJumping = p_110255_1_;
   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return super.canBeLeashedTo(p_184652_1_) && this.getCreatureAttribute() != CreatureAttribute.UNDEAD;
   }

   protected void onLeashDistance(float p_142017_1_) {
      if (p_142017_1_ > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }

   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(8);
   }

   public void setBreeding(boolean p_110242_1_) {
      this.setHorseWatchableBoolean(8, p_110242_1_);
   }

   public void setHorseSaddled(boolean p_110251_1_) {
      this.setHorseWatchableBoolean(4, p_110251_1_);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int p_110238_1_) {
      this.temper = p_110238_1_;
   }

   public int increaseTemper(int p_110198_1_) {
      int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
      this.setTemper(i);
      return i;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      Entity entity = p_70097_1_.getTrueSource();
      return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public boolean canBePushed() {
      return !this.isBeingRidden();
   }

   private void eatingHorse() {
      this.openHorseMouth();
      if (!this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      if (p_225503_1_ > 1.0F) {
         this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
      }

      int i = this.func_225508_e_(p_225503_1_, p_225503_2_);
      if (i <= 0) {
         return false;
      } else {
         this.attackEntityFrom(DamageSource.FALL, (float)i);
         if (this.isBeingRidden()) {
            Iterator var4 = this.getRecursivePassengers().iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               entity.attackEntityFrom(DamageSource.FALL, (float)i);
            }
         }

         this.func_226295_cZ_();
         return true;
      }
   }

   protected int func_225508_e_(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ * 0.5F - 3.0F) * p_225508_2_);
   }

   protected int getInventorySize() {
      return 2;
   }

   protected void initHorseChest() {
      Inventory inventory = this.horseChest;
      this.horseChest = new Inventory(this.getInventorySize());
      if (inventory != null) {
         inventory.removeListener(this);
         int i = Math.min(inventory.getSizeInventory(), this.horseChest.getSizeInventory());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               this.horseChest.setInventorySlotContents(j, itemstack.copy());
            }
         }
      }

      this.horseChest.addListener(this);
      this.updateHorseSlots();
      this.itemHandler = LazyOptional.of(() -> {
         return new InvWrapper(this.horseChest);
      });
   }

   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         this.setHorseSaddled(!this.horseChest.getStackInSlot(0).isEmpty() && this.canBeSaddled());
      }

   }

   public void onInventoryChanged(IInventory p_76316_1_) {
      boolean flag = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20 && !flag && this.isHorseSaddled()) {
         this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
      }

   }

   public double getHorseJumpStrength() {
      return this.getAttribute(JUMP_STRENGTH).getValue();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      return null;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      return null;
   }

   public boolean canBeSaddled() {
      return true;
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   @Nullable
   protected SoundEvent getAngrySound() {
      this.makeHorseRear();
      return null;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         BlockState blockstate = this.world.getBlockState(p_180429_1_.up());
         SoundType soundtype = p_180429_2_.getSoundType(this.world, p_180429_1_, this);
         if (blockstate.getBlock() == Blocks.SNOW) {
            soundtype = blockstate.getSoundType(this.world, p_180429_1_, this);
         }

         if (this.isBeingRidden() && this.canGallop) {
            ++this.gallopTime;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playGallopSound(soundtype);
            } else if (this.gallopTime <= 5) {
               this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
         } else if (soundtype == SoundType.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         }
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, p_190680_1_.getVolume() * 0.15F, p_190680_1_.getPitch());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(JUMP_STRENGTH);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(53.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22499999403953552D);
   }

   public int getMaxSpawnedInChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   protected float getSoundVolume() {
      return 0.8F;
   }

   public int getTalkInterval() {
      return 400;
   }

   public void openGUI(PlayerEntity p_110199_1_) {
      if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(p_110199_1_)) && this.isTame()) {
         p_110199_1_.openHorseInventory(this, this.horseChest);
      }

   }

   protected boolean handleEating(PlayerEntity p_190678_1_, ItemStack p_190678_2_) {
      boolean flag = false;
      float f = 0.0F;
      int i = 0;
      int j = 0;
      Item item = p_190678_2_.getItem();
      if (item == Items.WHEAT) {
         f = 2.0F;
         i = 20;
         j = 3;
      } else if (item == Items.SUGAR) {
         f = 1.0F;
         i = 30;
         j = 3;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         f = 20.0F;
         i = 180;
      } else if (item == Items.APPLE) {
         f = 3.0F;
         i = 60;
         j = 3;
      } else if (item == Items.GOLDEN_CARROT) {
         f = 4.0F;
         i = 60;
         j = 5;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
         f = 10.0F;
         i = 240;
         j = 10;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isChild() && i > 0) {
         this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.addGrowth(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.world.isRemote) {
            this.increaseTemper(j);
         }
      }

      if (flag) {
         this.eatingHorse();
      }

      return flag;
   }

   protected void mountTo(PlayerEntity p_110237_1_) {
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.world.isRemote) {
         p_110237_1_.rotationYaw = this.rotationYaw;
         p_110237_1_.rotationPitch = this.rotationPitch;
         p_110237_1_.startRiding(this);
      }

   }

   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() && this.isBeingRidden() && this.isHorseSaddled() || this.isEatingHaystack() || this.isRearing();
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   private void moveTail() {
      this.tailCounter = 1;
   }

   protected void dropInventory() {
      super.dropInventory();
      if (this.horseChest != null) {
         for(int i = 0; i < this.horseChest.getSizeInventory(); ++i) {
            ItemStack itemstack = this.horseChest.getStackInSlot(i);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
               this.entityDropItem(itemstack);
            }
         }
      }

   }

   public void livingTick() {
      if (this.rand.nextInt(200) == 0) {
         this.moveTail();
      }

      super.livingTick();
      if (!this.world.isRemote && this.isAlive()) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (this.canEatGrass()) {
            if (!this.isEatingHaystack() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState((new BlockPos(this)).down()).getBlock() == Blocks.GRASS_BLOCK) {
               this.setEatingHaystack(true);
            }

            if (this.isEatingHaystack() && ++this.eatingCounter > 50) {
               this.eatingCounter = 0;
               this.setEatingHaystack(false);
            }
         }

         this.followMother();
      }

   }

   protected void followMother() {
      if (this.isBreeding() && this.isChild() && !this.isEatingHaystack()) {
         LivingEntity livingentity = this.world.getClosestEntityWithinAABB(AbstractHorseEntity.class, field_213618_bK, this, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.getBoundingBox().grow(16.0D));
         if (livingentity != null && this.getDistanceSq(livingentity) > 4.0D) {
            this.navigator.getPathToEntityLiving(livingentity, 0);
         }
      }

   }

   public boolean canEatGrass() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(64, false);
      }

      if ((this.canPassengerSteer() || this.isServerWorld()) && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.tailCounter > 0 && ++this.tailCounter > 8) {
         this.tailCounter = 0;
      }

      if (this.sprintCounter > 0) {
         ++this.sprintCounter;
         if (this.sprintCounter > 300) {
            this.sprintCounter = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.headLean = 0.0F;
         this.prevHeadLean = this.headLean;
         this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.allowStandSliding = false;
         this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(64)) {
         this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }

   }

   private void openHorseMouth() {
      if (!this.world.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(64, true);
      }

   }

   public void setEatingHaystack(boolean p_110227_1_) {
      this.setHorseWatchableBoolean(16, p_110227_1_);
   }

   public void setRearing(boolean p_110219_1_) {
      if (p_110219_1_) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(32, p_110219_1_);
   }

   private void makeHorseRear() {
      if (this.canPassengerSteer() || this.isServerWorld()) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }

   }

   public void makeMad() {
      this.makeHorseRear();
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public boolean setTamedBy(PlayerEntity p_110263_1_) {
      this.setOwnerUniqueId(p_110263_1_.getUniqueID());
      this.setHorseTamed(true);
      if (p_110263_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)p_110263_1_, this);
      }

      this.world.setEntityState(this, (byte)7);
      return true;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isAlive()) {
         if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled()) {
            LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
            this.rotationYaw = livingentity.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = livingentity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            float f = livingentity.moveStrafing * 0.5F;
            float f1 = livingentity.moveForward;
            if (f1 <= 0.0F) {
               f1 *= 0.25F;
               this.gallopTime = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding) {
               f = 0.0F;
               f1 = 0.0F;
            }

            double d2;
            double d3;
            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
               d2 = this.getHorseJumpStrength() * (double)this.jumpPower * (double)this.func_226269_ah_();
               if (this.isPotionActive(Effects.JUMP_BOOST)) {
                  d3 = d2 + (double)((float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
               } else {
                  d3 = d2;
               }

               Vec3d vec3d = this.getMotion();
               this.setMotion(vec3d.x, d3, vec3d.z);
               this.setHorseJumping(true);
               this.isAirBorne = true;
               if (f1 > 0.0F) {
                  float f2 = MathHelper.sin(this.rotationYaw * 0.017453292F);
                  float f3 = MathHelper.cos(this.rotationYaw * 0.017453292F);
                  this.setMotion(this.getMotion().add((double)(-0.4F * f2 * this.jumpPower), 0.0D, (double)(0.4F * f3 * this.jumpPower)));
                  this.playJumpSound();
               }

               this.jumpPower = 0.0F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
            if (this.canPassengerSteer()) {
               this.setAIMoveSpeed((float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
               super.travel(new Vec3d((double)f, p_213352_1_.y, (double)f1));
            } else if (livingentity instanceof PlayerEntity) {
               this.setMotion(Vec3d.ZERO);
            }

            if (this.onGround) {
               this.jumpPower = 0.0F;
               this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            d2 = this.func_226277_ct_() - this.prevPosX;
            d3 = this.func_226281_cx_() - this.prevPosZ;
            float f4 = MathHelper.sqrt(d2 * d2 + d3 * d3) * 4.0F;
            if (f4 > 1.0F) {
               f4 = 1.0F;
            }

            this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
         } else {
            this.jumpMovementFactor = 0.02F;
            super.travel(p_213352_1_);
         }
      }

   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("EatingHaystack", this.isEatingHaystack());
      p_213281_1_.putBoolean("Bred", this.isBreeding());
      p_213281_1_.putInt("Temper", this.getTemper());
      p_213281_1_.putBoolean("Tame", this.isTame());
      if (this.getOwnerUniqueId() != null) {
         p_213281_1_.putString("OwnerUUID", this.getOwnerUniqueId().toString());
      }

      if (!this.horseChest.getStackInSlot(0).isEmpty()) {
         p_213281_1_.put("SaddleItem", this.horseChest.getStackInSlot(0).write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setEatingHaystack(p_70037_1_.getBoolean("EatingHaystack"));
      this.setBreeding(p_70037_1_.getBoolean("Bred"));
      this.setTemper(p_70037_1_.getInt("Temper"));
      this.setHorseTamed(p_70037_1_.getBoolean("Tame"));
      String s;
      if (p_70037_1_.contains("OwnerUUID", 8)) {
         s = p_70037_1_.getString("OwnerUUID");
      } else {
         String s1 = p_70037_1_.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         this.setOwnerUniqueId(UUID.fromString(s));
      }

      IAttributeInstance iattributeinstance = this.getAttributes().getAttributeInstanceByName("Speed");
      if (iattributeinstance != null) {
         this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(iattributeinstance.getBaseValue() * 0.25D);
      }

      if (p_70037_1_.contains("SaddleItem", 10)) {
         ItemStack itemstack = ItemStack.read(p_70037_1_.getCompound("SaddleItem"));
         if (itemstack.getItem() == Items.SADDLE) {
            this.horseChest.setInventorySlotContents(0, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      return false;
   }

   protected boolean canMate() {
      return !this.isBeingRidden() && !this.isPassenger() && this.isTame() && !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return null;
   }

   protected void setOffspringAttributes(AgeableEntity p_190681_1_, AbstractHorseEntity p_190681_2_) {
      double d0 = this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + (double)this.getModifiedMaxHealth();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(d0 / 3.0D);
      double d1 = this.getAttribute(JUMP_STRENGTH).getBaseValue() + p_190681_1_.getAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength();
      p_190681_2_.getAttribute(JUMP_STRENGTH).setBaseValue(d1 / 3.0D);
      double d2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0D);
   }

   public boolean canBeSteered() {
      return this.getControllingPassenger() instanceof LivingEntity;
   }

   @OnlyIn(Dist.CLIENT)
   public float getGrassEatingAmount(float p_110258_1_) {
      return MathHelper.lerp(p_110258_1_, this.prevHeadLean, this.headLean);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRearingAmount(float p_110223_1_) {
      return MathHelper.lerp(p_110223_1_, this.prevRearingAmount, this.rearingAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMouthOpennessAngle(float p_110201_1_) {
      return MathHelper.lerp(p_110201_1_, this.prevMouthOpenness, this.mouthOpenness);
   }

   @OnlyIn(Dist.CLIENT)
   public void setJumpPower(int p_110206_1_) {
      if (this.isHorseSaddled()) {
         if (p_110206_1_ < 0) {
            p_110206_1_ = 0;
         } else {
            this.allowStandSliding = true;
            this.makeHorseRear();
         }

         if (p_110206_1_ >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * (float)p_110206_1_ / 90.0F;
         }
      }

   }

   public boolean canJump() {
      return this.isHorseSaddled();
   }

   public void handleStartJump(int p_184775_1_) {
      this.allowStandSliding = true;
      this.makeHorseRear();
   }

   public void handleStopJump() {
   }

   @OnlyIn(Dist.CLIENT)
   protected void spawnHorseParticles(boolean p_110216_1_) {
      IParticleData iparticledata = p_110216_1_ ? ParticleTypes.HEART : ParticleTypes.SMOKE;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(iparticledata, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.spawnHorseParticles(true);
      } else if (p_70103_1_ == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void updatePassenger(Entity p_184232_1_) {
      super.updatePassenger(p_184232_1_);
      if (p_184232_1_ instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)p_184232_1_;
         this.renderYawOffset = mobentity.renderYawOffset;
      }

      if (this.prevRearingAmount > 0.0F) {
         float f3 = MathHelper.sin(this.renderYawOffset * 0.017453292F);
         float f = MathHelper.cos(this.renderYawOffset * 0.017453292F);
         float f1 = 0.7F * this.prevRearingAmount;
         float f2 = 0.15F * this.prevRearingAmount;
         p_184232_1_.setPosition(this.func_226277_ct_() + (double)(f1 * f3), this.func_226278_cu_() + this.getMountedYOffset() + p_184232_1_.getYOffset() + (double)f2, this.func_226281_cx_() - (double)(f1 * f));
         if (p_184232_1_ instanceof LivingEntity) {
            ((LivingEntity)p_184232_1_).renderYawOffset = this.renderYawOffset;
         }
      }

   }

   protected float getModifiedMaxHealth() {
      return 15.0F + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
   }

   protected double getModifiedJumpStrength() {
      return 0.4000000059604645D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D;
   }

   protected double getModifiedMovementSpeed() {
      return (0.44999998807907104D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D) * 0.25D;
   }

   public boolean isOnLadder() {
      return false;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.95F;
   }

   public boolean wearsArmor() {
      return false;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return false;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      int i = p_174820_1_ - 400;
      if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
         if (i == 0 && p_174820_2_.getItem() != Items.SADDLE) {
            return false;
         } else if (i == 1 && (!this.wearsArmor() || !this.isArmor(p_174820_2_))) {
            return false;
         } else {
            this.horseChest.setInventorySlotContents(i, p_174820_2_);
            this.updateHorseSlots();
            return true;
         }
      } else {
         int j = p_174820_1_ - 500 + 2;
         if (j >= 2 && j < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(j, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226258_a_(0.2F);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      return this.isAlive() && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.itemHandler != null ? this.itemHandler.cast() : super.getCapability(p_getCapability_1_, p_getCapability_2_);
   }

   public void remove(boolean p_remove_1_) {
      super.remove(p_remove_1_);
      if (!p_remove_1_ && this.itemHandler != null) {
         this.itemHandler.invalidate();
         this.itemHandler = null;
      }

   }

   static {
      field_213618_bK = (new EntityPredicate()).setDistance(16.0D).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setCustomPredicate(IS_HORSE_BREEDING);
      JUMP_STRENGTH = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
      STATUS = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.BYTE);
      OWNER_UNIQUE_ID = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   }
}
