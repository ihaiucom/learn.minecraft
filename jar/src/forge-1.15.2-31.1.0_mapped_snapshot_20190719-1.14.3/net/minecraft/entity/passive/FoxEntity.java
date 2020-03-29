package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageAtNightGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class FoxEntity extends AnimalEntity {
   private static final DataParameter<Integer> FOX_TYPE;
   private static final DataParameter<Byte> FOX_FLAGS;
   private static final DataParameter<Optional<UUID>> TRUSTED_UUID_SECONDARY;
   private static final DataParameter<Optional<UUID>> TRUSTED_UUID_MAIN;
   private static final Predicate<ItemEntity> field_213511_bE;
   private static final Predicate<Entity> field_213512_bF;
   private static final Predicate<Entity> field_213513_bG;
   private static final Predicate<Entity> field_213514_bH;
   private Goal attackAnimals;
   private Goal attackTurtles;
   private Goal attackFish;
   private float field_213518_bL;
   private float field_213519_bM;
   private float field_213520_bN;
   private float field_213521_bO;
   private int eatTicks;

   public FoxEntity(EntityType<? extends FoxEntity> p_i50271_1_, World p_i50271_2_) {
      super(p_i50271_1_, p_i50271_2_);
      this.lookController = new FoxEntity.LookHelperController();
      this.moveController = new FoxEntity.MoveHelperController();
      this.setPathPriority(PathNodeType.DANGER_OTHER, 0.0F);
      this.setPathPriority(PathNodeType.DAMAGE_OTHER, 0.0F);
      this.setCanPickUpLoot(true);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TRUSTED_UUID_SECONDARY, Optional.empty());
      this.dataManager.register(TRUSTED_UUID_MAIN, Optional.empty());
      this.dataManager.register(FOX_TYPE, 0);
      this.dataManager.register(FOX_FLAGS, (byte)0);
   }

   protected void registerGoals() {
      this.attackAnimals = new NearestAttackableTargetGoal(this, AnimalEntity.class, 10, false, false, (p_lambda$registerGoals$4_0_) -> {
         return p_lambda$registerGoals$4_0_ instanceof ChickenEntity || p_lambda$registerGoals$4_0_ instanceof RabbitEntity;
      });
      this.attackTurtles = new NearestAttackableTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.TARGET_DRY_BABY);
      this.attackFish = new NearestAttackableTargetGoal(this, AbstractFishEntity.class, 20, false, false, (p_lambda$registerGoals$5_0_) -> {
         return p_lambda$registerGoals$5_0_ instanceof AbstractGroupFishEntity;
      });
      this.goalSelector.addGoal(0, new FoxEntity.SwimGoal());
      this.goalSelector.addGoal(1, new FoxEntity.JumpGoal());
      this.goalSelector.addGoal(2, new FoxEntity.PanicGoal(2.2D));
      this.goalSelector.addGoal(3, new FoxEntity.MateGoal(1.0D));
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D, (p_lambda$registerGoals$6_1_) -> {
         return field_213514_bH.test(p_lambda$registerGoals$6_1_) && !this.isTrustedUUID(p_lambda$registerGoals$6_1_.getUniqueID()) && !this.isFoxAggroed();
      }));
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, WolfEntity.class, 8.0F, 1.6D, 1.4D, (p_lambda$registerGoals$7_1_) -> {
         return !((WolfEntity)p_lambda$registerGoals$7_1_).isTamed() && !this.isFoxAggroed();
      }));
      this.goalSelector.addGoal(5, new FoxEntity.FollowTargetGoal());
      this.goalSelector.addGoal(6, new FoxEntity.PounceGoal());
      this.goalSelector.addGoal(6, new FoxEntity.FindShelterGoal(1.25D));
      this.goalSelector.addGoal(7, new FoxEntity.BiteGoal(1.2000000476837158D, true));
      this.goalSelector.addGoal(7, new FoxEntity.SleepGoal());
      this.goalSelector.addGoal(8, new FoxEntity.FollowGoal(this, 1.25D));
      this.goalSelector.addGoal(9, new FoxEntity.StrollGoal(32, 200));
      this.goalSelector.addGoal(10, new FoxEntity.EatBerriesGoal(1.2000000476837158D, 12, 2));
      this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(11, new FoxEntity.FindItemsGoal());
      this.goalSelector.addGoal(12, new FoxEntity.WatchGoal(this, PlayerEntity.class, 24.0F));
      this.goalSelector.addGoal(13, new FoxEntity.SitAndLookGoal());
      this.targetSelector.addGoal(3, new FoxEntity.RevengeGoal(LivingEntity.class, false, false, (p_lambda$registerGoals$8_1_) -> {
         return field_213512_bF.test(p_lambda$registerGoals$8_1_) && !this.isTrustedUUID(p_lambda$registerGoals$8_1_.getUniqueID());
      }));
   }

   public SoundEvent getEatSound(ItemStack p_213353_1_) {
      return SoundEvents.ENTITY_FOX_EAT;
   }

   public void livingTick() {
      if (!this.world.isRemote && this.isAlive() && this.isServerWorld()) {
         ++this.eatTicks;
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (this.canEatItem(itemstack)) {
            if (this.eatTicks > 600) {
               ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
               if (!itemstack1.isEmpty()) {
                  this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
               }

               this.eatTicks = 0;
            } else if (this.eatTicks > 560 && this.rand.nextFloat() < 0.1F) {
               this.playSound(this.getEatSound(itemstack), 1.0F, 1.0F);
               this.world.setEntityState(this, (byte)45);
            }
         }

         LivingEntity livingentity = this.getAttackTarget();
         if (livingentity == null || !livingentity.isAlive()) {
            this.setCrouching(false);
            this.func_213502_u(false);
         }
      }

      if (this.isSleeping() || this.isMovementBlocked()) {
         this.isJumping = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
      }

      super.livingTick();
      if (this.isFoxAggroed() && this.rand.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
      }

   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   private boolean canEatItem(ItemStack p_213464_1_) {
      return p_213464_1_.getItem().isFood() && this.getAttackTarget() == null && this.onGround && !this.isSleeping();
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      if (this.rand.nextFloat() < 0.2F) {
         float f = this.rand.nextFloat();
         ItemStack itemstack;
         if (f < 0.05F) {
            itemstack = new ItemStack(Items.EMERALD);
         } else if (f < 0.2F) {
            itemstack = new ItemStack(Items.EGG);
         } else if (f < 0.4F) {
            itemstack = this.rand.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (f < 0.6F) {
            itemstack = new ItemStack(Items.WHEAT);
         } else if (f < 0.8F) {
            itemstack = new ItemStack(Items.LEATHER);
         } else {
            itemstack = new ItemStack(Items.FEATHER);
         }

         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 45) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            for(int i = 0; i < 8; ++i) {
               Vec3d vec3d = (new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * 0.017453292F).rotateYaw(-this.rotationYaw * 0.017453292F);
               this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.func_226277_ct_() + this.getLookVec().x / 2.0D, this.func_226278_cu_(), this.func_226281_cx_() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   public FoxEntity createChild(AgeableEntity p_90011_1_) {
      FoxEntity foxentity = (FoxEntity)EntityType.FOX.create(this.world);
      foxentity.setVariantType(this.rand.nextBoolean() ? this.getVariantType() : ((FoxEntity)p_90011_1_).getVariantType());
      return foxentity;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      Biome biome = p_213386_1_.func_226691_t_(new BlockPos(this));
      FoxEntity.Type foxentity$type = FoxEntity.Type.getTypeByBiome(biome);
      boolean flag = false;
      if (p_213386_4_ instanceof FoxEntity.FoxData) {
         foxentity$type = ((FoxEntity.FoxData)p_213386_4_).field_220366_a;
         if (((FoxEntity.FoxData)p_213386_4_).func_226257_a_() >= 2) {
            flag = true;
         }
      } else {
         p_213386_4_ = new FoxEntity.FoxData(foxentity$type);
      }

      this.setVariantType(foxentity$type);
      if (flag) {
         this.setGrowingAge(-24000);
      }

      this.func_213501_ej();
      this.setEquipmentBasedOnDifficulty(p_213386_2_);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   private void func_213501_ej() {
      if (this.getVariantType() == FoxEntity.Type.RED) {
         this.targetSelector.addGoal(4, this.attackAnimals);
         this.targetSelector.addGoal(4, this.attackTurtles);
         this.targetSelector.addGoal(6, this.attackFish);
      } else {
         this.targetSelector.addGoal(4, this.attackFish);
         this.targetSelector.addGoal(6, this.attackAnimals);
         this.targetSelector.addGoal(6, this.attackTurtles);
      }

   }

   protected void consumeItemFromStack(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (this.isBreedingItem(p_175505_2_)) {
         this.playSound(this.getEatSound(p_175505_2_), 1.0F, 1.0F);
      }

      super.consumeItemFromStack(p_175505_1_, p_175505_2_);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isChild() ? p_213348_2_.height * 0.85F : 0.4F;
   }

   public FoxEntity.Type getVariantType() {
      return FoxEntity.Type.getTypeByIndex((Integer)this.dataManager.get(FOX_TYPE));
   }

   private void setVariantType(FoxEntity.Type p_213474_1_) {
      this.dataManager.set(FOX_TYPE, p_213474_1_.getIndex());
   }

   private List<UUID> getTrustedUUIDs() {
      List<UUID> list = Lists.newArrayList();
      list.add(((Optional)this.dataManager.get(TRUSTED_UUID_SECONDARY)).orElse((UUID)null));
      list.add(((Optional)this.dataManager.get(TRUSTED_UUID_MAIN)).orElse((UUID)null));
      return list;
   }

   private void addTrustedUUID(@Nullable UUID p_213465_1_) {
      if (((Optional)this.dataManager.get(TRUSTED_UUID_SECONDARY)).isPresent()) {
         this.dataManager.set(TRUSTED_UUID_MAIN, Optional.ofNullable(p_213465_1_));
      } else {
         this.dataManager.set(TRUSTED_UUID_SECONDARY, Optional.ofNullable(p_213465_1_));
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      List<UUID> list = this.getTrustedUUIDs();
      ListNBT listnbt = new ListNBT();
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         UUID uuid = (UUID)var4.next();
         if (uuid != null) {
            listnbt.add(NBTUtil.writeUniqueId(uuid));
         }
      }

      p_213281_1_.put("TrustedUUIDs", listnbt);
      p_213281_1_.putBoolean("Sleeping", this.isSleeping());
      p_213281_1_.putString("Type", this.getVariantType().getName());
      p_213281_1_.putBoolean("Sitting", this.isSitting());
      p_213281_1_.putBoolean("Crouching", this.isCrouching());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ListNBT listnbt = p_70037_1_.getList("TrustedUUIDs", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.addTrustedUUID(NBTUtil.readUniqueId(listnbt.getCompound(i)));
      }

      this.setSleeping(p_70037_1_.getBoolean("Sleeping"));
      this.setVariantType(FoxEntity.Type.getTypeByName(p_70037_1_.getString("Type")));
      this.setSitting(p_70037_1_.getBoolean("Sitting"));
      this.setCrouching(p_70037_1_.getBoolean("Crouching"));
      this.func_213501_ej();
   }

   public boolean isSitting() {
      return this.getFoxFlag(1);
   }

   public void setSitting(boolean p_213466_1_) {
      this.setFoxFlag(1, p_213466_1_);
   }

   public boolean func_213472_dX() {
      return this.getFoxFlag(64);
   }

   private void func_213492_v(boolean p_213492_1_) {
      this.setFoxFlag(64, p_213492_1_);
   }

   private boolean isFoxAggroed() {
      return this.getFoxFlag(128);
   }

   private void setFoxAggroed(boolean p_213482_1_) {
      this.setFoxFlag(128, p_213482_1_);
   }

   public boolean isSleeping() {
      return this.getFoxFlag(32);
   }

   private void setSleeping(boolean p_213485_1_) {
      this.setFoxFlag(32, p_213485_1_);
   }

   private void setFoxFlag(int p_213505_1_, boolean p_213505_2_) {
      if (p_213505_2_) {
         this.dataManager.set(FOX_FLAGS, (byte)((Byte)this.dataManager.get(FOX_FLAGS) | p_213505_1_));
      } else {
         this.dataManager.set(FOX_FLAGS, (byte)((Byte)this.dataManager.get(FOX_FLAGS) & ~p_213505_1_));
      }

   }

   private boolean getFoxFlag(int p_213507_1_) {
      return ((Byte)this.dataManager.get(FOX_FLAGS) & p_213507_1_) != 0;
   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_213365_1_);
      if (!this.getItemStackFromSlot(equipmentslottype).isEmpty()) {
         return false;
      } else {
         return equipmentslottype == EquipmentSlotType.MAINHAND && super.func_213365_e(p_213365_1_);
      }
   }

   protected boolean canEquipItem(ItemStack p_175448_1_) {
      Item item = p_175448_1_.getItem();
      ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      return itemstack.isEmpty() || this.eatTicks > 0 && item.isFood() && !itemstack.getItem().isFood();
   }

   private void spitOutItem(ItemStack p_213495_1_) {
      if (!p_213495_1_.isEmpty() && !this.world.isRemote) {
         ItemEntity itementity = new ItemEntity(this.world, this.func_226277_ct_() + this.getLookVec().x, this.func_226278_cu_() + 1.0D, this.func_226281_cx_() + this.getLookVec().z, p_213495_1_);
         itementity.setPickupDelay(40);
         itementity.setThrowerId(this.getUniqueID());
         this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
         this.world.addEntity(itementity);
      }

   }

   private void spawnItem(ItemStack p_213486_1_) {
      ItemEntity itementity = new ItemEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_213486_1_);
      this.world.addEntity(itementity);
   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (this.canEquipItem(itemstack)) {
         int i = itemstack.getCount();
         if (i > 1) {
            this.spawnItem(itemstack.split(i - 1));
         }

         this.spitOutItem(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
         this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
         this.onItemPickup(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
         this.eatTicks = 0;
      }

   }

   public void tick() {
      super.tick();
      if (this.isServerWorld()) {
         boolean flag = this.isInWater();
         if (flag || this.getAttackTarget() != null || this.world.isThundering()) {
            this.func_213454_em();
         }

         if (flag || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.func_213472_dX() && this.world.rand.nextFloat() < 0.2F) {
            BlockPos blockpos = new BlockPos(this);
            BlockState blockstate = this.world.getBlockState(blockpos);
            this.world.playEvent(2001, blockpos, Block.getStateId(blockstate));
         }
      }

      this.field_213519_bM = this.field_213518_bL;
      if (this.func_213467_eg()) {
         this.field_213518_bL += (1.0F - this.field_213518_bL) * 0.4F;
      } else {
         this.field_213518_bL += (0.0F - this.field_213518_bL) * 0.4F;
      }

      this.field_213521_bO = this.field_213520_bN;
      if (this.isCrouching()) {
         this.field_213520_bN += 0.2F;
         if (this.field_213520_bN > 3.0F) {
            this.field_213520_bN = 3.0F;
         }
      } else {
         this.field_213520_bN = 0.0F;
      }

   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.SWEET_BERRIES;
   }

   protected void onChildSpawnFromEgg(PlayerEntity p_213406_1_, AgeableEntity p_213406_2_) {
      ((FoxEntity)p_213406_2_).addTrustedUUID(p_213406_1_.getUniqueID());
   }

   public boolean func_213480_dY() {
      return this.getFoxFlag(16);
   }

   public void func_213461_s(boolean p_213461_1_) {
      this.setFoxFlag(16, p_213461_1_);
   }

   public boolean func_213490_ee() {
      return this.field_213520_bN == 3.0F;
   }

   public void setCrouching(boolean p_213451_1_) {
      this.setFoxFlag(4, p_213451_1_);
   }

   public boolean isCrouching() {
      return this.getFoxFlag(4);
   }

   public void func_213502_u(boolean p_213502_1_) {
      this.setFoxFlag(8, p_213502_1_);
   }

   public boolean func_213467_eg() {
      return this.getFoxFlag(8);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213475_v(float p_213475_1_) {
      return MathHelper.lerp(p_213475_1_, this.field_213519_bM, this.field_213518_bL) * 0.11F * 3.1415927F;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213503_w(float p_213503_1_) {
      return MathHelper.lerp(p_213503_1_, this.field_213521_bO, this.field_213520_bN);
   }

   public void setAttackTarget(@Nullable LivingEntity p_70624_1_) {
      if (this.isFoxAggroed() && p_70624_1_ == null) {
         this.setFoxAggroed(false);
      }

      super.setAttackTarget(p_70624_1_);
   }

   protected int func_225508_e_(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ - 5.0F) * p_225508_2_);
   }

   private void func_213454_em() {
      this.setSleeping(false);
   }

   private void func_213499_en() {
      this.func_213502_u(false);
      this.setCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setFoxAggroed(false);
      this.func_213492_v(false);
   }

   private boolean func_213478_eo() {
      return !this.isSleeping() && !this.isSitting() && !this.func_213472_dX();
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent == SoundEvents.ENTITY_FOX_SCREECH) {
         this.playSound(soundevent, 2.0F, this.getSoundPitch());
      } else {
         super.playAmbientSound();
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.ENTITY_FOX_SLEEP;
      } else {
         if (!this.world.isDaytime() && this.rand.nextFloat() < 0.1F) {
            List<PlayerEntity> list = this.world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D), EntityPredicates.NOT_SPECTATING);
            if (list.isEmpty()) {
               return SoundEvents.ENTITY_FOX_SCREECH;
            }
         }

         return SoundEvents.ENTITY_FOX_AMBIENT;
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_FOX_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_FOX_DEATH;
   }

   private boolean isTrustedUUID(UUID p_213468_1_) {
      return this.getTrustedUUIDs().contains(p_213468_1_);
   }

   protected void spawnDrops(DamageSource p_213345_1_) {
      ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      if (!itemstack.isEmpty()) {
         this.entityDropItem(itemstack);
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      }

      super.spawnDrops(p_213345_1_);
   }

   public static boolean func_213481_a(FoxEntity p_213481_0_, LivingEntity p_213481_1_) {
      double d0 = p_213481_1_.func_226281_cx_() - p_213481_0_.func_226281_cx_();
      double d1 = p_213481_1_.func_226277_ct_() - p_213481_0_.func_226277_ct_();
      double d2 = d0 / d1;
      int i = true;

      for(int j = 0; j < 6; ++j) {
         double d3 = d2 == 0.0D ? 0.0D : d0 * (double)((float)j / 6.0F);
         double d4 = d2 == 0.0D ? d1 * (double)((float)j / 6.0F) : d3 / d2;

         for(int k = 1; k < 4; ++k) {
            if (!p_213481_0_.world.getBlockState(new BlockPos(p_213481_0_.func_226277_ct_() + d4, p_213481_0_.func_226278_cu_() + (double)k, p_213481_0_.func_226281_cx_() + d3)).getMaterial().isReplaceable()) {
               return false;
            }
         }
      }

      return true;
   }

   static {
      FOX_TYPE = EntityDataManager.createKey(FoxEntity.class, DataSerializers.VARINT);
      FOX_FLAGS = EntityDataManager.createKey(FoxEntity.class, DataSerializers.BYTE);
      TRUSTED_UUID_SECONDARY = EntityDataManager.createKey(FoxEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
      TRUSTED_UUID_MAIN = EntityDataManager.createKey(FoxEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
      field_213511_bE = (p_lambda$static$0_0_) -> {
         return !p_lambda$static$0_0_.cannotPickup() && p_lambda$static$0_0_.isAlive();
      };
      field_213512_bF = (p_lambda$static$1_0_) -> {
         if (!(p_lambda$static$1_0_ instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity livingentity = (LivingEntity)p_lambda$static$1_0_;
            return livingentity.getLastAttackedEntity() != null && livingentity.getLastAttackedEntityTime() < livingentity.ticksExisted + 600;
         }
      };
      field_213513_bG = (p_lambda$static$2_0_) -> {
         return p_lambda$static$2_0_ instanceof ChickenEntity || p_lambda$static$2_0_ instanceof RabbitEntity;
      };
      field_213514_bH = (p_lambda$static$3_0_) -> {
         return !p_lambda$static$3_0_.func_226273_bm_() && EntityPredicates.CAN_AI_TARGET.test(p_lambda$static$3_0_);
      };
   }

   class WatchGoal extends LookAtGoal {
      public WatchGoal(MobEntity p_i50733_2_, Class<? extends LivingEntity> p_i50733_3_, float p_i50733_4_) {
         super(p_i50733_2_, p_i50733_3_, p_i50733_4_);
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !FoxEntity.this.func_213472_dX() && !FoxEntity.this.func_213467_eg();
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && !FoxEntity.this.func_213472_dX() && !FoxEntity.this.func_213467_eg();
      }
   }

   public static enum Type {
      RED(0, "red", new Biome[]{Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS}),
      SNOW(1, "snow", new Biome[]{Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS});

      private static final FoxEntity.Type[] field_221088_c = (FoxEntity.Type[])Arrays.stream(values()).sorted(Comparator.comparingInt(FoxEntity.Type::getIndex)).toArray((p_lambda$static$0_0_) -> {
         return new FoxEntity.Type[p_lambda$static$0_0_];
      });
      private static final Map<String, FoxEntity.Type> TYPES_BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(FoxEntity.Type::getName, (p_lambda$static$1_0_) -> {
         return p_lambda$static$1_0_;
      }));
      private final int index;
      private final String name;
      private final List<Biome> spawnBiomes;

      private Type(int p_i50721_3_, String p_i50721_4_, Biome... p_i50721_5_) {
         this.index = p_i50721_3_;
         this.name = p_i50721_4_;
         this.spawnBiomes = Arrays.asList(p_i50721_5_);
      }

      public String getName() {
         return this.name;
      }

      public List<Biome> getSpawnBiomes() {
         return this.spawnBiomes;
      }

      public int getIndex() {
         return this.index;
      }

      public static FoxEntity.Type getTypeByName(String p_221087_0_) {
         return (FoxEntity.Type)TYPES_BY_NAME.getOrDefault(p_221087_0_, RED);
      }

      public static FoxEntity.Type getTypeByIndex(int p_221080_0_) {
         if (p_221080_0_ < 0 || p_221080_0_ > field_221088_c.length) {
            p_221080_0_ = 0;
         }

         return field_221088_c[p_221080_0_];
      }

      public static FoxEntity.Type getTypeByBiome(Biome p_221086_0_) {
         return SNOW.getSpawnBiomes().contains(p_221086_0_) ? SNOW : RED;
      }
   }

   class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
      public SwimGoal() {
         super(FoxEntity.this);
      }

      public void startExecuting() {
         super.startExecuting();
         FoxEntity.this.func_213499_en();
      }

      public boolean shouldExecute() {
         return FoxEntity.this.isInWater() && FoxEntity.this.getSubmergedHeight() > 0.25D || FoxEntity.this.isInLava();
      }
   }

   class StrollGoal extends MoveThroughVillageAtNightGoal {
      public StrollGoal(int p_i50726_2_, int p_i50726_3_) {
         super(FoxEntity.this, p_i50726_3_);
      }

      public void startExecuting() {
         FoxEntity.this.func_213499_en();
         super.startExecuting();
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && this.func_220759_g();
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.func_220759_g();
      }

      private boolean func_220759_g() {
         return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isFoxAggroed() && FoxEntity.this.getAttackTarget() == null;
      }
   }

   class SleepGoal extends FoxEntity.BaseGoal {
      private int field_220825_c;

      public SleepGoal() {
         super(null);
         this.field_220825_c = FoxEntity.this.rand.nextInt(140);
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      public boolean shouldExecute() {
         if (FoxEntity.this.moveStrafing == 0.0F && FoxEntity.this.moveVertical == 0.0F && FoxEntity.this.moveForward == 0.0F) {
            return this.func_220823_j() || FoxEntity.this.isSleeping();
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return this.func_220823_j();
      }

      private boolean func_220823_j() {
         if (this.field_220825_c > 0) {
            --this.field_220825_c;
            return false;
         } else {
            return FoxEntity.this.world.isDaytime() && this.func_220813_g() && !this.func_220814_h();
         }
      }

      public void resetTask() {
         this.field_220825_c = FoxEntity.this.rand.nextInt(140);
         FoxEntity.this.func_213499_en();
      }

      public void startExecuting() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.func_213502_u(false);
         FoxEntity.this.setJumping(false);
         FoxEntity.this.setSleeping(true);
         FoxEntity.this.getNavigator().clearPath();
         FoxEntity.this.getMoveHelper().setMoveTo(FoxEntity.this.func_226277_ct_(), FoxEntity.this.func_226278_cu_(), FoxEntity.this.func_226281_cx_(), 0.0D);
      }
   }

   class SitAndLookGoal extends FoxEntity.BaseGoal {
      private double field_220819_c;
      private double field_220820_d;
      private int field_220821_e;
      private int field_220822_f;

      public SitAndLookGoal() {
         super(null);
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         return FoxEntity.this.getRevengeTarget() == null && FoxEntity.this.getRNG().nextFloat() < 0.02F && !FoxEntity.this.isSleeping() && FoxEntity.this.getAttackTarget() == null && FoxEntity.this.getNavigator().noPath() && !this.func_220814_h() && !FoxEntity.this.func_213480_dY() && !FoxEntity.this.isCrouching();
      }

      public boolean shouldContinueExecuting() {
         return this.field_220822_f > 0;
      }

      public void startExecuting() {
         this.func_220817_j();
         this.field_220822_f = 2 + FoxEntity.this.getRNG().nextInt(3);
         FoxEntity.this.setSitting(true);
         FoxEntity.this.getNavigator().clearPath();
      }

      public void resetTask() {
         FoxEntity.this.setSitting(false);
      }

      public void tick() {
         --this.field_220821_e;
         if (this.field_220821_e <= 0) {
            --this.field_220822_f;
            this.func_220817_j();
         }

         FoxEntity.this.getLookController().setLookPosition(FoxEntity.this.func_226277_ct_() + this.field_220819_c, FoxEntity.this.func_226280_cw_(), FoxEntity.this.func_226281_cx_() + this.field_220820_d, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
      }

      private void func_220817_j() {
         double d0 = 6.283185307179586D * FoxEntity.this.getRNG().nextDouble();
         this.field_220819_c = Math.cos(d0);
         this.field_220820_d = Math.sin(d0);
         this.field_220821_e = 80 + FoxEntity.this.getRNG().nextInt(20);
      }
   }

   class RevengeGoal extends NearestAttackableTargetGoal<LivingEntity> {
      @Nullable
      private LivingEntity field_220786_j;
      private LivingEntity field_220787_k;
      private int field_220788_l;

      public RevengeGoal(Class<LivingEntity> p_i50743_2_, boolean p_i50743_3_, @Nullable boolean p_i50743_4_, Predicate<LivingEntity> p_i50743_5_) {
         super(FoxEntity.this, p_i50743_2_, 10, p_i50743_3_, p_i50743_4_, p_i50743_5_);
      }

      public boolean shouldExecute() {
         if (this.targetChance > 0 && this.goalOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
         } else {
            Iterator var1 = FoxEntity.this.getTrustedUUIDs().iterator();

            while(var1.hasNext()) {
               UUID uuid = (UUID)var1.next();
               if (uuid != null && FoxEntity.this.world instanceof ServerWorld) {
                  Entity entity = ((ServerWorld)FoxEntity.this.world).getEntityByUuid(uuid);
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingentity = (LivingEntity)entity;
                     this.field_220787_k = livingentity;
                     this.field_220786_j = livingentity.getRevengeTarget();
                     int i = livingentity.getRevengeTimer();
                     return i != this.field_220788_l && this.isSuitableTarget(this.field_220786_j, this.targetEntitySelector);
                  }
               }
            }

            return false;
         }
      }

      public void startExecuting() {
         FoxEntity.this.setAttackTarget(this.field_220786_j);
         this.nearestTarget = this.field_220786_j;
         if (this.field_220787_k != null) {
            this.field_220788_l = this.field_220787_k.getRevengeTimer();
         }

         FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
         FoxEntity.this.setFoxAggroed(true);
         FoxEntity.this.func_213454_em();
         super.startExecuting();
      }
   }

   public class PounceGoal extends net.minecraft.entity.ai.goal.JumpGoal {
      public boolean shouldExecute() {
         if (!FoxEntity.this.func_213490_ee()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getAttackTarget();
            if (livingentity != null && livingentity.isAlive()) {
               if (livingentity.getAdjustedHorizontalFacing() != livingentity.getHorizontalFacing()) {
                  return false;
               } else {
                  boolean flag = FoxEntity.func_213481_a(FoxEntity.this, livingentity);
                  if (!flag) {
                     FoxEntity.this.getNavigator().getPathToEntityLiving(livingentity, 0);
                     FoxEntity.this.setCrouching(false);
                     FoxEntity.this.func_213502_u(false);
                  }

                  return flag;
               }
            } else {
               return false;
            }
         }
      }

      public boolean shouldContinueExecuting() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null && livingentity.isAlive()) {
            double d0 = FoxEntity.this.getMotion().y;
            return (d0 * d0 >= 0.05000000074505806D || Math.abs(FoxEntity.this.rotationPitch) >= 15.0F || !FoxEntity.this.onGround) && !FoxEntity.this.func_213472_dX();
         } else {
            return false;
         }
      }

      public boolean isPreemptible() {
         return false;
      }

      public void startExecuting() {
         FoxEntity.this.setJumping(true);
         FoxEntity.this.func_213461_s(true);
         FoxEntity.this.func_213502_u(false);
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, 60.0F, 30.0F);
         Vec3d vec3d = (new Vec3d(livingentity.func_226277_ct_() - FoxEntity.this.func_226277_ct_(), livingentity.func_226278_cu_() - FoxEntity.this.func_226278_cu_(), livingentity.func_226281_cx_() - FoxEntity.this.func_226281_cx_())).normalize();
         FoxEntity.this.setMotion(FoxEntity.this.getMotion().add(vec3d.x * 0.8D, 0.9D, vec3d.z * 0.8D));
         FoxEntity.this.getNavigator().clearPath();
      }

      public void resetTask() {
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.field_213520_bN = 0.0F;
         FoxEntity.this.field_213521_bO = 0.0F;
         FoxEntity.this.func_213502_u(false);
         FoxEntity.this.func_213461_s(false);
      }

      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null) {
            FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, 60.0F, 30.0F);
         }

         if (!FoxEntity.this.func_213472_dX()) {
            Vec3d vec3d = FoxEntity.this.getMotion();
            if (vec3d.y * vec3d.y < 0.029999999329447746D && FoxEntity.this.rotationPitch != 0.0F) {
               FoxEntity.this.rotationPitch = MathHelper.func_226167_j_(FoxEntity.this.rotationPitch, 0.0F, 0.2F);
            } else {
               double d0 = Math.sqrt(Entity.func_213296_b(vec3d));
               double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.length()) * 57.2957763671875D;
               FoxEntity.this.rotationPitch = (float)d1;
            }
         }

         if (livingentity != null && FoxEntity.this.getDistance(livingentity) <= 2.0F) {
            FoxEntity.this.attackEntityAsMob(livingentity);
         } else if (FoxEntity.this.rotationPitch > 0.0F && FoxEntity.this.onGround && (float)FoxEntity.this.getMotion().y != 0.0F && FoxEntity.this.world.getBlockState(new BlockPos(FoxEntity.this)).getBlock() == Blocks.SNOW) {
            FoxEntity.this.rotationPitch = 60.0F;
            FoxEntity.this.setAttackTarget((LivingEntity)null);
            FoxEntity.this.func_213492_v(true);
         }

      }
   }

   class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      public PanicGoal(double p_i50729_2_) {
         super(FoxEntity.this, p_i50729_2_);
      }

      public boolean shouldExecute() {
         return !FoxEntity.this.isFoxAggroed() && super.shouldExecute();
      }
   }

   class MoveHelperController extends MovementController {
      public MoveHelperController() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (FoxEntity.this.func_213478_eo()) {
            super.tick();
         }

      }
   }

   class MateGoal extends BreedGoal {
      public MateGoal(double p_i50738_2_) {
         super(FoxEntity.this, p_i50738_2_);
      }

      public void startExecuting() {
         ((FoxEntity)this.animal).func_213499_en();
         ((FoxEntity)this.field_75391_e).func_213499_en();
         super.startExecuting();
      }

      protected void spawnBaby() {
         FoxEntity foxentity = (FoxEntity)this.animal.createChild(this.field_75391_e);
         if (foxentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            ServerPlayerEntity serverplayerentity1 = this.field_75391_e.getLoveCause();
            ServerPlayerEntity serverplayerentity2 = serverplayerentity;
            if (serverplayerentity != null) {
               foxentity.addTrustedUUID(serverplayerentity.getUniqueID());
            } else {
               serverplayerentity2 = serverplayerentity1;
            }

            if (serverplayerentity1 != null && serverplayerentity != serverplayerentity1) {
               foxentity.addTrustedUUID(serverplayerentity1.getUniqueID());
            }

            if (serverplayerentity2 != null) {
               serverplayerentity2.addStat(Stats.ANIMALS_BRED);
               CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity2, this.animal, this.field_75391_e, foxentity);
            }

            int i = true;
            this.animal.setGrowingAge(6000);
            this.field_75391_e.setGrowingAge(6000);
            this.animal.resetInLove();
            this.field_75391_e.resetInLove();
            foxentity.setGrowingAge(-24000);
            foxentity.setLocationAndAngles(this.animal.func_226277_ct_(), this.animal.func_226278_cu_(), this.animal.func_226281_cx_(), 0.0F, 0.0F);
            this.world.addEntity(foxentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
               this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.func_226277_ct_(), this.animal.func_226278_cu_(), this.animal.func_226281_cx_(), this.animal.getRNG().nextInt(7) + 1));
            }
         }

      }
   }

   public class LookHelperController extends LookController {
      public LookHelperController() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (!FoxEntity.this.isSleeping()) {
            super.tick();
         }

      }

      protected boolean func_220680_b() {
         return !FoxEntity.this.func_213480_dY() && !FoxEntity.this.isCrouching() && !FoxEntity.this.func_213467_eg() & !FoxEntity.this.func_213472_dX();
      }
   }

   class JumpGoal extends Goal {
      int field_220811_a;

      public JumpGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         return FoxEntity.this.func_213472_dX();
      }

      public boolean shouldContinueExecuting() {
         return this.shouldExecute() && this.field_220811_a > 0;
      }

      public void startExecuting() {
         this.field_220811_a = 40;
      }

      public void resetTask() {
         FoxEntity.this.func_213492_v(false);
      }

      public void tick() {
         --this.field_220811_a;
      }
   }

   public static class FoxData extends AgeableEntity.AgeableData {
      public final FoxEntity.Type field_220366_a;

      public FoxData(FoxEntity.Type p_i50734_1_) {
         this.func_226259_a_(false);
         this.field_220366_a = p_i50734_1_;
      }
   }

   class FollowTargetGoal extends Goal {
      public FollowTargetGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         if (FoxEntity.this.isSleeping()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getAttackTarget();
            return livingentity != null && livingentity.isAlive() && FoxEntity.field_213513_bG.test(livingentity) && FoxEntity.this.getDistanceSq(livingentity) > 36.0D && !FoxEntity.this.isCrouching() && !FoxEntity.this.func_213467_eg() && !FoxEntity.this.isJumping;
         }
      }

      public void startExecuting() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.func_213492_v(false);
      }

      public void resetTask() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null && FoxEntity.func_213481_a(FoxEntity.this, livingentity)) {
            FoxEntity.this.func_213502_u(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigator().clearPath();
            FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
         } else {
            FoxEntity.this.func_213502_u(false);
            FoxEntity.this.setCrouching(false);
         }

      }

      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
         if (FoxEntity.this.getDistanceSq(livingentity) <= 36.0D) {
            FoxEntity.this.func_213502_u(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigator().clearPath();
         } else {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving(livingentity, 1.5D);
         }

      }
   }

   class FollowGoal extends FollowParentGoal {
      private final FoxEntity owner;

      public FollowGoal(FoxEntity p_i50735_2_, double p_i50735_3_) {
         super(p_i50735_2_, p_i50735_3_);
         this.owner = p_i50735_2_;
      }

      public boolean shouldExecute() {
         return !this.owner.isFoxAggroed() && super.shouldExecute();
      }

      public boolean shouldContinueExecuting() {
         return !this.owner.isFoxAggroed() && super.shouldContinueExecuting();
      }

      public void startExecuting() {
         this.owner.func_213499_en();
         super.startExecuting();
      }
   }

   class FindShelterGoal extends FleeSunGoal {
      private int cooldown = 100;

      public FindShelterGoal(double p_i50724_2_) {
         super(FoxEntity.this, p_i50724_2_);
      }

      public boolean shouldExecute() {
         if (!FoxEntity.this.isSleeping() && this.creature.getAttackTarget() == null) {
            if (FoxEntity.this.world.isThundering()) {
               return true;
            } else if (this.cooldown > 0) {
               --this.cooldown;
               return false;
            } else {
               this.cooldown = 100;
               BlockPos blockpos = new BlockPos(this.creature);
               return FoxEntity.this.world.isDaytime() && FoxEntity.this.world.func_226660_f_(blockpos) && !((ServerWorld)FoxEntity.this.world).func_217483_b_(blockpos) && this.func_220702_g();
            }
         } else {
            return false;
         }
      }

      public void startExecuting() {
         FoxEntity.this.func_213499_en();
         super.startExecuting();
      }
   }

   class FindItemsGoal extends Goal {
      public FindItemsGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         if (!FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            return false;
         } else if (FoxEntity.this.getAttackTarget() == null && FoxEntity.this.getRevengeTarget() == null) {
            if (!FoxEntity.this.func_213478_eo()) {
               return false;
            } else if (FoxEntity.this.getRNG().nextInt(10) != 0) {
               return false;
            } else {
               List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.field_213511_bE);
               return !list.isEmpty() && FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
            }
         } else {
            return false;
         }
      }

      public void tick() {
         List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.field_213511_bE);
         ItemStack itemstack = FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (itemstack.isEmpty() && !list.isEmpty()) {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving((Entity)list.get(0), 1.2000000476837158D);
         }

      }

      public void startExecuting() {
         List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.field_213511_bE);
         if (!list.isEmpty()) {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving((Entity)list.get(0), 1.2000000476837158D);
         }

      }
   }

   public class EatBerriesGoal extends MoveToBlockGoal {
      protected int field_220731_g;

      public EatBerriesGoal(double p_i50737_2_, int p_i50737_4_, int p_i50737_5_) {
         super(FoxEntity.this, p_i50737_2_, p_i50737_4_, p_i50737_5_);
      }

      public double getTargetDistanceSq() {
         return 2.0D;
      }

      public boolean shouldMove() {
         return this.timeoutCounter % 100 == 0;
      }

      protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         BlockState blockstate = p_179488_1_.getBlockState(p_179488_2_);
         return blockstate.getBlock() == Blocks.SWEET_BERRY_BUSH && (Integer)blockstate.get(SweetBerryBushBlock.AGE) >= 2;
      }

      public void tick() {
         if (this.getIsAboveDestination()) {
            if (this.field_220731_g >= 40) {
               this.func_220730_m();
            } else {
               ++this.field_220731_g;
            }
         } else if (!this.getIsAboveDestination() && FoxEntity.this.rand.nextFloat() < 0.05F) {
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void func_220730_m() {
         if (ForgeEventFactory.getMobGriefingEvent(FoxEntity.this.world, FoxEntity.this)) {
            BlockState blockstate = FoxEntity.this.world.getBlockState(this.destinationBlock);
            if (blockstate.getBlock() == Blocks.SWEET_BERRY_BUSH) {
               int i = (Integer)blockstate.get(SweetBerryBushBlock.AGE);
               blockstate.with(SweetBerryBushBlock.AGE, 1);
               int j = 1 + FoxEntity.this.world.rand.nextInt(2) + (i == 3 ? 1 : 0);
               ItemStack itemstack = FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
               if (itemstack.isEmpty()) {
                  FoxEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                  --j;
               }

               if (j > 0) {
                  Block.spawnAsEntity(FoxEntity.this.world, this.destinationBlock, new ItemStack(Items.SWEET_BERRIES, j));
               }

               FoxEntity.this.playSound(SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, 1.0F, 1.0F);
               FoxEntity.this.world.setBlockState(this.destinationBlock, (BlockState)blockstate.with(SweetBerryBushBlock.AGE, 1), 2);
            }
         }

      }

      public boolean shouldExecute() {
         return !FoxEntity.this.isSleeping() && super.shouldExecute();
      }

      public void startExecuting() {
         this.field_220731_g = 0;
         FoxEntity.this.setSitting(false);
         super.startExecuting();
      }
   }

   class BiteGoal extends MeleeAttackGoal {
      public BiteGoal(double p_i50731_2_, boolean p_i50731_4_) {
         super(FoxEntity.this, p_i50731_2_, p_i50731_4_);
      }

      protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
         double d0 = this.getAttackReachSqr(p_190102_1_);
         if (p_190102_2_ <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.attackEntityAsMob(p_190102_1_);
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0F, 1.0F);
         }

      }

      public void startExecuting() {
         FoxEntity.this.func_213502_u(false);
         super.startExecuting();
      }

      public boolean shouldExecute() {
         return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.func_213472_dX() && super.shouldExecute();
      }
   }

   abstract class BaseGoal extends Goal {
      private final EntityPredicate field_220816_b;

      private BaseGoal() {
         this.field_220816_b = (new EntityPredicate()).setDistance(12.0D).setLineOfSiteRequired().setCustomPredicate(FoxEntity.this.new AlertablePredicate());
      }

      protected boolean func_220813_g() {
         BlockPos blockpos = new BlockPos(FoxEntity.this);
         return !FoxEntity.this.world.func_226660_f_(blockpos) && FoxEntity.this.getBlockPathWeight(blockpos) >= 0.0F;
      }

      protected boolean func_220814_h() {
         return !FoxEntity.this.world.getTargettableEntitiesWithinAABB(LivingEntity.class, this.field_220816_b, FoxEntity.this, FoxEntity.this.getBoundingBox().grow(12.0D, 6.0D, 12.0D)).isEmpty();
      }

      // $FF: synthetic method
      BaseGoal(Object p_i50740_2_) {
         this();
      }
   }

   public class AlertablePredicate implements Predicate<LivingEntity> {
      public boolean test(LivingEntity p_test_1_) {
         if (p_test_1_ instanceof FoxEntity) {
            return false;
         } else if (!(p_test_1_ instanceof ChickenEntity) && !(p_test_1_ instanceof RabbitEntity) && !(p_test_1_ instanceof MonsterEntity)) {
            if (p_test_1_ instanceof TameableEntity) {
               return !((TameableEntity)p_test_1_).isTamed();
            } else if (p_test_1_ instanceof PlayerEntity && (p_test_1_.isSpectator() || ((PlayerEntity)p_test_1_).isCreative())) {
               return false;
            } else if (FoxEntity.this.isTrustedUUID(p_test_1_.getUniqueID())) {
               return false;
            } else {
               return !p_test_1_.isSleeping() && !p_test_1_.func_226273_bm_();
            }
         } else {
            return true;
         }
      }
   }
}
