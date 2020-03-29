package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
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

public class TurtleEntity extends AnimalEntity {
   private static final DataParameter<BlockPos> HOME_POS;
   private static final DataParameter<Boolean> HAS_EGG;
   private static final DataParameter<Boolean> IS_DIGGING;
   private static final DataParameter<BlockPos> TRAVEL_POS;
   private static final DataParameter<Boolean> GOING_HOME;
   private static final DataParameter<Boolean> TRAVELLING;
   private int isDigging;
   public static final Predicate<LivingEntity> TARGET_DRY_BABY;

   public TurtleEntity(EntityType<? extends TurtleEntity> p_i50241_1_, World p_i50241_2_) {
      super(p_i50241_1_, p_i50241_2_);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.moveController = new TurtleEntity.MoveHelperController(this);
      this.stepHeight = 1.0F;
   }

   public void setHome(BlockPos p_203011_1_) {
      this.dataManager.set(HOME_POS, p_203011_1_);
   }

   private BlockPos getHome() {
      return (BlockPos)this.dataManager.get(HOME_POS);
   }

   private void setTravelPos(BlockPos p_203019_1_) {
      this.dataManager.set(TRAVEL_POS, p_203019_1_);
   }

   private BlockPos getTravelPos() {
      return (BlockPos)this.dataManager.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return (Boolean)this.dataManager.get(HAS_EGG);
   }

   private void setHasEgg(boolean p_203017_1_) {
      this.dataManager.set(HAS_EGG, p_203017_1_);
   }

   public boolean isDigging() {
      return (Boolean)this.dataManager.get(IS_DIGGING);
   }

   private void setDigging(boolean p_203015_1_) {
      this.isDigging = p_203015_1_ ? 1 : 0;
      this.dataManager.set(IS_DIGGING, p_203015_1_);
   }

   private boolean isGoingHome() {
      return (Boolean)this.dataManager.get(GOING_HOME);
   }

   private void setGoingHome(boolean p_203012_1_) {
      this.dataManager.set(GOING_HOME, p_203012_1_);
   }

   private boolean isTravelling() {
      return (Boolean)this.dataManager.get(TRAVELLING);
   }

