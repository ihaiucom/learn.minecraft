package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class ParrotEntity extends ShoulderRidingEntity implements IFlyingAnimal {
   private static final DataParameter<Integer> VARIANT;
   private static final Predicate<MobEntity> CAN_MIMIC;
   private static final Item DEADLY_ITEM;
   private static final Set<Item> TAME_ITEMS;
   private static final Map<EntityType<?>, SoundEvent> IMITATION_SOUND_EVENTS;
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   private float flapping = 1.0F;
   private boolean partyParrot;
   private BlockPos jukeboxPosition;

   public ParrotEntity(EntityType<? extends ParrotEntity> p_i50251_1_, World p_i50251_2_) {
      super(p_i50251_1_, p_i50251_2_);
      this.moveController = new FlyingMovementController(this, 10, false);
      this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
      this.setPathPriority(PathNodeType.COCOA, -1.0F);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setVariant(this.rand.nextInt(5));
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226259_a_(false);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4000000059604645D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, p_175447_1_);
      flyingpathnavigator.setCanOpenDoors(false);
      flyingpathnavigator.setCanSwim(true);
      flyingpathnavigator.setCanEnterDoors(true);
      return flyingpathnavigator;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.6F;
   }

   public void livingTick() {
      playMimicSound(this.world, this);
      if (this.jukeboxPosition == null || !this.jukeboxPosition.withinDistance(this.getPositionVec(), 3.46D) || this.world.getBlockState(this.jukeboxPosition).getBlock() != Blocks.JUKEBOX) {
         this.partyParrot = false;
         this.jukeboxPosition = null;
      }

      super.livingTick();
      this.calculateFlapping();
   }

   @OnlyIn(Dist.CLIENT)
   public void setPartying(BlockPos p_191987_1_, boolean p_191987_2_) {
      this.jukeboxPosition = p_191987_1_;
      this.partyParrot = p_191987_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isPartying() {
      return this.partyParrot;
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)((double)this.flapSpeed + (double)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
      this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)((double)this.flapping * 0.9D);
      Vec3d vec3d = this.getMotion();
      if (!this.onGround && vec3d.y < 0.0D) {
         this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
      }

      this.flap += this.flapping * 2.0F;
   }

   private static boolean playMimicSound(World p_192006_0_, Entity p_192006_1_) {
      if (p_192006_1_.isAlive() && !p_192006_1_.isSilent() && p_192006_0_.rand.nextInt(50) == 0) {
         List<MobEntity> list = p_192006_0_.getEntitiesWithinAABB(MobEntity.class, p_192006_1_.getBoundingBox().grow(20.0D), CAN_MIMIC);
         if (!list.isEmpty()) {
            MobEntity mobentity = (MobEntity)list.get(p_192006_0_.rand.nextInt(list.size()));
            if (!mobentity.isSilent()) {
               SoundEvent soundevent = getMimicSound(mobentity.getType());
               p_192006_0_.playSound((PlayerEntity)null, p_192006_1_.func_226277_ct_(), p_192006_1_.func_226278_cu_(), p_192006_1_.func_226281_cx_(), soundevent, p_192006_1_.getSoundCategory(), 0.7F, getPitch(p_192006_0_.rand));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (!this.isTamed() && TAME_ITEMS.contains(itemstack.getItem())) {
         if (!p_184645_1_.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.isSilent()) {
            this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         }

         if (!this.world.isRemote) {
            if (this.rand.nextInt(10) == 0 && !ForgeEventFactory.onAnimalTame(this, p_184645_1_)) {
               this.setTamedBy(p_184645_1_);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.world.setEntityState(this, (byte)6);
            }
         }

         return true;
      } else if (itemstack.getItem() == DEADLY_ITEM) {
         if (!p_184645_1_.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         this.addPotionEffect(new EffectInstance(Effects.POISON, 900));
         if (p_184645_1_.isCreative() || !this.isInvulnerable()) {
            this.attackEntityFrom(DamageSource.causePlayerDamage(p_184645_1_), Float.MAX_VALUE);
         }

         return true;
      } else if (!this.isFlying() && this.isTamed() && this.isOwner(p_184645_1_)) {
         if (!this.world.isRemote) {
            this.sitGoal.setSitting(!this.isSitting());
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   public static boolean func_223317_c(EntityType<ParrotEntity> p_223317_0_, IWorld p_223317_1_, SpawnReason p_223317_2_, BlockPos p_223317_3_, Random p_223317_4_) {
      Block block = p_223317_1_.getBlockState(p_223317_3_.down()).getBlock();
      return (block.isIn(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || block instanceof LogBlock || block == Blocks.AIR) && p_223317_1_.func_226659_b_(p_223317_3_, 0) > 8;
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      return false;
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return null;
   }

   public static void playAmbientSound(World p_192005_0_, Entity p_192005_1_) {
      if (!p_192005_1_.isSilent() && !playMimicSound(p_192005_0_, p_192005_1_) && p_192005_0_.rand.nextInt(200) == 0) {
         p_192005_0_.playSound((PlayerEntity)null, p_192005_1_.func_226277_ct_(), p_192005_1_.func_226278_cu_(), p_192005_1_.func_226281_cx_(), getAmbientSound(p_192005_0_.rand), p_192005_1_.getSoundCategory(), 1.0F, getPitch(p_192005_0_.rand));
      }

   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getAmbientSound(this.rand);
   }

   private static SoundEvent getAmbientSound(Random p_192003_0_) {
      if (p_192003_0_.nextInt(1000) == 0) {
         List<EntityType<?>> list = Lists.newArrayList(IMITATION_SOUND_EVENTS.keySet());
         return getMimicSound((EntityType)list.get(p_192003_0_.nextInt(list.size())));
      } else {
         return SoundEvents.ENTITY_PARROT_AMBIENT;
      }
   }

   private static SoundEvent getMimicSound(EntityType<?> p_200610_0_) {
      return (SoundEvent)IMITATION_SOUND_EVENTS.getOrDefault(p_200610_0_, SoundEvents.ENTITY_PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PARROT_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
   }

   protected float playFlySound(float p_191954_1_) {
      this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
      return p_191954_1_ + this.flapSpeed / 2.0F;
   }

   protected boolean makeFlySound() {
      return true;
   }

   protected float getSoundPitch() {
      return getPitch(this.rand);
   }

   private static float getPitch(Random p_192000_0_) {
      return (p_192000_0_.nextFloat() - p_192000_0_.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   public boolean canBePushed() {
      return true;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
      if (!(p_82167_1_ instanceof PlayerEntity)) {
         super.collideWithEntity(p_82167_1_);
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (this.sitGoal != null) {
            this.sitGoal.setSitting(false);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public int getVariant() {
      return MathHelper.clamp((Integer)this.dataManager.get(VARIANT), 0, 4);
   }

   public void setVariant(int p_191997_1_) {
      this.dataManager.set(VARIANT, p_191997_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VARIANT, 0);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Variant", this.getVariant());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setVariant(p_70037_1_.getInt("Variant"));
   }

   public boolean isFlying() {
      return !this.onGround;
   }

   static {
      VARIANT = EntityDataManager.createKey(ParrotEntity.class, DataSerializers.VARINT);
      CAN_MIMIC = new Predicate<MobEntity>() {
         public boolean test(@Nullable MobEntity p_test_1_) {
            return p_test_1_ != null && ParrotEntity.IMITATION_SOUND_EVENTS.containsKey(p_test_1_.getType());
         }
      };
      DEADLY_ITEM = Items.COOKIE;
      TAME_ITEMS = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
      IMITATION_SOUND_EVENTS = (Map)Util.make(Maps.newHashMap(), (p_lambda$static$0_0_) -> {
         p_lambda$static$0_0_.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
         p_lambda$static$0_0_.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
         p_lambda$static$0_0_.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
         p_lambda$static$0_0_.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
         p_lambda$static$0_0_.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
         p_lambda$static$0_0_.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
         p_lambda$static$0_0_.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
         p_lambda$static$0_0_.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
         p_lambda$static$0_0_.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
         p_lambda$static$0_0_.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
         p_lambda$static$0_0_.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
         p_lambda$static$0_0_.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
         p_lambda$static$0_0_.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
         p_lambda$static$0_0_.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
         p_lambda$static$0_0_.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
         p_lambda$static$0_0_.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
         p_lambda$static$0_0_.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
         p_lambda$static$0_0_.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
         p_lambda$static$0_0_.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
         p_lambda$static$0_0_.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
         p_lambda$static$0_0_.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
         p_lambda$static$0_0_.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
         p_lambda$static$0_0_.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
         p_lambda$static$0_0_.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
         p_lambda$static$0_0_.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
         p_lambda$static$0_0_.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
         p_lambda$static$0_0_.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
         p_lambda$static$0_0_.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
         p_lambda$static$0_0_.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
      });
   }
}
