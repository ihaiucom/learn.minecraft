package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DolphinEntity extends WaterMobEntity {
   private static final DataParameter<BlockPos> TREASURE_POS;
   private static final DataParameter<Boolean> GOT_FISH;
   private static final DataParameter<Integer> MOISTNESS;
   private static final EntityPredicate field_213810_bA;
   public static final Predicate<ItemEntity> ITEM_SELECTOR;

   public DolphinEntity(EntityType<? extends DolphinEntity> p_i50275_1_, World p_i50275_2_) {
      super(p_i50275_1_, p_i50275_2_);
      this.moveController = new DolphinEntity.MoveHelperController(this);
      this.lookController = new DolphinLookController(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setAir(this.getMaxAir());
      this.rotationPitch = 0.0F;
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected void updateAir(int p_209207_1_) {
   }

   public void setTreasurePos(BlockPos p_208012_1_) {
      this.dataManager.set(TREASURE_POS, p_208012_1_);
   }

   public BlockPos getTreasurePos() {
      return (BlockPos)this.dataManager.get(TREASURE_POS);
   }

   public boolean hasGotFish() {
      return (Boolean)this.dataManager.get(GOT_FISH);
   }

   public void setGotFish(boolean p_208008_1_) {
      this.dataManager.set(GOT_FISH, p_208008_1_);
   }

   public int getMoistness() {
      return (Integer)this.dataManager.get(MOISTNESS);
   }

   public void setMoistness(int p_211137_1_) {
      this.dataManager.set(MOISTNESS, p_211137_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TREASURE_POS, BlockPos.ZERO);
      this.dataManager.register(GOT_FISH, false);
      this.dataManager.register(MOISTNESS, 2400);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("TreasurePosX", this.getTreasurePos().getX());
      p_213281_1_.putInt("TreasurePosY", this.getTreasurePos().getY());
      p_213281_1_.putInt("TreasurePosZ", this.getTreasurePos().getZ());
      p_213281_1_.putBoolean("GotFish", this.hasGotFish());
      p_213281_1_.putInt("Moistness", this.getMoistness());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      int lvt_2_1_ = p_70037_1_.getInt("TreasurePosX");
      int lvt_3_1_ = p_70037_1_.getInt("TreasurePosY");
      int lvt_4_1_ = p_70037_1_.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(lvt_2_1_, lvt_3_1_, lvt_4_1_));
      super.readAdditional(p_70037_1_);
      this.setGotFish(p_70037_1_.getBoolean("GotFish"));
      this.setMoistness(p_70037_1_.getInt("Moistness"));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreatheAirGoal(this));
      this.goalSelector.addGoal(0, new FindWaterGoal(this));
      this.goalSelector.addGoal(1, new DolphinEntity.SwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new DolphinEntity.SwimWithPlayerGoal(this, 4.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
      this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2000000476837158D, true));
      this.goalSelector.addGoal(8, new DolphinEntity.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{GuardianEntity.class})).setCallsForHelp());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.2000000476837158D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new SwimmerPathNavigator(this, p_175447_1_);
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean lvt_2_1_ = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (lvt_2_1_) {
         this.applyEnchantments(this, p_70652_1_);
         this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return lvt_2_1_;
   }

   public int getMaxAir() {
      return 4800;
   }

   protected int determineNextAir(int p_207300_1_) {
      return this.getMaxAir();
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.3F;
   }

   public int getVerticalFaceSpeed() {
      return 1;
   }

   public int getHorizontalFaceSpeed() {
      return 1;
   }

   protected boolean canBeRidden(Entity p_184228_1_) {
      return true;
   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType lvt_2_1_ = MobEntity.getSlotForItemStack(p_213365_1_);
      if (!this.getItemStackFromSlot(lvt_2_1_).isEmpty()) {
         return false;
      } else {
         return lvt_2_1_ == EquipmentSlotType.MAINHAND && super.func_213365_e(p_213365_1_);
      }
   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
         ItemStack lvt_2_1_ = p_175445_1_.getItem();
         if (this.canEquipItem(lvt_2_1_)) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, lvt_2_1_);
            this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
            this.onItemPickup(p_175445_1_, lvt_2_1_.getCount());
            p_175445_1_.remove();
         }
      }

   }

   public void tick() {
      super.tick();
      if (!this.isAIDisabled()) {
         if (this.isInWaterRainOrBubbleColumn()) {
            this.setMoistness(2400);
         } else {
            this.setMoistness(this.getMoistness() - 1);
            if (this.getMoistness() <= 0) {
               this.attackEntityFrom(DamageSource.DRYOUT, 1.0F);
            }

            if (this.onGround) {
               this.setMotion(this.getMotion().add((double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.rotationYaw = this.rand.nextFloat() * 360.0F;
               this.onGround = false;
               this.isAirBorne = true;
            }
         }

         if (this.world.isRemote && this.isInWater() && this.getMotion().lengthSquared() > 0.03D) {
            Vec3d lvt_1_1_ = this.getLook(0.0F);
            float lvt_2_1_ = MathHelper.cos(this.rotationYaw * 0.017453292F) * 0.3F;
            float lvt_3_1_ = MathHelper.sin(this.rotationYaw * 0.017453292F) * 0.3F;
            float lvt_4_1_ = 1.2F - this.rand.nextFloat() * 0.7F;

            for(int lvt_5_1_ = 0; lvt_5_1_ < 2; ++lvt_5_1_) {
               this.world.addParticle(ParticleTypes.DOLPHIN, this.func_226277_ct_() - lvt_1_1_.x * (double)lvt_4_1_ + (double)lvt_2_1_, this.func_226278_cu_() - lvt_1_1_.y, this.func_226281_cx_() - lvt_1_1_.z * (double)lvt_4_1_ + (double)lvt_3_1_, 0.0D, 0.0D, 0.0D);
               this.world.addParticle(ParticleTypes.DOLPHIN, this.func_226277_ct_() - lvt_1_1_.x * (double)lvt_4_1_ - (double)lvt_2_1_, this.func_226278_cu_() - lvt_1_1_.y, this.func_226281_cx_() - lvt_1_1_.z * (double)lvt_4_1_ - (double)lvt_3_1_, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 38) {
         this.func_208401_a(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void func_208401_a(IParticleData p_208401_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 7; ++lvt_2_1_) {
         double lvt_3_1_ = this.rand.nextGaussian() * 0.01D;
         double lvt_5_1_ = this.rand.nextGaussian() * 0.01D;
         double lvt_7_1_ = this.rand.nextGaussian() * 0.01D;
         this.world.addParticle(p_208401_1_, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.2D, this.func_226287_g_(1.0D), lvt_3_1_, lvt_5_1_, lvt_7_1_);
      }

   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (!lvt_3_1_.isEmpty() && lvt_3_1_.getItem().isIn(ItemTags.FISHES)) {
         if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!p_184645_1_.abilities.isCreativeMode) {
            lvt_3_1_.shrink(1);
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   public static boolean func_223364_b(EntityType<DolphinEntity> p_223364_0_, IWorld p_223364_1_, SpawnReason p_223364_2_, BlockPos p_223364_3_, Random p_223364_4_) {
      return p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel() && (p_223364_1_.func_226691_t_(p_223364_3_) != Biomes.OCEAN || p_223364_1_.func_226691_t_(p_223364_3_) != Biomes.DEEP_OCEAN) && p_223364_1_.getFluidState(p_223364_3_).isTagged(FluidTags.WATER);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_DOLPHIN_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_DOLPHIN_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DOLPHIN_SWIM;
   }

   protected boolean closeToTarget() {
      BlockPos lvt_1_1_ = this.getNavigator().getTargetPos();
      return lvt_1_1_ != null ? lvt_1_1_.withinDistance(this.getPositionVec(), 12.0D) : false;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(this.getAIMoveSpeed(), p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
         if (this.getAttackTarget() == null) {
            this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return true;
   }

   static {
      TREASURE_POS = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.BLOCK_POS);
      GOT_FISH = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.BOOLEAN);
      MOISTNESS = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.VARINT);
      field_213810_bA = (new EntityPredicate()).setDistance(10.0D).allowFriendlyFire().allowInvulnerable().setLineOfSiteRequired();
      ITEM_SELECTOR = (p_205023_0_) -> {
         return !p_205023_0_.cannotPickup() && p_205023_0_.isAlive() && p_205023_0_.isInWater();
      };
   }

   static class SwimToTreasureGoal extends Goal {
      private final DolphinEntity dolphin;
      private boolean field_208058_b;

      SwimToTreasureGoal(DolphinEntity p_i49344_1_) {
         this.dolphin = p_i49344_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean isPreemptible() {
         return false;
      }

      public boolean shouldExecute() {
         return this.dolphin.hasGotFish() && this.dolphin.getAir() >= 100;
      }

      public boolean shouldContinueExecuting() {
         BlockPos lvt_1_1_ = this.dolphin.getTreasurePos();
         return !(new BlockPos((double)lvt_1_1_.getX(), this.dolphin.func_226278_cu_(), (double)lvt_1_1_.getZ())).withinDistance(this.dolphin.getPositionVec(), 4.0D) && !this.field_208058_b && this.dolphin.getAir() >= 100;
      }

      public void startExecuting() {
         if (this.dolphin.world instanceof ServerWorld) {
            ServerWorld lvt_1_1_ = (ServerWorld)this.dolphin.world;
            this.field_208058_b = false;
            this.dolphin.getNavigator().clearPath();
            BlockPos lvt_2_1_ = new BlockPos(this.dolphin);
            String lvt_3_1_ = (double)lvt_1_1_.rand.nextFloat() >= 0.5D ? "Ocean_Ruin" : "Shipwreck";
            BlockPos lvt_4_1_ = lvt_1_1_.findNearestStructure(lvt_3_1_, lvt_2_1_, 50, false);
            if (lvt_4_1_ == null) {
               BlockPos lvt_5_1_ = lvt_1_1_.findNearestStructure(lvt_3_1_.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", lvt_2_1_, 50, false);
               if (lvt_5_1_ == null) {
                  this.field_208058_b = true;
                  return;
               }

               this.dolphin.setTreasurePos(lvt_5_1_);
            } else {
               this.dolphin.setTreasurePos(lvt_4_1_);
            }

            lvt_1_1_.setEntityState(this.dolphin, (byte)38);
         }
      }

      public void resetTask() {
         BlockPos lvt_1_1_ = this.dolphin.getTreasurePos();
         if ((new BlockPos((double)lvt_1_1_.getX(), this.dolphin.func_226278_cu_(), (double)lvt_1_1_.getZ())).withinDistance(this.dolphin.getPositionVec(), 4.0D) || this.field_208058_b) {
            this.dolphin.setGotFish(false);
         }

      }

      public void tick() {
         World lvt_1_1_ = this.dolphin.world;
         if (this.dolphin.closeToTarget() || this.dolphin.getNavigator().noPath()) {
            Vec3d lvt_2_1_ = new Vec3d(this.dolphin.getTreasurePos());
            Vec3d lvt_3_1_ = RandomPositionGenerator.findRandomTargetTowardsScaled(this.dolphin, 16, 1, lvt_2_1_, 0.39269909262657166D);
            if (lvt_3_1_ == null) {
               lvt_3_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 4, lvt_2_1_);
            }

            if (lvt_3_1_ != null) {
               BlockPos lvt_4_1_ = new BlockPos(lvt_3_1_);
               if (!lvt_1_1_.getFluidState(lvt_4_1_).isTagged(FluidTags.WATER) || !lvt_1_1_.getBlockState(lvt_4_1_).allowsMovement(lvt_1_1_, lvt_4_1_, PathType.WATER)) {
                  lvt_3_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 5, lvt_2_1_);
               }
            }

            if (lvt_3_1_ == null) {
               this.field_208058_b = true;
               return;
            }

            this.dolphin.getLookController().setLookPosition(lvt_3_1_.x, lvt_3_1_.y, lvt_3_1_.z, (float)(this.dolphin.getHorizontalFaceSpeed() + 20), (float)this.dolphin.getVerticalFaceSpeed());
            this.dolphin.getNavigator().tryMoveToXYZ(lvt_3_1_.x, lvt_3_1_.y, lvt_3_1_.z, 1.3D);
            if (lvt_1_1_.rand.nextInt(80) == 0) {
               lvt_1_1_.setEntityState(this.dolphin, (byte)38);
            }
         }

      }
   }

   static class SwimWithPlayerGoal extends Goal {
      private final DolphinEntity dolphin;
      private final double speed;
      private PlayerEntity targetPlayer;

      SwimWithPlayerGoal(DolphinEntity p_i48994_1_, double p_i48994_2_) {
         this.dolphin = p_i48994_1_;
         this.speed = p_i48994_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         this.targetPlayer = this.dolphin.world.getClosestPlayer(DolphinEntity.field_213810_bA, this.dolphin);
         return this.targetPlayer == null ? false : this.targetPlayer.isSwimming();
      }

      public boolean shouldContinueExecuting() {
         return this.targetPlayer != null && this.targetPlayer.isSwimming() && this.dolphin.getDistanceSq(this.targetPlayer) < 256.0D;
      }

      public void startExecuting() {
         this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
      }

      public void resetTask() {
         this.targetPlayer = null;
         this.dolphin.getNavigator().clearPath();
      }

      public void tick() {
         this.dolphin.getLookController().setLookPositionWithEntity(this.targetPlayer, (float)(this.dolphin.getHorizontalFaceSpeed() + 20), (float)this.dolphin.getVerticalFaceSpeed());
         if (this.dolphin.getDistanceSq(this.targetPlayer) < 6.25D) {
            this.dolphin.getNavigator().clearPath();
         } else {
            this.dolphin.getNavigator().tryMoveToEntityLiving(this.targetPlayer, this.speed);
         }

         if (this.targetPlayer.isSwimming() && this.targetPlayer.world.rand.nextInt(6) == 0) {
            this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
         }

      }
   }

   class PlayWithItemsGoal extends Goal {
      private int field_205154_b;

      private PlayWithItemsGoal() {
      }

      public boolean shouldExecute() {
         if (this.field_205154_b > DolphinEntity.this.ticksExisted) {
            return false;
         } else {
            List<ItemEntity> lvt_1_1_ = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), DolphinEntity.ITEM_SELECTOR);
            return !lvt_1_1_.isEmpty() || !DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
         }
      }

      public void startExecuting() {
         List<ItemEntity> lvt_1_1_ = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), DolphinEntity.ITEM_SELECTOR);
         if (!lvt_1_1_.isEmpty()) {
            DolphinEntity.this.getNavigator().tryMoveToEntityLiving((Entity)lvt_1_1_.get(0), 1.2000000476837158D);
            DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.field_205154_b = 0;
      }

      public void resetTask() {
         ItemStack lvt_1_1_ = DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!lvt_1_1_.isEmpty()) {
            this.func_220810_a(lvt_1_1_);
            DolphinEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            this.field_205154_b = DolphinEntity.this.ticksExisted + DolphinEntity.this.rand.nextInt(100);
         }

      }

      public void tick() {
         List<ItemEntity> lvt_1_1_ = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), DolphinEntity.ITEM_SELECTOR);
         ItemStack lvt_2_1_ = DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!lvt_2_1_.isEmpty()) {
            this.func_220810_a(lvt_2_1_);
            DolphinEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
         } else if (!lvt_1_1_.isEmpty()) {
            DolphinEntity.this.getNavigator().tryMoveToEntityLiving((Entity)lvt_1_1_.get(0), 1.2000000476837158D);
         }

      }

      private void func_220810_a(ItemStack p_220810_1_) {
         if (!p_220810_1_.isEmpty()) {
            double lvt_2_1_ = DolphinEntity.this.func_226280_cw_() - 0.30000001192092896D;
            ItemEntity lvt_4_1_ = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.func_226277_ct_(), lvt_2_1_, DolphinEntity.this.func_226281_cx_(), p_220810_1_);
            lvt_4_1_.setPickupDelay(40);
            lvt_4_1_.setThrowerId(DolphinEntity.this.getUniqueID());
            float lvt_5_1_ = 0.3F;
            float lvt_6_1_ = DolphinEntity.this.rand.nextFloat() * 6.2831855F;
            float lvt_7_1_ = 0.02F * DolphinEntity.this.rand.nextFloat();
            lvt_4_1_.setMotion((double)(0.3F * -MathHelper.sin(DolphinEntity.this.rotationYaw * 0.017453292F) * MathHelper.cos(DolphinEntity.this.rotationPitch * 0.017453292F) + MathHelper.cos(lvt_6_1_) * lvt_7_1_), (double)(0.3F * MathHelper.sin(DolphinEntity.this.rotationPitch * 0.017453292F) * 1.5F), (double)(0.3F * MathHelper.cos(DolphinEntity.this.rotationYaw * 0.017453292F) * MathHelper.cos(DolphinEntity.this.rotationPitch * 0.017453292F) + MathHelper.sin(lvt_6_1_) * lvt_7_1_));
            DolphinEntity.this.world.addEntity(lvt_4_1_);
         }
      }

      // $FF: synthetic method
      PlayWithItemsGoal(Object p_i48944_2_) {
         this();
      }
   }

   static class MoveHelperController extends MovementController {
      private final DolphinEntity dolphin;

      public MoveHelperController(DolphinEntity p_i48945_1_) {
         super(p_i48945_1_);
         this.dolphin = p_i48945_1_;
      }

      public void tick() {
         if (this.dolphin.isInWater()) {
            this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, 0.005D, 0.0D));
         }

         if (this.action == MovementController.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
            double lvt_1_1_ = this.posX - this.dolphin.func_226277_ct_();
            double lvt_3_1_ = this.posY - this.dolphin.func_226278_cu_();
            double lvt_5_1_ = this.posZ - this.dolphin.func_226281_cx_();
            double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
            if (lvt_7_1_ < 2.500000277905201E-7D) {
               this.mob.setMoveForward(0.0F);
            } else {
               float lvt_9_1_ = (float)(MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
               this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, lvt_9_1_, 10.0F);
               this.dolphin.renderYawOffset = this.dolphin.rotationYaw;
               this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
               float lvt_10_1_ = (float)(this.speed * this.dolphin.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
               if (this.dolphin.isInWater()) {
                  this.dolphin.setAIMoveSpeed(lvt_10_1_ * 0.02F);
                  float lvt_11_1_ = -((float)(MathHelper.atan2(lvt_3_1_, (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_)) * 57.2957763671875D));
                  lvt_11_1_ = MathHelper.clamp(MathHelper.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                  this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, lvt_11_1_, 5.0F);
                  float lvt_12_1_ = MathHelper.cos(this.dolphin.rotationPitch * 0.017453292F);
                  float lvt_13_1_ = MathHelper.sin(this.dolphin.rotationPitch * 0.017453292F);
                  this.dolphin.moveForward = lvt_12_1_ * lvt_10_1_;
                  this.dolphin.moveVertical = -lvt_13_1_ * lvt_10_1_;
               } else {
                  this.dolphin.setAIMoveSpeed(lvt_10_1_ * 0.1F);
               }

            }
         } else {
            this.dolphin.setAIMoveSpeed(0.0F);
            this.dolphin.setMoveStrafing(0.0F);
            this.dolphin.setMoveVertical(0.0F);
            this.dolphin.setMoveForward(0.0F);
         }
      }
   }
}
