package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EndermanEntity extends MonsterEntity {
   private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier ATTACKING_SPEED_BOOST;
   private static final DataParameter<Optional<BlockState>> CARRIED_BLOCK;
   private static final DataParameter<Boolean> SCREAMING;
   private static final DataParameter<Boolean> field_226535_bx_;
   private static final Predicate<LivingEntity> field_213627_bA;
   private int field_226536_bz_ = Integer.MIN_VALUE;
   private int targetChangeTime;

   public EndermanEntity(EntityType<? extends EndermanEntity> p_i50210_1_, World p_i50210_2_) {
      super(p_i50210_1_, p_i50210_2_);
      this.stepHeight = 1.0F;
      this.setPathPriority(PathNodeType.WATER, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EndermanEntity.StareGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(10, new EndermanEntity.PlaceBlockGoal(this));
      this.goalSelector.addGoal(11, new EndermanEntity.TakeBlockGoal(this));
      this.targetSelector.addGoal(1, new EndermanEntity.FindPlayerGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, EndermiteEntity.class, 10, true, false, field_213627_bA));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   public void setAttackTarget(@Nullable LivingEntity p_70624_1_) {
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (p_70624_1_ == null) {
         this.targetChangeTime = 0;
         this.dataManager.set(SCREAMING, false);
         this.dataManager.set(field_226535_bx_, false);
         iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
      } else {
         this.targetChangeTime = this.ticksExisted;
         this.dataManager.set(SCREAMING, true);
         if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
            iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
         }
      }

      super.setAttackTarget(p_70624_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CARRIED_BLOCK, Optional.empty());
      this.dataManager.register(SCREAMING, false);
      this.dataManager.register(field_226535_bx_, false);
   }

   public void func_226539_l_() {
      if (this.ticksExisted >= this.field_226536_bz_ + 400) {
         this.field_226536_bz_ = this.ticksExisted;
         if (!this.isSilent()) {
            this.world.playSound(this.func_226277_ct_(), this.func_226280_cw_(), this.func_226281_cx_(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
         }
      }

   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (SCREAMING.equals(p_184206_1_) && this.func_226537_et_() && this.world.isRemote) {
         this.func_226539_l_();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      BlockState blockstate = this.getHeldBlockState();
      if (blockstate != null) {
         p_213281_1_.put("carriedBlockState", NBTUtil.writeBlockState(blockstate));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      BlockState blockstate = null;
      if (p_70037_1_.contains("carriedBlockState", 10)) {
         blockstate = NBTUtil.readBlockState(p_70037_1_.getCompound("carriedBlockState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      this.func_195406_b(blockstate);
   }

   private boolean shouldAttackPlayer(PlayerEntity p_70821_1_) {
      ItemStack itemstack = (ItemStack)p_70821_1_.inventory.armorInventory.get(3);
      if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
         return false;
      } else {
         Vec3d vec3d = p_70821_1_.getLook(1.0F).normalize();
         Vec3d vec3d1 = new Vec3d(this.func_226277_ct_() - p_70821_1_.func_226277_ct_(), this.func_226280_cw_() - p_70821_1_.func_226280_cw_(), this.func_226281_cx_() - p_70821_1_.func_226281_cx_());
         double d0 = vec3d1.length();
         vec3d1 = vec3d1.normalize();
         double d1 = vec3d.dotProduct(vec3d1);
         return d1 > 1.0D - 0.025D / d0 ? p_70821_1_.canEntityBeSeen(this) : false;
      }
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 2.55F;
   }

   public void livingTick() {
      if (this.world.isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.world.addParticle(ParticleTypes.PORTAL, this.func_226282_d_(0.5D), this.func_226279_cv_() - 0.25D, this.func_226287_g_(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.isJumping = false;
      super.livingTick();
   }

   protected void updateAITasks() {
      if (this.isInWaterRainOrBubbleColumn()) {
         this.attackEntityFrom(DamageSource.DROWN, 1.0F);
      }

      if (this.world.isDaytime() && this.ticksExisted >= this.targetChangeTime + 600) {
         float f = this.getBrightness();
         if (f > 0.5F && this.world.func_226660_f_(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
            this.setAttackTarget((LivingEntity)null);
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   protected boolean teleportRandomly() {
      if (!this.world.isRemote() && this.isAlive()) {
         double d0 = this.func_226277_ct_() + (this.rand.nextDouble() - 0.5D) * 64.0D;
         double d1 = this.func_226278_cu_() + (double)(this.rand.nextInt(64) - 32);
         double d2 = this.func_226281_cx_() + (this.rand.nextDouble() - 0.5D) * 64.0D;
         return this.teleportTo(d0, d1, d2);
      } else {
         return false;
      }
   }

   private boolean teleportToEntity(Entity p_70816_1_) {
      Vec3d vec3d = new Vec3d(this.func_226277_ct_() - p_70816_1_.func_226277_ct_(), this.func_226283_e_(0.5D) - p_70816_1_.func_226280_cw_(), this.func_226281_cx_() - p_70816_1_.func_226281_cx_());
      vec3d = vec3d.normalize();
      double d0 = 16.0D;
      double d1 = this.func_226277_ct_() + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
      double d2 = this.func_226278_cu_() + (double)(this.rand.nextInt(16) - 8) - vec3d.y * 16.0D;
      double d3 = this.func_226281_cx_() + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
      return this.teleportTo(d1, d2, d3);
   }

   private boolean teleportTo(double p_70825_1_, double p_70825_3_, double p_70825_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_70825_1_, p_70825_3_, p_70825_5_);

      while(blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
         blockpos$mutable.move(Direction.DOWN);
      }

      BlockState blockstate = this.world.getBlockState(blockpos$mutable);
      boolean flag = blockstate.getMaterial().blocksMovement();
      boolean flag1 = blockstate.getFluidState().isTagged(FluidTags.WATER);
      if (flag && !flag1) {
         EnderTeleportEvent event = new EnderTeleportEvent(this, p_70825_1_, p_70825_3_, p_70825_5_, 0.0F);
         if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
         } else {
            boolean flag2 = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
               this.world.playSound((PlayerEntity)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
               this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
         }
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isScreaming() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMAN_DEATH;
   }

   protected void dropSpecialItems(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropSpecialItems(p_213333_1_, p_213333_2_, p_213333_3_);
      BlockState blockstate = this.getHeldBlockState();
      if (blockstate != null) {
         this.entityDropItem(blockstate.getBlock());
      }

   }

   public void func_195406_b(@Nullable BlockState p_195406_1_) {
      this.dataManager.set(CARRIED_BLOCK, Optional.ofNullable(p_195406_1_));
   }

   @Nullable
   public BlockState getHeldBlockState() {
      return (BlockState)((Optional)this.dataManager.get(CARRIED_BLOCK)).orElse((BlockState)null);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!(p_70097_1_ instanceof IndirectEntityDamageSource) && p_70097_1_ != DamageSource.FIREWORKS) {
         boolean flag = super.attackEntityFrom(p_70097_1_, p_70097_2_);
         if (!this.world.isRemote() && p_70097_1_.isUnblockable() && this.rand.nextInt(10) != 0) {
            this.teleportRandomly();
         }

         return flag;
      } else {
         for(int i = 0; i < 64; ++i) {
            if (this.teleportRandomly()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isScreaming() {
      return (Boolean)this.dataManager.get(SCREAMING);
   }

   public boolean func_226537_et_() {
      return (Boolean)this.dataManager.get(field_226535_bx_);
   }

   public void func_226538_eu_() {
      this.dataManager.set(field_226535_bx_, true);
   }

   static {
      ATTACKING_SPEED_BOOST = (new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION)).setSaved(false);
      CARRIED_BLOCK = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
      SCREAMING = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.BOOLEAN);
      field_226535_bx_ = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.BOOLEAN);
      field_213627_bA = (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_ instanceof EndermiteEntity && ((EndermiteEntity)p_lambda$static$0_0_).isSpawnedByPlayer();
      };
   }

   static class TakeBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public TakeBlockGoal(EndermanEntity p_i45841_1_) {
         this.enderman = p_i45841_1_;
      }

      public boolean shouldExecute() {
         if (this.enderman.getHeldBlockState() != null) {
            return false;
         } else if (!ForgeEventFactory.getMobGriefingEvent(this.enderman.world, this.enderman)) {
            return false;
         } else {
            return this.enderman.getRNG().nextInt(20) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.world;
         int i = MathHelper.floor(this.enderman.func_226277_ct_() - 2.0D + random.nextDouble() * 4.0D);
         int j = MathHelper.floor(this.enderman.func_226278_cu_() + random.nextDouble() * 3.0D);
         int k = MathHelper.floor(this.enderman.func_226281_cx_() - 2.0D + random.nextDouble() * 4.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = world.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         Vec3d vec3d = new Vec3d((double)MathHelper.floor(this.enderman.func_226277_ct_()) + 0.5D, (double)j + 0.5D, (double)MathHelper.floor(this.enderman.func_226281_cx_()) + 0.5D);
         Vec3d vec3d1 = new Vec3d((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
         BlockRayTraceResult blockraytraceresult = world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, this.enderman));
         boolean flag = blockraytraceresult.getPos().equals(blockpos);
         if (block.isIn(BlockTags.ENDERMAN_HOLDABLE) && flag) {
            this.enderman.func_195406_b(blockstate);
            world.removeBlock(blockpos, false);
         }

      }
   }

   static class StareGoal extends Goal {
      private final EndermanEntity field_220835_a;
      private LivingEntity field_226540_b_;

      public StareGoal(EndermanEntity p_i50520_1_) {
         this.field_220835_a = p_i50520_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         this.field_226540_b_ = this.field_220835_a.getAttackTarget();
         if (!(this.field_226540_b_ instanceof PlayerEntity)) {
            return false;
         } else {
            double d0 = this.field_226540_b_.getDistanceSq(this.field_220835_a);
            return d0 > 256.0D ? false : this.field_220835_a.shouldAttackPlayer((PlayerEntity)this.field_226540_b_);
         }
      }

      public void startExecuting() {
         this.field_220835_a.getNavigator().clearPath();
      }

      public void tick() {
         this.field_220835_a.getLookController().func_220679_a(this.field_226540_b_.func_226277_ct_(), this.field_226540_b_.func_226280_cw_(), this.field_226540_b_.func_226281_cx_());
      }
   }

   static class PlaceBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public PlaceBlockGoal(EndermanEntity p_i45843_1_) {
         this.enderman = p_i45843_1_;
      }

      public boolean shouldExecute() {
         if (this.enderman.getHeldBlockState() == null) {
            return false;
         } else if (!ForgeEventFactory.getMobGriefingEvent(this.enderman.world, this.enderman)) {
            return false;
         } else {
            return this.enderman.getRNG().nextInt(2000) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRNG();
         IWorld iworld = this.enderman.world;
         int i = MathHelper.floor(this.enderman.func_226277_ct_() - 1.0D + random.nextDouble() * 2.0D);
         int j = MathHelper.floor(this.enderman.func_226278_cu_() + random.nextDouble() * 2.0D);
         int k = MathHelper.floor(this.enderman.func_226281_cx_() - 1.0D + random.nextDouble() * 2.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = iworld.getBlockState(blockpos);
         BlockPos blockpos1 = blockpos.down();
         BlockState blockstate1 = iworld.getBlockState(blockpos1);
         BlockState blockstate2 = this.enderman.getHeldBlockState();
         if (blockstate2 != null && this.func_220836_a(iworld, blockpos, blockstate2, blockstate, blockstate1, blockpos1) && !ForgeEventFactory.onBlockPlace(this.enderman, new BlockSnapshot(iworld, blockpos, blockstate1), Direction.UP)) {
            iworld.setBlockState(blockpos, blockstate2, 3);
            this.enderman.func_195406_b((BlockState)null);
         }

      }

      private boolean func_220836_a(IWorldReader p_220836_1_, BlockPos p_220836_2_, BlockState p_220836_3_, BlockState p_220836_4_, BlockState p_220836_5_, BlockPos p_220836_6_) {
         return p_220836_4_.isAir(p_220836_1_, p_220836_2_) && !p_220836_5_.isAir(p_220836_1_, p_220836_6_) && p_220836_5_.func_224756_o(p_220836_1_, p_220836_6_) && p_220836_3_.isValidPosition(p_220836_1_, p_220836_2_);
      }
   }

   static class FindPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      private final EndermanEntity enderman;
      private PlayerEntity player;
      private int aggroTime;
      private int teleportTime;
      private final EntityPredicate field_220791_m;
      private final EntityPredicate field_220792_n = (new EntityPredicate()).setLineOfSiteRequired();

      public FindPlayerGoal(EndermanEntity p_i45842_1_) {
         super(p_i45842_1_, PlayerEntity.class, false);
         this.enderman = p_i45842_1_;
         this.field_220791_m = (new EntityPredicate()).setDistance(this.getTargetDistance()).setCustomPredicate((p_lambda$new$0_1_) -> {
            return p_i45842_1_.shouldAttackPlayer((PlayerEntity)p_lambda$new$0_1_);
         });
      }

      public boolean shouldExecute() {
         this.player = this.enderman.world.getClosestPlayer(this.field_220791_m, this.enderman);
         return this.player != null;
      }

      public void startExecuting() {
         this.aggroTime = 5;
         this.teleportTime = 0;
         this.enderman.func_226538_eu_();
      }

      public void resetTask() {
         this.player = null;
         super.resetTask();
      }

      public boolean shouldContinueExecuting() {
         if (this.player != null) {
            if (!this.enderman.shouldAttackPlayer(this.player)) {
               return false;
            } else {
               this.enderman.faceEntity(this.player, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.nearestTarget != null && this.field_220792_n.canTarget(this.enderman, this.nearestTarget) ? true : super.shouldContinueExecuting();
         }
      }

      public void tick() {
         if (this.player != null) {
            if (--this.aggroTime <= 0) {
               this.nearestTarget = this.player;
               this.player = null;
               super.startExecuting();
            }
         } else {
            if (this.nearestTarget != null && !this.enderman.isPassenger()) {
               if (this.enderman.shouldAttackPlayer((PlayerEntity)this.nearestTarget)) {
                  if (this.nearestTarget.getDistanceSq(this.enderman) < 16.0D) {
                     this.enderman.teleportRandomly();
                  }

                  this.teleportTime = 0;
               } else if (this.nearestTarget.getDistanceSq(this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.enderman.teleportToEntity(this.nearestTarget)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }

      }
   }
}
