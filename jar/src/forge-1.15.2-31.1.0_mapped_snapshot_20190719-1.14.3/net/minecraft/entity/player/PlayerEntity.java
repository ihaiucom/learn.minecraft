package net.minecraft.entity.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;

public abstract class PlayerEntity extends LivingEntity {
   public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
   protected HashMap<ResourceLocation, BlockPos> spawnPosMap = new HashMap();
   protected HashMap<ResourceLocation, Boolean> spawnForcedMap = new HashMap();
   public static final IAttribute REACH_DISTANCE = (new RangedAttribute((IAttribute)null, "generic.reachDistance", 5.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public static final EntitySize STANDING_SIZE = EntitySize.flexible(0.6F, 1.8F);
   private static final Map<Pose, EntitySize> SIZE_BY_POSE;
   private static final DataParameter<Float> ABSORPTION;
   private static final DataParameter<Integer> PLAYER_SCORE;
   protected static final DataParameter<Byte> PLAYER_MODEL_FLAG;
   protected static final DataParameter<Byte> MAIN_HAND;
   protected static final DataParameter<CompoundNBT> LEFT_SHOULDER_ENTITY;
   protected static final DataParameter<CompoundNBT> RIGHT_SHOULDER_ENTITY;
   private long field_223730_e;
   public final PlayerInventory inventory = new PlayerInventory(this);
   protected EnderChestInventory enterChestInventory = new EnderChestInventory();
   public final PlayerContainer container;
   public Container openContainer;
   protected FoodStats foodStats = new FoodStats();
   protected int flyToggleTimer;
   public float prevCameraYaw;
   public float cameraYaw;
   public int xpCooldown;
   public double prevChasingPosX;
   public double prevChasingPosY;
   public double prevChasingPosZ;
   public double chasingPosX;
   public double chasingPosY;
   public double chasingPosZ;
   private int sleepTimer;
   protected boolean eyesInWaterPlayer;
   protected BlockPos spawnPos;
   protected boolean spawnForced;
   public final PlayerAbilities abilities = new PlayerAbilities();
   public int experienceLevel;
   public int experienceTotal;
   public float experience;
   protected int xpSeed;
   protected final float speedInAir = 0.02F;
   private int lastXPSound;
   private final GameProfile gameProfile;
   @OnlyIn(Dist.CLIENT)
   private boolean hasReducedDebug;
   private ItemStack itemStackMainHand;
   private final CooldownTracker cooldownTracker;
   @Nullable
   public FishingBobberEntity fishingBobber;
   private DimensionType spawnDimension;
   private final Collection<ITextComponent> prefixes;
   private final Collection<ITextComponent> suffixes;
   private final LazyOptional<IItemHandler> playerMainHandler;
   private final LazyOptional<IItemHandler> playerEquipmentHandler;
   private final LazyOptional<IItemHandler> playerJoinedHandler;

   public PlayerEntity(World p_i45324_1_, GameProfile p_i45324_2_) {
      super(EntityType.PLAYER, p_i45324_1_);
      this.itemStackMainHand = ItemStack.EMPTY;
      this.cooldownTracker = this.createCooldownTracker();
      this.spawnDimension = DimensionType.OVERWORLD;
      this.prefixes = new LinkedList();
      this.suffixes = new LinkedList();
      this.playerMainHandler = LazyOptional.of(() -> {
         return new PlayerMainInvWrapper(this.inventory);
      });
      this.playerEquipmentHandler = LazyOptional.of(() -> {
         return new CombinedInvWrapper(new IItemHandlerModifiable[]{new PlayerArmorInvWrapper(this.inventory), new PlayerOffhandInvWrapper(this.inventory)});
      });
      this.playerJoinedHandler = LazyOptional.of(() -> {
         return new PlayerInvWrapper(this.inventory);
      });
      this.setUniqueId(getUUID(p_i45324_2_));
      this.gameProfile = p_i45324_2_;
      this.container = new PlayerContainer(this.inventory, !p_i45324_1_.isRemote, this);
      this.openContainer = this.container;
      BlockPos blockpos = p_i45324_1_.getSpawnPoint();
      this.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)(blockpos.getY() + 1), (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
      this.unused180 = 180.0F;
   }

   public boolean func_223729_a(World p_223729_1_, BlockPos p_223729_2_, GameType p_223729_3_) {
      if (!p_223729_3_.hasLimitedInteractions()) {
         return false;
      } else if (p_223729_3_ == GameType.SPECTATOR) {
         return true;
      } else if (this.isAllowEdit()) {
         return false;
      } else {
         ItemStack itemstack = this.getHeldItemMainhand();
         return itemstack.isEmpty() || !itemstack.canDestroy(p_223729_1_.getTags(), new CachedBlockInfo(p_223729_1_, p_223729_2_, false));
      }
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.LUCK);
      this.getAttributes().registerAttribute(REACH_DISTANCE);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ABSORPTION, 0.0F);
      this.dataManager.register(PLAYER_SCORE, 0);
      this.dataManager.register(PLAYER_MODEL_FLAG, (byte)0);
      this.dataManager.register(MAIN_HAND, (byte)1);
      this.dataManager.register(LEFT_SHOULDER_ENTITY, new CompoundNBT());
      this.dataManager.register(RIGHT_SHOULDER_ENTITY, new CompoundNBT());
   }

   public void tick() {
      BasicEventHooks.onPlayerPreTick(this);
      this.noClip = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.xpCooldown > 0) {
         --this.xpCooldown;
      }

      if (this.isSleeping()) {
         ++this.sleepTimer;
         if (this.sleepTimer > 100) {
            this.sleepTimer = 100;
         }

         if (!this.world.isRemote && !ForgeEventFactory.fireSleepingTimeCheck(this, this.getBedPosition())) {
            this.func_225652_a_(false, true);
         }
      } else if (this.sleepTimer > 0) {
         ++this.sleepTimer;
         if (this.sleepTimer >= 110) {
            this.sleepTimer = 0;
         }
      }

      this.updateEyesInWaterPlayer();
      super.tick();
      if (!this.world.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.container;
      }

      if (this.isBurning() && this.abilities.disableDamage) {
         this.extinguish();
      }

      this.updateCape();
      if (!this.world.isRemote) {
         this.foodStats.tick(this);
         this.addStat(Stats.PLAY_ONE_MINUTE);
         if (this.isAlive()) {
            this.addStat(Stats.TIME_SINCE_DEATH);
         }

         if (this.func_226273_bm_()) {
            this.addStat(Stats.field_226147_n_);
         }

         if (!this.isSleeping()) {
            this.addStat(Stats.TIME_SINCE_REST);
         }
      }

      int i = 29999999;
      double d0 = MathHelper.clamp(this.func_226277_ct_(), -2.9999999E7D, 2.9999999E7D);
      double d1 = MathHelper.clamp(this.func_226281_cx_(), -2.9999999E7D, 2.9999999E7D);
      if (d0 != this.func_226277_ct_() || d1 != this.func_226281_cx_()) {
         this.setPosition(d0, this.func_226278_cu_(), d1);
      }

      ++this.ticksSinceLastSwing;
      ItemStack itemstack = this.getHeldItemMainhand();
      if (!ItemStack.areItemStacksEqual(this.itemStackMainHand, itemstack)) {
         if (!ItemStack.areItemsEqualIgnoreDurability(this.itemStackMainHand, itemstack)) {
            this.resetCooldown();
         }

         this.itemStackMainHand = itemstack.copy();
      }

