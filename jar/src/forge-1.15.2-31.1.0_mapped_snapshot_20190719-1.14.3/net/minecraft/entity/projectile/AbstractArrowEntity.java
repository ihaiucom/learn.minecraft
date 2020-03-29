package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class AbstractArrowEntity extends Entity implements IProjectile {
   private static final DataParameter<Byte> CRITICAL;
   protected static final DataParameter<Optional<UUID>> field_212362_a;
   private static final DataParameter<Byte> field_213876_as;
   @Nullable
   private BlockState inBlockState;
   protected boolean inGround;
   protected int timeInGround;
   public AbstractArrowEntity.PickupStatus pickupStatus;
   public int arrowShake;
   public UUID shootingEntity;
   private int ticksInGround;
   private int ticksInAir;
   private double damage;
   private int knockbackStrength;
   private SoundEvent field_213877_ay;
   private IntOpenHashSet field_213878_az;
   private List<Entity> field_213875_aA;

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48546_1_, World p_i48546_2_) {
      super(p_i48546_1_, p_i48546_2_);
      this.pickupStatus = AbstractArrowEntity.PickupStatus.DISALLOWED;
      this.damage = 2.0D;
      this.field_213877_ay = this.func_213867_k();
   }

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
      this(p_i48547_1_, p_i48547_8_);
      this.setPosition(p_i48547_2_, p_i48547_4_, p_i48547_6_);
   }

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48548_1_, LivingEntity p_i48548_2_, World p_i48548_3_) {
      this(p_i48548_1_, p_i48548_2_.func_226277_ct_(), p_i48548_2_.func_226280_cw_() - 0.10000000149011612D, p_i48548_2_.func_226281_cx_(), p_i48548_3_);
      this.setShooter(p_i48548_2_);
      if (p_i48548_2_ instanceof PlayerEntity) {
         this.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
      }

   }

   public void setHitSound(SoundEvent p_213869_1_) {
      this.field_213877_ay = p_213869_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   protected void registerData() {
      this.dataManager.register(CRITICAL, (byte)0);
      this.dataManager.register(field_212362_a, Optional.empty());
      this.dataManager.register(field_213876_as, (byte)0);
   }

   public void shoot(Entity p_184547_1_, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
      float f = -MathHelper.sin(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
      float f1 = -MathHelper.sin(p_184547_2_ * 0.017453292F);
      float f2 = MathHelper.cos(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
      this.shoot((double)f, (double)f1, (double)f2, p_184547_5_, p_184547_6_);
      this.setMotion(this.getMotion().add(p_184547_1_.getMotion().x, p_184547_1_.onGround ? 0.0D : p_184547_1_.getMotion().y, p_184547_1_.getMotion().z));
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setMotion(vec3d);
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
      this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.ticksInGround = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRotation(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setMotion(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * 57.2957763671875D);
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * 57.2957763671875D);
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
         this.ticksInGround = 0;
      }

   }

   public void tick() {
      super.tick();
      boolean flag = this.func_203047_q();
      Vec3d vec3d = this.getMotion();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(func_213296_b(vec3d));
         this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
         this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

      BlockPos blockpos = new BlockPos(this);
      BlockState blockstate = this.world.getBlockState(blockpos);
      Vec3d vec3d3;
      if (!blockstate.isAir(this.world, blockpos) && !flag) {
         VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
         if (!voxelshape.isEmpty()) {
            vec3d3 = this.getPositionVec();
            Iterator var7 = voxelshape.toBoundingBoxList().iterator();

            while(var7.hasNext()) {
               AxisAlignedBB axisalignedbb = (AxisAlignedBB)var7.next();
               if (axisalignedbb.offset(blockpos).contains(vec3d3)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.arrowShake > 0) {
         --this.arrowShake;
      }

      if (this.isWet()) {
         this.extinguish();
      }

      if (this.inGround && !flag) {
         if (this.inBlockState != blockstate && this.world.func_226664_a_(this.getBoundingBox().grow(0.06D))) {
            this.inGround = false;
            this.setMotion(vec3d.mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else if (!this.world.isRemote) {
            this.func_225516_i_();
         }

         ++this.timeInGround;
      } else {
         this.timeInGround = 0;
         ++this.ticksInAir;
         Vec3d vec3d2 = this.getPositionVec();
         vec3d3 = vec3d2.add(vec3d);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d2, vec3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
         if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) {
            vec3d3 = ((RayTraceResult)raytraceresult).getHitVec();
         }

         while(!this.removed) {
            EntityRayTraceResult entityraytraceresult = this.func_213866_a(vec3d2, vec3d3);
            if (entityraytraceresult != null) {
               raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() == RayTraceResult.Type.ENTITY) {
               Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
               Entity entity1 = this.getShooter();
               if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canAttackPlayer((PlayerEntity)entity)) {
                  raytraceresult = null;
                  entityraytraceresult = null;
               }
            }

            if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact((AbstractArrowEntity)this, (RayTraceResult)raytraceresult)) {
               this.onHit((RayTraceResult)raytraceresult);
               this.isAirBorne = true;
            }

            if (entityraytraceresult == null || this.func_213874_s() <= 0) {
               break;
            }

            raytraceresult = null;
         }

         vec3d = this.getMotion();
         double d3 = vec3d.x;
         double d4 = vec3d.y;
         double d0 = vec3d.z;
         if (this.getIsCritical()) {
            for(int i = 0; i < 4; ++i) {
               this.world.addParticle(ParticleTypes.CRIT, this.func_226277_ct_() + d3 * (double)i / 4.0D, this.func_226278_cu_() + d4 * (double)i / 4.0D, this.func_226281_cx_() + d0 * (double)i / 4.0D, -d3, -d4 + 0.2D, -d0);
            }
         }

         double d5 = this.func_226277_ct_() + d3;
         double d1 = this.func_226278_cu_() + d4;
         double d2 = this.func_226281_cx_() + d0;
         float f1 = MathHelper.sqrt(func_213296_b(vec3d));
         if (flag) {
            this.rotationYaw = (float)(MathHelper.atan2(-d3, -d0) * 57.2957763671875D);
         } else {
            this.rotationYaw = (float)(MathHelper.atan2(d3, d0) * 57.2957763671875D);
         }

         for(this.rotationPitch = (float)(MathHelper.atan2(d4, (double)f1) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         }

         while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
         this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
         float f2 = 0.99F;
         float f3 = 0.05F;
         if (this.isInWater()) {
            for(int j = 0; j < 4; ++j) {
               float f4 = 0.25F;
               this.world.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
            }

            f2 = this.getWaterDrag();
         }

         this.setMotion(vec3d.scale((double)f2));
         if (!this.hasNoGravity() && !flag) {
            Vec3d vec3d4 = this.getMotion();
            this.setMotion(vec3d4.x, vec3d4.y - 0.05000000074505806D, vec3d4.z);
         }

         this.setPosition(d5, d1, d2);
         this.doBlockCollisions();
      }

   }

   protected void func_225516_i_() {
      ++this.ticksInGround;
      if (this.ticksInGround >= 1200) {
         this.remove();
      }

   }

   protected void onHit(RayTraceResult p_184549_1_) {
      RayTraceResult.Type raytraceresult$type = p_184549_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
         this.func_213868_a((EntityRayTraceResult)p_184549_1_);
      } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_184549_1_;
         BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
         this.inBlockState = blockstate;
         Vec3d vec3d = blockraytraceresult.getHitVec().subtract(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
         this.setMotion(vec3d);
         Vec3d vec3d1 = vec3d.normalize().scale(0.05000000074505806D);
         this.func_226288_n_(this.func_226277_ct_() - vec3d1.x, this.func_226278_cu_() - vec3d1.y, this.func_226281_cx_() - vec3d1.z);
         this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.arrowShake = 7;
         this.setIsCritical(false);
         this.func_213872_b((byte)0);
         this.setHitSound(SoundEvents.ENTITY_ARROW_HIT);
         this.func_213865_o(false);
         this.func_213870_w();
         blockstate.onProjectileCollision(this.world, blockstate, blockraytraceresult, this);
      }

   }

   private void func_213870_w() {
      if (this.field_213875_aA != null) {
         this.field_213875_aA.clear();
      }

      if (this.field_213878_az != null) {
         this.field_213878_az.clear();
      }

   }

   protected void func_213868_a(EntityRayTraceResult p_213868_1_) {
      Entity entity = p_213868_1_.getEntity();
      float f = (float)this.getMotion().length();
      int i = MathHelper.ceil(Math.max((double)f * this.damage, 0.0D));
      if (this.func_213874_s() > 0) {
         if (this.field_213878_az == null) {
            this.field_213878_az = new IntOpenHashSet(5);
         }

         if (this.field_213875_aA == null) {
            this.field_213875_aA = Lists.newArrayListWithCapacity(5);
         }

         if (this.field_213878_az.size() >= this.func_213874_s() + 1) {
            this.remove();
            return;
         }

         this.field_213878_az.add(entity.getEntityId());
      }

      if (this.getIsCritical()) {
         i += this.rand.nextInt(i / 2 + 2);
      }

      Entity entity1 = this.getShooter();
      DamageSource damagesource;
      if (entity1 == null) {
         damagesource = DamageSource.causeArrowDamage(this, this);
      } else {
         damagesource = DamageSource.causeArrowDamage(this, entity1);
         if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastAttackedEntity(entity);
         }
      }

      boolean flag = entity.getType() == EntityType.ENDERMAN;
      int j = entity.func_223314_ad();
      if (this.isBurning() && !flag) {
         entity.setFire(5);
      }

      if (entity.attackEntityFrom(damagesource, (float)i)) {
         if (flag) {
            return;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            if (!this.world.isRemote && this.func_213874_s() <= 0) {
               livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
            }

            if (this.knockbackStrength > 0) {
               Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockbackStrength * 0.6D);
               if (vec3d.lengthSquared() > 0.0D) {
                  livingentity.addVelocity(vec3d.x, 0.1D, vec3d.z);
               }
            }

            if (!this.world.isRemote && entity1 instanceof LivingEntity) {
               EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
               EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity);
            }

            this.arrowHit(livingentity);
            if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)entity1).connection.sendPacket(new SChangeGameStatePacket(6, 0.0F));
            }

            if (!entity.isAlive() && this.field_213875_aA != null) {
               this.field_213875_aA.add(livingentity);
            }

            if (!this.world.isRemote && entity1 instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity1;
               if (this.field_213875_aA != null && this.func_213873_r()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, this.field_213875_aA, this.field_213875_aA.size());
               } else if (!entity.isAlive() && this.func_213873_r()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, Arrays.asList(entity), 0);
               }
            }
         }

         this.playSound(this.field_213877_ay, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         if (this.func_213874_s() <= 0) {
            this.remove();
         }
      } else {
         entity.func_223308_g(j);
         this.setMotion(this.getMotion().scale(-0.1D));
         this.rotationYaw += 180.0F;
         this.prevRotationYaw += 180.0F;
         this.ticksInAir = 0;
         if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
            if (this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
               this.entityDropItem(this.getArrowStack(), 0.1F);
            }

            this.remove();
         }
      }

   }

   protected SoundEvent func_213867_k() {
      return SoundEvents.ENTITY_ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSound() {
      return this.field_213877_ay;
   }

   protected void arrowHit(LivingEntity p_184548_1_) {
   }

   @Nullable
   protected EntityRayTraceResult func_213866_a(Vec3d p_213866_1_, Vec3d p_213866_2_) {
      return ProjectileHelper.func_221271_a(this.world, this, p_213866_1_, p_213866_2_, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_lambda$func_213866_a$0_1_) -> {
         return !p_lambda$func_213866_a$0_1_.isSpectator() && p_lambda$func_213866_a$0_1_.isAlive() && p_lambda$func_213866_a$0_1_.canBeCollidedWith() && (p_lambda$func_213866_a$0_1_ != this.getShooter() || this.ticksInAir >= 5) && (this.field_213878_az == null || !this.field_213878_az.contains(p_lambda$func_213866_a$0_1_.getEntityId()));
      });
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("life", (short)this.ticksInGround);
      if (this.inBlockState != null) {
         p_213281_1_.put("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
      }

      p_213281_1_.putByte("shake", (byte)this.arrowShake);
      p_213281_1_.putBoolean("inGround", this.inGround);
      p_213281_1_.putByte("pickup", (byte)this.pickupStatus.ordinal());
      p_213281_1_.putDouble("damage", this.damage);
      p_213281_1_.putBoolean("crit", this.getIsCritical());
      p_213281_1_.putByte("PierceLevel", this.func_213874_s());
      if (this.shootingEntity != null) {
         p_213281_1_.putUniqueId("OwnerUUID", this.shootingEntity);
      }

      p_213281_1_.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.field_213877_ay).toString());
      p_213281_1_.putBoolean("ShotFromCrossbow", this.func_213873_r());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.ticksInGround = p_70037_1_.getShort("life");
      if (p_70037_1_.contains("inBlockState", 10)) {
         this.inBlockState = NBTUtil.readBlockState(p_70037_1_.getCompound("inBlockState"));
      }

      this.arrowShake = p_70037_1_.getByte("shake") & 255;
      this.inGround = p_70037_1_.getBoolean("inGround");
      if (p_70037_1_.contains("damage", 99)) {
         this.damage = p_70037_1_.getDouble("damage");
      }

      if (p_70037_1_.contains("pickup", 99)) {
         this.pickupStatus = AbstractArrowEntity.PickupStatus.getByOrdinal(p_70037_1_.getByte("pickup"));
      } else if (p_70037_1_.contains("player", 99)) {
         this.pickupStatus = p_70037_1_.getBoolean("player") ? AbstractArrowEntity.PickupStatus.ALLOWED : AbstractArrowEntity.PickupStatus.DISALLOWED;
      }

      this.setIsCritical(p_70037_1_.getBoolean("crit"));
      this.func_213872_b(p_70037_1_.getByte("PierceLevel"));
      if (p_70037_1_.hasUniqueId("OwnerUUID")) {
         this.shootingEntity = p_70037_1_.getUniqueId("OwnerUUID");
      }

      if (p_70037_1_.contains("SoundEvent", 8)) {
         this.field_213877_ay = (SoundEvent)Registry.SOUND_EVENT.getValue(new ResourceLocation(p_70037_1_.getString("SoundEvent"))).orElse(this.func_213867_k());
      }

      this.func_213865_o(p_70037_1_.getBoolean("ShotFromCrossbow"));
   }

   public void setShooter(@Nullable Entity p_212361_1_) {
      this.shootingEntity = p_212361_1_ == null ? null : p_212361_1_.getUniqueID();
      if (p_212361_1_ instanceof PlayerEntity) {
         this.pickupStatus = ((PlayerEntity)p_212361_1_).abilities.isCreativeMode ? AbstractArrowEntity.PickupStatus.CREATIVE_ONLY : AbstractArrowEntity.PickupStatus.ALLOWED;
      }

   }

   @Nullable
   public Entity getShooter() {
      return this.shootingEntity != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntityByUuid(this.shootingEntity) : null;
   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      if (!this.world.isRemote && (this.inGround || this.func_203047_q()) && this.arrowShake <= 0) {
         boolean flag = this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED || this.pickupStatus == AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && p_70100_1_.abilities.isCreativeMode || this.func_203047_q() && this.getShooter().getUniqueID() == p_70100_1_.getUniqueID();
         if (this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED && !p_70100_1_.inventory.addItemStackToInventory(this.getArrowStack())) {
            flag = false;
         }

         if (flag) {
            p_70100_1_.onItemPickup(this, 1);
            this.remove();
         }
      }

   }

   protected abstract ItemStack getArrowStack();

   protected boolean func_225502_at_() {
      return false;
   }

   public void setDamage(double p_70239_1_) {
      this.damage = p_70239_1_;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int p_70240_1_) {
      this.knockbackStrength = p_70240_1_;
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   public void setIsCritical(boolean p_70243_1_) {
      this.func_203049_a(1, p_70243_1_);
   }

   public void func_213872_b(byte p_213872_1_) {
      this.dataManager.set(field_213876_as, p_213872_1_);
   }

   private void func_203049_a(int p_203049_1_, boolean p_203049_2_) {
      byte b0 = (Byte)this.dataManager.get(CRITICAL);
      if (p_203049_2_) {
         this.dataManager.set(CRITICAL, (byte)(b0 | p_203049_1_));
      } else {
         this.dataManager.set(CRITICAL, (byte)(b0 & ~p_203049_1_));
      }

   }

   public boolean getIsCritical() {
      byte b0 = (Byte)this.dataManager.get(CRITICAL);
      return (b0 & 1) != 0;
   }

   public boolean func_213873_r() {
      byte b0 = (Byte)this.dataManager.get(CRITICAL);
      return (b0 & 4) != 0;
   }

   public byte func_213874_s() {
      return (Byte)this.dataManager.get(field_213876_as);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity p_190547_1_, float p_190547_2_) {
      int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, p_190547_1_);
      int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, p_190547_1_);
      this.setDamage((double)(p_190547_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.world.getDifficulty().getId() * 0.11F));
      if (i > 0) {
         this.setDamage(this.getDamage() + (double)i * 0.5D + 0.5D);
      }

      if (j > 0) {
         this.setKnockbackStrength(j);
      }

      if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, p_190547_1_) > 0) {
         this.setFire(100);
      }

   }

   protected float getWaterDrag() {
      return 0.6F;
   }

   public void func_203045_n(boolean p_203045_1_) {
      this.noClip = p_203045_1_;
      this.func_203049_a(2, p_203045_1_);
   }

   public boolean func_203047_q() {
      if (!this.world.isRemote) {
         return this.noClip;
      } else {
         return ((Byte)this.dataManager.get(CRITICAL) & 2) != 0;
      }
   }

   public void func_213865_o(boolean p_213865_1_) {
      this.func_203049_a(4, p_213865_1_);
   }

   public IPacket<?> createSpawnPacket() {
      Entity entity = this.getShooter();
      return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getEntityId());
   }

   static {
      CRITICAL = EntityDataManager.createKey(AbstractArrowEntity.class, DataSerializers.BYTE);
      field_212362_a = EntityDataManager.createKey(AbstractArrowEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
      field_213876_as = EntityDataManager.createKey(AbstractArrowEntity.class, DataSerializers.BYTE);
   }

   public static enum PickupStatus {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static AbstractArrowEntity.PickupStatus getByOrdinal(int p_188795_0_) {
         if (p_188795_0_ < 0 || p_188795_0_ > values().length) {
            p_188795_0_ = 0;
         }

         return values()[p_188795_0_];
      }
   }
}
