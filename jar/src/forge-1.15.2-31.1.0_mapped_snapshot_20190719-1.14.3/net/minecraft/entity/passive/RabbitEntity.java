package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class RabbitEntity extends AnimalEntity {
   private static final DataParameter<Integer> RABBIT_TYPE;
   private static final ResourceLocation KILLER_BUNNY;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;
   private int carrotTicks;

   public RabbitEntity(EntityType<? extends RabbitEntity> p_i50247_1_, World p_i50247_2_) {
      super(p_i50247_1_, p_i50247_2_);
      this.jumpController = new RabbitEntity.JumpHelperController(this);
      this.moveController = new RabbitEntity.MoveHelperController(this);
      this.setMovementSpeed(0.0D);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(1, new RabbitEntity.PanicGoal(this, 2.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal(this, PlayerEntity.class, 8.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal(this, WolfEntity.class, 10.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal(this, MonsterEntity.class, 4.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(5, new RabbitEntity.RaidFarmGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
   }

   protected float getJumpUpwardsMotion() {
      if (this.collidedHorizontally || this.moveController.isUpdating() && this.moveController.getY() > this.func_226278_cu_() + 0.5D) {
         return 0.5F;
      } else {
         Path path = this.navigator.getPath();
         if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
            Vec3d vec3d = path.getPosition(this);
            if (vec3d.y > this.func_226278_cu_() + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveController.getSpeed() <= 0.6D ? 0.2F : 0.3F;
      }
   }

   protected void jump() {
      super.jump();
      double d0 = this.moveController.getSpeed();
      if (d0 > 0.0D) {
         double d1 = func_213296_b(this.getMotion());
         if (d1 < 0.01D) {
            this.moveRelative(0.1F, new Vec3d(0.0D, 0.0D, 1.0D));
         }
      }

      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getJumpCompletion(float p_175521_1_) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
   }

   public void setMovementSpeed(double p_175515_1_) {
      this.getNavigator().setSpeed(p_175515_1_);
      this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), p_175515_1_);
   }

   public void setJumping(boolean p_70637_1_) {
      super.setJumping(p_70637_1_);
      if (p_70637_1_) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(RABBIT_TYPE, 0);
   }

   public void updateAITasks() {
      if (this.currentMoveTypeDuration > 0) {
         --this.currentMoveTypeDuration;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks -= this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            LivingEntity livingentity = this.getAttackTarget();
            if (livingentity != null && this.getDistanceSq(livingentity) < 16.0D) {
               this.calculateRotationYaw(livingentity.func_226277_ct_(), livingentity.func_226281_cx_());
               this.moveController.setMoveTo(livingentity.func_226277_ct_(), livingentity.func_226278_cu_(), livingentity.func_226281_cx_(), this.moveController.getSpeed());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         RabbitEntity.JumpHelperController rabbitentity$jumphelpercontroller = (RabbitEntity.JumpHelperController)this.jumpController;
         if (!rabbitentity$jumphelpercontroller.getIsJumping()) {
            if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0) {
               Path path = this.navigator.getPath();
               Vec3d vec3d = new Vec3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());
               if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                  vec3d = path.getPosition(this);
               }

               this.calculateRotationYaw(vec3d.x, vec3d.z);
               this.startJumping();
            }
         } else if (!rabbitentity$jumphelpercontroller.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public void spawnRunningParticles() {
   }

   private void calculateRotationYaw(double p_175533_1_, double p_175533_3_) {
      this.rotationYaw = (float)(MathHelper.atan2(p_175533_3_ - this.func_226281_cx_(), p_175533_1_ - this.func_226277_ct_()) * 57.2957763671875D) - 90.0F;
   }

   private void enableJumpControl() {
      ((RabbitEntity.JumpHelperController)this.jumpController).setCanJump(true);
   }

   private void disableJumpControl() {
      ((RabbitEntity.JumpHelperController)this.jumpController).setCanJump(false);
   }

   private void updateMoveTypeDuration() {
      if (this.moveController.getSpeed() < 2.2D) {
         this.currentMoveTypeDuration = 10;
      } else {
         this.currentMoveTypeDuration = 1;
      }

   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   public void livingTick() {
      super.livingTick();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("RabbitType", this.getRabbitType());
      p_213281_1_.putInt("MoreCarrotTicks", this.carrotTicks);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setRabbitType(p_70037_1_.getInt("RabbitType"));
      this.carrotTicks = p_70037_1_.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RABBIT_DEATH;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   public SoundCategory getSoundCategory() {
      return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   private boolean isRabbitBreedingItem(Item p_175525_1_) {
      return p_175525_1_ == Items.CARROT || p_175525_1_ == Items.GOLDEN_CARROT || p_175525_1_ == Blocks.DANDELION.asItem();
   }

   public RabbitEntity createChild(AgeableEntity p_90011_1_) {
      RabbitEntity rabbitentity = (RabbitEntity)EntityType.RABBIT.create(this.world);
      int i = this.func_213610_a(this.world);
      if (this.rand.nextInt(20) != 0) {
         if (p_90011_1_ instanceof RabbitEntity && this.rand.nextBoolean()) {
            i = ((RabbitEntity)p_90011_1_).getRabbitType();
         } else {
            i = this.getRabbitType();
         }
      }

      rabbitentity.setRabbitType(i);
      return rabbitentity;
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return this.isRabbitBreedingItem(p_70877_1_.getItem());
   }

   public int getRabbitType() {
      return (Integer)this.dataManager.get(RABBIT_TYPE);
   }

   public void setRabbitType(int p_175529_1_) {
      if (p_175529_1_ == 99) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
         this.goalSelector.addGoal(4, new RabbitEntity.EvilAttackGoal(this));
         this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp());
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, WolfEntity.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TranslationTextComponent(Util.makeTranslationKey("entity", KILLER_BUNNY), new Object[0]));
         }
      }

      this.dataManager.set(RABBIT_TYPE, p_175529_1_);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      int i = this.func_213610_a(p_213386_1_);
      if (p_213386_4_ instanceof RabbitEntity.RabbitData) {
         i = ((RabbitEntity.RabbitData)p_213386_4_).typeData;
      } else {
         p_213386_4_ = new RabbitEntity.RabbitData(i);
      }

      this.setRabbitType(i);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   private int func_213610_a(IWorld p_213610_1_) {
      Biome biome = p_213610_1_.func_226691_t_(new BlockPos(this));
      int i = this.rand.nextInt(100);
      if (biome.getPrecipitation() == Biome.RainType.SNOW) {
         return i < 80 ? 1 : 3;
      } else if (biome.getCategory() == Biome.Category.DESERT) {
         return 4;
      } else {
         return i < 50 ? 0 : (i < 90 ? 5 : 2);
      }
   }

   public static boolean func_223321_c(EntityType<RabbitEntity> p_223321_0_, IWorld p_223321_1_, SpawnReason p_223321_2_, BlockPos p_223321_3_, Random p_223321_4_) {
      Block block = p_223321_1_.getBlockState(p_223321_3_.down()).getBlock();
      return (block == Blocks.GRASS_BLOCK || block == Blocks.SNOW || block == Blocks.SAND) && p_223321_1_.func_226659_b_(p_223321_3_, 0) > 8;
   }

   private boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 1) {
         this.createRunningParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   static {
      RABBIT_TYPE = EntityDataManager.createKey(RabbitEntity.class, DataSerializers.VARINT);
      KILLER_BUNNY = new ResourceLocation("killer_bunny");
   }

   static class RaidFarmGoal extends MoveToBlockGoal {
      private final RabbitEntity rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public RaidFarmGoal(RabbitEntity p_i45860_1_) {
         super(p_i45860_1_, 0.699999988079071D, 16);
         this.rabbit = p_i45860_1_;
      }

      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!ForgeEventFactory.getMobGriefingEvent(this.rabbit.world, this.rabbit)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.isCarrotEaten();
            this.wantsToRaid = true;
         }

         return super.shouldExecute();
      }

      public boolean shouldContinueExecuting() {
         return this.canRaid && super.shouldContinueExecuting();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookController().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.rabbit.getVerticalFaceSpeed());
         if (this.getIsAboveDestination()) {
            World world = this.rabbit.world;
            BlockPos blockpos = this.destinationBlock.up();
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (this.canRaid && block instanceof CarrotBlock) {
               Integer integer = (Integer)blockstate.get(CarrotBlock.AGE);
               if (integer == 0) {
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                  world.func_225521_a_(blockpos, true, this.rabbit);
               } else {
                  world.setBlockState(blockpos, (BlockState)blockstate.with(CarrotBlock.AGE, integer - 1), 2);
                  world.playEvent(2001, blockpos, Block.getStateId(blockstate));
               }

               this.rabbit.carrotTicks = 40;
            }

            this.canRaid = false;
            this.runDelay = 10;
         }

      }

      protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
         Block block = p_179488_1_.getBlockState(p_179488_2_).getBlock();
         if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            p_179488_2_ = p_179488_2_.up();
            BlockState blockstate = p_179488_1_.getBlockState(p_179488_2_);
            block = blockstate.getBlock();
            if (block instanceof CarrotBlock && ((CarrotBlock)block).isMaxAge(blockstate)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }

   public static class RabbitData extends AgeableEntity.AgeableData {
      public final int typeData;

      public RabbitData(int p_i45864_1_) {
         this.typeData = p_i45864_1_;
         this.func_226258_a_(1.0F);
      }
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      private final RabbitEntity rabbit;

      public PanicGoal(RabbitEntity p_i45861_1_, double p_i45861_2_) {
         super(p_i45861_1_, p_i45861_2_);
         this.rabbit = p_i45861_1_;
      }

      public void tick() {
         super.tick();
         this.rabbit.setMovementSpeed(this.speed);
      }
   }

   static class MoveHelperController extends MovementController {
      private final RabbitEntity rabbit;
      private double nextJumpSpeed;

      public MoveHelperController(RabbitEntity p_i45862_1_) {
         super(p_i45862_1_);
         this.rabbit = p_i45862_1_;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.isJumping && !((RabbitEntity.JumpHelperController)this.rabbit.jumpController).getIsJumping()) {
            this.rabbit.setMovementSpeed(0.0D);
         } else if (this.isUpdating()) {
            this.rabbit.setMovementSpeed(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
         if (this.rabbit.isInWater()) {
            p_75642_7_ = 1.5D;
         }

         super.setMoveTo(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
         if (p_75642_7_ > 0.0D) {
            this.nextJumpSpeed = p_75642_7_;
         }

      }
   }

   public class JumpHelperController extends JumpController {
      private final RabbitEntity rabbit;
      private boolean canJump;

      public JumpHelperController(RabbitEntity p_i45863_2_) {
         super(p_i45863_2_);
         this.rabbit = p_i45863_2_;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean p_180066_1_) {
         this.canJump = p_180066_1_;
      }

      public void tick() {
         if (this.isJumping) {
            this.rabbit.startJumping();
            this.isJumping = false;
         }

      }
   }

   static class EvilAttackGoal extends MeleeAttackGoal {
      public EvilAttackGoal(RabbitEntity p_i45867_1_) {
         super(p_i45867_1_, 1.4D, true);
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         return (double)(4.0F + p_179512_1_.getWidth());
      }
   }

   static class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final RabbitEntity rabbit;

      public AvoidEntityGoal(RabbitEntity p_i46403_1_, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
         super(p_i46403_1_, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
         this.rabbit = p_i46403_1_;
      }

      public boolean shouldExecute() {
         return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
      }
   }
}