      this.updateTurtleHelmet();
      this.cooldownTracker.tick();
      this.updatePose();
      BasicEventHooks.onPlayerPostTick(this);
   }

   public boolean func_226563_dT_() {
      return this.func_225608_bj_();
   }

   protected boolean func_226564_dU_() {
      return this.func_225608_bj_();
   }

   protected boolean func_226565_dV_() {
      return this.func_225608_bj_();
   }

   protected boolean updateEyesInWaterPlayer() {
      this.eyesInWaterPlayer = this.areEyesInFluid(FluidTags.WATER, true);
      return this.eyesInWaterPlayer;
   }

   private void updateTurtleHelmet() {
      ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
      if (itemstack.getItem() == Items.TURTLE_HELMET && !this.areEyesInFluid(FluidTags.WATER)) {
         this.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 200, 0, false, false, true));
      }

   }

   protected CooldownTracker createCooldownTracker() {
      return new CooldownTracker();
   }

   private void updateCape() {
      this.prevChasingPosX = this.chasingPosX;
      this.prevChasingPosY = this.chasingPosY;
      this.prevChasingPosZ = this.chasingPosZ;
      double d0 = this.func_226277_ct_() - this.chasingPosX;
      double d1 = this.func_226278_cu_() - this.chasingPosY;
      double d2 = this.func_226281_cx_() - this.chasingPosZ;
      double d3 = 10.0D;
      if (d0 > 10.0D) {
         this.chasingPosX = this.func_226277_ct_();
         this.prevChasingPosX = this.chasingPosX;
      }

      if (d2 > 10.0D) {
         this.chasingPosZ = this.func_226281_cx_();
         this.prevChasingPosZ = this.chasingPosZ;
      }

      if (d1 > 10.0D) {
         this.chasingPosY = this.func_226278_cu_();
         this.prevChasingPosY = this.chasingPosY;
      }

      if (d0 < -10.0D) {
         this.chasingPosX = this.func_226277_ct_();
         this.prevChasingPosX = this.chasingPosX;
      }

      if (d2 < -10.0D) {
         this.chasingPosZ = this.func_226281_cx_();
         this.prevChasingPosZ = this.chasingPosZ;
      }

      if (d1 < -10.0D) {
         this.chasingPosY = this.func_226278_cu_();
         this.prevChasingPosY = this.chasingPosY;
      }

      this.chasingPosX += d0 * 0.25D;
      this.chasingPosZ += d2 * 0.25D;
      this.chasingPosY += d1 * 0.25D;
   }

   protected void updatePose() {
      if (this.isPoseClear(Pose.SWIMMING)) {
         Pose pose;
         if (this.isElytraFlying()) {
            pose = Pose.FALL_FLYING;
         } else if (this.isSleeping()) {
            pose = Pose.SLEEPING;
         } else if (this.isSwimming()) {
            pose = Pose.SWIMMING;
         } else if (this.isSpinAttacking()) {
            pose = Pose.SPIN_ATTACK;
         } else if (this.func_225608_bj_() && !this.abilities.isFlying) {
            pose = Pose.CROUCHING;
         } else {
            pose = Pose.STANDING;
         }

         Pose pose1;
         if (!this.isSpectator() && !this.isPassenger() && !this.isPoseClear(pose)) {
            if (this.isPoseClear(Pose.CROUCHING)) {
               pose1 = Pose.CROUCHING;
            } else {
               pose1 = Pose.SWIMMING;
            }
         } else {
            pose1 = pose;
         }

         this.setPose(pose1);
      }

   }

   public int getMaxInPortalTime() {
      return this.abilities.disableDamage ? 1 : 80;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_PLAYER_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_PLAYER_SPLASH;
   }

   protected SoundEvent getHighspeedSplashSound() {
      return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
   }

   public int getPortalCooldown() {
      return 10;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      this.world.playSound(this, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_);
   }

   public void func_213823_a(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.PLAYERS;
   }

   protected int getFireImmuneTicks() {
      return 20;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 9) {
         this.onItemUseFinish();
      } else if (p_70103_1_ == 23) {
         this.hasReducedDebug = false;
      } else if (p_70103_1_ == 22) {
         this.hasReducedDebug = true;
      } else if (p_70103_1_ == 43) {
         this.func_213824_a(ParticleTypes.CLOUD);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void func_213824_a(IParticleData p_213824_1_) {
      for(int i = 0; i < 5; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(p_213824_1_, this.func_226282_d_(1.0D), this.func_226279_cv_() + 1.0D, this.func_226287_g_(1.0D), d0, d1, d2);
      }

   }

   public void closeScreen() {
      this.openContainer = this.container;
   }

   public void updateRidden() {
      if (!this.world.isRemote && this.func_226564_dU_() && this.isPassenger()) {
         this.stopRiding();
         this.func_226284_e_(false);
      } else {
         double d0 = this.func_226277_ct_();
         double d1 = this.func_226278_cu_();
         double d2 = this.func_226281_cx_();
         float f = this.rotationYaw;
         float f1 = this.rotationPitch;
         super.updateRidden();
         this.prevCameraYaw = this.cameraYaw;
         this.cameraYaw = 0.0F;
         this.addMountedMovementStat(this.func_226277_ct_() - d0, this.func_226278_cu_() - d1, this.func_226281_cx_() - d2);
         if (this.getRidingEntity() instanceof LivingEntity && ((LivingEntity)this.getRidingEntity()).shouldRiderFaceForward(this)) {
            this.rotationPitch = f1;
            this.rotationYaw = f;
            this.renderYawOffset = ((LivingEntity)this.getRidingEntity()).renderYawOffset;
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void preparePlayerToSpawn() {
      this.setPose(Pose.STANDING);
      super.preparePlayerToSpawn();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   protected void updateEntityActionState() {
      super.updateEntityActionState();
      this.updateArmSwingProgress();
      this.rotationYawHead = this.rotationYaw;
   }

   public void livingTick() {
      if (this.flyToggleTimer > 0) {
         --this.flyToggleTimer;
      }

      if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
         if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
            this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
         }
      }

      this.inventory.tick();
      this.prevCameraYaw = this.cameraYaw;
      super.livingTick();
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (!this.world.isRemote) {
         iattributeinstance.setBaseValue((double)this.abilities.getWalkSpeed());
      }

      this.jumpMovementFactor = 0.02F;
      if (this.isSprinting()) {
         this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + 0.005999999865889549D);
      }

      this.setAIMoveSpeed((float)iattributeinstance.getValue());
      float f;
      if (this.onGround && this.getHealth() > 0.0F && !this.isSwimming()) {
         f = Math.min(0.1F, MathHelper.sqrt(func_213296_b(this.getMotion())));
      } else {
         f = 0.0F;
      }

      this.cameraYaw += (f - this.cameraYaw) * 0.4F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AxisAlignedBB axisalignedbb;
         if (this.isPassenger() && !this.getRidingEntity().removed) {
            axisalignedbb = this.getBoundingBox().union(this.getRidingEntity().getBoundingBox()).grow(1.0D, 0.0D, 1.0D);
         } else {
            axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
         }

         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);

         for(int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (!entity.removed) {
               this.collideWithPlayer(entity);
            }
         }
      }

      this.playShoulderEntityAmbientSound(this.getLeftShoulderEntity());
      this.playShoulderEntityAmbientSound(this.getRightShoulderEntity());
      if (!this.world.isRemote && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.isFlying || this.isSleeping()) {
         this.spawnShoulderEntities();
      }

   }

   private void playShoulderEntityAmbientSound(@Nullable CompoundNBT p_192028_1_) {
      if (p_192028_1_ != null && !p_192028_1_.contains("Silent") || !p_192028_1_.getBoolean("Silent")) {
         String s = p_192028_1_.getString("id");
         EntityType.byKey(s).filter((p_lambda$playShoulderEntityAmbientSound$0_0_) -> {
            return p_lambda$playShoulderEntityAmbientSound$0_0_ == EntityType.PARROT;
         }).ifPresent((p_lambda$playShoulderEntityAmbientSound$1_1_) -> {
            ParrotEntity.playAmbientSound(this.world, this);
         });
      }

   }

   private void collideWithPlayer(Entity p_71044_1_) {
      p_71044_1_.onCollideWithPlayer(this);
   }

   public int getScore() {
      return (Integer)this.dataManager.get(PLAYER_SCORE);
   }

   public void setScore(int p_85040_1_) {
      this.dataManager.set(PLAYER_SCORE, p_85040_1_);
   }

   public void addScore(int p_85039_1_) {
      int i = this.getScore();
      this.dataManager.set(PLAYER_SCORE, i + p_85039_1_);
   }

   public void onDeath(DamageSource p_70645_1_) {
      if (!ForgeHooks.onLivingDeath(this, p_70645_1_)) {
         super.onDeath(p_70645_1_);
         this.func_226264_Z_();
         if (!this.isSpectator()) {
            this.spawnDrops(p_70645_1_);
         }

         if (p_70645_1_ != null) {
            this.setMotion((double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * 0.017453292F) * 0.1F));
         } else {
            this.setMotion(0.0D, 0.1D, 0.0D);
         }

         this.addStat(Stats.DEATHS);
         this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
         this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
         this.extinguish();
         this.setFlag(0, false);
      }
   }

   protected void dropInventory() {
      super.dropInventory();
      if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAllItems();
      }

   }

   protected void destroyVanishingCursedItems() {
      for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
         ItemStack itemstack = this.inventory.getStackInSlot(i);
         if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) {
            this.inventory.removeStackFromSlot(i);
         }
      }

   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      if (p_184601_1_ == DamageSource.ON_FIRE) {
         return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
      } else if (p_184601_1_ == DamageSource.DROWN) {
         return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
      } else {
         return p_184601_1_ == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.ENTITY_PLAYER_HURT;
      }
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PLAYER_DEATH;
   }

   public boolean func_225609_n_(boolean p_225609_1_) {
      ItemStack stack = this.inventory.getCurrentItem();
      if (!stack.isEmpty() && stack.onDroppedByPlayer(this)) {
         return ForgeHooks.onPlayerTossEvent(this, this.inventory.decrStackSize(this.inventory.currentItem, p_225609_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1), true) != null;
      } else {
         return false;
      }
   }

   @Nullable
   public ItemEntity dropItem(ItemStack p_71019_1_, boolean p_71019_2_) {
      return ForgeHooks.onPlayerTossEvent(this, p_71019_1_, false);
   }

   @Nullable
   public ItemEntity dropItem(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
      if (p_146097_1_.isEmpty()) {
         return null;
      } else {
         double d0 = this.func_226280_cw_() - 0.30000001192092896D;
         ItemEntity itementity = new ItemEntity(this.world, this.func_226277_ct_(), d0, this.func_226281_cx_(), p_146097_1_);
         itementity.setPickupDelay(40);
         if (p_146097_3_) {
            itementity.setThrowerId(this.getUniqueID());
         }

         float f;
         float f1;
         if (p_146097_2_) {
            f = this.rand.nextFloat() * 0.5F;
            f1 = this.rand.nextFloat() * 6.2831855F;
            itementity.setMotion((double)(-MathHelper.sin(f1) * f), 0.20000000298023224D, (double)(MathHelper.cos(f1) * f));
         } else {
            f = 0.3F;
            f1 = MathHelper.sin(this.rotationPitch * 0.017453292F);
            float f2 = MathHelper.cos(this.rotationPitch * 0.017453292F);
            float f3 = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float f4 = MathHelper.cos(this.rotationYaw * 0.017453292F);
            float f5 = this.rand.nextFloat() * 6.2831855F;
            float f6 = 0.02F * this.rand.nextFloat();
            itementity.setMotion((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f1 * 0.3F + 0.1F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
         }

         return itementity;
      }
   }

   /** @deprecated */
   @Deprecated
   public float getDigSpeed(BlockState p_184813_1_) {
      return this.getDigSpeed(p_184813_1_, (BlockPos)null);
   }

   public float getDigSpeed(BlockState p_getDigSpeed_1_, @Nullable BlockPos p_getDigSpeed_2_) {
      float f = this.inventory.getDestroySpeed(p_getDigSpeed_1_);
      if (f > 1.0F) {
         int i = EnchantmentHelper.getEfficiencyModifier(this);
         ItemStack itemstack = this.getHeldItemMainhand();
         if (i > 0 && !itemstack.isEmpty()) {
            f += (float)(i * i + 1);
         }
      }

      if (EffectUtils.hasMiningSpeedup(this)) {
         f *= 1.0F + (float)(EffectUtils.getMiningSpeedup(this) + 1) * 0.2F;
      }

      if (this.isPotionActive(Effects.MINING_FATIGUE)) {
         float f1;
         switch(this.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
         case 0:
            f1 = 0.3F;
            break;
         case 1:
            f1 = 0.09F;
            break;
         case 2:
            f1 = 0.0027F;
            break;
         case 3:
         default:
            f1 = 8.1E-4F;
         }

         f *= f1;
      }

      if (this.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
         f /= 5.0F;
      }

      if (!this.onGround) {
         f /= 5.0F;
      }

      f = ForgeEventFactory.getBreakSpeed(this, p_getDigSpeed_1_, f, p_getDigSpeed_2_);
      return f;
   }

   public boolean canHarvestBlock(BlockState p_184823_1_) {
      return ForgeEventFactory.doPlayerHarvestCheck(this, p_184823_1_, p_184823_1_.getMaterial().isToolNotRequired() || this.inventory.canHarvestBlock(p_184823_1_));
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setUniqueId(getUUID(this.gameProfile));
      ListNBT listnbt = p_70037_1_.getList("Inventory", 10);
      this.inventory.read(listnbt);
      this.inventory.currentItem = p_70037_1_.getInt("SelectedItemSlot");
      this.sleepTimer = p_70037_1_.getShort("SleepTimer");
      this.experience = p_70037_1_.getFloat("XpP");
      this.experienceLevel = p_70037_1_.getInt("XpLevel");
      this.experienceTotal = p_70037_1_.getInt("XpTotal");
      this.xpSeed = p_70037_1_.getInt("XpSeed");
      if (this.xpSeed == 0) {
         this.xpSeed = this.rand.nextInt();
      }

      this.setScore(p_70037_1_.getInt("Score"));
      if (p_70037_1_.contains("SpawnX", 99) && p_70037_1_.contains("SpawnY", 99) && p_70037_1_.contains("SpawnZ", 99)) {
         this.spawnPos = new BlockPos(p_70037_1_.getInt("SpawnX"), p_70037_1_.getInt("SpawnY"), p_70037_1_.getInt("SpawnZ"));
         this.spawnForced = p_70037_1_.getBoolean("SpawnForced");
      }

      p_70037_1_.getList("Spawns", 10).forEach((p_lambda$readAdditional$2_1_) -> {
         CompoundNBT data = (CompoundNBT)p_lambda$readAdditional$2_1_;
         ResourceLocation dim = new ResourceLocation(data.getString("Dim"));
         this.spawnPosMap.put(dim, new BlockPos(data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ")));
         this.spawnForcedMap.put(dim, data.getBoolean("SpawnForced"));
      });
      DimensionType spawnDim = null;
      if (p_70037_1_.contains("SpawnDimension", 8)) {
         spawnDim = DimensionType.byName(new ResourceLocation(p_70037_1_.getString("SpawnDimension")));
      }

      this.spawnDimension = spawnDim != null ? spawnDim : DimensionType.OVERWORLD;
      this.foodStats.read(p_70037_1_);
      this.abilities.read(p_70037_1_);
      if (p_70037_1_.contains("EnderItems", 9)) {
         this.enterChestInventory.read(p_70037_1_.getList("EnderItems", 10));
      }

      if (p_70037_1_.contains("ShoulderEntityLeft", 10)) {
         this.setLeftShoulderEntity(p_70037_1_.getCompound("ShoulderEntityLeft"));
      }

      if (p_70037_1_.contains("ShoulderEntityRight", 10)) {
         this.setRightShoulderEntity(p_70037_1_.getCompound("ShoulderEntityRight"));
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      p_213281_1_.put("Inventory", this.inventory.write(new ListNBT()));
      p_213281_1_.putInt("SelectedItemSlot", this.inventory.currentItem);
      p_213281_1_.putShort("SleepTimer", (short)this.sleepTimer);
      p_213281_1_.putFloat("XpP", this.experience);
      p_213281_1_.putInt("XpLevel", this.experienceLevel);
      p_213281_1_.putInt("XpTotal", this.experienceTotal);
      p_213281_1_.putInt("XpSeed", this.xpSeed);
      p_213281_1_.putInt("Score", this.getScore());
      if (this.spawnPos != null) {
         p_213281_1_.putInt("SpawnX", this.spawnPos.getX());
         p_213281_1_.putInt("SpawnY", this.spawnPos.getY());
         p_213281_1_.putInt("SpawnZ", this.spawnPos.getZ());
         p_213281_1_.putBoolean("SpawnForced", this.spawnForced);
      }

      this.foodStats.write(p_213281_1_);
      this.abilities.write(p_213281_1_);
      p_213281_1_.put("EnderItems", this.enterChestInventory.write());
      if (!this.getLeftShoulderEntity().isEmpty()) {
         p_213281_1_.put("ShoulderEntityLeft", this.getLeftShoulderEntity());
      }

      if (!this.getRightShoulderEntity().isEmpty()) {
         p_213281_1_.put("ShoulderEntityRight", this.getRightShoulderEntity());
      }

      ListNBT spawnlist = new ListNBT();
      this.spawnPosMap.forEach((p_lambda$writeAdditional$3_2_, p_lambda$writeAdditional$3_3_) -> {
         if (p_lambda$writeAdditional$3_3_ != null) {
            CompoundNBT data = new CompoundNBT();
            data.putString("Dim", p_lambda$writeAdditional$3_2_.toString());
            data.putInt("SpawnX", p_lambda$writeAdditional$3_3_.getX());
            data.putInt("SpawnY", p_lambda$writeAdditional$3_3_.getY());
            data.putInt("SpawnZ", p_lambda$writeAdditional$3_3_.getZ());
            data.putBoolean("SpawnForced", (Boolean)this.spawnForcedMap.getOrDefault(p_lambda$writeAdditional$3_2_, false));
            spawnlist.add(data);
         }

      });
      p_213281_1_.put("Spawns", spawnlist);
      if (this.spawnDimension != DimensionType.OVERWORLD) {
         p_213281_1_.putString("SpawnDimension", this.spawnDimension.getRegistryName().toString());
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      if (super.isInvulnerableTo(p_180431_1_)) {
         return true;
      } else if (p_180431_1_ == DamageSource.DROWN) {
         return !this.world.getGameRules().getBoolean(GameRules.field_226679_A_);
      } else if (p_180431_1_ == DamageSource.FALL) {
         return !this.world.getGameRules().getBoolean(GameRules.field_226680_B_);
      } else if (p_180431_1_.isFireDamage()) {
         return !this.world.getGameRules().getBoolean(GameRules.field_226681_C_);
      } else {
         return false;
      }
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_)) {
         return false;
      } else if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (this.abilities.disableDamage && !p_70097_1_.canHarmInCreative()) {
         return false;
      } else {
         this.idleTime = 0;
         if (this.getHealth() <= 0.0F) {
            return false;
         } else {
            this.spawnShoulderEntities();
            if (p_70097_1_.isDifficultyScaled()) {
               if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                  p_70097_2_ = 0.0F;
               }

               if (this.world.getDifficulty() == Difficulty.EASY) {
                  p_70097_2_ = Math.min(p_70097_2_ / 2.0F + 1.0F, p_70097_2_);
               }

               if (this.world.getDifficulty() == Difficulty.HARD) {
                  p_70097_2_ = p_70097_2_ * 3.0F / 2.0F;
               }
            }

            return p_70097_2_ == 0.0F ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
         }
      }
   }

   protected void blockUsingShield(LivingEntity p_190629_1_) {
      super.blockUsingShield(p_190629_1_);
      if (p_190629_1_.getHeldItemMainhand().canDisableShield(this.activeItemStack, this, p_190629_1_)) {
         this.disableShield(true);
      }

   }

   public boolean canAttackPlayer(PlayerEntity p_96122_1_) {
      Team team = this.getTeam();
      Team team1 = p_96122_1_.getTeam();
      if (team == null) {
         return true;
      } else {
         return !team.isSameTeam(team1) ? true : team.getAllowFriendlyFire();
      }
   }

   protected void damageArmor(float p_70675_1_) {
      this.inventory.damageArmor(p_70675_1_);
   }

   protected void damageShield(float p_184590_1_) {
      if (p_184590_1_ >= 3.0F && this.activeItemStack.isShield(this)) {
         int i = 1 + MathHelper.floor(p_184590_1_);
         Hand hand = this.getActiveHand();
         this.activeItemStack.damageItem(i, this, (p_lambda$damageShield$4_2_) -> {
            p_lambda$damageShield$4_2_.sendBreakAnimation(hand);
            ForgeEventFactory.onPlayerDestroyItem(this, this.activeItemStack, hand);
         });
         if (this.activeItemStack.isEmpty()) {
            if (hand == Hand.MAIN_HAND) {
               this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            } else {
               this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
            }

            this.activeItemStack = ItemStack.EMPTY;
            this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
         }
      }

   }

   protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
      if (!this.isInvulnerableTo(p_70665_1_)) {
         p_70665_2_ = ForgeHooks.onLivingHurt(this, p_70665_1_, p_70665_2_);
         if (p_70665_2_ <= 0.0F) {
            return;
         }

         p_70665_2_ = this.applyArmorCalculations(p_70665_1_, p_70665_2_);
         p_70665_2_ = this.applyPotionDamageCalculations(p_70665_1_, p_70665_2_);
         float f2 = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (p_70665_2_ - f2));
         f2 = ForgeHooks.onLivingDamage(this, p_70665_1_, f2);
         float f = p_70665_2_ - f2;
         if (f > 0.0F && f < 3.4028235E37F) {
            this.addStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
         }

         if (f2 != 0.0F) {
            this.addExhaustion(p_70665_1_.getHungerDamage());
            float f1 = this.getHealth();
            this.setHealth(this.getHealth() - f2);
            this.getCombatTracker().trackDamage(p_70665_1_, f1, f2);
            if (f2 < 3.4028235E37F) {
               this.addStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0F));
            }
         }
      }

   }

   public void openSignEditor(SignTileEntity p_175141_1_) {
   }

   public void openMinecartCommandBlock(CommandBlockLogic p_184809_1_) {
   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
   }

   public void openStructureBlock(StructureBlockTileEntity p_189807_1_) {
   }

   public void func_213826_a(JigsawTileEntity p_213826_1_) {
   }

   public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
   }

   public OptionalInt openContainer(@Nullable INamedContainerProvider p_213829_1_) {
      return OptionalInt.empty();
   }

   public void func_213818_a(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
   }

   public void openBook(ItemStack p_184814_1_, Hand p_184814_2_) {
   }

   public ActionResultType interactOn(Entity p_190775_1_, Hand p_190775_2_) {
      if (this.isSpectator()) {
         if (p_190775_1_ instanceof INamedContainerProvider) {
            this.openContainer((INamedContainerProvider)p_190775_1_);
         }

         return ActionResultType.PASS;
      } else {
         ActionResultType cancelResult = ForgeHooks.onInteractEntity(this, p_190775_1_, p_190775_2_);
         if (cancelResult != null) {
            return cancelResult;
         } else {
            ItemStack itemstack = this.getHeldItem(p_190775_2_);
            ItemStack itemstack1 = itemstack.copy();
            if (p_190775_1_.processInitialInteract(this, p_190775_2_)) {
               if (this.abilities.isCreativeMode && itemstack == this.getHeldItem(p_190775_2_) && itemstack.getCount() < itemstack1.getCount()) {
                  itemstack.setCount(itemstack1.getCount());
               }

               if (!this.abilities.isCreativeMode && itemstack.isEmpty()) {
                  ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, p_190775_2_);
               }

               return ActionResultType.SUCCESS;
            } else {
               if (!itemstack.isEmpty() && p_190775_1_ instanceof LivingEntity) {
                  if (this.abilities.isCreativeMode) {
                     itemstack = itemstack1;
                  }

                  if (itemstack.interactWithEntity(this, (LivingEntity)p_190775_1_, p_190775_2_)) {
                     if (itemstack.isEmpty() && !this.abilities.isCreativeMode) {
                        ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, p_190775_2_);
                        this.setHeldItem(p_190775_2_, ItemStack.EMPTY);
                     }

                     return ActionResultType.SUCCESS;
                  }
               }

               return ActionResultType.PASS;
            }
         }
      }
   }

   public double getYOffset() {
      return -0.35D;
   }

   public void stopRiding() {
      super.stopRiding();
      this.rideCooldown = 0;
   }

   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() || this.isSleeping();
   }

   protected Vec3d func_225514_a_(Vec3d p_225514_1_, MoverType p_225514_2_) {
      if ((p_225514_2_ == MoverType.SELF || p_225514_2_ == MoverType.PLAYER) && this.onGround && this.func_226565_dV_()) {
         double d0 = p_225514_1_.x;
         double d1 = p_225514_1_.z;
         double var7 = 0.05D;

         while(true) {
            while(d0 != 0.0D && this.world.func_226665_a__(this, this.getBoundingBox().offset(d0, (double)(-this.stepHeight), 0.0D))) {
               if (d0 < 0.05D && d0 >= -0.05D) {
                  d0 = 0.0D;
               } else if (d0 > 0.0D) {
                  d0 -= 0.05D;
               } else {
                  d0 += 0.05D;
               }
            }

            while(true) {
               while(d1 != 0.0D && this.world.func_226665_a__(this, this.getBoundingBox().offset(0.0D, (double)(-this.stepHeight), d1))) {
                  if (d1 < 0.05D && d1 >= -0.05D) {
                     d1 = 0.0D;
                  } else if (d1 > 0.0D) {
                     d1 -= 0.05D;
                  } else {
                     d1 += 0.05D;
                  }
               }

               while(true) {
                  while(d0 != 0.0D && d1 != 0.0D && this.world.func_226665_a__(this, this.getBoundingBox().offset(d0, (double)(-this.stepHeight), d1))) {
                     if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                     } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                     } else {
                        d0 += 0.05D;
                     }

                     if (d1 < 0.05D && d1 >= -0.05D) {
                        d1 = 0.0D;
                     } else if (d1 > 0.0D) {
                        d1 -= 0.05D;
                     } else {
                        d1 += 0.05D;
                     }
                  }

                  p_225514_1_ = new Vec3d(d0, p_225514_1_.y, d1);
                  return p_225514_1_;
               }
            }
         }
      } else {
         return p_225514_1_;
      }
   }

   public void attackTargetEntityWithCurrentItem(Entity p_71059_1_) {
      if (ForgeHooks.onPlayerAttackTarget(this, p_71059_1_)) {
         if (p_71059_1_.canBeAttackedWithItem() && !p_71059_1_.hitByEntity(this)) {
            float f = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
            float f1;
            if (p_71059_1_ instanceof LivingEntity) {
               f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity)p_71059_1_).getCreatureAttribute());
            } else {
               f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), CreatureAttribute.UNDEFINED);
            }

            float f2 = this.getCooledAttackStrength(0.5F);
            f *= 0.2F + f2 * f2 * 0.8F;
            f1 *= f2;
            this.resetCooldown();
            if (f > 0.0F || f1 > 0.0F) {
               boolean flag = f2 > 0.9F;
               boolean flag1 = false;
               int i = 0;
               int i = i + EnchantmentHelper.getKnockbackModifier(this);
               if (this.isSprinting() && flag) {
                  this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                  ++i;
                  flag1 = true;
               }

               boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Effects.BLINDNESS) && !this.isPassenger() && p_71059_1_ instanceof LivingEntity;
               flag2 = flag2 && !this.isSprinting();
               CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(this, p_71059_1_, flag2, flag2 ? 1.5F : 1.0F);
               flag2 = hitResult != null;
               if (flag2) {
                  f *= hitResult.getDamageModifier();
               }

               f += f1;
               boolean flag3 = false;
               double d0 = (double)(this.distanceWalkedModified - this.prevDistanceWalkedModified);
               if (flag && !flag2 && !flag1 && this.onGround && d0 < (double)this.getAIMoveSpeed()) {
                  ItemStack itemstack = this.getHeldItem(Hand.MAIN_HAND);
                  if (itemstack.getItem() instanceof SwordItem) {
                     flag3 = true;
                  }
               }

               float f4 = 0.0F;
               boolean flag4 = false;
               int j = EnchantmentHelper.getFireAspectModifier(this);
               if (p_71059_1_ instanceof LivingEntity) {
                  f4 = ((LivingEntity)p_71059_1_).getHealth();
                  if (j > 0 && !p_71059_1_.isBurning()) {
                     flag4 = true;
                     p_71059_1_.setFire(1);
                  }
               }

               Vec3d vec3d = p_71059_1_.getMotion();
               boolean flag5 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(this), f);
               if (flag5) {
                  if (i > 0) {
                     if (p_71059_1_ instanceof LivingEntity) {
                        ((LivingEntity)p_71059_1_).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                     } else {
                        p_71059_1_.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * (float)i * 0.5F));
                     }

                     this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
                     this.setSprinting(false);
                  }

                  if (flag3) {
                     float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;
                     Iterator var19 = this.world.getEntitiesWithinAABB(LivingEntity.class, p_71059_1_.getBoundingBox().grow(1.0D, 0.25D, 1.0D)).iterator();

                     label174:
                     while(true) {
                        LivingEntity livingentity;
                        do {
                           do {
                              do {
                                 do {
                                    if (!var19.hasNext()) {
                                       this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                       this.spawnSweepParticles();
                                       break label174;
                                    }

                                    livingentity = (LivingEntity)var19.next();
                                 } while(livingentity == this);
                              } while(livingentity == p_71059_1_);
                           } while(this.isOnSameTeam(livingentity));
                        } while(livingentity instanceof ArmorStandEntity && ((ArmorStandEntity)livingentity).hasMarker());

                        if (this.getDistanceSq(livingentity) < 9.0D) {
                           livingentity.knockBack(this, 0.4F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                           livingentity.attackEntityFrom(DamageSource.causePlayerDamage(this), f3);
                        }
                     }
                  }

                  if (p_71059_1_ instanceof ServerPlayerEntity && p_71059_1_.velocityChanged) {
                     ((ServerPlayerEntity)p_71059_1_).connection.sendPacket(new SEntityVelocityPacket(p_71059_1_));
                     p_71059_1_.velocityChanged = false;
                     p_71059_1_.setMotion(vec3d);
                  }

                  if (flag2) {
                     this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                     this.onCriticalHit(p_71059_1_);
                  }

                  if (!flag2 && !flag3) {
                     if (flag) {
                        this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                     } else {
                        this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                     }
                  }

                  if (f1 > 0.0F) {
                     this.onEnchantmentCritical(p_71059_1_);
                  }

                  this.setLastAttackedEntity(p_71059_1_);
                  if (p_71059_1_ instanceof LivingEntity) {
                     EnchantmentHelper.applyThornEnchantments((LivingEntity)p_71059_1_, this);
                  }

                  EnchantmentHelper.applyArthropodEnchantments(this, p_71059_1_);
                  ItemStack itemstack1 = this.getHeldItemMainhand();
                  Entity entity = p_71059_1_;
                  if (p_71059_1_ instanceof EnderDragonPartEntity) {
                     entity = ((EnderDragonPartEntity)p_71059_1_).dragon;
                  }

                  if (!this.world.isRemote && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                     ItemStack copy = itemstack1.copy();
                     itemstack1.hitEntity((LivingEntity)entity, this);
                     if (itemstack1.isEmpty()) {
                        ForgeEventFactory.onPlayerDestroyItem(this, copy, Hand.MAIN_HAND);
                        this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                     }
                  }

                  if (p_71059_1_ instanceof LivingEntity) {
                     float f5 = f4 - ((LivingEntity)p_71059_1_).getHealth();
                     this.addStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                     if (j > 0) {
                        p_71059_1_.setFire(j * 4);
                     }

                     if (this.world instanceof ServerWorld && f5 > 2.0F) {
                        int k = (int)((double)f5 * 0.5D);
                        ((ServerWorld)this.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, p_71059_1_.func_226277_ct_(), p_71059_1_.func_226283_e_(0.5D), p_71059_1_.func_226281_cx_(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                     }
                  }

                  this.addExhaustion(0.1F);
               } else {
                  this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                  if (flag4) {
                     p_71059_1_.extinguish();
                  }
               }
            }
         }

      }
   }

   protected void spinAttack(LivingEntity p_204804_1_) {
      this.attackTargetEntityWithCurrentItem(p_204804_1_);
   }

   public void disableShield(boolean p_190777_1_) {
      float f = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
      if (p_190777_1_) {
         f += 0.75F;
      }

      if (this.rand.nextFloat() < f) {
         this.getCooldownTracker().setCooldown(this.getActiveItemStack().getItem(), 100);
         this.resetActiveHand();
         this.world.setEntityState(this, (byte)30);
      }

   }

   public void onCriticalHit(Entity p_71009_1_) {
   }

   public void onEnchantmentCritical(Entity p_71047_1_) {
   }

   public void spawnSweepParticles() {
      double d0 = (double)(-MathHelper.sin(this.rotationYaw * 0.017453292F));
      double d1 = (double)MathHelper.cos(this.rotationYaw * 0.017453292F);
      if (this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.SWEEP_ATTACK, this.func_226277_ct_() + d0, this.func_226283_e_(0.5D), this.func_226281_cx_() + d1, 0, d0, 0.0D, d1, 0.0D);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void respawnPlayer() {
   }

   public void remove(boolean p_remove_1_) {
      super.remove(p_remove_1_);
      this.container.onContainerClosed(this);
      if (this.openContainer != null) {
         this.openContainer.onContainerClosed(this);
      }

   }

   public boolean isUser() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public Either<PlayerEntity.SleepResult, Unit> trySleep(BlockPos p_213819_1_) {
      Optional<BlockPos> optAt = Optional.of(p_213819_1_);
      PlayerEntity.SleepResult ret = ForgeEventFactory.onPlayerSleepInBed(this, optAt);
      if (ret != null) {
         return Either.left(ret);
      } else {
         Direction direction = (Direction)this.world.getBlockState(p_213819_1_).get(HorizontalBlock.HORIZONTAL_FACING);
         if (!this.world.isRemote) {
            if (this.isSleeping() || !this.isAlive()) {
               return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
            }

            if (!this.world.dimension.isSurfaceWorld()) {
               return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
            }

            if (!ForgeEventFactory.fireSleepingTimeCheck(this, optAt)) {
               this.func_226560_a_(p_213819_1_, false, true);
               return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
            }

            if (!this.bedInRange(p_213819_1_, direction)) {
               return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
            }

            if (this.func_213828_b(p_213819_1_, direction)) {
               return Either.left(PlayerEntity.SleepResult.OBSTRUCTED);
            }

            if (!this.isCreative()) {
               double d0 = 8.0D;
               double d1 = 5.0D;
               Vec3d vec3d = new Vec3d((double)p_213819_1_.getX() + 0.5D, (double)p_213819_1_.getY(), (double)p_213819_1_.getZ() + 0.5D);
               List<MonsterEntity> list = this.world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB(vec3d.getX() - 8.0D, vec3d.getY() - 5.0D, vec3d.getZ() - 8.0D, vec3d.getX() + 8.0D, vec3d.getY() + 5.0D, vec3d.getZ() + 8.0D), (p_lambda$trySleep$5_1_) -> {
                  return p_lambda$trySleep$5_1_.isPreventingPlayerRest(this);
               });
               if (!list.isEmpty()) {
                  return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
               }
            }
         }

         this.startSleeping(p_213819_1_);
         this.sleepTimer = 0;
         if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).updateAllPlayersSleepingFlag();
         }

         return Either.right(Unit.INSTANCE);
      }
   }

   public void startSleeping(BlockPos p_213342_1_) {
      this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.func_226560_a_(p_213342_1_, false, true);
      super.startSleeping(p_213342_1_);
   }

   private boolean bedInRange(BlockPos p_190774_1_, Direction p_190774_2_) {
      if (p_190774_2_ == null) {
         return false;
      } else {
         return this.func_230126_g_(p_190774_1_) || this.func_230126_g_(p_190774_1_.offset(p_190774_2_.getOpposite()));
      }
   }

   private boolean func_230126_g_(BlockPos p_230126_1_) {
      Vec3d vec3d = new Vec3d((double)p_230126_1_.getX() + 0.5D, (double)p_230126_1_.getY(), (double)p_230126_1_.getZ() + 0.5D);
      return Math.abs(this.func_226277_ct_() - vec3d.getX()) <= 3.0D && Math.abs(this.func_226278_cu_() - vec3d.getY()) <= 2.0D && Math.abs(this.func_226281_cx_() - vec3d.getZ()) <= 3.0D;
   }

   private boolean func_213828_b(BlockPos p_213828_1_, Direction p_213828_2_) {
      BlockPos blockpos = p_213828_1_.up();
      return !this.isNormalCube(blockpos) || !this.isNormalCube(blockpos.offset(p_213828_2_.getOpposite()));
   }

   public void func_225652_a_(boolean p_225652_1_, boolean p_225652_2_) {
      ForgeEventFactory.onPlayerWakeup(this, p_225652_1_, p_225652_2_);
      super.wakeUp();
      if (this.world instanceof ServerWorld && p_225652_2_) {
         ((ServerWorld)this.world).updateAllPlayersSleepingFlag();
      }

      this.sleepTimer = p_225652_1_ ? 0 : 100;
   }

   public void wakeUp() {
      this.func_225652_a_(true, true);
   }

   public static Optional<Vec3d> func_213822_a(IWorldReader p_213822_0_, BlockPos p_213822_1_, boolean p_213822_2_) {
      BlockState blockState = p_213822_0_.getBlockState(p_213822_1_);
      if (!blockState.isBed(p_213822_0_, p_213822_1_, (LivingEntity)null)) {
         if (!p_213822_2_) {
            return Optional.empty();
         } else {
            boolean flag = blockState.getBlock().canSpawnInBlock();
            boolean flag1 = p_213822_0_.getBlockState(p_213822_1_.up()).getBlock().canSpawnInBlock();
            return flag && flag1 ? Optional.of(new Vec3d((double)p_213822_1_.getX() + 0.5D, (double)p_213822_1_.getY() + 0.1D, (double)p_213822_1_.getZ() + 0.5D)) : Optional.empty();
         }
      } else {
         return blockState.getBedSpawnPosition(EntityType.PLAYER, p_213822_0_, p_213822_1_, (LivingEntity)null);
      }
   }

   public boolean isPlayerFullyAsleep() {
      return this.isSleeping() && this.sleepTimer >= 100;
   }

   public int getSleepTimer() {
      return this.sleepTimer;
   }

   public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
   }

   /** @deprecated */
   @Deprecated
   public BlockPos getBedLocation() {
      return this.getBedLocation(this.dimension);
   }

   public BlockPos getBedLocation(DimensionType p_getBedLocation_1_) {
      return p_getBedLocation_1_ == DimensionType.OVERWORLD ? this.spawnPos : (BlockPos)this.spawnPosMap.get(p_getBedLocation_1_.getRegistryName());
   }

   /** @deprecated */
   @Deprecated
   public boolean isSpawnForced() {
      return this.isSpawnForced(this.dimension);
   }

   public boolean isSpawnForced(DimensionType p_isSpawnForced_1_) {
      return p_isSpawnForced_1_ == DimensionType.OVERWORLD ? this.spawnForced : (Boolean)this.spawnForcedMap.getOrDefault(p_isSpawnForced_1_.getRegistryName(), false);
   }

   /** @deprecated */
   @Deprecated
   public void func_226560_a_(BlockPos p_226560_1_, boolean p_226560_2_, boolean p_226560_3_) {
      this.setSpawnPoint(p_226560_1_, p_226560_2_, p_226560_3_, this.dimension);
   }

   public void setSpawnPoint(@Nullable BlockPos p_setSpawnPoint_1_, boolean p_setSpawnPoint_2_, boolean p_setSpawnPoint_3_, DimensionType p_setSpawnPoint_4_) {
      if (!ForgeEventFactory.onPlayerSpawnSet(this, p_setSpawnPoint_1_, p_setSpawnPoint_2_)) {
         if (p_setSpawnPoint_4_ != DimensionType.OVERWORLD) {
            if (p_setSpawnPoint_1_ != null) {
               BlockPos old = (BlockPos)this.spawnPosMap.put(p_setSpawnPoint_4_.getRegistryName(), p_setSpawnPoint_1_);
               this.spawnForcedMap.put(p_setSpawnPoint_4_.getRegistryName(), p_setSpawnPoint_2_);
               if (p_setSpawnPoint_3_ && !p_setSpawnPoint_1_.equals(old)) {
                  this.sendMessage(new TranslationTextComponent("block.minecraft.bed.set_spawn", new Object[0]));
               }
            } else {
               this.spawnPosMap.remove(p_setSpawnPoint_4_.getRegistryName());
               this.spawnForcedMap.remove(p_setSpawnPoint_4_.getRegistryName());
            }

         } else {
            if (p_setSpawnPoint_1_ != null) {
               if (p_setSpawnPoint_3_ && !p_setSpawnPoint_1_.equals(this.spawnPos)) {
                  this.sendMessage(new TranslationTextComponent("block.minecraft.bed.set_spawn", new Object[0]));
               }

               this.spawnPos = p_setSpawnPoint_1_;
               this.spawnForced = p_setSpawnPoint_2_;
            } else {
               this.spawnPos = null;
               this.spawnForced = false;
            }

         }
      }
   }

   public void addStat(ResourceLocation p_195066_1_) {
      this.addStat(Stats.CUSTOM.get(p_195066_1_));
   }

   public void addStat(ResourceLocation p_195067_1_, int p_195067_2_) {
      this.addStat(Stats.CUSTOM.get(p_195067_1_), p_195067_2_);
   }

   public void addStat(Stat<?> p_71029_1_) {
      this.addStat((Stat)p_71029_1_, 1);
   }

   public void addStat(Stat<?> p_71064_1_, int p_71064_2_) {
   }

   public void takeStat(Stat<?> p_175145_1_) {
   }

   public int unlockRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return 0;
   }

   public void unlockRecipes(ResourceLocation[] p_193102_1_) {
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return 0;
   }

   public void jump() {
      super.jump();
      this.addStat(Stats.JUMP);
      if (this.isSprinting()) {
         this.addExhaustion(0.2F);
      } else {
         this.addExhaustion(0.05F);
      }

   }

   public void travel(Vec3d p_213352_1_) {
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226278_cu_();
      double d2 = this.func_226281_cx_();
      double d5;
      if (this.isSwimming() && !this.isPassenger()) {
         d5 = this.getLookVec().y;
         double d4 = d5 < -0.2D ? 0.085D : 0.06D;
         if (d5 <= 0.0D || this.isJumping || !this.world.getBlockState(new BlockPos(this.func_226277_ct_(), this.func_226278_cu_() + 1.0D - 0.1D, this.func_226281_cx_())).getFluidState().isEmpty()) {
            Vec3d vec3d1 = this.getMotion();
            this.setMotion(vec3d1.add(0.0D, (d5 - vec3d1.y) * d4, 0.0D));
         }
      }

      if (this.abilities.isFlying && !this.isPassenger()) {
         d5 = this.getMotion().y;
         float f = this.jumpMovementFactor;
         this.jumpMovementFactor = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
         super.travel(p_213352_1_);
         Vec3d vec3d = this.getMotion();
         this.setMotion(vec3d.x, d5 * 0.6D, vec3d.z);
         this.jumpMovementFactor = f;
         this.fallDistance = 0.0F;
         this.setFlag(7, false);
      } else {
         super.travel(p_213352_1_);
      }

      this.addMovementStat(this.func_226277_ct_() - d0, this.func_226278_cu_() - d1, this.func_226281_cx_() - d2);
   }

   public void updateSwimming() {
      if (this.abilities.isFlying) {
         this.setSwimming(false);
      } else {
         super.updateSwimming();
      }

   }

   protected boolean isNormalCube(BlockPos p_207401_1_) {
      return !this.world.getBlockState(p_207401_1_).func_229980_m_(this.world, p_207401_1_);
   }

   public float getAIMoveSpeed() {
      return (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
   }

   public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
      if (!this.isPassenger()) {
         int l;
         if (this.isSwimming()) {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 0) {
               this.addStat(Stats.SWIM_ONE_CM, l);
               this.addExhaustion(0.01F * (float)l * 0.01F);
            }
         } else if (this.areEyesInFluid(FluidTags.WATER, true)) {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 0) {
               this.addStat(Stats.WALK_UNDER_WATER_ONE_CM, l);
               this.addExhaustion(0.01F * (float)l * 0.01F);
            }
         } else if (this.isInWater()) {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 0) {
               this.addStat(Stats.WALK_ON_WATER_ONE_CM, l);
               this.addExhaustion(0.01F * (float)l * 0.01F);
            }
         } else if (this.isOnLadder()) {
            if (p_71000_3_ > 0.0D) {
               this.addStat(Stats.CLIMB_ONE_CM, (int)Math.round(p_71000_3_ * 100.0D));
            }
         } else if (this.onGround) {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 0) {
               if (this.isSprinting()) {
                  this.addStat(Stats.SPRINT_ONE_CM, l);
                  this.addExhaustion(0.1F * (float)l * 0.01F);
               } else if (this.isCrouching()) {
                  this.addStat(Stats.CROUCH_ONE_CM, l);
                  this.addExhaustion(0.0F * (float)l * 0.01F);
               } else {
                  this.addStat(Stats.WALK_ONE_CM, l);
                  this.addExhaustion(0.0F * (float)l * 0.01F);
               }
            }
         } else if (this.isElytraFlying()) {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
            this.addStat(Stats.AVIATE_ONE_CM, l);
         } else {
            l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
            if (l > 25) {
               this.addStat(Stats.FLY_ONE_CM, l);
            }
         }
      }

   }

   private void addMountedMovementStat(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
      if (this.isPassenger()) {
         int i = Math.round(MathHelper.sqrt(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0F);
         if (i > 0) {
            if (this.getRidingEntity() instanceof AbstractMinecartEntity) {
               this.addStat(Stats.MINECART_ONE_CM, i);
            } else if (this.getRidingEntity() instanceof BoatEntity) {
               this.addStat(Stats.BOAT_ONE_CM, i);
            } else if (this.getRidingEntity() instanceof PigEntity) {
               this.addStat(Stats.PIG_ONE_CM, i);
            } else if (this.getRidingEntity() instanceof AbstractHorseEntity) {
               this.addStat(Stats.HORSE_ONE_CM, i);
            }
         }
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      if (this.abilities.allowFlying) {
         ForgeEventFactory.onPlayerFall(this, p_225503_1_, p_225503_2_);
         return false;
      } else {
         if (p_225503_1_ >= 2.0F) {
            this.addStat(Stats.FALL_ONE_CM, (int)Math.round((double)p_225503_1_ * 100.0D));
         }

         return super.func_225503_b_(p_225503_1_, p_225503_2_);
      }
   }

   public boolean func_226566_ei_() {
      if (!this.onGround && !this.isElytraFlying() && !this.isInWater()) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemstack)) {
            this.func_226567_ej_();
            return true;
         }
      }

      return false;
   }

   public void func_226567_ej_() {
      this.setFlag(7, true);
   }

   public void func_226568_ek_() {
      this.setFlag(7, true);
      this.setFlag(7, false);
   }

   protected void doWaterSplashEffect() {
      if (!this.isSpectator()) {
         super.doWaterSplashEffect();
      }

   }

   protected SoundEvent getFallSound(int p_184588_1_) {
      return p_184588_1_ > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
   }

   public void onKillEntity(LivingEntity p_70074_1_) {
      this.addStat(Stats.ENTITY_KILLED.get(p_70074_1_.getType()));
   }

   public void setMotionMultiplier(BlockState p_213295_1_, Vec3d p_213295_2_) {
      if (!this.abilities.isFlying) {
         super.setMotionMultiplier(p_213295_1_, p_213295_2_);
      }

   }

   public void giveExperiencePoints(int p_195068_1_) {
      PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange(this, p_195068_1_);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         p_195068_1_ = event.getAmount();
         this.addScore(p_195068_1_);
         this.experience += (float)p_195068_1_ / (float)this.xpBarCap();
         this.experienceTotal = MathHelper.clamp(this.experienceTotal + p_195068_1_, 0, Integer.MAX_VALUE);

         while(this.experience < 0.0F) {
            float f = this.experience * (float)this.xpBarCap();
            if (this.experienceLevel > 0) {
               this.addExperienceLevel(-1);
               this.experience = 1.0F + f / (float)this.xpBarCap();
            } else {
               this.addExperienceLevel(-1);
               this.experience = 0.0F;
            }
         }

         while(this.experience >= 1.0F) {
            this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
            this.addExperienceLevel(1);
            this.experience /= (float)this.xpBarCap();
         }

      }
   }

   public int getXPSeed() {
      return this.xpSeed;
   }

   public void onEnchant(ItemStack p_192024_1_, int p_192024_2_) {
      this.experienceLevel -= p_192024_2_;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      this.xpSeed = this.rand.nextInt();
   }

   public void addExperienceLevel(int p_82242_1_) {
      PlayerXpEvent.LevelChange event = new PlayerXpEvent.LevelChange(this, p_82242_1_);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         p_82242_1_ = event.getLevels();
         this.experienceLevel += p_82242_1_;
         if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0F;
            this.experienceTotal = 0;
         }

         if (p_82242_1_ > 0 && this.experienceLevel % 5 == 0 && (float)this.lastXPSound < (float)this.ticksExisted - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
            this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
            this.lastXPSound = this.ticksExisted;
         }

      }
   }

   public int xpBarCap() {
      if (this.experienceLevel >= 30) {
         return 112 + (this.experienceLevel - 30) * 9;
      } else {
         return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
      }
   }

   public void addExhaustion(float p_71020_1_) {
      if (!this.abilities.disableDamage && !this.world.isRemote) {
         this.foodStats.addExhaustion(p_71020_1_);
      }

   }

   public FoodStats getFoodStats() {
      return this.foodStats;
   }

   public boolean canEat(boolean p_71043_1_) {
      return this.abilities.disableDamage || p_71043_1_ || this.foodStats.needFood();
   }

   public boolean shouldHeal() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public boolean isAllowEdit() {
      return this.abilities.allowEdit;
   }

   public boolean canPlayerEdit(BlockPos p_175151_1_, Direction p_175151_2_, ItemStack p_175151_3_) {
      if (this.abilities.allowEdit) {
         return true;
      } else {
         BlockPos blockpos = p_175151_1_.offset(p_175151_2_.getOpposite());
         CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.world, blockpos, false);
         return p_175151_3_.canPlaceOn(this.world.getTags(), cachedblockinfo);
      }
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
         int i = this.experienceLevel * 7;
         return i > 100 ? 100 : i;
      } else {
         return 0;
      }
   }

   protected boolean isPlayer() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return true;
   }

   protected boolean func_225502_at_() {
      return !this.abilities.isFlying && (!this.onGround || !this.func_226273_bm_());
   }

   public void sendPlayerAbilities() {
   }

   public void setGameType(GameType p_71033_1_) {
   }

   public ITextComponent getName() {
      return new StringTextComponent(this.gameProfile.getName());
   }

   public EnderChestInventory getInventoryEnderChest() {
      return this.enterChestInventory;
   }

   public ItemStack getItemStackFromSlot(EquipmentSlotType p_184582_1_) {
      if (p_184582_1_ == EquipmentSlotType.MAINHAND) {
         return this.inventory.getCurrentItem();
      } else if (p_184582_1_ == EquipmentSlotType.OFFHAND) {
         return (ItemStack)this.inventory.offHandInventory.get(0);
      } else {
         return p_184582_1_.getSlotType() == EquipmentSlotType.Group.ARMOR ? (ItemStack)this.inventory.armorInventory.get(p_184582_1_.getIndex()) : ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      if (p_184201_1_ == EquipmentSlotType.MAINHAND) {
         this.playEquipSound(p_184201_2_);
         this.inventory.mainInventory.set(this.inventory.currentItem, p_184201_2_);
      } else if (p_184201_1_ == EquipmentSlotType.OFFHAND) {
         this.playEquipSound(p_184201_2_);
         this.inventory.offHandInventory.set(0, p_184201_2_);
      } else if (p_184201_1_.getSlotType() == EquipmentSlotType.Group.ARMOR) {
         this.playEquipSound(p_184201_2_);
         this.inventory.armorInventory.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   public boolean addItemStackToInventory(ItemStack p_191521_1_) {
      this.playEquipSound(p_191521_1_);
      return this.inventory.addItemStackToInventory(p_191521_1_);
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return Lists.newArrayList(new ItemStack[]{this.getHeldItemMainhand(), this.getHeldItemOffhand()});
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.inventory.armorInventory;
   }

   public boolean addShoulderEntity(CompoundNBT p_192027_1_) {
      if (!this.isPassenger() && this.onGround && !this.isInWater()) {
         if (this.getLeftShoulderEntity().isEmpty()) {
            this.setLeftShoulderEntity(p_192027_1_);
            this.field_223730_e = this.world.getGameTime();
            return true;
         } else if (this.getRightShoulderEntity().isEmpty()) {
            this.setRightShoulderEntity(p_192027_1_);
            this.field_223730_e = this.world.getGameTime();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void spawnShoulderEntities() {
      if (this.field_223730_e + 20L < this.world.getGameTime()) {
         this.spawnShoulderEntity(this.getLeftShoulderEntity());
         this.setLeftShoulderEntity(new CompoundNBT());
         this.spawnShoulderEntity(this.getRightShoulderEntity());
         this.setRightShoulderEntity(new CompoundNBT());
      }

   }

   private void spawnShoulderEntity(CompoundNBT p_192026_1_) {
      if (!this.world.isRemote && !p_192026_1_.isEmpty()) {
         EntityType.loadEntityUnchecked(p_192026_1_, this.world).ifPresent((p_lambda$spawnShoulderEntity$6_1_) -> {
            if (p_lambda$spawnShoulderEntity$6_1_ instanceof TameableEntity) {
               ((TameableEntity)p_lambda$spawnShoulderEntity$6_1_).setOwnerId(this.entityUniqueID);
            }

            p_lambda$spawnShoulderEntity$6_1_.setPosition(this.func_226277_ct_(), this.func_226278_cu_() + 0.699999988079071D, this.func_226281_cx_());
            ((ServerWorld)this.world).summonEntity(p_lambda$spawnShoulderEntity$6_1_);
         });
      }

   }

   public abstract boolean isSpectator();

   public boolean isSwimming() {
      return !this.abilities.isFlying && !this.isSpectator() && super.isSwimming();
   }

   public abstract boolean isCreative();

   public boolean isPushedByWater() {
      return !this.abilities.isFlying;
   }

   public Scoreboard getWorldScoreboard() {
      return this.world.getScoreboard();
   }

   public ITextComponent getDisplayName() {
      ITextComponent itextcomponent = new StringTextComponent("");
      this.prefixes.forEach((p_lambda$getDisplayName$7_1_) -> {
         itextcomponent.appendSibling(p_lambda$getDisplayName$7_1_);
      });
      itextcomponent.appendSibling(ScorePlayerTeam.formatMemberName(this.getTeam(), this.getName()));
      this.suffixes.forEach((p_lambda$getDisplayName$8_1_) -> {
         itextcomponent.appendSibling(p_lambda$getDisplayName$8_1_);
      });
      return this.addTellEvent(itextcomponent);
   }

   public ITextComponent getDisplayNameAndUUID() {
      return (new StringTextComponent("")).appendSibling(this.getName()).appendText(" (").appendText(this.gameProfile.getId().toString()).appendText(")");
   }

   private ITextComponent addTellEvent(ITextComponent p_208016_1_) {
      String s = this.getGameProfile().getName();
      return p_208016_1_.applyTextStyle((p_lambda$addTellEvent$9_2_) -> {
         p_lambda$addTellEvent$9_2_.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " ")).setHoverEvent(this.getHoverEvent()).setInsertion(s);
      });
   }

   public String getScoreboardName() {
      return this.getGameProfile().getName();
   }

   public float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      switch(p_213348_1_) {
      case SWIMMING:
      case FALL_FLYING:
      case SPIN_ATTACK:
         return 0.4F;
      case CROUCHING:
         return 1.27F;
      default:
         return 1.62F;
      }
   }

   public void setAbsorptionAmount(float p_110149_1_) {
      if (p_110149_1_ < 0.0F) {
         p_110149_1_ = 0.0F;
      }

      this.getDataManager().set(ABSORPTION, p_110149_1_);
   }

   public float getAbsorptionAmount() {
      return (Float)this.getDataManager().get(ABSORPTION);
   }

   public static UUID getUUID(GameProfile p_146094_0_) {
      UUID uuid = p_146094_0_.getId();
      if (uuid == null) {
         uuid = getOfflineUUID(p_146094_0_.getName());
      }

      return uuid;
   }

   public static UUID getOfflineUUID(String p_175147_0_) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_175147_0_).getBytes(StandardCharsets.UTF_8));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWearing(PlayerModelPart p_175148_1_) {
      return ((Byte)this.getDataManager().get(PLAYER_MODEL_FLAG) & p_175148_1_.getPartMask()) == p_175148_1_.getPartMask();
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ >= 0 && p_174820_1_ < this.inventory.mainInventory.size()) {
         this.inventory.setInventorySlotContents(p_174820_1_, p_174820_2_);
         return true;
      } else {
         EquipmentSlotType equipmentslottype;
         if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
            equipmentslottype = EquipmentSlotType.HEAD;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
            equipmentslottype = EquipmentSlotType.CHEST;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
            equipmentslottype = EquipmentSlotType.LEGS;
         } else if (p_174820_1_ == 100 + EquipmentSlotType.FEET.getIndex()) {
            equipmentslottype = EquipmentSlotType.FEET;
         } else {
            equipmentslottype = null;
         }

         if (p_174820_1_ == 98) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, p_174820_2_);
            return true;
         } else if (p_174820_1_ == 99) {
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, p_174820_2_);
            return true;
         } else if (equipmentslottype == null) {
            int i = p_174820_1_ - 200;
            if (i >= 0 && i < this.enterChestInventory.getSizeInventory()) {
               this.enterChestInventory.setInventorySlotContents(i, p_174820_2_);
               return true;
            } else {
               return false;
            }
         } else {
            if (!p_174820_2_.isEmpty()) {
               if (!(p_174820_2_.getItem() instanceof ArmorItem) && !(p_174820_2_.getItem() instanceof ElytraItem)) {
                  if (equipmentslottype != EquipmentSlotType.HEAD) {
                     return false;
                  }
               } else if (MobEntity.getSlotForItemStack(p_174820_2_) != equipmentslottype) {
                  return false;
               }
            }

            this.inventory.setInventorySlotContents(equipmentslottype.getIndex() + this.inventory.mainInventory.size(), p_174820_2_);
            return true;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasReducedDebug() {
      return this.hasReducedDebug;
   }

   @OnlyIn(Dist.CLIENT)
   public void setReducedDebug(boolean p_175150_1_) {
      this.hasReducedDebug = p_175150_1_;
   }

   public HandSide getPrimaryHand() {
      return (Byte)this.dataManager.get(MAIN_HAND) == 0 ? HandSide.LEFT : HandSide.RIGHT;
   }

   public void setPrimaryHand(HandSide p_184819_1_) {
      this.dataManager.set(MAIN_HAND, (byte)(p_184819_1_ == HandSide.LEFT ? 0 : 1));
   }

   public CompoundNBT getLeftShoulderEntity() {
      return (CompoundNBT)this.dataManager.get(LEFT_SHOULDER_ENTITY);
   }

   protected void setLeftShoulderEntity(CompoundNBT p_192029_1_) {
      this.dataManager.set(LEFT_SHOULDER_ENTITY, p_192029_1_);
   }

   public CompoundNBT getRightShoulderEntity() {
      return (CompoundNBT)this.dataManager.get(RIGHT_SHOULDER_ENTITY);
   }

   protected void setRightShoulderEntity(CompoundNBT p_192031_1_) {
      this.dataManager.set(RIGHT_SHOULDER_ENTITY, p_192031_1_);
   }

   public float getCooldownPeriod() {
      return (float)(1.0D / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0D);
   }

   public float getCooledAttackStrength(float p_184825_1_) {
      return MathHelper.clamp(((float)this.ticksSinceLastSwing + p_184825_1_) / this.getCooldownPeriod(), 0.0F, 1.0F);
   }

   public void resetCooldown() {
      this.ticksSinceLastSwing = 0;
   }

   public CooldownTracker getCooldownTracker() {
      return this.cooldownTracker;
   }

   protected float func_225515_ai_() {
      return !this.abilities.isFlying && !this.isElytraFlying() ? super.func_225515_ai_() : 1.0F;
   }

   public float getLuck() {
      return (float)this.getAttribute(SharedMonsterAttributes.LUCK).getValue();
   }

   public boolean canUseCommandBlock() {
      return this.abilities.isCreativeMode && this.getPermissionLevel() >= 2;
   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_213365_1_);
      return this.getItemStackFromSlot(equipmentslottype).isEmpty();
   }

   public EntitySize getSize(Pose p_213305_1_) {
      return (EntitySize)SIZE_BY_POSE.getOrDefault(p_213305_1_, STANDING_SIZE);
   }

   public ItemStack findAmmo(ItemStack p_213356_1_) {
      if (!(p_213356_1_.getItem() instanceof ShootableItem)) {
         return ItemStack.EMPTY;
      } else {
         Predicate<ItemStack> predicate = ((ShootableItem)p_213356_1_.getItem()).getAmmoPredicate();
         ItemStack itemstack = ShootableItem.getHeldAmmo(this, predicate);
         if (!itemstack.isEmpty()) {
            return itemstack;
         } else {
            predicate = ((ShootableItem)p_213356_1_.getItem()).getInventoryAmmoPredicate();

            for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
               ItemStack itemstack1 = this.inventory.getStackInSlot(i);
               if (predicate.test(itemstack1)) {
                  return itemstack1;
               }
            }

            return this.abilities.isCreativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
         }
      }
   }

   public ItemStack onFoodEaten(World p_213357_1_, ItemStack p_213357_2_) {
      this.getFoodStats().consume(p_213357_2_.getItem(), p_213357_2_);
      this.addStat(Stats.ITEM_USED.get(p_213357_2_.getItem()));
      p_213357_1_.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, p_213357_1_.rand.nextFloat() * 0.1F + 0.9F);
      if (this instanceof ServerPlayerEntity) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)this, p_213357_2_);
      }

      return super.onFoodEaten(p_213357_1_, p_213357_2_);
   }

   public DimensionType getSpawnDimension() {
      return this.spawnDimension;
   }

   public void setSpawnDimenion(DimensionType p_setSpawnDimenion_1_) {
      this.spawnDimension = p_setSpawnDimenion_1_;
   }

   public Collection<ITextComponent> getPrefixes() {
      return this.prefixes;
   }

   public Collection<ITextComponent> getSuffixes() {
      return this.suffixes;
   }

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      if (this.isAlive() && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (p_getCapability_2_ == null) {
            return this.playerJoinedHandler.cast();
         }

         if (p_getCapability_2_.getAxis().isVertical()) {
            return this.playerMainHandler.cast();
         }

         if (p_getCapability_2_.getAxis().isHorizontal()) {
            return this.playerEquipmentHandler.cast();
         }
      }

      return super.getCapability(p_getCapability_1_, p_getCapability_2_);
   }

   static {
      SIZE_BY_POSE = ImmutableMap.builder().put(Pose.STANDING, STANDING_SIZE).put(Pose.SLEEPING, SLEEPING_SIZE).put(Pose.FALL_FLYING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SWIMMING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntitySize.flexible(0.6F, 0.6F)).put(Pose.CROUCHING, EntitySize.flexible(0.6F, 1.5F)).put(Pose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();
      ABSORPTION = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.FLOAT);
      PLAYER_SCORE = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.VARINT);
      PLAYER_MODEL_FLAG = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.BYTE);
      MAIN_HAND = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.BYTE);
      LEFT_SHOULDER_ENTITY = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.COMPOUND_NBT);
      RIGHT_SHOULDER_ENTITY = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.COMPOUND_NBT);
   }

   public static enum SleepResult {
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW(new TranslationTextComponent("block.minecraft.bed.no_sleep", new Object[0])),
      TOO_FAR_AWAY(new TranslationTextComponent("block.minecraft.bed.too_far_away", new Object[0])),
      OBSTRUCTED(new TranslationTextComponent("block.minecraft.bed.obstructed", new Object[0])),
      OTHER_PROBLEM,
      NOT_SAFE(new TranslationTextComponent("block.minecraft.bed.not_safe", new Object[0]));

      @Nullable
      private final ITextComponent message;

      private SleepResult() {
         this.message = null;
      }

      private SleepResult(ITextComponent p_i50668_3_) {
         this.message = p_i50668_3_;
      }

      @Nullable
      public ITextComponent getMessage() {
         return this.message;
      }
   }
}
