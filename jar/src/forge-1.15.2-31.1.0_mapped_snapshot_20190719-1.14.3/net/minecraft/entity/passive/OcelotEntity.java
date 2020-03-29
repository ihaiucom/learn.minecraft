package net.minecraft.entity.passive;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class OcelotEntity extends AnimalEntity {
   private static final Ingredient BREEDING_ITEMS;
   private static final DataParameter<Boolean> IS_TRUSTING;
   private OcelotEntity.AvoidEntityGoal<PlayerEntity> field_213531_bB;
   private OcelotEntity.TemptGoal field_70914_e;

   public OcelotEntity(EntityType<? extends OcelotEntity> p_i50254_1_, World p_i50254_2_) {
      super(p_i50254_1_, p_i50254_2_);
      this.func_213529_dV();
   }

   private boolean isTrusting() {
      return (Boolean)this.dataManager.get(IS_TRUSTING);
   }

   private void setTrusting(boolean p_213528_1_) {
      this.dataManager.set(IS_TRUSTING, p_213528_1_);
      this.func_213529_dV();
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("Trusting", this.isTrusting());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setTrusting(p_70037_1_.getBoolean("Trusting"));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(IS_TRUSTING, false);
   }

   protected void registerGoals() {
      this.field_70914_e = new OcelotEntity.TemptGoal(this, 0.6D, BREEDING_ITEMS, true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, this.field_70914_e);
      this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(9, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, ChickenEntity.class, false));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.TARGET_DRY_BABY));
   }

   public void updateAITasks() {
      if (this.getMoveHelper().isUpdating()) {
         double d0 = this.getMoveHelper().getSpeed();
         if (d0 == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (d0 == 1.33D) {
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

   public boolean canDespawn(double p_213397_1_) {
      return !this.isTrusting() && this.ticksExisted > 2400;
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

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_OCELOT_AMBIENT;
   }

   public int getTalkInterval() {
      return 900;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_OCELOT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_OCELOT_DEATH;
   }

   private float func_226517_es_() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_226517_es_());
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if ((this.field_70914_e == null || this.field_70914_e.isRunning()) && !this.isTrusting() && this.isBreedingItem(itemstack) && p_184645_1_.getDistanceSq(this) < 9.0D) {
         this.consumeItemFromStack(p_184645_1_, itemstack);
         if (!this.world.isRemote) {
            if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, p_184645_1_)) {
               this.setTrusting(true);
               this.func_213527_s(true);
               this.world.setEntityState(this, (byte)41);
            } else {
               this.func_213527_s(false);
               this.world.setEntityState(this, (byte)40);
            }
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 41) {
         this.func_213527_s(true);
      } else if (p_70103_1_ == 40) {
         this.func_213527_s(false);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   private void func_213527_s(boolean p_213527_1_) {
      IParticleData iparticledata = ParticleTypes.HEART;
      if (!p_213527_1_) {
         iparticledata = ParticleTypes.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(iparticledata, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), d0, d1, d2);
      }

   }

   protected void func_213529_dV() {
      if (this.field_213531_bB == null) {
         this.field_213531_bB = new OcelotEntity.AvoidEntityGoal(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.field_213531_bB);
      if (!this.isTrusting()) {
         this.goalSelector.addGoal(4, this.field_213531_bB);
      }

   }

   public OcelotEntity createChild(AgeableEntity p_90011_1_) {
      return (OcelotEntity)EntityType.OCELOT.create(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return BREEDING_ITEMS.test(p_70877_1_);
   }

   public static boolean func_223319_c(EntityType<OcelotEntity> p_223319_0_, IWorld p_223319_1_, SpawnReason p_223319_2_, BlockPos p_223319_3_, Random p_223319_4_) {
      return p_223319_4_.nextInt(3) != 0;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      if (p_205019_1_.func_226668_i_(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = new BlockPos(this);
         if (blockpos.getY() < p_205019_1_.getSeaLevel()) {
            return false;
         }

         BlockState blockstate = p_205019_1_.getBlockState(blockpos.down());
         Block block = blockstate.getBlock();
         if (block == Blocks.GRASS_BLOCK || blockstate.isIn(BlockTags.LEAVES)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226258_a_(1.0F);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   static {
      BREEDING_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON);
      IS_TRUSTING = EntityDataManager.createKey(OcelotEntity.class, DataSerializers.BOOLEAN);
   }

   static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
      private final OcelotEntity ocelot;

      public TemptGoal(OcelotEntity p_i50036_1_, double p_i50036_2_, Ingredient p_i50036_4_, boolean p_i50036_5_) {
         super(p_i50036_1_, p_i50036_2_, p_i50036_4_, p_i50036_5_);
         this.ocelot = p_i50036_1_;
      }

      protected boolean isScaredByPlayerMovement() {
         return super.isScaredByPlayerMovement() && !this.ocelot.isTrusting();
      }
   }

   static class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final OcelotEntity ocelot;

      public AvoidEntityGoal(OcelotEntity p_i50037_1_, Class<T> p_i50037_2_, float p_i50037_3_, double p_i50037_4_, double p_i50037_6_) {
         Predicate var10006 = EntityPredicates.CAN_AI_TARGET;
         super(p_i50037_1_, p_i50037_2_, p_i50037_3_, p_i50037_4_, p_i50037_6_, var10006::test);
         this.ocelot = p_i50037_1_;
      }

      public boolean shouldExecute() {
         return !this.ocelot.isTrusting() && super.shouldExecute();
      }

      public boolean shouldContinueExecuting() {
         return !this.ocelot.isTrusting() && super.shouldContinueExecuting();
      }
   }
}
