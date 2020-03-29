package net.minecraft.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VexEntity extends MonsterEntity {
   protected static final DataParameter<Byte> VEX_FLAGS;
   private MobEntity owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean limitedLifespan;
   private int limitedLifeTicks;

   public VexEntity(EntityType<? extends VexEntity> p_i50190_1_, World p_i50190_2_) {
      super(p_i50190_1_, p_i50190_2_);
      this.moveController = new VexEntity.MoveHelperController(this);
      this.experienceValue = 3;
   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      super.move(p_213315_1_, p_213315_2_);
      this.doBlockCollisions();
   }

   public void tick() {
      this.noClip = true;
      super.tick();
      this.noClip = false;
      this.setNoGravity(true);
      if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.attackEntityFrom(DamageSource.STARVE, 1.0F);
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(4, new VexEntity.ChargeAttackGoal());
      this.goalSelector.addGoal(8, new VexEntity.MoveRandomGoal());
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(2, new VexEntity.CopyOwnerTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VEX_FLAGS, (byte)0);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("BoundX")) {
         this.boundOrigin = new BlockPos(p_70037_1_.getInt("BoundX"), p_70037_1_.getInt("BoundY"), p_70037_1_.getInt("BoundZ"));
      }

      if (p_70037_1_.contains("LifeTicks")) {
         this.setLimitedLife(p_70037_1_.getInt("LifeTicks"));
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.boundOrigin != null) {
         p_213281_1_.putInt("BoundX", this.boundOrigin.getX());
         p_213281_1_.putInt("BoundY", this.boundOrigin.getY());
         p_213281_1_.putInt("BoundZ", this.boundOrigin.getZ());
      }

      if (this.limitedLifespan) {
         p_213281_1_.putInt("LifeTicks", this.limitedLifeTicks);
      }

   }

   public MobEntity getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos p_190651_1_) {
      this.boundOrigin = p_190651_1_;
   }

   private boolean getVexFlag(int p_190656_1_) {
      int lvt_2_1_ = (Byte)this.dataManager.get(VEX_FLAGS);
      return (lvt_2_1_ & p_190656_1_) != 0;
   }

   private void setVexFlag(int p_190660_1_, boolean p_190660_2_) {
      int lvt_3_1_ = (Byte)this.dataManager.get(VEX_FLAGS);
      int lvt_3_1_;
      if (p_190660_2_) {
         lvt_3_1_ = lvt_3_1_ | p_190660_1_;
      } else {
         lvt_3_1_ = lvt_3_1_ & ~p_190660_1_;
      }

      this.dataManager.set(VEX_FLAGS, (byte)(lvt_3_1_ & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setCharging(boolean p_190648_1_) {
      this.setVexFlag(1, p_190648_1_);
   }

   public void setOwner(MobEntity p_190658_1_) {
      this.owner = p_190658_1_;
   }

   public void setLimitedLife(int p_190653_1_) {
      this.limitedLifespan = true;
      this.limitedLifeTicks = p_190653_1_;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_VEX_HURT;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setEquipmentBasedOnDifficulty(p_213386_2_);
      this.setEnchantmentBasedOnDifficulty(p_213386_2_);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
   }

   static {
      VEX_FLAGS = EntityDataManager.createKey(VexEntity.class, DataSerializers.BYTE);
   }

   class CopyOwnerTargetGoal extends TargetGoal {
      private final EntityPredicate field_220803_b = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();

      public CopyOwnerTargetGoal(CreatureEntity p_i47231_2_) {
         super(p_i47231_2_, false);
      }

      public boolean shouldExecute() {
         return VexEntity.this.owner != null && VexEntity.this.owner.getAttackTarget() != null && this.isSuitableTarget(VexEntity.this.owner.getAttackTarget(), this.field_220803_b);
      }

      public void startExecuting() {
         VexEntity.this.setAttackTarget(VexEntity.this.owner.getAttackTarget());
         super.startExecuting();
      }
   }

   class MoveRandomGoal extends Goal {
      public MoveRandomGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         return !VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void tick() {
         BlockPos lvt_1_1_ = VexEntity.this.getBoundOrigin();
         if (lvt_1_1_ == null) {
            lvt_1_1_ = new BlockPos(VexEntity.this);
         }

         for(int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_) {
            BlockPos lvt_3_1_ = lvt_1_1_.add(VexEntity.this.rand.nextInt(15) - 7, VexEntity.this.rand.nextInt(11) - 5, VexEntity.this.rand.nextInt(15) - 7);
            if (VexEntity.this.world.isAirBlock(lvt_3_1_)) {
               VexEntity.this.moveController.setMoveTo((double)lvt_3_1_.getX() + 0.5D, (double)lvt_3_1_.getY() + 0.5D, (double)lvt_3_1_.getZ() + 0.5D, 0.25D);
               if (VexEntity.this.getAttackTarget() == null) {
                  VexEntity.this.getLookController().setLookPosition((double)lvt_3_1_.getX() + 0.5D, (double)lvt_3_1_.getY() + 0.5D, (double)lvt_3_1_.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   class ChargeAttackGoal extends Goal {
      public ChargeAttackGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         if (VexEntity.this.getAttackTarget() != null && !VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.rand.nextInt(7) == 0) {
            return VexEntity.this.getDistanceSq(VexEntity.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.isCharging() && VexEntity.this.getAttackTarget() != null && VexEntity.this.getAttackTarget().isAlive();
      }

      public void startExecuting() {
         LivingEntity lvt_1_1_ = VexEntity.this.getAttackTarget();
         Vec3d lvt_2_1_ = lvt_1_1_.getEyePosition(1.0F);
         VexEntity.this.moveController.setMoveTo(lvt_2_1_.x, lvt_2_1_.y, lvt_2_1_.z, 1.0D);
         VexEntity.this.setCharging(true);
         VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
      }

      public void resetTask() {
         VexEntity.this.setCharging(false);
      }

      public void tick() {
         LivingEntity lvt_1_1_ = VexEntity.this.getAttackTarget();
         if (VexEntity.this.getBoundingBox().intersects(lvt_1_1_.getBoundingBox())) {
            VexEntity.this.attackEntityAsMob(lvt_1_1_);
            VexEntity.this.setCharging(false);
         } else {
            double lvt_2_1_ = VexEntity.this.getDistanceSq(lvt_1_1_);
            if (lvt_2_1_ < 9.0D) {
               Vec3d lvt_4_1_ = lvt_1_1_.getEyePosition(1.0F);
               VexEntity.this.moveController.setMoveTo(lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z, 1.0D);
            }
         }

      }
   }

   class MoveHelperController extends MovementController {
      public MoveHelperController(VexEntity p_i47230_2_) {
         super(p_i47230_2_);
      }

      public void tick() {
         if (this.action == MovementController.Action.MOVE_TO) {
            Vec3d lvt_1_1_ = new Vec3d(this.posX - VexEntity.this.func_226277_ct_(), this.posY - VexEntity.this.func_226278_cu_(), this.posZ - VexEntity.this.func_226281_cx_());
            double lvt_2_1_ = lvt_1_1_.length();
            if (lvt_2_1_ < VexEntity.this.getBoundingBox().getAverageEdgeLength()) {
               this.action = MovementController.Action.WAIT;
               VexEntity.this.setMotion(VexEntity.this.getMotion().scale(0.5D));
            } else {
               VexEntity.this.setMotion(VexEntity.this.getMotion().add(lvt_1_1_.scale(this.speed * 0.05D / lvt_2_1_)));
               if (VexEntity.this.getAttackTarget() == null) {
                  Vec3d lvt_4_1_ = VexEntity.this.getMotion();
                  VexEntity.this.rotationYaw = -((float)MathHelper.atan2(lvt_4_1_.x, lvt_4_1_.z)) * 57.295776F;
                  VexEntity.this.renderYawOffset = VexEntity.this.rotationYaw;
               } else {
                  double lvt_4_2_ = VexEntity.this.getAttackTarget().func_226277_ct_() - VexEntity.this.func_226277_ct_();
                  double lvt_6_1_ = VexEntity.this.getAttackTarget().func_226281_cx_() - VexEntity.this.func_226281_cx_();
                  VexEntity.this.rotationYaw = -((float)MathHelper.atan2(lvt_4_2_, lvt_6_1_)) * 57.295776F;
                  VexEntity.this.renderYawOffset = VexEntity.this.rotationYaw;
               }
            }

         }
      }
   }
}
