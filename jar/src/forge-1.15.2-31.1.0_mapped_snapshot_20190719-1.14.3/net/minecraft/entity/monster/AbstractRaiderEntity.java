package net.minecraft.entity.monster;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRaidGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractRaiderEntity extends PatrollerEntity {
   protected static final DataParameter<Boolean> field_213666_c;
   private static final Predicate<ItemEntity> field_213665_b;
   @Nullable
   protected Raid raid;
   private int wave;
   private boolean canJoinRaid;
   private int field_213664_bB;

   protected AbstractRaiderEntity(EntityType<? extends AbstractRaiderEntity> p_i50143_1_, World p_i50143_2_) {
      super(p_i50143_1_, p_i50143_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new AbstractRaiderEntity.PromoteLeaderGoal(this));
      this.goalSelector.addGoal(3, new MoveTowardsRaidGoal(this));
      this.goalSelector.addGoal(4, new AbstractRaiderEntity.InvadeHomeGoal(this, 1.0499999523162842D, 1));
      this.goalSelector.addGoal(5, new AbstractRaiderEntity.CelebrateRaidLossGoal(this));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(field_213666_c, false);
   }

   public abstract void func_213660_a(int var1, boolean var2);

   public boolean func_213658_ej() {
      return this.canJoinRaid;
   }

   public void func_213644_t(boolean p_213644_1_) {
      this.canJoinRaid = p_213644_1_;
   }

   public void livingTick() {
      if (this.world instanceof ServerWorld && this.isAlive()) {
         Raid lvt_1_1_ = this.getRaid();
         if (this.func_213658_ej()) {
            if (lvt_1_1_ == null) {
               if (this.world.getGameTime() % 20L == 0L) {
                  Raid lvt_2_1_ = ((ServerWorld)this.world).findRaid(new BlockPos(this));
                  if (lvt_2_1_ != null && RaidManager.func_215165_a(this, lvt_2_1_)) {
                     lvt_2_1_.func_221317_a(lvt_2_1_.func_221315_l(), this, (BlockPos)null, true);
                  }
               }
            } else {
               LivingEntity lvt_2_2_ = this.getAttackTarget();
               if (lvt_2_2_ != null && (lvt_2_2_.getType() == EntityType.PLAYER || lvt_2_2_.getType() == EntityType.IRON_GOLEM)) {
                  this.idleTime = 0;
               }
            }
         }
      }

      super.livingTick();
   }

   protected void func_213623_ec() {
      this.idleTime += 2;
   }

   public void onDeath(DamageSource p_70645_1_) {
      if (this.world instanceof ServerWorld) {
         Entity lvt_2_1_ = p_70645_1_.getTrueSource();
         Raid lvt_3_1_ = this.getRaid();
         if (lvt_3_1_ != null) {
            if (this.isLeader()) {
               lvt_3_1_.removeLeader(this.func_213642_em());
            }

            if (lvt_2_1_ != null && lvt_2_1_.getType() == EntityType.PLAYER) {
               lvt_3_1_.addHero(lvt_2_1_);
            }

            lvt_3_1_.leaveRaid(this, false);
         }

         if (this.isLeader() && lvt_3_1_ == null && ((ServerWorld)this.world).findRaid(new BlockPos(this)) == null) {
            ItemStack lvt_4_1_ = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
            PlayerEntity lvt_5_1_ = null;
            if (lvt_2_1_ instanceof PlayerEntity) {
               lvt_5_1_ = (PlayerEntity)lvt_2_1_;
            } else if (lvt_2_1_ instanceof WolfEntity) {
               WolfEntity lvt_7_1_ = (WolfEntity)lvt_2_1_;
               LivingEntity lvt_8_1_ = lvt_7_1_.getOwner();
               if (lvt_7_1_.isTamed() && lvt_8_1_ instanceof PlayerEntity) {
                  lvt_5_1_ = (PlayerEntity)lvt_8_1_;
               }
            }

            if (!lvt_4_1_.isEmpty() && ItemStack.areItemStacksEqual(lvt_4_1_, Raid.createIllagerBanner()) && lvt_5_1_ != null) {
               EffectInstance lvt_7_2_ = lvt_5_1_.getActivePotionEffect(Effects.BAD_OMEN);
               int lvt_8_2_ = 1;
               int lvt_8_2_;
               if (lvt_7_2_ != null) {
                  lvt_8_2_ = lvt_8_2_ + lvt_7_2_.getAmplifier();
                  lvt_5_1_.removeActivePotionEffect(Effects.BAD_OMEN);
               } else {
                  lvt_8_2_ = lvt_8_2_ - 1;
               }

               lvt_8_2_ = MathHelper.clamp(lvt_8_2_, 0, 5);
               EffectInstance lvt_9_1_ = new EffectInstance(Effects.BAD_OMEN, 120000, lvt_8_2_, false, false, true);
               if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                  lvt_5_1_.addPotionEffect(lvt_9_1_);
               }
            }
         }
      }

      super.onDeath(p_70645_1_);
   }

   public boolean func_213634_ed() {
      return !this.isRaidActive();
   }

   public void setRaid(@Nullable Raid p_213652_1_) {
      this.raid = p_213652_1_;
   }

   @Nullable
   public Raid getRaid() {
      return this.raid;
   }

   public boolean isRaidActive() {
      return this.getRaid() != null && this.getRaid().isActive();
   }

   public void setWave(int p_213651_1_) {
      this.wave = p_213651_1_;
   }

   public int func_213642_em() {
      return this.wave;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_213656_en() {
      return (Boolean)this.dataManager.get(field_213666_c);
   }

   public void func_213655_u(boolean p_213655_1_) {
      this.dataManager.set(field_213666_c, p_213655_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Wave", this.wave);
      p_213281_1_.putBoolean("CanJoinRaid", this.canJoinRaid);
      if (this.raid != null) {
         p_213281_1_.putInt("RaidId", this.raid.getId());
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.wave = p_70037_1_.getInt("Wave");
      this.canJoinRaid = p_70037_1_.getBoolean("CanJoinRaid");
      if (p_70037_1_.contains("RaidId", 3)) {
         if (this.world instanceof ServerWorld) {
            this.raid = ((ServerWorld)this.world).getRaids().func_215167_a(p_70037_1_.getInt("RaidId"));
         }

         if (this.raid != null) {
            this.raid.joinRaid(this.wave, this, false);
            if (this.isLeader()) {
               this.raid.setLeader(this.wave, this);
            }
         }
      }

   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      ItemStack lvt_2_1_ = p_175445_1_.getItem();
      boolean lvt_3_1_ = this.isRaidActive() && this.getRaid().getLeader(this.func_213642_em()) != null;
      if (this.isRaidActive() && !lvt_3_1_ && ItemStack.areItemStacksEqual(lvt_2_1_, Raid.createIllagerBanner())) {
         EquipmentSlotType lvt_4_1_ = EquipmentSlotType.HEAD;
         ItemStack lvt_5_1_ = this.getItemStackFromSlot(lvt_4_1_);
         double lvt_6_1_ = (double)this.getDropChance(lvt_4_1_);
         if (!lvt_5_1_.isEmpty() && (double)Math.max(this.rand.nextFloat() - 0.1F, 0.0F) < lvt_6_1_) {
            this.entityDropItem(lvt_5_1_);
         }

         this.setItemStackToSlot(lvt_4_1_, lvt_2_1_);
         this.onItemPickup(p_175445_1_, lvt_2_1_.getCount());
         p_175445_1_.remove();
         this.getRaid().setLeader(this.func_213642_em(), this);
         this.setLeader(true);
      } else {
         super.updateEquipmentIfNeeded(p_175445_1_);
      }

   }

   public boolean canDespawn(double p_213397_1_) {
      return this.getRaid() == null ? super.canDespawn(p_213397_1_) : false;
   }

   public boolean preventDespawn() {
      return this.getRaid() != null;
   }

   public int func_213661_eo() {
      return this.field_213664_bB;
   }

   public void func_213653_b(int p_213653_1_) {
      this.field_213664_bB = p_213653_1_;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isRaidActive()) {
         this.getRaid().updateBarPercentage();
      }

      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.func_213644_t(this.getType() != EntityType.WITCH || p_213386_3_ != SpawnReason.NATURAL);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public abstract SoundEvent getRaidLossSound();

   static {
      field_213666_c = EntityDataManager.createKey(AbstractRaiderEntity.class, DataSerializers.BOOLEAN);
      field_213665_b = (p_213647_0_) -> {
         return !p_213647_0_.cannotPickup() && p_213647_0_.isAlive() && ItemStack.areItemStacksEqual(p_213647_0_.getItem(), Raid.createIllagerBanner());
      };
   }

   static class InvadeHomeGoal extends Goal {
      private final AbstractRaiderEntity field_220864_a;
      private final double field_220865_b;
      private BlockPos field_220866_c;
      private final List<BlockPos> field_220867_d = Lists.newArrayList();
      private final int field_220868_e;
      private boolean field_220869_f;

      public InvadeHomeGoal(AbstractRaiderEntity p_i50570_1_, double p_i50570_2_, int p_i50570_4_) {
         this.field_220864_a = p_i50570_1_;
         this.field_220865_b = p_i50570_2_;
         this.field_220868_e = p_i50570_4_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         this.func_220861_j();
         return this.func_220862_g() && this.func_220863_h() && this.field_220864_a.getAttackTarget() == null;
      }

      private boolean func_220862_g() {
         return this.field_220864_a.isRaidActive() && !this.field_220864_a.getRaid().func_221319_a();
      }

      private boolean func_220863_h() {
         ServerWorld lvt_1_1_ = (ServerWorld)this.field_220864_a.world;
         BlockPos lvt_2_1_ = new BlockPos(this.field_220864_a);
         Optional<BlockPos> lvt_3_1_ = lvt_1_1_.func_217443_B().func_219163_a((p_220859_0_) -> {
            return p_220859_0_ == PointOfInterestType.HOME;
         }, this::func_220860_a, PointOfInterestManager.Status.ANY, lvt_2_1_, 48, this.field_220864_a.rand);
         if (!lvt_3_1_.isPresent()) {
            return false;
         } else {
            this.field_220866_c = ((BlockPos)lvt_3_1_.get()).toImmutable();
            return true;
         }
      }

      public boolean shouldContinueExecuting() {
         if (this.field_220864_a.getNavigator().noPath()) {
            return false;
         } else {
            return this.field_220864_a.getAttackTarget() == null && !this.field_220866_c.withinDistance(this.field_220864_a.getPositionVec(), (double)(this.field_220864_a.getWidth() + (float)this.field_220868_e)) && !this.field_220869_f;
         }
      }

      public void resetTask() {
         if (this.field_220866_c.withinDistance(this.field_220864_a.getPositionVec(), (double)this.field_220868_e)) {
            this.field_220867_d.add(this.field_220866_c);
         }

      }

      public void startExecuting() {
         super.startExecuting();
         this.field_220864_a.setIdleTime(0);
         this.field_220864_a.getNavigator().tryMoveToXYZ((double)this.field_220866_c.getX(), (double)this.field_220866_c.getY(), (double)this.field_220866_c.getZ(), this.field_220865_b);
         this.field_220869_f = false;
      }

      public void tick() {
         if (this.field_220864_a.getNavigator().noPath()) {
            Vec3d lvt_1_1_ = new Vec3d(this.field_220866_c);
            Vec3d lvt_2_1_ = RandomPositionGenerator.findRandomTargetTowardsScaled(this.field_220864_a, 16, 7, lvt_1_1_, 0.3141592741012573D);
            if (lvt_2_1_ == null) {
               lvt_2_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_220864_a, 8, 7, lvt_1_1_);
            }

            if (lvt_2_1_ == null) {
               this.field_220869_f = true;
               return;
            }

            this.field_220864_a.getNavigator().tryMoveToXYZ(lvt_2_1_.x, lvt_2_1_.y, lvt_2_1_.z, this.field_220865_b);
         }

      }

      private boolean func_220860_a(BlockPos p_220860_1_) {
         Iterator var2 = this.field_220867_d.iterator();

         BlockPos lvt_3_1_;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            lvt_3_1_ = (BlockPos)var2.next();
         } while(!Objects.equals(p_220860_1_, lvt_3_1_));

         return false;
      }

      private void func_220861_j() {
         if (this.field_220867_d.size() > 2) {
            this.field_220867_d.remove(0);
         }

      }
   }

   public class FindTargetGoal extends Goal {
      private final AbstractRaiderEntity field_220853_c;
      private final float field_220854_d;
      public final EntityPredicate field_220851_a = (new EntityPredicate()).setDistance(8.0D).setSkipAttackChecks().allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setUseInvisibilityCheck();

      public FindTargetGoal(AbstractIllagerEntity p_i50573_2_, float p_i50573_3_) {
         this.field_220853_c = p_i50573_2_;
         this.field_220854_d = p_i50573_3_ * p_i50573_3_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = this.field_220853_c.getRevengeTarget();
         return this.field_220853_c.getRaid() == null && this.field_220853_c.isPatrolling() && this.field_220853_c.getAttackTarget() != null && !this.field_220853_c.isAggressive() && (lvt_1_1_ == null || lvt_1_1_.getType() != EntityType.PLAYER);
      }

      public void startExecuting() {
         super.startExecuting();
         this.field_220853_c.getNavigator().clearPath();
         List<AbstractRaiderEntity> lvt_1_1_ = this.field_220853_c.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220851_a, this.field_220853_c, this.field_220853_c.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
         Iterator var2 = lvt_1_1_.iterator();

         while(var2.hasNext()) {
            AbstractRaiderEntity lvt_3_1_ = (AbstractRaiderEntity)var2.next();
            lvt_3_1_.setAttackTarget(this.field_220853_c.getAttackTarget());
         }

      }

      public void resetTask() {
         super.resetTask();
         LivingEntity lvt_1_1_ = this.field_220853_c.getAttackTarget();
         if (lvt_1_1_ != null) {
            List<AbstractRaiderEntity> lvt_2_1_ = this.field_220853_c.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220851_a, this.field_220853_c, this.field_220853_c.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
            Iterator var3 = lvt_2_1_.iterator();

            while(var3.hasNext()) {
               AbstractRaiderEntity lvt_4_1_ = (AbstractRaiderEntity)var3.next();
               lvt_4_1_.setAttackTarget(lvt_1_1_);
               lvt_4_1_.setAggroed(true);
            }

            this.field_220853_c.setAggroed(true);
         }

      }

      public void tick() {
         LivingEntity lvt_1_1_ = this.field_220853_c.getAttackTarget();
         if (lvt_1_1_ != null) {
            if (this.field_220853_c.getDistanceSq(lvt_1_1_) > (double)this.field_220854_d) {
               this.field_220853_c.getLookController().setLookPositionWithEntity(lvt_1_1_, 30.0F, 30.0F);
               if (this.field_220853_c.rand.nextInt(50) == 0) {
                  this.field_220853_c.playAmbientSound();
               }
            } else {
               this.field_220853_c.setAggroed(true);
            }

            super.tick();
         }
      }
   }

   public class CelebrateRaidLossGoal extends Goal {
      private final AbstractRaiderEntity field_220858_b;

      CelebrateRaidLossGoal(AbstractRaiderEntity p_i50571_2_) {
         this.field_220858_b = p_i50571_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         Raid lvt_1_1_ = this.field_220858_b.getRaid();
         return this.field_220858_b.isAlive() && this.field_220858_b.getAttackTarget() == null && lvt_1_1_ != null && lvt_1_1_.isLoss();
      }

      public void startExecuting() {
         this.field_220858_b.func_213655_u(true);
         super.startExecuting();
      }

      public void resetTask() {
         this.field_220858_b.func_213655_u(false);
         super.resetTask();
      }

      public void tick() {
         if (!this.field_220858_b.isSilent() && this.field_220858_b.rand.nextInt(100) == 0) {
            AbstractRaiderEntity.this.playSound(AbstractRaiderEntity.this.getRaidLossSound(), AbstractRaiderEntity.this.getSoundVolume(), AbstractRaiderEntity.this.getSoundPitch());
         }

         if (!this.field_220858_b.isPassenger() && this.field_220858_b.rand.nextInt(50) == 0) {
            this.field_220858_b.getJumpController().setJumping();
         }

         super.tick();
      }
   }

   public class PromoteLeaderGoal<T extends AbstractRaiderEntity> extends Goal {
      private final T field_220856_b;

      public PromoteLeaderGoal(T p_i50572_2_) {
         this.field_220856_b = p_i50572_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         Raid lvt_1_1_ = this.field_220856_b.getRaid();
         if (this.field_220856_b.isRaidActive() && !this.field_220856_b.getRaid().func_221319_a() && this.field_220856_b.canBeLeader() && !ItemStack.areItemStacksEqual(this.field_220856_b.getItemStackFromSlot(EquipmentSlotType.HEAD), Raid.createIllagerBanner())) {
            AbstractRaiderEntity lvt_2_1_ = lvt_1_1_.getLeader(this.field_220856_b.func_213642_em());
            if (lvt_2_1_ == null || !lvt_2_1_.isAlive()) {
               List<ItemEntity> lvt_3_1_ = this.field_220856_b.world.getEntitiesWithinAABB(ItemEntity.class, this.field_220856_b.getBoundingBox().grow(16.0D, 8.0D, 16.0D), AbstractRaiderEntity.field_213665_b);
               if (!lvt_3_1_.isEmpty()) {
                  return this.field_220856_b.getNavigator().tryMoveToEntityLiving((Entity)lvt_3_1_.get(0), 1.149999976158142D);
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.field_220856_b.getNavigator().getTargetPos().withinDistance(this.field_220856_b.getPositionVec(), 1.414D)) {
            List<ItemEntity> lvt_1_1_ = this.field_220856_b.world.getEntitiesWithinAABB(ItemEntity.class, this.field_220856_b.getBoundingBox().grow(4.0D, 4.0D, 4.0D), AbstractRaiderEntity.field_213665_b);
            if (!lvt_1_1_.isEmpty()) {
               this.field_220856_b.updateEquipmentIfNeeded((ItemEntity)lvt_1_1_.get(0));
            }
         }

      }
   }
}
