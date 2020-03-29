package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SquidEntity extends WaterMobEntity {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float lastTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float rotateSpeed;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;

   public SquidEntity(EntityType<? extends SquidEntity> p_i50243_1_, World p_i50243_2_) {
      super(p_i50243_1_, p_i50243_2_);
      this.rand.setSeed((long)this.getEntityId());
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SquidEntity.MoveRandomGoal(this));
      this.goalSelector.addGoal(1, new SquidEntity.FleeGoal());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SQUID_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected boolean func_225502_at_() {
      return false;
   }

   public void livingTick() {
      super.livingTick();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation += this.rotationVelocity;
      if ((double)this.squidRotation > 6.283185307179586D) {
         if (this.world.isRemote) {
            this.squidRotation = 6.2831855F;
         } else {
            this.squidRotation = (float)((double)this.squidRotation - 6.283185307179586D);
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.world.setEntityState(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubbleColumn()) {
         if (this.squidRotation < 3.1415927F) {
            float lvt_1_1_ = this.squidRotation / 3.1415927F;
            this.tentacleAngle = MathHelper.sin(lvt_1_1_ * lvt_1_1_ * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)lvt_1_1_ > 0.75D) {
               this.randomMotionSpeed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.world.isRemote) {
            this.setMotion((double)(this.randomMotionVecX * this.randomMotionSpeed), (double)(this.randomMotionVecY * this.randomMotionSpeed), (double)(this.randomMotionVecZ * this.randomMotionSpeed));
         }

         Vec3d lvt_1_2_ = this.getMotion();
         float lvt_2_1_ = MathHelper.sqrt(func_213296_b(lvt_1_2_));
         this.renderYawOffset += (-((float)MathHelper.atan2(lvt_1_2_.x, lvt_1_2_.z)) * 57.295776F - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)((double)this.squidYaw + 3.141592653589793D * (double)this.rotateSpeed * 1.5D);
         this.squidPitch += (-((float)MathHelper.atan2((double)lvt_2_1_, lvt_1_2_.y)) * 57.295776F - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * 3.1415927F * 0.25F;
         if (!this.world.isRemote) {
            double lvt_1_3_ = this.getMotion().y;
            if (this.isPotionActive(Effects.LEVITATION)) {
               lvt_1_3_ = 0.05D * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1);
            } else if (!this.hasNoGravity()) {
               lvt_1_3_ -= 0.08D;
            }

            this.setMotion(0.0D, lvt_1_3_ * 0.9800000190734863D, 0.0D);
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02D);
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (super.attackEntityFrom(p_70097_1_, p_70097_2_) && this.getRevengeTarget() != null) {
         this.squirtInk();
         return true;
      } else {
         return false;
      }
   }

   private Vec3d func_207400_b(Vec3d p_207400_1_) {
      Vec3d lvt_2_1_ = p_207400_1_.rotatePitch(this.prevSquidPitch * 0.017453292F);
      lvt_2_1_ = lvt_2_1_.rotateYaw(-this.prevRenderYawOffset * 0.017453292F);
      return lvt_2_1_;
   }

   private void squirtInk() {
      this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
      Vec3d lvt_1_1_ = this.func_207400_b(new Vec3d(0.0D, -1.0D, 0.0D)).add(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());

      for(int lvt_2_1_ = 0; lvt_2_1_ < 30; ++lvt_2_1_) {
         Vec3d lvt_3_1_ = this.func_207400_b(new Vec3d((double)this.rand.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.rand.nextFloat() * 0.6D - 0.3D));
         Vec3d lvt_4_1_ = lvt_3_1_.scale(0.3D + (double)(this.rand.nextFloat() * 2.0F));
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.SQUID_INK, lvt_1_1_.x, lvt_1_1_.y + 0.5D, lvt_1_1_.z, 0, lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z, 0.10000000149011612D);
      }

   }

   public void travel(Vec3d p_213352_1_) {
      this.move(MoverType.SELF, this.getMotion());
   }

   public static boolean func_223365_b(EntityType<SquidEntity> p_223365_0_, IWorld p_223365_1_, SpawnReason p_223365_2_, BlockPos p_223365_3_, Random p_223365_4_) {
      return p_223365_3_.getY() > 45 && p_223365_3_.getY() < p_223365_1_.getSeaLevel();
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void setMovementVector(float p_175568_1_, float p_175568_2_, float p_175568_3_) {
      this.randomMotionVecX = p_175568_1_;
      this.randomMotionVecY = p_175568_2_;
      this.randomMotionVecZ = p_175568_3_;
   }

   public boolean hasMovementVector() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   class FleeGoal extends Goal {
      private int tickCounter;

      private FleeGoal() {
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = SquidEntity.this.getRevengeTarget();
         if (SquidEntity.this.isInWater() && lvt_1_1_ != null) {
            return SquidEntity.this.getDistanceSq(lvt_1_1_) < 100.0D;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         this.tickCounter = 0;
      }

      public void tick() {
         ++this.tickCounter;
         LivingEntity lvt_1_1_ = SquidEntity.this.getRevengeTarget();
         if (lvt_1_1_ != null) {
            Vec3d lvt_2_1_ = new Vec3d(SquidEntity.this.func_226277_ct_() - lvt_1_1_.func_226277_ct_(), SquidEntity.this.func_226278_cu_() - lvt_1_1_.func_226278_cu_(), SquidEntity.this.func_226281_cx_() - lvt_1_1_.func_226281_cx_());
            BlockState lvt_3_1_ = SquidEntity.this.world.getBlockState(new BlockPos(SquidEntity.this.func_226277_ct_() + lvt_2_1_.x, SquidEntity.this.func_226278_cu_() + lvt_2_1_.y, SquidEntity.this.func_226281_cx_() + lvt_2_1_.z));
            IFluidState lvt_4_1_ = SquidEntity.this.world.getFluidState(new BlockPos(SquidEntity.this.func_226277_ct_() + lvt_2_1_.x, SquidEntity.this.func_226278_cu_() + lvt_2_1_.y, SquidEntity.this.func_226281_cx_() + lvt_2_1_.z));
            if (lvt_4_1_.isTagged(FluidTags.WATER) || lvt_3_1_.isAir()) {
               double lvt_5_1_ = lvt_2_1_.length();
               if (lvt_5_1_ > 0.0D) {
                  lvt_2_1_.normalize();
                  float lvt_7_1_ = 3.0F;
                  if (lvt_5_1_ > 5.0D) {
                     lvt_7_1_ = (float)((double)lvt_7_1_ - (lvt_5_1_ - 5.0D) / 5.0D);
                  }

                  if (lvt_7_1_ > 0.0F) {
                     lvt_2_1_ = lvt_2_1_.scale((double)lvt_7_1_);
                  }
               }

               if (lvt_3_1_.isAir()) {
                  lvt_2_1_ = lvt_2_1_.subtract(0.0D, lvt_2_1_.y, 0.0D);
               }

               SquidEntity.this.setMovementVector((float)lvt_2_1_.x / 20.0F, (float)lvt_2_1_.y / 20.0F, (float)lvt_2_1_.z / 20.0F);
            }

            if (this.tickCounter % 10 == 5) {
               SquidEntity.this.world.addParticle(ParticleTypes.BUBBLE, SquidEntity.this.func_226277_ct_(), SquidEntity.this.func_226278_cu_(), SquidEntity.this.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
            }

         }
      }

      // $FF: synthetic method
      FleeGoal(Object p_i48825_2_) {
         this();
      }
   }

   class MoveRandomGoal extends Goal {
      private final SquidEntity squid;

      public MoveRandomGoal(SquidEntity p_i48823_2_) {
         this.squid = p_i48823_2_;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void tick() {
         int lvt_1_1_ = this.squid.getIdleTime();
         if (lvt_1_1_ > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasMovementVector()) {
            float lvt_2_1_ = this.squid.getRNG().nextFloat() * 6.2831855F;
            float lvt_3_1_ = MathHelper.cos(lvt_2_1_) * 0.2F;
            float lvt_4_1_ = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float lvt_5_1_ = MathHelper.sin(lvt_2_1_) * 0.2F;
            this.squid.setMovementVector(lvt_3_1_, lvt_4_1_, lvt_5_1_);
         }

      }
   }
}
