package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.goal.BreakBlockGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.eventbus.api.Event.Result;

public class ZombieEntity extends MonsterEntity {
   protected static final IAttribute SPAWN_REINFORCEMENTS_CHANCE = (new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
   private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier BABY_SPEED_BOOST;
   private static final DataParameter<Boolean> IS_CHILD;
   private static final DataParameter<Integer> VILLAGER_TYPE;
   private static final DataParameter<Boolean> DROWNING;
   private static final Predicate<Difficulty> field_213699_bC;
   private final BreakDoorGoal breakDoor;
   private boolean isBreakDoorsTaskSet;
   private int inWaterTime;
   private int drownedConversionTime;

   public ZombieEntity(EntityType<? extends ZombieEntity> p_i48549_1_, World p_i48549_2_) {
      super(p_i48549_1_, p_i48549_2_);
      this.breakDoor = new BreakDoorGoal(this, field_213699_bC);
   }

   public ZombieEntity(World p_i1745_1_) {
      this(EntityType.ZOMBIE, p_i1745_1_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new ZombieEntity.AttackTurtleEggGoal(this, 1.0D, 3));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.applyEntityAI();
   }

   protected void applyEntityAI() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::isBreakDoorsTaskSet));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp(ZombiePigmanEntity.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
      this.getAttributes().registerAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.rand.nextDouble() * (Double)ForgeConfig.SERVER.zombieBaseSummonChance.get());
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(IS_CHILD, false);
      this.getDataManager().register(VILLAGER_TYPE, 0);
      this.getDataManager().register(DROWNING, false);
   }

   public boolean isDrowning() {
      return (Boolean)this.getDataManager().get(DROWNING);
   }

   public boolean isBreakDoorsTaskSet() {
      return this.isBreakDoorsTaskSet;
   }

   public void setBreakDoorsAItask(boolean p_146070_1_) {
      if (this.canBreakDoors()) {
         if (this.isBreakDoorsTaskSet != p_146070_1_) {
            this.isBreakDoorsTaskSet = p_146070_1_;
            ((GroundPathNavigator)this.getNavigator()).setBreakDoors(p_146070_1_);
            if (p_146070_1_) {
               this.goalSelector.addGoal(1, this.breakDoor);
            } else {
               this.goalSelector.removeGoal(this.breakDoor);
            }
         }
      } else if (this.isBreakDoorsTaskSet) {
         this.goalSelector.removeGoal(this.breakDoor);
         this.isBreakDoorsTaskSet = false;
      }

   }

   protected boolean canBreakDoors() {
      return true;
   }

   public boolean isChild() {
      return (Boolean)this.getDataManager().get(IS_CHILD);
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      if (this.isChild()) {
         this.experienceValue = (int)((float)this.experienceValue * 2.5F);
      }

      return super.getExperiencePoints(p_70693_1_);
   }

   public void setChild(boolean p_82227_1_) {
      this.getDataManager().set(IS_CHILD, p_82227_1_);
      if (this.world != null && !this.world.isRemote) {
         IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         iattributeinstance.removeModifier(BABY_SPEED_BOOST);
         if (p_82227_1_) {
            iattributeinstance.applyModifier(BABY_SPEED_BOOST);
         }
      }

   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (IS_CHILD.equals(p_184206_1_)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   protected boolean shouldDrown() {
      return true;
   }

   public void tick() {
      if (!this.world.isRemote && this.isAlive()) {
         if (this.isDrowning()) {
            --this.drownedConversionTime;
            if (this.drownedConversionTime < 0) {
               this.onDrowned();
            }
         } else if (this.shouldDrown()) {
            if (this.areEyesInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startDrowning(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void livingTick() {
      if (this.isAlive()) {
         boolean flag = this.shouldBurnInDay() && this.isInDaylight();
         if (flag) {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
            if (!itemstack.isEmpty()) {
               if (itemstack.isDamageable()) {
                  itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
                  if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
                     this.sendBreakAnimation(EquipmentSlotType.HEAD);
                     this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
                  }
               }

               flag = false;
            }

            if (flag) {
               this.setFire(8);
            }
         }
      }

      super.livingTick();
   }

   private void startDrowning(int p_204704_1_) {
      this.drownedConversionTime = p_204704_1_;
      this.getDataManager().set(DROWNING, true);
   }

   protected void onDrowned() {
      this.func_213698_b(EntityType.DROWNED);
      this.world.playEvent((PlayerEntity)null, 1040, new BlockPos(this), 0);
   }

   protected void func_213698_b(EntityType<? extends ZombieEntity> p_213698_1_) {
      if (!this.removed) {
         ZombieEntity zombieentity = (ZombieEntity)p_213698_1_.create(this.world);
         zombieentity.copyLocationAndAnglesFrom(this);
         zombieentity.setCanPickUpLoot(this.canPickUpLoot());
         zombieentity.setBreakDoorsAItask(zombieentity.canBreakDoors() && this.isBreakDoorsTaskSet());
         zombieentity.applyAttributeBonuses(zombieentity.world.getDifficultyForLocation(new BlockPos(zombieentity)).getClampedAdditionalDifficulty());
         zombieentity.setChild(this.isChild());
         zombieentity.setNoAI(this.isAIDisabled());
         EquipmentSlotType[] var3 = EquipmentSlotType.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EquipmentSlotType equipmentslottype = var3[var5];
            ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
            if (!itemstack.isEmpty()) {
               zombieentity.setItemStackToSlot(equipmentslottype, itemstack.copy());
               zombieentity.setDropChance(equipmentslottype, this.getDropChance(equipmentslottype));
               itemstack.setCount(0);
            }
         }

         if (this.hasCustomName()) {
            zombieentity.setCustomName(this.getCustomName());
            zombieentity.setCustomNameVisible(this.isCustomNameVisible());
         }

         if (this.isNoDespawnRequired()) {
            zombieentity.enablePersistence();
         }

         zombieentity.setInvulnerable(this.isInvulnerable());
         this.world.addEntity(zombieentity);
         this.remove();
      }

   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      Item item = itemstack.getItem();
      if (item instanceof SpawnEggItem && ((SpawnEggItem)item).hasType(itemstack.getTag(), this.getType())) {
         if (!this.world.isRemote) {
            ZombieEntity zombieentity = (ZombieEntity)this.getType().create(this.world);
            if (zombieentity != null) {
               zombieentity.setChild(true);
               zombieentity.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 0.0F, 0.0F);
               this.world.addEntity(zombieentity);
               if (itemstack.hasDisplayName()) {
                  zombieentity.setCustomName(itemstack.getDisplayName());
               }

               if (!p_184645_1_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   protected boolean shouldBurnInDay() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
         return false;
      } else {
         LivingEntity livingentity = this.getAttackTarget();
         if (livingentity == null && p_70097_1_.getTrueSource() instanceof LivingEntity) {
            livingentity = (LivingEntity)p_70097_1_.getTrueSource();
         }

         int i = MathHelper.floor(this.func_226277_ct_());
         int j = MathHelper.floor(this.func_226278_cu_());
         int k = MathHelper.floor(this.func_226281_cx_());
         ZombieEvent.SummonAidEvent event = ForgeEventFactory.fireZombieSummonAid(this, this.world, i, j, k, livingentity, this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getValue());
         if (event.getResult() == Result.DENY) {
            return true;
         } else {
            if (event.getResult() == Result.ALLOW || livingentity != null && this.world.getDifficulty() == Difficulty.HARD && (double)this.rand.nextFloat() < this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
               ZombieEntity zombieentity = event.getCustomSummonedAid() != null && event.getResult() == Result.ALLOW ? event.getCustomSummonedAid() : (ZombieEntity)EntityType.ZOMBIE.create(this.world);

               for(int l = 0; l < 50; ++l) {
                  int i1 = i + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                  int j1 = j + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                  int k1 = k + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                  BlockPos blockpos = new BlockPos(i1, j1 - 1, k1);
                  if (this.world.getBlockState(blockpos).func_215682_a(this.world, blockpos, zombieentity) && this.world.getLight(new BlockPos(i1, j1, k1)) < 10) {
                     zombieentity.setPosition((double)i1, (double)j1, (double)k1);
                     if (!this.world.isPlayerWithin((double)i1, (double)j1, (double)k1, 7.0D) && this.world.func_226668_i_(zombieentity) && this.world.func_226669_j_(zombieentity) && !this.world.containsAnyLiquid(zombieentity.getBoundingBox())) {
                        this.world.addEntity(zombieentity);
                        if (livingentity != null) {
                           zombieentity.setAttackTarget(livingentity);
                        }

                        zombieentity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(zombieentity)), SpawnReason.REINFORCEMENT, (ILivingEntityData)null, (CompoundNBT)null);
                        this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                        zombieentity.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                        break;
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean flag = super.attackEntityAsMob(p_70652_1_);
      if (flag) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F) {
            p_70652_1_.setFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      super.setEquipmentBasedOnDifficulty(p_180481_1_);
      if (this.rand.nextFloat() < (this.world.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int i = this.rand.nextInt(3);
         if (i == 0) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.isChild()) {
         p_213281_1_.putBoolean("IsBaby", true);
      }

      p_213281_1_.putBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
      p_213281_1_.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      p_213281_1_.putInt("DrownedConversionTime", this.isDrowning() ? this.drownedConversionTime : -1);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.getBoolean("IsBaby")) {
         this.setChild(true);
      }

      this.setBreakDoorsAItask(p_70037_1_.getBoolean("CanBreakDoors"));
      this.inWaterTime = p_70037_1_.getInt("InWaterTime");
      if (p_70037_1_.contains("DrownedConversionTime", 99) && p_70037_1_.getInt("DrownedConversionTime") > -1) {
         this.startDrowning(p_70037_1_.getInt("DrownedConversionTime"));
      }

   }

   public void onKillEntity(LivingEntity p_70074_1_) {
      super.onKillEntity(p_70074_1_);
      if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && p_70074_1_ instanceof VillagerEntity) {
         if (this.world.getDifficulty() != Difficulty.HARD && this.rand.nextBoolean()) {
            return;
         }

         VillagerEntity villagerentity = (VillagerEntity)p_70074_1_;
         ZombieVillagerEntity zombievillagerentity = (ZombieVillagerEntity)EntityType.ZOMBIE_VILLAGER.create(this.world);
         zombievillagerentity.copyLocationAndAnglesFrom(villagerentity);
         villagerentity.remove();
         zombievillagerentity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(zombievillagerentity)), SpawnReason.CONVERSION, new ZombieEntity.GroupData(false), (CompoundNBT)null);
         zombievillagerentity.func_213792_a(villagerentity.getVillagerData());
         zombievillagerentity.func_223727_a((INBT)villagerentity.func_223722_es().func_220914_a(NBTDynamicOps.INSTANCE).getValue());
         zombievillagerentity.func_213790_g(villagerentity.getOffers().func_222199_a());
         zombievillagerentity.func_213789_a(villagerentity.getXp());
         zombievillagerentity.setChild(villagerentity.isChild());
         zombievillagerentity.setNoAI(villagerentity.isAIDisabled());
         if (villagerentity.hasCustomName()) {
            zombievillagerentity.setCustomName(villagerentity.getCustomName());
            zombievillagerentity.setCustomNameVisible(villagerentity.isCustomNameVisible());
         }

         if (this.isNoDespawnRequired()) {
            zombievillagerentity.enablePersistence();
         }

         zombievillagerentity.setInvulnerable(this.isInvulnerable());
         this.world.addEntity(zombievillagerentity);
         this.world.playEvent((PlayerEntity)null, 1026, new BlockPos(this), 0);
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isChild() ? 0.93F : 1.74F;
   }

   protected boolean canEquipItem(ItemStack p_175448_1_) {
      return p_175448_1_.getItem() == Items.EGG && this.isChild() && this.isPassenger() ? false : super.canEquipItem(p_175448_1_);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      ILivingEntityData p_213386_4_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      float f = p_213386_2_.getClampedAdditionalDifficulty();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);
      if (p_213386_4_ == null) {
         p_213386_4_ = new ZombieEntity.GroupData((double)p_213386_1_.getRandom().nextFloat() < (Double)ForgeConfig.SERVER.zombieBabyChance.get());
      }

      if (p_213386_4_ instanceof ZombieEntity.GroupData) {
         ZombieEntity.GroupData zombieentity$groupdata = (ZombieEntity.GroupData)p_213386_4_;
         if (zombieentity$groupdata.isChild) {
            this.setChild(true);
            if ((double)p_213386_1_.getRandom().nextFloat() < 0.05D) {
               List<ChickenEntity> list = p_213386_1_.getEntitiesWithinAABB(ChickenEntity.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), EntityPredicates.IS_STANDALONE);
               if (!list.isEmpty()) {
                  ChickenEntity chickenentity = (ChickenEntity)list.get(0);
                  chickenentity.setChickenJockey(true);
                  this.startRiding(chickenentity);
               }
            } else if ((double)p_213386_1_.getRandom().nextFloat() < 0.05D) {
               ChickenEntity chickenentity1 = (ChickenEntity)EntityType.CHICKEN.create(this.world);
               chickenentity1.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, 0.0F);
               chickenentity1.onInitialSpawn(p_213386_1_, p_213386_2_, SpawnReason.JOCKEY, (ILivingEntityData)null, (CompoundNBT)null);
               chickenentity1.setChickenJockey(true);
               p_213386_1_.addEntity(chickenentity1);
               this.startRiding(chickenentity1);
            }
         }

         this.setBreakDoorsAItask(this.canBreakDoors() && this.rand.nextFloat() < f * 0.1F);
         this.setEquipmentBasedOnDifficulty(p_213386_2_);
         this.setEnchantmentBasedOnDifficulty(p_213386_2_);
      }

      if (this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F) {
            this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.inventoryArmorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
         }
      }

      this.applyAttributeBonuses(f);
      return (ILivingEntityData)p_213386_4_;
   }

   protected void applyAttributeBonuses(float p_207304_1_) {
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
      double d0 = this.rand.nextDouble() * 1.5D * (double)p_207304_1_;
      if (d0 > 1.0D) {
         this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }

      if (this.rand.nextFloat() < p_207304_1_ * 0.05F) {
         this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
         this.setBreakDoorsAItask(this.canBreakDoors());
      }

   }

   public double getYOffset() {
      return this.isChild() ? 0.0D : -0.45D;
   }

   protected void dropSpecialItems(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropSpecialItems(p_213333_1_, p_213333_2_, p_213333_3_);
      Entity entity = p_213333_1_.getTrueSource();
      if (entity instanceof CreeperEntity) {
         CreeperEntity creeperentity = (CreeperEntity)entity;
         if (creeperentity.ableToCauseSkullDrop()) {
            creeperentity.incrementDroppedSkulls();
            ItemStack itemstack = this.getSkullDrop();
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack);
            }
         }
      }

   }

   protected ItemStack getSkullDrop() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   static {
      BABY_SPEED_BOOST = new AttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
      IS_CHILD = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.BOOLEAN);
      VILLAGER_TYPE = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.VARINT);
      DROWNING = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.BOOLEAN);
      field_213699_bC = (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_ == Difficulty.HARD;
      };
   }

   public class GroupData implements ILivingEntityData {
      public final boolean isChild;

      private GroupData(boolean p_i47328_2_) {
         this.isChild = p_i47328_2_;
      }

      // $FF: synthetic method
      GroupData(boolean p_i47329_2_, Object p_i47329_3_) {
         this(p_i47329_2_);
      }
   }

   class AttackTurtleEggGoal extends BreakBlockGoal {
      AttackTurtleEggGoal(CreatureEntity p_i50465_2_, double p_i50465_3_, int p_i50465_5_) {
         super(Blocks.TURTLE_EGG, p_i50465_2_, p_i50465_3_, p_i50465_5_);
      }

      public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
         p_203114_1_.playSound((PlayerEntity)null, p_203114_2_, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.rand.nextFloat() * 0.2F);
      }

      public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
         p_203116_1_.playSound((PlayerEntity)null, p_203116_2_, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203116_1_.rand.nextFloat() * 0.2F);
      }

      public double getTargetDistanceSq() {
         return 1.14D;
      }
   }
}
