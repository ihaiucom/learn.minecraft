package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatEntity extends TameableEntity {
   private static final Ingredient BREEDING_ITEMS;
   private static final DataParameter<Integer> CAT_TYPE;
   private static final DataParameter<Boolean> field_213428_bG;
   private static final DataParameter<Boolean> field_213429_bH;
   private static final DataParameter<Integer> COLLAR_COLOR;
   public static final Map<Integer, ResourceLocation> field_213425_bD;
   private CatEntity.AvoidPlayerGoal<PlayerEntity> avoidPlayerGoal;
   private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
   private float field_213433_bL;
   private float field_213434_bM;
   private float field_213435_bN;
   private float field_213436_bO;
   private float field_213437_bP;
   private float field_213438_bQ;

   public CatEntity(EntityType<? extends CatEntity> p_i50284_1_, World p_i50284_2_) {
      super(p_i50284_1_, p_i50284_2_);
   }

   public ResourceLocation getCatTypeName() {
      return (ResourceLocation)field_213425_bD.getOrDefault(this.getCatType(), field_213425_bD.get(0));
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.temptGoal = new CatEntity.TemptGoal(this, 0.6D, BREEDING_ITEMS, true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(1, new CatEntity.MorningGiftGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1D, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8D));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal(this, RabbitEntity.class, false, (Predicate)null));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal(this, TurtleEntity.class, false, TurtleEntity.TARGET_DRY_BABY));
   }

   public int getCatType() {
      return (Integer)this.dataManager.get(CAT_TYPE);
   }

   public void setCatType(int p_213422_1_) {
      if (p_213422_1_ < 0 || p_213422_1_ >= 11) {
         p_213422_1_ = this.rand.nextInt(10);
      }

      this.dataManager.set(CAT_TYPE, p_213422_1_);
   }

   public void func_213419_u(boolean p_213419_1_) {
      this.dataManager.set(field_213428_bG, p_213419_1_);
   }

   public boolean func_213416_eg() {
      return (Boolean)this.dataManager.get(field_213428_bG);
   }

   public void func_213415_v(boolean p_213415_1_) {
      this.dataManager.set(field_213429_bH, p_213415_1_);
   }

   public boolean func_213409_eh() {
      return (Boolean)this.dataManager.get(field_213429_bH);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.dataManager.get(COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_213417_1_) {
      this.dataManager.set(COLLAR_COLOR, p_213417_1_.getId());
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CAT_TYPE, 1);
      this.dataManager.register(field_213428_bG, false);
      this.dataManager.register(field_213429_bH, false);
      this.dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("CatType", this.getCatType());
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setCatType(p_70037_1_.getInt("CatType"));
      if (p_70037_1_.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(p_70037_1_.getInt("CollarColor")));
      }

   }

   public void updateAITasks() {
      if (this.getMoveHelper().isUpdating()) {
         double lvt_1_1_ = this.getMoveHelper().getSpeed();
         if (lvt_1_1_ == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (lvt_1_1_ == 1.33D) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(Pose.STANDING);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTamed()) {
         if (this.isInLove()) {
            return SoundEvents.ENTITY_CAT_PURR;
         } else {
            return this.rand.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
         }
      } else {
         return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
      }
   }

   public int getTalkInterval() {
      return 120;
   }

   public void func_213420_ej() {
      this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CAT_DEATH;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void consumeItemFromStack(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (this.isBreedingItem(p_175505_2_)) {
         this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
      }

      super.consumeItemFromStack(p_175505_1_, p_175505_2_);
   }

   private float func_226510_eF_() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_226510_eF_());
   }

   public void tick() {
      super.tick();
      if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTamed() && this.ticksExisted % 100 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.func_213412_ek();
   }

   private void func_213412_ek() {
      if ((this.func_213416_eg() || this.func_213409_eh()) && this.ticksExisted % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.rand.nextFloat() - this.rand.nextFloat()), 1.0F);
      }

      this.func_213418_el();
      this.func_213411_em();
   }

   private void func_213418_el() {
      this.field_213434_bM = this.field_213433_bL;
      this.field_213436_bO = this.field_213435_bN;
      if (this.func_213416_eg()) {
         this.field_213433_bL = Math.min(1.0F, this.field_213433_bL + 0.15F);
         this.field_213435_bN = Math.min(1.0F, this.field_213435_bN + 0.08F);
      } else {
         this.field_213433_bL = Math.max(0.0F, this.field_213433_bL - 0.22F);
         this.field_213435_bN = Math.max(0.0F, this.field_213435_bN - 0.13F);
      }

   }

   private void func_213411_em() {
      this.field_213438_bQ = this.field_213437_bP;
      if (this.func_213409_eh()) {
         this.field_213437_bP = Math.min(1.0F, this.field_213437_bP + 0.1F);
      } else {
         this.field_213437_bP = Math.max(0.0F, this.field_213437_bP - 0.13F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float func_213408_v(float p_213408_1_) {
      return MathHelper.lerp(p_213408_1_, this.field_213434_bM, this.field_213433_bL);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213421_w(float p_213421_1_) {
      return MathHelper.lerp(p_213421_1_, this.field_213436_bO, this.field_213435_bN);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213424_x(float p_213424_1_) {
      return MathHelper.lerp(p_213424_1_, this.field_213438_bQ, this.field_213437_bP);
   }

   public CatEntity createChild(AgeableEntity p_90011_1_) {
      CatEntity lvt_2_1_ = (CatEntity)EntityType.CAT.create(this.world);
      if (p_90011_1_ instanceof CatEntity) {
         if (this.rand.nextBoolean()) {
            lvt_2_1_.setCatType(this.getCatType());
         } else {
            lvt_2_1_.setCatType(((CatEntity)p_90011_1_).getCatType());
         }

         if (this.isTamed()) {
            lvt_2_1_.setOwnerId(this.getOwnerId());
            lvt_2_1_.setTamed(true);
            if (this.rand.nextBoolean()) {
               lvt_2_1_.setCollarColor(this.getCollarColor());
            } else {
               lvt_2_1_.setCollarColor(((CatEntity)p_90011_1_).getCollarColor());
            }
         }
      }

      return lvt_2_1_;
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      if (!this.isTamed()) {
         return false;
      } else if (!(p_70878_1_ instanceof CatEntity)) {
         return false;
      } else {
         CatEntity lvt_2_1_ = (CatEntity)p_70878_1_;
         return lvt_2_1_.isTamed() && super.canMateWith(p_70878_1_);
      }
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (p_213386_1_.getCurrentMoonPhaseFactor() > 0.9F) {
         this.setCatType(this.rand.nextInt(11));
      } else {
         this.setCatType(this.rand.nextInt(10));
      }

      if (Feature.SWAMP_HUT.isPositionInsideStructure(p_213386_1_, new BlockPos(this))) {
         this.setCatType(10);
         this.enablePersistence();
      }

      return p_213386_4_;
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      Item lvt_4_1_ = lvt_3_1_.getItem();
      if (lvt_3_1_.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (this.world.isRemote) {
         return this.isTamed() && this.isOwner(p_184645_1_) || this.isBreedingItem(lvt_3_1_);
      } else {
         boolean lvt_5_2_;
         if (this.isTamed()) {
            if (this.isOwner(p_184645_1_)) {
               if (!(lvt_4_1_ instanceof DyeItem)) {
                  if (lvt_4_1_.isFood() && this.isBreedingItem(lvt_3_1_) && this.getHealth() < this.getMaxHealth()) {
                     this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
                     this.heal((float)lvt_4_1_.getFood().getHealing());
                     return true;
                  }

                  lvt_5_2_ = super.processInteract(p_184645_1_, p_184645_2_);
                  if (!lvt_5_2_ || this.isChild()) {
                     this.sitGoal.setSitting(!this.isSitting());
                  }

                  return lvt_5_2_;
               }

               DyeColor lvt_5_1_ = ((DyeItem)lvt_4_1_).getDyeColor();
               if (lvt_5_1_ != this.getCollarColor()) {
                  this.setCollarColor(lvt_5_1_);
                  if (!p_184645_1_.abilities.isCreativeMode) {
                     lvt_3_1_.shrink(1);
                  }

                  this.enablePersistence();
                  return true;
               }
            }
         } else if (this.isBreedingItem(lvt_3_1_)) {
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
            if (this.rand.nextInt(3) == 0) {
               this.setTamedBy(p_184645_1_);
               this.sitGoal.setSitting(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.world.setEntityState(this, (byte)6);
            }

            this.enablePersistence();
            return true;
         }

         lvt_5_2_ = super.processInteract(p_184645_1_, p_184645_2_);
         if (lvt_5_2_) {
            this.enablePersistence();
         }

         return lvt_5_2_;
      }
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return BREEDING_ITEMS.test(p_70877_1_);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.5F;
   }

   public boolean canDespawn(double p_213397_1_) {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   protected void setupTamedAI() {
      if (this.avoidPlayerGoal == null) {
         this.avoidPlayerGoal = new CatEntity.AvoidPlayerGoal(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.avoidPlayerGoal);
      if (!this.isTamed()) {
         this.goalSelector.addGoal(4, this.avoidPlayerGoal);
      }

   }

   // $FF: synthetic method
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return this.createChild(p_90011_1_);
   }

   static {
      BREEDING_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON);
      CAT_TYPE = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
      field_213428_bG = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
      field_213429_bH = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
      COLLAR_COLOR = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
      field_213425_bD = (Map)Util.make(Maps.newHashMap(), (p_213410_0_) -> {
         p_213410_0_.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
         p_213410_0_.put(1, new ResourceLocation("textures/entity/cat/black.png"));
         p_213410_0_.put(2, new ResourceLocation("textures/entity/cat/red.png"));
         p_213410_0_.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
         p_213410_0_.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
         p_213410_0_.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
         p_213410_0_.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
         p_213410_0_.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
         p_213410_0_.put(8, new ResourceLocation("textures/entity/cat/white.png"));
         p_213410_0_.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
         p_213410_0_.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
      });
   }

   static class MorningGiftGoal extends Goal {
      private final CatEntity cat;
      private PlayerEntity owner;
      private BlockPos bedPos;
      private int tickCounter;

      public MorningGiftGoal(CatEntity p_i50439_1_) {
         this.cat = p_i50439_1_;
      }

      public boolean shouldExecute() {
         if (!this.cat.isTamed()) {
            return false;
         } else if (this.cat.isSitting()) {
            return false;
         } else {
            LivingEntity lvt_1_1_ = this.cat.getOwner();
            if (lvt_1_1_ instanceof PlayerEntity) {
               this.owner = (PlayerEntity)lvt_1_1_;
               if (!lvt_1_1_.isSleeping()) {
                  return false;
               }

               if (this.cat.getDistanceSq(this.owner) > 100.0D) {
                  return false;
               }

               BlockPos lvt_2_1_ = new BlockPos(this.owner);
               BlockState lvt_3_1_ = this.cat.world.getBlockState(lvt_2_1_);
               if (lvt_3_1_.getBlock().isIn(BlockTags.BEDS)) {
                  Direction lvt_4_1_ = (Direction)lvt_3_1_.get(BedBlock.HORIZONTAL_FACING);
                  this.bedPos = new BlockPos(lvt_2_1_.getX() - lvt_4_1_.getXOffset(), lvt_2_1_.getY(), lvt_2_1_.getZ() - lvt_4_1_.getZOffset());
                  return !this.func_220805_g();
               }
            }

            return false;
         }
      }

      private boolean func_220805_g() {
         List<CatEntity> lvt_1_1_ = this.cat.world.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(this.bedPos)).grow(2.0D));
         Iterator var2 = lvt_1_1_.iterator();

         CatEntity lvt_3_1_;
         do {
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               lvt_3_1_ = (CatEntity)var2.next();
            } while(lvt_3_1_ == this.cat);
         } while(!lvt_3_1_.func_213416_eg() && !lvt_3_1_.func_213409_eh());

         return true;
      }

      public boolean shouldContinueExecuting() {
         return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.func_220805_g();
      }

      public void startExecuting() {
         if (this.bedPos != null) {
            this.cat.getAISit().setSitting(false);
            this.cat.getNavigator().tryMoveToXYZ((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.100000023841858D);
         }

      }

      public void resetTask() {
         this.cat.func_213419_u(false);
         float lvt_1_1_ = this.cat.world.getCelestialAngle(1.0F);
         if (this.owner.getSleepTimer() >= 100 && (double)lvt_1_1_ > 0.77D && (double)lvt_1_1_ < 0.8D && (double)this.cat.world.getRandom().nextFloat() < 0.7D) {
            this.func_220804_h();
         }

         this.tickCounter = 0;
         this.cat.func_213415_v(false);
         this.cat.getNavigator().clearPath();
      }

      private void func_220804_h() {
         Random lvt_1_1_ = this.cat.getRNG();
         BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable();
         lvt_2_1_.setPos((Entity)this.cat);
         this.cat.attemptTeleport((double)(lvt_2_1_.getX() + lvt_1_1_.nextInt(11) - 5), (double)(lvt_2_1_.getY() + lvt_1_1_.nextInt(5) - 2), (double)(lvt_2_1_.getZ() + lvt_1_1_.nextInt(11) - 5), false);
         lvt_2_1_.setPos((Entity)this.cat);
         LootTable lvt_3_1_ = this.cat.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_CAT_MORNING_GIFT);
         LootContext.Builder lvt_4_1_ = (new LootContext.Builder((ServerWorld)this.cat.world)).withParameter(LootParameters.POSITION, lvt_2_1_).withParameter(LootParameters.THIS_ENTITY, this.cat).withRandom(lvt_1_1_);
         List<ItemStack> lvt_5_1_ = lvt_3_1_.generate(lvt_4_1_.build(LootParameterSets.GIFT));
         Iterator var6 = lvt_5_1_.iterator();

         while(var6.hasNext()) {
            ItemStack lvt_7_1_ = (ItemStack)var6.next();
            this.cat.world.addEntity(new ItemEntity(this.cat.world, (double)((float)lvt_2_1_.getX() - MathHelper.sin(this.cat.renderYawOffset * 0.017453292F)), (double)lvt_2_1_.getY(), (double)((float)lvt_2_1_.getZ() + MathHelper.cos(this.cat.renderYawOffset * 0.017453292F)), lvt_7_1_));
         }

      }

      public void tick() {
         if (this.owner != null && this.bedPos != null) {
            this.cat.getAISit().setSitting(false);
            this.cat.getNavigator().tryMoveToXYZ((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.100000023841858D);
            if (this.cat.getDistanceSq(this.owner) < 2.5D) {
               ++this.tickCounter;
               if (this.tickCounter > 16) {
                  this.cat.func_213419_u(true);
                  this.cat.func_213415_v(false);
               } else {
                  this.cat.faceEntity(this.owner, 45.0F, 45.0F);
                  this.cat.func_213415_v(true);
               }
            } else {
               this.cat.func_213419_u(false);
            }
         }

      }
   }

   static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
      @Nullable
      private PlayerEntity temptingPlayer;
      private final CatEntity cat;

      public TemptGoal(CatEntity p_i50438_1_, double p_i50438_2_, Ingredient p_i50438_4_, boolean p_i50438_5_) {
         super(p_i50438_1_, p_i50438_2_, p_i50438_4_, p_i50438_5_);
         this.cat = p_i50438_1_;
      }

      public void tick() {
         super.tick();
         if (this.temptingPlayer == null && this.creature.getRNG().nextInt(600) == 0) {
            this.temptingPlayer = this.closestPlayer;
         } else if (this.creature.getRNG().nextInt(500) == 0) {
            this.temptingPlayer = null;
         }

      }

      protected boolean isScaredByPlayerMovement() {
         return this.temptingPlayer != null && this.temptingPlayer.equals(this.closestPlayer) ? false : super.isScaredByPlayerMovement();
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.cat.isTamed();
      }
   }

   static class AvoidPlayerGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final CatEntity field_220873_i;

      public AvoidPlayerGoal(CatEntity p_i50440_1_, Class<T> p_i50440_2_, float p_i50440_3_, double p_i50440_4_, double p_i50440_6_) {
         Predicate var10006 = EntityPredicates.CAN_AI_TARGET;
         super(p_i50440_1_, p_i50440_2_, p_i50440_3_, p_i50440_4_, p_i50440_6_, var10006::test);
         this.field_220873_i = p_i50440_1_;
      }

      public boolean shouldExecute() {
         return !this.field_220873_i.isTamed() && super.shouldExecute();
      }

      public boolean shouldContinueExecuting() {
         return !this.field_220873_i.isTamed() && super.shouldContinueExecuting();
      }
   }
}