   private void setTravelling(boolean p_203021_1_) {
      this.dataManager.set(TRAVELLING, p_203021_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HOME_POS, BlockPos.ZERO);
      this.dataManager.register(HAS_EGG, false);
      this.dataManager.register(TRAVEL_POS, BlockPos.ZERO);
      this.dataManager.register(GOING_HOME, false);
      this.dataManager.register(TRAVELLING, false);
      this.dataManager.register(IS_DIGGING, false);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("HomePosX", this.getHome().getX());
      p_213281_1_.putInt("HomePosY", this.getHome().getY());
      p_213281_1_.putInt("HomePosZ", this.getHome().getZ());
      p_213281_1_.putBoolean("HasEgg", this.hasEgg());
      p_213281_1_.putInt("TravelPosX", this.getTravelPos().getX());
      p_213281_1_.putInt("TravelPosY", this.getTravelPos().getY());
      p_213281_1_.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      int lvt_2_1_ = p_70037_1_.getInt("HomePosX");
      int lvt_3_1_ = p_70037_1_.getInt("HomePosY");
      int lvt_4_1_ = p_70037_1_.getInt("HomePosZ");
      this.setHome(new BlockPos(lvt_2_1_, lvt_3_1_, lvt_4_1_));
      super.readAdditional(p_70037_1_);
      this.setHasEgg(p_70037_1_.getBoolean("HasEgg"));
      int lvt_5_1_ = p_70037_1_.getInt("TravelPosX");
      int lvt_6_1_ = p_70037_1_.getInt("TravelPosY");
      int lvt_7_1_ = p_70037_1_.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_));
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setHome(new BlockPos(this));
      this.setTravelPos(BlockPos.ZERO);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public static boolean func_223322_c(EntityType<TurtleEntity> p_223322_0_, IWorld p_223322_1_, SpawnReason p_223322_2_, BlockPos p_223322_3_, Random p_223322_4_) {
      return p_223322_3_.getY() < p_223322_1_.getSeaLevel() + 4 && p_223322_1_.getBlockState(p_223322_3_.down()).getBlock() == Blocks.SAND && p_223322_1_.func_226659_b_(p_223322_3_, 0) > 8;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new TurtleEntity.PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new TurtleEntity.MateGoal(this, 1.0D));
      this.goalSelector.addGoal(1, new TurtleEntity.LayEggGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new TurtleEntity.PlayerTemptGoal(this, 1.1D, Blocks.SEAGRASS.asItem()));
      this.goalSelector.addGoal(3, new TurtleEntity.GoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new TurtleEntity.GoHomeGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new TurtleEntity.TravelGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(9, new TurtleEntity.WanderGoal(this, 1.0D, 100));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public int getTalkInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isChild() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float p_203006_1_) {
      super.playSwimSound(p_203006_1_ * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      SoundEvent lvt_3_1_ = this.isChild() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
      this.playSound(lvt_3_1_, 0.15F, 1.0F);
   }

   public boolean canBreed() {
      return super.canBreed() && !this.hasEgg();
   }

   protected float determineNextStepDistance() {
      return this.distanceWalkedOnStepModified + 0.15F;
   }

   public float getRenderScale() {
      return this.isChild() ? 0.3F : 1.0F;
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new TurtleEntity.Navigator(this, p_175447_1_);
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return (AgeableEntity)EntityType.TURTLE.create(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Blocks.SEAGRASS.asItem();
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      if (!this.isGoingHome() && p_205022_2_.getFluidState(p_205022_1_).isTagged(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return p_205022_2_.getBlockState(p_205022_1_.down()).getBlock() == Blocks.SAND ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
      }
   }

   public void livingTick() {
      super.livingTick();
      if (this.isAlive() && this.isDigging() && this.isDigging >= 1 && this.isDigging % 5 == 0) {
         BlockPos lvt_1_1_ = new BlockPos(this);
         if (this.world.getBlockState(lvt_1_1_.down()).getBlock() == Blocks.SAND) {
            this.world.playEvent(2001, lvt_1_1_, Block.getStateId(Blocks.SAND.getDefaultState()));
         }
      }

   }

   protected void onGrowingAdult() {
      super.onGrowingAdult();
      if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
         this.entityDropItem(Items.SCUTE, 1);
      }

   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(0.1F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
         if (this.getAttackTarget() == null && (!this.isGoingHome() || !this.getHome().withinDistance(this.getPositionVec(), 20.0D))) {
            this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return false;
   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   static {
      HOME_POS = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BLOCK_POS);
      HAS_EGG = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
      IS_DIGGING = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
      TRAVEL_POS = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BLOCK_POS);
      GOING_HOME = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
      TRAVELLING = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
      TARGET_DRY_BABY = (p_213616_0_) -> {
         return p_213616_0_.isChild() && !p_213616_0_.isInWater();
      };
   }

   static class Navigator extends SwimmerPathNavigator {
      Navigator(TurtleEntity p_i48815_1_, World p_i48815_2_) {
         super(p_i48815_1_, p_i48815_2_);
      }

      protected boolean canNavigate() {
         return true;
      }

      protected PathFinder getPathFinder(int p_179679_1_) {
         this.nodeProcessor = new WalkAndSwimNodeProcessor();
         return new PathFinder(this.nodeProcessor, p_179679_1_);
      }

      public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
         if (this.entity instanceof TurtleEntity) {
            TurtleEntity lvt_2_1_ = (TurtleEntity)this.entity;
            if (lvt_2_1_.isTravelling()) {
               return this.world.getBlockState(p_188555_1_).getBlock() == Blocks.WATER;
            }
         }

         return !this.world.getBlockState(p_188555_1_.down()).isAir();
      }
   }

   static class MoveHelperController extends MovementController {
      private final TurtleEntity turtle;

      MoveHelperController(TurtleEntity p_i48817_1_) {
         super(p_i48817_1_);
         this.turtle = p_i48817_1_;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.setMotion(this.turtle.getMotion().add(0.0D, 0.005D, 0.0D));
            if (!this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 16.0D)) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isChild()) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.action == MovementController.Action.MOVE_TO && !this.turtle.getNavigator().noPath()) {
            double lvt_1_1_ = this.posX - this.turtle.func_226277_ct_();
            double lvt_3_1_ = this.posY - this.turtle.func_226278_cu_();
            double lvt_5_1_ = this.posZ - this.turtle.func_226281_cx_();
            double lvt_7_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_);
            lvt_3_1_ /= lvt_7_1_;
            float lvt_9_1_ = (float)(MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
            this.turtle.rotationYaw = this.limitAngle(this.turtle.rotationYaw, lvt_9_1_, 90.0F);
            this.turtle.renderYawOffset = this.turtle.rotationYaw;
            float lvt_10_1_ = (float)(this.speed * this.turtle.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setAIMoveSpeed(MathHelper.lerp(0.125F, this.turtle.getAIMoveSpeed(), lvt_10_1_));
            this.turtle.setMotion(this.turtle.getMotion().add(0.0D, (double)this.turtle.getAIMoveSpeed() * lvt_3_1_ * 0.1D, 0.0D));
         } else {
            this.turtle.setAIMoveSpeed(0.0F);
         }
      }
   }

   static class GoToWaterGoal extends MoveToBlockGoal {
      private final TurtleEntity turtle;

      private GoToWaterGoal(TurtleEntity p_i48819_1_, double p_i48819_2_) {
         super(p_i48819_1_, p_i48819_1_.isChild() ? 2.0D : p_i48819_2_, 24);
         this.turtle = p_i48819_1_;
         this.field_203112_e = -1;
      }

      public boolean shouldContinueExecuting() {
         return !this.turtle.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.turtle.world, this.destinationBlock);
      }

      public boolean shouldExecute() {
         if (this.turtle.isChild() && !this.turtle.isInWater()) {
            return super.shouldExecute();
         } else {
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
         }
      }

      public boolean shouldMove() {
         return this.timeoutCounter % 160 == 0;
      }

      protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         Block lvt_3_1_ = p_179488_1_.getBlockState(p_179488_2_).getBlock();
         return lvt_3_1_ == Blocks.WATER;
      }

      // $FF: synthetic method
      GoToWaterGoal(TurtleEntity p_i48820_1_, double p_i48820_2_, Object p_i48820_4_) {
         this(p_i48820_1_, p_i48820_2_);
      }
   }

   static class WanderGoal extends RandomWalkingGoal {
      private final TurtleEntity turtle;

      private WanderGoal(TurtleEntity p_i48813_1_, double p_i48813_2_, int p_i48813_4_) {
         super(p_i48813_1_, p_i48813_2_, p_i48813_4_);
         this.turtle = p_i48813_1_;
      }

      public boolean shouldExecute() {
         return !this.creature.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
      }

      // $FF: synthetic method
      WanderGoal(TurtleEntity p_i48814_1_, double p_i48814_2_, int p_i48814_4_, Object p_i48814_5_) {
         this(p_i48814_1_, p_i48814_2_, p_i48814_4_);
      }
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final TurtleEntity turtle;

      LayEggGoal(TurtleEntity p_i48818_1_, double p_i48818_2_) {
         super(p_i48818_1_, p_i48818_2_, 16);
         this.turtle = p_i48818_1_;
      }

      public boolean shouldExecute() {
         return this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0D) ? super.shouldExecute() : false;
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0D);
      }

      public void tick() {
         super.tick();
         BlockPos lvt_1_1_ = new BlockPos(this.turtle);
         if (!this.turtle.isInWater() && this.getIsAboveDestination()) {
            if (this.turtle.isDigging < 1) {
               this.turtle.setDigging(true);
            } else if (this.turtle.isDigging > 200) {
               World lvt_2_1_ = this.turtle.world;
               lvt_2_1_.playSound((PlayerEntity)null, lvt_1_1_, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + lvt_2_1_.rand.nextFloat() * 0.2F);
               lvt_2_1_.setBlockState(this.destinationBlock.up(), (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.rand.nextInt(4) + 1), 3);
               this.turtle.setHasEgg(false);
               this.turtle.setDigging(false);
               this.turtle.setInLove(600);
            }

            if (this.turtle.isDigging()) {
               this.turtle.isDigging++;
            }
         }

      }

      protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         if (!p_179488_1_.isAirBlock(p_179488_2_.up())) {
            return false;
         } else {
            Block lvt_3_1_ = p_179488_1_.getBlockState(p_179488_2_).getBlock();
            return lvt_3_1_ == Blocks.SAND;
         }
      }
   }

   static class MateGoal extends BreedGoal {
      private final TurtleEntity turtle;

      MateGoal(TurtleEntity p_i48822_1_, double p_i48822_2_) {
         super(p_i48822_1_, p_i48822_2_);
         this.turtle = p_i48822_1_;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.turtle.hasEgg();
      }

      protected void spawnBaby() {
         ServerPlayerEntity lvt_1_1_ = this.animal.getLoveCause();
         if (lvt_1_1_ == null && this.field_75391_e.getLoveCause() != null) {
            lvt_1_1_ = this.field_75391_e.getLoveCause();
         }

         if (lvt_1_1_ != null) {
            lvt_1_1_.addStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(lvt_1_1_, this.animal, this.field_75391_e, (AgeableEntity)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.resetInLove();
         this.field_75391_e.resetInLove();
         Random lvt_2_1_ = this.animal.getRNG();
         if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.func_226277_ct_(), this.animal.func_226278_cu_(), this.animal.func_226281_cx_(), lvt_2_1_.nextInt(7) + 1));
         }

      }
   }

   static class PlayerTemptGoal extends Goal {
      private static final EntityPredicate field_220834_a = (new EntityPredicate()).setDistance(10.0D).allowFriendlyFire().allowInvulnerable();
      private final TurtleEntity turtle;
      private final double speed;
      private PlayerEntity tempter;
      private int cooldown;
      private final Set<Item> temptItems;

      PlayerTemptGoal(TurtleEntity p_i48812_1_, double p_i48812_2_, Item p_i48812_4_) {
         this.turtle = p_i48812_1_;
         this.speed = p_i48812_2_;
         this.temptItems = Sets.newHashSet(new Item[]{p_i48812_4_});
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         if (this.cooldown > 0) {
            --this.cooldown;
            return false;
         } else {
            this.tempter = this.turtle.world.getClosestPlayer(field_220834_a, this.turtle);
            if (this.tempter == null) {
               return false;
            } else {
               return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
            }
         }
      }

      private boolean isTemptedBy(ItemStack p_203131_1_) {
         return this.temptItems.contains(p_203131_1_.getItem());
      }

      public boolean shouldContinueExecuting() {
         return this.shouldExecute();
      }

      public void resetTask() {
         this.tempter = null;
         this.turtle.getNavigator().clearPath();
         this.cooldown = 100;
      }

      public void tick() {
         this.turtle.getLookController().setLookPositionWithEntity(this.tempter, (float)(this.turtle.getHorizontalFaceSpeed() + 20), (float)this.turtle.getVerticalFaceSpeed());
         if (this.turtle.getDistanceSq(this.tempter) < 6.25D) {
            this.turtle.getNavigator().clearPath();
         } else {
            this.turtle.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
         }

      }
   }

   static class GoHomeGoal extends Goal {
      private final TurtleEntity turtle;
      private final double speed;
      private boolean field_203129_c;
      private int field_203130_d;

      GoHomeGoal(TurtleEntity p_i48821_1_, double p_i48821_2_) {
         this.turtle = p_i48821_1_;
         this.speed = p_i48821_2_;
      }

      public boolean shouldExecute() {
         if (this.turtle.isChild()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRNG().nextInt(700) != 0) {
            return false;
         } else {
            return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 64.0D);
         }
      }

      public void startExecuting() {
         this.turtle.setGoingHome(true);
         this.field_203129_c = false;
         this.field_203130_d = 0;
      }

      public void resetTask() {
         this.turtle.setGoingHome(false);
      }

      public boolean shouldContinueExecuting() {
         return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 7.0D) && !this.field_203129_c && this.field_203130_d <= 600;
      }

      public void tick() {
         BlockPos lvt_1_1_ = this.turtle.getHome();
         boolean lvt_2_1_ = lvt_1_1_.withinDistance(this.turtle.getPositionVec(), 16.0D);
         if (lvt_2_1_) {
            ++this.field_203130_d;
         }

         if (this.turtle.getNavigator().noPath()) {
            Vec3d lvt_3_1_ = new Vec3d(lvt_1_1_);
            Vec3d lvt_4_1_ = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, lvt_3_1_, 0.3141592741012573D);
            if (lvt_4_1_ == null) {
               lvt_4_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, lvt_3_1_);
            }

            if (lvt_4_1_ != null && !lvt_2_1_ && this.turtle.world.getBlockState(new BlockPos(lvt_4_1_)).getBlock() != Blocks.WATER) {
               lvt_4_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 16, 5, lvt_3_1_);
            }

            if (lvt_4_1_ == null) {
               this.field_203129_c = true;
               return;
            }

            this.turtle.getNavigator().tryMoveToXYZ(lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z, this.speed);
         }

      }
   }

   static class TravelGoal extends Goal {
      private final TurtleEntity turtle;
      private final double field_203138_b;
      private boolean field_203139_c;

      TravelGoal(TurtleEntity p_i48811_1_, double p_i48811_2_) {
         this.turtle = p_i48811_1_;
         this.field_203138_b = p_i48811_2_;
      }

      public boolean shouldExecute() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void startExecuting() {
         int lvt_1_1_ = true;
         int lvt_2_1_ = true;
         Random lvt_3_1_ = this.turtle.rand;
         int lvt_4_1_ = lvt_3_1_.nextInt(1025) - 512;
         int lvt_5_1_ = lvt_3_1_.nextInt(9) - 4;
         int lvt_6_1_ = lvt_3_1_.nextInt(1025) - 512;
         if ((double)lvt_5_1_ + this.turtle.func_226278_cu_() > (double)(this.turtle.world.getSeaLevel() - 1)) {
            lvt_5_1_ = 0;
         }

         BlockPos lvt_7_1_ = new BlockPos((double)lvt_4_1_ + this.turtle.func_226277_ct_(), (double)lvt_5_1_ + this.turtle.func_226278_cu_(), (double)lvt_6_1_ + this.turtle.func_226281_cx_());
         this.turtle.setTravelPos(lvt_7_1_);
         this.turtle.setTravelling(true);
         this.field_203139_c = false;
      }

      public void tick() {
         if (this.turtle.getNavigator().noPath()) {
            Vec3d lvt_1_1_ = new Vec3d(this.turtle.getTravelPos());
            Vec3d lvt_2_1_ = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, lvt_1_1_, 0.3141592741012573D);
            if (lvt_2_1_ == null) {
               lvt_2_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, lvt_1_1_);
            }

            if (lvt_2_1_ != null) {
               int lvt_3_1_ = MathHelper.floor(lvt_2_1_.x);
               int lvt_4_1_ = MathHelper.floor(lvt_2_1_.z);
               int lvt_5_1_ = true;
               if (!this.turtle.world.isAreaLoaded(lvt_3_1_ - 34, 0, lvt_4_1_ - 34, lvt_3_1_ + 34, 0, lvt_4_1_ + 34)) {
                  lvt_2_1_ = null;
               }
            }

            if (lvt_2_1_ == null) {
               this.field_203139_c = true;
               return;
            }

            this.turtle.getNavigator().tryMoveToXYZ(lvt_2_1_.x, lvt_2_1_.y, lvt_2_1_.z, this.field_203138_b);
         }

      }

      public boolean shouldContinueExecuting() {
         return !this.turtle.getNavigator().noPath() && !this.field_203139_c && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void resetTask() {
         this.turtle.setTravelling(false);
         super.resetTask();
      }
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      PanicGoal(TurtleEntity p_i48816_1_, double p_i48816_2_) {
         super(p_i48816_1_, p_i48816_2_);
      }

      public boolean shouldExecute() {
         if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
            return false;
         } else {
            BlockPos lvt_1_1_ = this.getRandPos(this.creature.world, this.creature, 7, 4);
            if (lvt_1_1_ != null) {
               this.randPosX = (double)lvt_1_1_.getX();
               this.randPosY = (double)lvt_1_1_.getY();
               this.randPosZ = (double)lvt_1_1_.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }
}
