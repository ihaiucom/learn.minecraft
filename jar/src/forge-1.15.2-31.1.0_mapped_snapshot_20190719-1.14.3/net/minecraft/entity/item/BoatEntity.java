package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoatEntity extends Entity {
   private static final DataParameter<Integer> TIME_SINCE_HIT;
   private static final DataParameter<Integer> FORWARD_DIRECTION;
   private static final DataParameter<Float> DAMAGE_TAKEN;
   private static final DataParameter<Integer> BOAT_TYPE;
   private static final DataParameter<Boolean> field_199704_e;
   private static final DataParameter<Boolean> field_199705_f;
   private static final DataParameter<Integer> ROCKING_TICKS;
   private final float[] paddlePositions;
   private float momentum;
   private float outOfControlTicks;
   private float deltaRotation;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYaw;
   private double lerpPitch;
   private boolean leftInputDown;
   private boolean rightInputDown;
   private boolean forwardInputDown;
   private boolean backInputDown;
   private double waterLevel;
   private float boatGlide;
   private BoatEntity.Status status;
   private BoatEntity.Status previousStatus;
   private double lastYd;
   private boolean rocking;
   private boolean field_203060_aN;
   private float rockingIntensity;
   private float rockingAngle;
   private float prevRockingAngle;

   public BoatEntity(EntityType<? extends BoatEntity> p_i50129_1_, World p_i50129_2_) {
      super(p_i50129_1_, p_i50129_2_);
      this.paddlePositions = new float[2];
      this.preventEntitySpawning = true;
   }

   public BoatEntity(World p_i1705_1_, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_) {
      this(EntityType.BOAT, p_i1705_1_);
      this.setPosition(p_i1705_2_, p_i1705_4_, p_i1705_6_);
      this.setMotion(Vec3d.ZERO);
      this.prevPosX = p_i1705_2_;
      this.prevPosY = p_i1705_4_;
      this.prevPosZ = p_i1705_6_;
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(TIME_SINCE_HIT, 0);
      this.dataManager.register(FORWARD_DIRECTION, 1);
      this.dataManager.register(DAMAGE_TAKEN, 0.0F);
      this.dataManager.register(BOAT_TYPE, BoatEntity.Type.OAK.ordinal());
      this.dataManager.register(field_199704_e, false);
      this.dataManager.register(field_199705_f, false);
      this.dataManager.register(ROCKING_TICKS, 0);
   }

   @Nullable
   public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
      return p_70114_1_.canBePushed() ? p_70114_1_.getBoundingBox() : null;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getBoundingBox();
   }

   public boolean canBePushed() {
      return true;
   }

   public double getMountedYOffset() {
      return -0.1D;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!this.world.isRemote && !this.removed) {
         if (p_70097_1_ instanceof IndirectEntityDamageSource && p_70097_1_.getTrueSource() != null && this.isPassenger(p_70097_1_.getTrueSource())) {
            return false;
         } else {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + p_70097_2_ * 10.0F);
            this.markVelocityChanged();
            boolean flag = p_70097_1_.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)p_70097_1_.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamageTaken() > 40.0F) {
               if (!flag && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                  this.entityDropItem(this.getItemBoat());
               }

               this.remove();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void onEnterBubbleColumnWithAirAbove(boolean p_203002_1_) {
      if (!this.world.isRemote) {
         this.rocking = true;
         this.field_203060_aN = p_203002_1_;
         if (this.getRockingTicks() == 0) {
            this.setRockingTicks(60);
         }
      }

      this.world.addParticle(ParticleTypes.SPLASH, this.func_226277_ct_() + (double)this.rand.nextFloat(), this.func_226278_cu_() + 0.7D, this.func_226281_cx_() + (double)this.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
      if (this.rand.nextInt(20) == 0) {
         this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.getSplashSound(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.rand.nextFloat(), false);
      }

   }

   public void applyEntityCollision(Entity p_70108_1_) {
      if (p_70108_1_ instanceof BoatEntity) {
         if (p_70108_1_.getBoundingBox().minY < this.getBoundingBox().maxY) {
            super.applyEntityCollision(p_70108_1_);
         }
      } else if (p_70108_1_.getBoundingBox().minY <= this.getBoundingBox().minY) {
         super.applyEntityCollision(p_70108_1_);
      }

   }

   public Item getItemBoat() {
      switch(this.getBoatType()) {
      case OAK:
      default:
         return Items.OAK_BOAT;
      case SPRUCE:
         return Items.SPRUCE_BOAT;
      case BIRCH:
         return Items.BIRCH_BOAT;
      case JUNGLE:
         return Items.JUNGLE_BOAT;
      case ACACIA:
         return Items.ACACIA_BOAT;
      case DARK_OAK:
         return Items.DARK_OAK_BOAT;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.lerpX = p_180426_1_;
      this.lerpY = p_180426_3_;
      this.lerpZ = p_180426_5_;
      this.lerpYaw = (double)p_180426_7_;
      this.lerpPitch = (double)p_180426_8_;
      this.lerpSteps = 10;
   }

   public Direction getAdjustedHorizontalFacing() {
      return this.getHorizontalFacing().rotateY();
   }

   public void tick() {
      this.previousStatus = this.status;
      this.status = this.getBoatStatus();
      if (this.status != BoatEntity.Status.UNDER_WATER && this.status != BoatEntity.Status.UNDER_FLOWING_WATER) {
         this.outOfControlTicks = 0.0F;
      } else {
         ++this.outOfControlTicks;
      }

      if (!this.world.isRemote && this.outOfControlTicks >= 60.0F) {
         this.removePassengers();
      }

      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      super.tick();
      this.tickLerp();
      if (this.canPassengerSteer()) {
         if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof PlayerEntity)) {
            this.setPaddleState(false, false);
         }

         this.updateMotion();
         if (this.world.isRemote) {
            this.controlBoat();
            this.world.sendPacketToServer(new CSteerBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
         }

         this.move(MoverType.SELF, this.getMotion());
      } else {
         this.setMotion(Vec3d.ZERO);
      }

      this.updateRocking();

      for(int i = 0; i <= 1; ++i) {
         if (this.getPaddleState(i)) {
            if (!this.isSilent() && (double)(this.paddlePositions[i] % 6.2831855F) <= 0.7853981852531433D && ((double)this.paddlePositions[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
               SoundEvent soundevent = this.getPaddleSound();
               if (soundevent != null) {
                  Vec3d vec3d = this.getLook(1.0F);
                  double d0 = i == 1 ? -vec3d.z : vec3d.z;
                  double d1 = i == 1 ? vec3d.x : -vec3d.x;
                  this.world.playSound((PlayerEntity)null, this.func_226277_ct_() + d0, this.func_226278_cu_(), this.func_226281_cx_() + d1, soundevent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.rand.nextFloat());
               }
            }

            this.paddlePositions[i] = (float)((double)this.paddlePositions[i] + 0.39269909262657166D);
         } else {
            this.paddlePositions[i] = 0.0F;
         }
      }

      this.doBlockCollisions();
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntityPredicates.pushableBy(this));
      if (!list.isEmpty()) {
         boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof PlayerEntity);

         for(int j = 0; j < list.size(); ++j) {
            Entity entity = (Entity)list.get(j);
            if (!entity.isPassenger((Entity)this)) {
               if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterMobEntity) && !(entity instanceof PlayerEntity)) {
                  entity.startRiding(this);
               } else {
                  this.applyEntityCollision(entity);
               }
            }
         }
      }

   }

   private void updateRocking() {
      int k;
      if (this.world.isRemote) {
         k = this.getRockingTicks();
         if (k > 0) {
            this.rockingIntensity += 0.05F;
         } else {
            this.rockingIntensity -= 0.1F;
         }

         this.rockingIntensity = MathHelper.clamp(this.rockingIntensity, 0.0F, 1.0F);
         this.prevRockingAngle = this.rockingAngle;
         this.rockingAngle = 10.0F * (float)Math.sin((double)(0.5F * (float)this.world.getGameTime())) * this.rockingIntensity;
      } else {
         if (!this.rocking) {
            this.setRockingTicks(0);
         }

         k = this.getRockingTicks();
         if (k > 0) {
            --k;
            this.setRockingTicks(k);
            int j = 60 - k - 1;
            if (j > 0 && k == 0) {
               this.setRockingTicks(0);
               Vec3d vec3d = this.getMotion();
               if (this.field_203060_aN) {
                  this.setMotion(vec3d.add(0.0D, -0.7D, 0.0D));
                  this.removePassengers();
               } else {
                  this.setMotion(vec3d.x, this.isPassenger(PlayerEntity.class) ? 2.7D : 0.6D, vec3d.z);
               }
            }

            this.rocking = false;
         }
      }

   }

   @Nullable
   protected SoundEvent getPaddleSound() {
      switch(this.getBoatStatus()) {
      case IN_WATER:
      case UNDER_WATER:
      case UNDER_FLOWING_WATER:
         return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
      case ON_LAND:
         return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
      case IN_AIR:
      default:
         return null;
      }
   }

   private void tickLerp() {
      if (this.canPassengerSteer()) {
         this.lerpSteps = 0;
         this.func_213312_b(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
      }

      if (this.lerpSteps > 0) {
         double d0 = this.func_226277_ct_() + (this.lerpX - this.func_226277_ct_()) / (double)this.lerpSteps;
         double d1 = this.func_226278_cu_() + (this.lerpY - this.func_226278_cu_()) / (double)this.lerpSteps;
         double d2 = this.func_226281_cx_() + (this.lerpZ - this.func_226281_cx_()) / (double)this.lerpSteps;
         double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
         this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
         --this.lerpSteps;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

   }

   public void setPaddleState(boolean p_184445_1_, boolean p_184445_2_) {
      this.dataManager.set(field_199704_e, p_184445_1_);
      this.dataManager.set(field_199705_f, p_184445_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRowingTime(int p_184448_1_, float p_184448_2_) {
      return this.getPaddleState(p_184448_1_) ? (float)MathHelper.clampedLerp((double)this.paddlePositions[p_184448_1_] - 0.39269909262657166D, (double)this.paddlePositions[p_184448_1_], (double)p_184448_2_) : 0.0F;
   }

   private BoatEntity.Status getBoatStatus() {
      BoatEntity.Status boatentity$status = this.getUnderwaterStatus();
      if (boatentity$status != null) {
         this.waterLevel = this.getBoundingBox().maxY;
         return boatentity$status;
      } else if (this.checkInWater()) {
         return BoatEntity.Status.IN_WATER;
      } else {
         float f = this.getBoatGlide();
         if (f > 0.0F) {
            this.boatGlide = f;
            return BoatEntity.Status.ON_LAND;
         } else {
            return BoatEntity.Status.IN_AIR;
         }
      }
   }

   public float getWaterLevelAbove() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.maxY);
      int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var9 = null;

      float f;
      try {
         label161:
         for(int k1 = k; k1 < l; ++k1) {
            f = 0.0F;

            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutable.setPos(l1, k1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutable);
                  if (ifluidstate.isTagged(FluidTags.WATER)) {
                     f = Math.max(f, ifluidstate.func_215679_a(this.world, blockpos$pooledmutable));
                  }

                  if (f >= 1.0F) {
                     continue label161;
                  }
               }
            }

            if (f < 1.0F) {
               float f2 = (float)blockpos$pooledmutable.getY() + f;
               float var27 = f2;
               return var27;
            }
         }

         float f1 = (float)(l + 1);
         f = f1;
      } catch (Throwable var23) {
         var9 = var23;
         throw var23;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var9 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var22) {
                  var9.addSuppressed(var22);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return f;
   }

   public float getBoatGlide() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
      int i = MathHelper.floor(axisalignedbb1.minX) - 1;
      int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
      int k = MathHelper.floor(axisalignedbb1.minY) - 1;
      int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
      int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
      int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
      VoxelShape voxelshape = VoxelShapes.create(axisalignedbb1);
      float f = 0.0F;
      int k1 = 0;
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var13 = null;

      try {
         for(int l1 = i; l1 < j; ++l1) {
            for(int i2 = i1; i2 < j1; ++i2) {
               int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
               if (j2 != 2) {
                  for(int k2 = k; k2 < l; ++k2) {
                     if (j2 <= 0 || k2 != k && k2 != l - 1) {
                        blockpos$pooledmutable.setPos(l1, k2, i2);
                        BlockState blockstate = this.world.getBlockState(blockpos$pooledmutable);
                        if (!(blockstate.getBlock() instanceof LilyPadBlock) && VoxelShapes.compare(blockstate.getCollisionShape(this.world, blockpos$pooledmutable).withOffset((double)l1, (double)k2, (double)i2), voxelshape, IBooleanFunction.AND)) {
                           f += blockstate.getSlipperiness(this.world, blockpos$pooledmutable, this);
                           ++k1;
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var26) {
         var13 = var26;
         throw var26;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var13 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var25) {
                  var13.addSuppressed(var25);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return f / (float)k1;
   }

   private boolean checkInWater() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      boolean flag = false;
      this.waterLevel = Double.MIN_VALUE;
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var10 = null;

      try {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutable.setPos(k1, l1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutable);
                  if (ifluidstate.isTagged(FluidTags.WATER)) {
                     float f = (float)l1 + ifluidstate.func_215679_a(this.world, blockpos$pooledmutable);
                     this.waterLevel = Math.max((double)f, this.waterLevel);
                     flag |= axisalignedbb.minY < (double)f;
                  }
               }
            }
         }
      } catch (Throwable var23) {
         var10 = var23;
         throw var23;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var10 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var22) {
                  var10.addSuppressed(var22);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return flag;
   }

   @Nullable
   private BoatEntity.Status getUnderwaterStatus() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      double d0 = axisalignedbb.maxY + 0.001D;
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.maxY);
      int l = MathHelper.ceil(d0);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      boolean flag = false;
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var12 = null;

      try {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutable.setPos(k1, l1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutable);
                  if (ifluidstate.isTagged(FluidTags.WATER) && d0 < (double)((float)blockpos$pooledmutable.getY() + ifluidstate.func_215679_a(this.world, blockpos$pooledmutable))) {
                     if (!ifluidstate.isSource()) {
                        BoatEntity.Status boatentity$status = BoatEntity.Status.UNDER_FLOWING_WATER;
                        BoatEntity.Status var18 = boatentity$status;
                        return var18;
                     }

                     flag = true;
                  }
               }
            }
         }
      } catch (Throwable var28) {
         var12 = var28;
         throw var28;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var12 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var27) {
                  var12.addSuppressed(var27);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return flag ? BoatEntity.Status.UNDER_WATER : null;
   }

   private void updateMotion() {
      double d0 = -0.03999999910593033D;
      double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
      double d2 = 0.0D;
      this.momentum = 0.05F;
      if (this.previousStatus == BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.ON_LAND) {
         this.waterLevel = this.func_226283_e_(1.0D);
         this.setPosition(this.func_226277_ct_(), (double)(this.getWaterLevelAbove() - this.getHeight()) + 0.101D, this.func_226281_cx_());
         this.setMotion(this.getMotion().mul(1.0D, 0.0D, 1.0D));
         this.lastYd = 0.0D;
         this.status = BoatEntity.Status.IN_WATER;
      } else {
         if (this.status == BoatEntity.Status.IN_WATER) {
            d2 = (this.waterLevel - this.func_226278_cu_()) / (double)this.getHeight();
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.UNDER_FLOWING_WATER) {
            d1 = -7.0E-4D;
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.UNDER_WATER) {
            d2 = 0.009999999776482582D;
            this.momentum = 0.45F;
         } else if (this.status == BoatEntity.Status.IN_AIR) {
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.ON_LAND) {
            this.momentum = this.boatGlide;
            if (this.getControllingPassenger() instanceof PlayerEntity) {
               this.boatGlide /= 2.0F;
            }
         }

         Vec3d vec3d = this.getMotion();
         this.setMotion(vec3d.x * (double)this.momentum, vec3d.y + d1, vec3d.z * (double)this.momentum);
         this.deltaRotation *= this.momentum;
         if (d2 > 0.0D) {
            Vec3d vec3d1 = this.getMotion();
            this.setMotion(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
         }
      }

   }

   private void controlBoat() {
      if (this.isBeingRidden()) {
         float f = 0.0F;
         if (this.leftInputDown) {
            --this.deltaRotation;
         }

         if (this.rightInputDown) {
            ++this.deltaRotation;
         }

         if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
            f += 0.005F;
         }

         this.rotationYaw += this.deltaRotation;
         if (this.forwardInputDown) {
            f += 0.04F;
         }

         if (this.backInputDown) {
            f -= 0.005F;
         }

         this.setMotion(this.getMotion().add((double)(MathHelper.sin(-this.rotationYaw * 0.017453292F) * f), 0.0D, (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * f)));
         this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
      }

   }

   public void updatePassenger(Entity p_184232_1_) {
      if (this.isPassenger(p_184232_1_)) {
         float f = 0.0F;
         float f1 = (float)((this.removed ? 0.009999999776482582D : this.getMountedYOffset()) + p_184232_1_.getYOffset());
         if (this.getPassengers().size() > 1) {
            int i = this.getPassengers().indexOf(p_184232_1_);
            if (i == 0) {
               f = 0.2F;
            } else {
               f = -0.6F;
            }

            if (p_184232_1_ instanceof AnimalEntity) {
               f = (float)((double)f + 0.2D);
            }
         }

         Vec3d vec3d = (new Vec3d((double)f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - 1.5707964F);
         p_184232_1_.setPosition(this.func_226277_ct_() + vec3d.x, this.func_226278_cu_() + (double)f1, this.func_226281_cx_() + vec3d.z);
         p_184232_1_.rotationYaw += this.deltaRotation;
         p_184232_1_.setRotationYawHead(p_184232_1_.getRotationYawHead() + this.deltaRotation);
         this.applyYawToEntity(p_184232_1_);
         if (p_184232_1_ instanceof AnimalEntity && this.getPassengers().size() > 1) {
            int j = p_184232_1_.getEntityId() % 2 == 0 ? 90 : 270;
            p_184232_1_.setRenderYawOffset(((AnimalEntity)p_184232_1_).renderYawOffset + (float)j);
            p_184232_1_.setRotationYawHead(p_184232_1_.getRotationYawHead() + (float)j);
         }
      }

   }

   protected void applyYawToEntity(Entity p_184454_1_) {
      p_184454_1_.setRenderYawOffset(this.rotationYaw);
      float f = MathHelper.wrapDegrees(p_184454_1_.rotationYaw - this.rotationYaw);
      float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
      p_184454_1_.prevRotationYaw += f1 - f;
      p_184454_1_.rotationYaw += f1 - f;
      p_184454_1_.setRotationYawHead(p_184454_1_.rotationYaw);
   }

   @OnlyIn(Dist.CLIENT)
   public void applyOrientationToEntity(Entity p_184190_1_) {
      this.applyYawToEntity(p_184190_1_);
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putString("Type", this.getBoatType().getName());
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("Type", 8)) {
         this.setBoatType(BoatEntity.Type.getTypeFromString(p_70037_1_.getString("Type")));
      }

   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (p_184230_1_.func_226563_dT_()) {
         return false;
      } else {
         return !this.world.isRemote && this.outOfControlTicks < 60.0F ? p_184230_1_.startRiding(this) : false;
      }
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
      this.lastYd = this.getMotion().y;
      if (!this.isPassenger()) {
         if (p_184231_3_) {
            if (this.fallDistance > 3.0F) {
               if (this.status != BoatEntity.Status.ON_LAND) {
                  this.fallDistance = 0.0F;
                  return;
               }

               this.func_225503_b_(this.fallDistance, 1.0F);
               if (!this.world.isRemote && !this.removed) {
                  this.remove();
                  if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                     int j;
                     for(j = 0; j < 3; ++j) {
                        this.entityDropItem(this.getBoatType().asPlank());
                     }

                     for(j = 0; j < 2; ++j) {
                        this.entityDropItem(Items.STICK);
                     }
                  }
               }
            }

            this.fallDistance = 0.0F;
         } else if (!this.world.getFluidState((new BlockPos(this)).down()).isTagged(FluidTags.WATER) && p_184231_1_ < 0.0D) {
            this.fallDistance = (float)((double)this.fallDistance - p_184231_1_);
         }
      }

   }

   public boolean getPaddleState(int p_184457_1_) {
      return (Boolean)this.dataManager.get(p_184457_1_ == 0 ? field_199704_e : field_199705_f) && this.getControllingPassenger() != null;
   }

   public void setDamageTaken(float p_70266_1_) {
      this.dataManager.set(DAMAGE_TAKEN, p_70266_1_);
   }

   public float getDamageTaken() {
      return (Float)this.dataManager.get(DAMAGE_TAKEN);
   }

   public void setTimeSinceHit(int p_70265_1_) {
      this.dataManager.set(TIME_SINCE_HIT, p_70265_1_);
   }

   public int getTimeSinceHit() {
      return (Integer)this.dataManager.get(TIME_SINCE_HIT);
   }

   private void setRockingTicks(int p_203055_1_) {
      this.dataManager.set(ROCKING_TICKS, p_203055_1_);
   }

   private int getRockingTicks() {
      return (Integer)this.dataManager.get(ROCKING_TICKS);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRockingAngle(float p_203056_1_) {
      return MathHelper.lerp(p_203056_1_, this.prevRockingAngle, this.rockingAngle);
   }

   public void setForwardDirection(int p_70269_1_) {
      this.dataManager.set(FORWARD_DIRECTION, p_70269_1_);
   }

   public int getForwardDirection() {
      return (Integer)this.dataManager.get(FORWARD_DIRECTION);
   }

   public void setBoatType(BoatEntity.Type p_184458_1_) {
      this.dataManager.set(BOAT_TYPE, p_184458_1_.ordinal());
   }

   public BoatEntity.Type getBoatType() {
      return BoatEntity.Type.byId((Integer)this.dataManager.get(BOAT_TYPE));
   }

   protected boolean canFitPassenger(Entity p_184219_1_) {
      return this.getPassengers().size() < 2 && !this.areEyesInFluid(FluidTags.WATER);
   }

   @Nullable
   public Entity getControllingPassenger() {
      List<Entity> list = this.getPassengers();
      return list.isEmpty() ? null : (Entity)list.get(0);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_) {
      this.leftInputDown = p_184442_1_;
      this.rightInputDown = p_184442_2_;
      this.forwardInputDown = p_184442_3_;
      this.backInputDown = p_184442_4_;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   protected void addPassenger(Entity p_184200_1_) {
      super.addPassenger(p_184200_1_);
      if (this.canPassengerSteer() && this.lerpSteps > 0) {
         this.lerpSteps = 0;
         this.setPositionAndRotation(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYaw, (float)this.lerpPitch);
      }

   }

   static {
      TIME_SINCE_HIT = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
      FORWARD_DIRECTION = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
      DAMAGE_TAKEN = EntityDataManager.createKey(BoatEntity.class, DataSerializers.FLOAT);
      BOAT_TYPE = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
      field_199704_e = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
      field_199705_f = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
      ROCKING_TICKS = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
   }

   public static enum Type {
      OAK(Blocks.OAK_PLANKS, "oak"),
      SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
      BIRCH(Blocks.BIRCH_PLANKS, "birch"),
      JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
      ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
      DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

      private final String name;
      private final Block block;

      private Type(Block p_i48146_3_, String p_i48146_4_) {
         this.name = p_i48146_4_;
         this.block = p_i48146_3_;
      }

      public String getName() {
         return this.name;
      }

      public Block asPlank() {
         return this.block;
      }

      public String toString() {
         return this.name;
      }

      public static BoatEntity.Type byId(int p_184979_0_) {
         BoatEntity.Type[] aboatentity$type = values();
         if (p_184979_0_ < 0 || p_184979_0_ >= aboatentity$type.length) {
            p_184979_0_ = 0;
         }

         return aboatentity$type[p_184979_0_];
      }

      public static BoatEntity.Type getTypeFromString(String p_184981_0_) {
         BoatEntity.Type[] aboatentity$type = values();

         for(int i = 0; i < aboatentity$type.length; ++i) {
            if (aboatentity$type[i].getName().equals(p_184981_0_)) {
               return aboatentity$type[i];
            }
         }

         return aboatentity$type[0];
      }
   }

   public static enum Status {
      IN_WATER,
      UNDER_WATER,
      UNDER_FLOWING_WATER,
      ON_LAND,
      IN_AIR;
   }
}
