package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
   private static final DataParameter<Byte> HANGING;
   private static final EntityPredicate field_213813_c;
   private BlockPos spawnPosition;

   public BatEntity(EntityType<? extends BatEntity> p_i50290_1_, World p_i50290_2_) {
      super(p_i50290_1_, p_i50290_2_);
      this.setIsBatHanging(true);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HANGING, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BAT_DEATH;
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
   }

   public boolean getIsBatHanging() {
      return ((Byte)this.dataManager.get(HANGING) & 1) != 0;
   }

   public void setIsBatHanging(boolean p_82236_1_) {
      byte lvt_2_1_ = (Byte)this.dataManager.get(HANGING);
      if (p_82236_1_) {
         this.dataManager.set(HANGING, (byte)(lvt_2_1_ | 1));
      } else {
         this.dataManager.set(HANGING, (byte)(lvt_2_1_ & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.getIsBatHanging()) {
         this.setMotion(Vec3d.ZERO);
         this.func_226288_n_(this.func_226277_ct_(), (double)MathHelper.floor(this.func_226278_cu_()) + 1.0D - (double)this.getHeight(), this.func_226281_cx_());
      } else {
         this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
      }

   }

   protected void updateAITasks() {
      super.updateAITasks();
      BlockPos lvt_1_1_ = new BlockPos(this);
      BlockPos lvt_2_1_ = lvt_1_1_.up();
      if (this.getIsBatHanging()) {
         if (this.world.getBlockState(lvt_2_1_).isNormalCube(this.world, lvt_1_1_)) {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = (float)this.rand.nextInt(360);
            }

            if (this.world.getClosestPlayer(field_213813_c, this) != null) {
               this.setIsBatHanging(false);
               this.world.playEvent((PlayerEntity)null, 1025, lvt_1_1_, 0);
            }
         } else {
            this.setIsBatHanging(false);
            this.world.playEvent((PlayerEntity)null, 1025, lvt_1_1_, 0);
         }
      } else {
         if (this.spawnPosition != null && (!this.world.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.withinDistance(this.getPositionVec(), 2.0D)) {
            this.spawnPosition = new BlockPos(this.func_226277_ct_() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7), this.func_226278_cu_() + (double)this.rand.nextInt(6) - 2.0D, this.func_226281_cx_() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7));
         }

         double lvt_3_1_ = (double)this.spawnPosition.getX() + 0.5D - this.func_226277_ct_();
         double lvt_5_1_ = (double)this.spawnPosition.getY() + 0.1D - this.func_226278_cu_();
         double lvt_7_1_ = (double)this.spawnPosition.getZ() + 0.5D - this.func_226281_cx_();
         Vec3d lvt_9_1_ = this.getMotion();
         Vec3d lvt_10_1_ = lvt_9_1_.add((Math.signum(lvt_3_1_) * 0.5D - lvt_9_1_.x) * 0.10000000149011612D, (Math.signum(lvt_5_1_) * 0.699999988079071D - lvt_9_1_.y) * 0.10000000149011612D, (Math.signum(lvt_7_1_) * 0.5D - lvt_9_1_.z) * 0.10000000149011612D);
         this.setMotion(lvt_10_1_);
         float lvt_11_1_ = (float)(MathHelper.atan2(lvt_10_1_.z, lvt_10_1_.x) * 57.2957763671875D) - 90.0F;
         float lvt_12_1_ = MathHelper.wrapDegrees(lvt_11_1_ - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += lvt_12_1_;
         if (this.rand.nextInt(100) == 0 && this.world.getBlockState(lvt_2_1_).isNormalCube(this.world, lvt_2_1_)) {
            this.setIsBatHanging(true);
         }
      }

   }

   protected boolean func_225502_at_() {
      return false;
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (!this.world.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.dataManager.set(HANGING, p_70037_1_.getByte("BatFlags"));
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putByte("BatFlags", (Byte)this.dataManager.get(HANGING));
   }

   public static boolean func_223369_b(EntityType<BatEntity> p_223369_0_, IWorld p_223369_1_, SpawnReason p_223369_2_, BlockPos p_223369_3_, Random p_223369_4_) {
      if (p_223369_3_.getY() >= p_223369_1_.getSeaLevel()) {
         return false;
      } else {
         int lvt_5_1_ = p_223369_1_.getLight(p_223369_3_);
         int lvt_6_1_ = 4;
         if (isNearHalloween()) {
            lvt_6_1_ = 7;
         } else if (p_223369_4_.nextBoolean()) {
            return false;
         }

         return lvt_5_1_ > p_223369_4_.nextInt(lvt_6_1_) ? false : func_223315_a(p_223369_0_, p_223369_1_, p_223369_2_, p_223369_3_, p_223369_4_);
      }
   }

   private static boolean isNearHalloween() {
      LocalDate lvt_0_1_ = LocalDate.now();
      int lvt_1_1_ = lvt_0_1_.get(ChronoField.DAY_OF_MONTH);
      int lvt_2_1_ = lvt_0_1_.get(ChronoField.MONTH_OF_YEAR);
      return lvt_2_1_ == 10 && lvt_1_1_ >= 20 || lvt_2_1_ == 11 && lvt_1_1_ <= 3;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height / 2.0F;
   }

   static {
      HANGING = EntityDataManager.createKey(BatEntity.class, DataSerializers.BYTE);
      field_213813_c = (new EntityPredicate()).setDistance(4.0D).allowFriendlyFire();
   }
}
