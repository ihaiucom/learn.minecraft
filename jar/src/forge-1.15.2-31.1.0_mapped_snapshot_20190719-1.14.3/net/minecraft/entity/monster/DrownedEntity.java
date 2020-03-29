package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements IRangedAttackMob {
   private boolean swimmingUp;
   protected final SwimmerPathNavigator waterNavigator;
   protected final GroundPathNavigator groundNavigator;

   public DrownedEntity(EntityType<? extends DrownedEntity> p_i50212_1_, World p_i50212_2_) {
      super(p_i50212_1_, p_i50212_2_);
      this.stepHeight = 1.0F;
      this.moveController = new DrownedEntity.MoveHelperController(this);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.waterNavigator = new SwimmerPathNavigator(this, p_i50212_2_);
      this.groundNavigator = new GroundPathNavigator(this, p_i50212_2_);
   }

   protected void applyEntityAI() {
      this.goalSelector.addGoal(1, new DrownedEntity.GoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new DrownedEntity.TridentAttackGoal(this, 1.0D, 40, 10.0F));
      this.goalSelector.addGoal(2, new DrownedEntity.AttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new DrownedEntity.GoToBeachGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new DrownedEntity.SwimUpGoal(this, 1.0D, this.world.getSeaLevel()));
      this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{DrownedEntity.class})).setCallsForHelp(ZombiePigmanEntity.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAttack));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (this.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty() && this.rand.nextFloat() < 0.03F) {
         this.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.inventoryHandsDropChances[EquipmentSlotType.OFFHAND.getIndex()] = 2.0F;
      }

      return p_213386_4_;
   }

   public static boolean func_223332_b(EntityType<DrownedEntity> p_223332_0_, IWorld p_223332_1_, SpawnReason p_223332_2_, BlockPos p_223332_3_, Random p_223332_4_) {
      Biome lvt_5_1_ = p_223332_1_.func_226691_t_(p_223332_3_);
      boolean lvt_6_1_ = p_223332_1_.getDifficulty() != Difficulty.PEACEFUL && func_223323_a(p_223332_1_, p_223332_3_, p_223332_4_) && (p_223332_2_ == SpawnReason.SPAWNER || p_223332_1_.getFluidState(p_223332_3_).isTagged(FluidTags.WATER));
      if (lvt_5_1_ != Biomes.RIVER && lvt_5_1_ != Biomes.FROZEN_RIVER) {
         return p_223332_4_.nextInt(40) == 0 && func_223333_a(p_223332_1_, p_223332_3_) && lvt_6_1_;
      } else {
         return p_223332_4_.nextInt(15) == 0 && lvt_6_1_;
      }
   }

   private static boolean func_223333_a(IWorld p_223333_0_, BlockPos p_223333_1_) {
      return p_223333_1_.getY() < p_223333_0_.getSeaLevel() - 5;
   }

   protected boolean canBreakDoors() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_AMBIENT_WATER : SoundEvents.ENTITY_DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_HURT_WATER : SoundEvents.ENTITY_DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_DEATH_WATER : SoundEvents.ENTITY_DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DROWNED_SWIM;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      if ((double)this.rand.nextFloat() > 0.9D) {
         int lvt_2_1_ = this.rand.nextInt(16);
         if (lvt_2_1_ < 10) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack p_208003_1_, ItemStack p_208003_2_, EquipmentSlotType p_208003_3_) {
      if (p_208003_2_.getItem() == Items.NAUTILUS_SHELL) {
         return false;
      } else if (p_208003_2_.getItem() == Items.TRIDENT) {
         if (p_208003_1_.getItem() == Items.TRIDENT) {
            return p_208003_1_.getDamage() < p_208003_2_.getDamage();
         } else {
            return false;
         }
      } else {
         return p_208003_1_.getItem() == Items.TRIDENT ? true : super.shouldExchangeEquipment(p_208003_1_, p_208003_2_, p_208003_3_);
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return p_205019_1_.func_226668_i_(this);
   }

   public boolean shouldAttack(@Nullable LivingEntity p_204714_1_) {
      if (p_204714_1_ != null) {
         return !this.world.isDaytime() || p_204714_1_.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByWater() {
      return !this.isSwimming();
   }

   private boolean func_204715_dF() {
      if (this.swimmingUp) {
         return true;
      } else {
         LivingEntity lvt_1_1_ = this.getAttackTarget();
         return lvt_1_1_ != null && lvt_1_1_.isInWater();
      }
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
         this.moveRelative(0.01F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
      } else {
         super.travel(p_213352_1_);
      }

   }

   public void updateSwimming() {
      if (!this.world.isRemote) {
         if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
            this.navigator = this.waterNavigator;
            this.setSwimming(true);
         } else {
            this.navigator = this.groundNavigator;
            this.setSwimming(false);
         }
      }

   }

   protected boolean isCloseToPathTarget() {
      Path lvt_1_1_ = this.getNavigator().getPath();
      if (lvt_1_1_ != null) {
         BlockPos lvt_2_1_ = lvt_1_1_.func_224770_k();
         if (lvt_2_1_ != null) {
            double lvt_3_1_ = this.getDistanceSq((double)lvt_2_1_.getX(), (double)lvt_2_1_.getY(), (double)lvt_2_1_.getZ());
            if (lvt_3_1_ < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      TridentEntity lvt_3_1_ = new TridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
      double lvt_4_1_ = p_82196_1_.func_226277_ct_() - this.func_226277_ct_();
      double lvt_6_1_ = p_82196_1_.func_226283_e_(0.3333333333333333D) - lvt_3_1_.func_226278_cu_();
      double lvt_8_1_ = p_82196_1_.func_226281_cx_() - this.func_226281_cx_();
      double lvt_10_1_ = (double)MathHelper.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_8_1_ * lvt_8_1_);
      lvt_3_1_.shoot(lvt_4_1_, lvt_6_1_ + lvt_10_1_ * 0.20000000298023224D, lvt_8_1_, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(lvt_3_1_);
   }

   public void setSwimmingUp(boolean p_204713_1_) {
      this.swimmingUp = p_204713_1_;
   }

   static class MoveHelperController extends MovementController {
      private final DrownedEntity drowned;

      public MoveHelperController(DrownedEntity p_i48909_1_) {
         super(p_i48909_1_);
         this.drowned = p_i48909_1_;
      }

      public void tick() {
         LivingEntity lvt_1_1_ = this.drowned.getAttackTarget();
         if (this.drowned.func_204715_dF() && this.drowned.isInWater()) {
            if (lvt_1_1_ != null && lvt_1_1_.func_226278_cu_() > this.drowned.func_226278_cu_() || this.drowned.swimmingUp) {
               this.drowned.setMotion(this.drowned.getMotion().add(0.0D, 0.002D, 0.0D));
            }

            if (this.action != MovementController.Action.MOVE_TO || this.drowned.getNavigator().noPath()) {
               this.drowned.setAIMoveSpeed(0.0F);
               return;
            }

            double lvt_2_1_ = this.posX - this.drowned.func_226277_ct_();
            double lvt_4_1_ = this.posY - this.drowned.func_226278_cu_();
            double lvt_6_1_ = this.posZ - this.drowned.func_226281_cx_();
            double lvt_8_1_ = (double)MathHelper.sqrt(lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_);
            lvt_4_1_ /= lvt_8_1_;
            float lvt_10_1_ = (float)(MathHelper.atan2(lvt_6_1_, lvt_2_1_) * 57.2957763671875D) - 90.0F;
            this.drowned.rotationYaw = this.limitAngle(this.drowned.rotationYaw, lvt_10_1_, 90.0F);
            this.drowned.renderYawOffset = this.drowned.rotationYaw;
            float lvt_11_1_ = (float)(this.speed * this.drowned.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            float lvt_12_1_ = MathHelper.lerp(0.125F, this.drowned.getAIMoveSpeed(), lvt_11_1_);
            this.drowned.setAIMoveSpeed(lvt_12_1_);
            this.drowned.setMotion(this.drowned.getMotion().add((double)lvt_12_1_ * lvt_2_1_ * 0.005D, (double)lvt_12_1_ * lvt_4_1_ * 0.1D, (double)lvt_12_1_ * lvt_6_1_ * 0.005D));
         } else {
            if (!this.drowned.onGround) {
               this.drowned.setMotion(this.drowned.getMotion().add(0.0D, -0.008D, 0.0D));
            }

            super.tick();
         }

      }
   }

   static class AttackGoal extends ZombieAttackGoal {
      private final DrownedEntity field_204726_g;

      public AttackGoal(DrownedEntity p_i48913_1_, double p_i48913_2_, boolean p_i48913_4_) {
         super(p_i48913_1_, p_i48913_2_, p_i48913_4_);
         this.field_204726_g = p_i48913_1_;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }
   }

   static class GoToWaterGoal extends Goal {
      private final CreatureEntity field_204730_a;
      private double field_204731_b;
      private double field_204732_c;
      private double field_204733_d;
      private final double field_204734_e;
      private final World field_204735_f;

      public GoToWaterGoal(CreatureEntity p_i48910_1_, double p_i48910_2_) {
         this.field_204730_a = p_i48910_1_;
         this.field_204734_e = p_i48910_2_;
         this.field_204735_f = p_i48910_1_.world;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         if (!this.field_204735_f.isDaytime()) {
            return false;
         } else if (this.field_204730_a.isInWater()) {
            return false;
         } else {
            Vec3d lvt_1_1_ = this.func_204729_f();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.field_204731_b = lvt_1_1_.x;
               this.field_204732_c = lvt_1_1_.y;
               this.field_204733_d = lvt_1_1_.z;
               return true;
            }
         }
      }

      public boolean shouldContinueExecuting() {
         return !this.field_204730_a.getNavigator().noPath();
      }

      public void startExecuting() {
         this.field_204730_a.getNavigator().tryMoveToXYZ(this.field_204731_b, this.field_204732_c, this.field_204733_d, this.field_204734_e);
      }

      @Nullable
      private Vec3d func_204729_f() {
         Random lvt_1_1_ = this.field_204730_a.getRNG();
         BlockPos lvt_2_1_ = new BlockPos(this.field_204730_a);

         for(int lvt_3_1_ = 0; lvt_3_1_ < 10; ++lvt_3_1_) {
            BlockPos lvt_4_1_ = lvt_2_1_.add(lvt_1_1_.nextInt(20) - 10, 2 - lvt_1_1_.nextInt(8), lvt_1_1_.nextInt(20) - 10);
            if (this.field_204735_f.getBlockState(lvt_4_1_).getBlock() == Blocks.WATER) {
               return new Vec3d(lvt_4_1_);
            }
         }

         return null;
      }
   }

   static class GoToBeachGoal extends MoveToBlockGoal {
      private final DrownedEntity drowned;

      public GoToBeachGoal(DrownedEntity p_i48911_1_, double p_i48911_2_) {
         super(p_i48911_1_, p_i48911_2_, 8, 2);
         this.drowned = p_i48911_1_;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.drowned.world.isDaytime() && this.drowned.isInWater() && this.drowned.func_226278_cu_() >= (double)(this.drowned.world.getSeaLevel() - 3);
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting();
      }

      protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         BlockPos lvt_3_1_ = p_179488_2_.up();
         return p_179488_1_.isAirBlock(lvt_3_1_) && p_179488_1_.isAirBlock(lvt_3_1_.up()) ? p_179488_1_.getBlockState(p_179488_2_).func_215682_a(p_179488_1_, p_179488_2_, this.drowned) : false;
      }

      public void startExecuting() {
         this.drowned.setSwimmingUp(false);
         this.drowned.navigator = this.drowned.groundNavigator;
         super.startExecuting();
      }

      public void resetTask() {
         super.resetTask();
      }
   }

   static class SwimUpGoal extends Goal {
      private final DrownedEntity field_204736_a;
      private final double field_204737_b;
      private final int targetY;
      private boolean obstructed;

      public SwimUpGoal(DrownedEntity p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
         this.field_204736_a = p_i48908_1_;
         this.field_204737_b = p_i48908_2_;
         this.targetY = p_i48908_4_;
      }

      public boolean shouldExecute() {
         return !this.field_204736_a.world.isDaytime() && this.field_204736_a.isInWater() && this.field_204736_a.func_226278_cu_() < (double)(this.targetY - 2);
      }

      public boolean shouldContinueExecuting() {
         return this.shouldExecute() && !this.obstructed;
      }

      public void tick() {
         if (this.field_204736_a.func_226278_cu_() < (double)(this.targetY - 1) && (this.field_204736_a.getNavigator().noPath() || this.field_204736_a.isCloseToPathTarget())) {
            Vec3d lvt_1_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_204736_a, 4, 8, new Vec3d(this.field_204736_a.func_226277_ct_(), (double)(this.targetY - 1), this.field_204736_a.func_226281_cx_()));
            if (lvt_1_1_ == null) {
               this.obstructed = true;
               return;
            }

            this.field_204736_a.getNavigator().tryMoveToXYZ(lvt_1_1_.x, lvt_1_1_.y, lvt_1_1_.z, this.field_204737_b);
         }

      }

      public void startExecuting() {
         this.field_204736_a.setSwimmingUp(true);
         this.obstructed = false;
      }

      public void resetTask() {
         this.field_204736_a.setSwimmingUp(false);
      }
   }

   static class TridentAttackGoal extends RangedAttackGoal {
      private final DrownedEntity field_204728_a;

      public TridentAttackGoal(IRangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
         super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
         this.field_204728_a = (DrownedEntity)p_i48907_1_;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204728_a.getHeldItemMainhand().getItem() == Items.TRIDENT;
      }

      public void startExecuting() {
         super.startExecuting();
         this.field_204728_a.setAggroed(true);
         this.field_204728_a.setActiveHand(Hand.MAIN_HAND);
      }

      public void resetTask() {
         super.resetTask();
         this.field_204728_a.resetActiveHand();
         this.field_204728_a.setAggroed(false);
      }
   }
}
