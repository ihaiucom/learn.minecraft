package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class ThrowableEntity extends Entity implements IProjectile {
   private int xTile;
   private int yTile;
   private int zTile;
   protected boolean inGround;
   public int throwableShake;
   protected LivingEntity owner;
   private UUID ownerId;
   private Entity ignoreEntity;
   private int ignoreTime;

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48540_1_, World p_i48540_2_) {
      super(p_i48540_1_, p_i48540_2_);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48541_1_, double p_i48541_2_, double p_i48541_4_, double p_i48541_6_, World p_i48541_8_) {
      this(p_i48541_1_, p_i48541_8_);
      this.setPosition(p_i48541_2_, p_i48541_4_, p_i48541_6_);
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48542_1_, LivingEntity p_i48542_2_, World p_i48542_3_) {
      this(p_i48542_1_, p_i48542_2_.func_226277_ct_(), p_i48542_2_.func_226280_cw_() - 0.10000000149011612D, p_i48542_2_.func_226281_cx_(), p_i48542_3_);
      this.owner = p_i48542_2_;
      this.ownerId = p_i48542_2_.getUniqueID();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   public void shoot(Entity p_184538_1_, float p_184538_2_, float p_184538_3_, float p_184538_4_, float p_184538_5_, float p_184538_6_) {
      float f = -MathHelper.sin(p_184538_3_ * 0.017453292F) * MathHelper.cos(p_184538_2_ * 0.017453292F);
      float f1 = -MathHelper.sin((p_184538_2_ + p_184538_4_) * 0.017453292F);
      float f2 = MathHelper.cos(p_184538_3_ * 0.017453292F) * MathHelper.cos(p_184538_2_ * 0.017453292F);
      this.shoot((double)f, (double)f1, (double)f2, p_184538_5_, p_184538_6_);
      Vec3d vec3d = p_184538_1_.getMotion();
      this.setMotion(this.getMotion().add(vec3d.x, p_184538_1_.onGround ? 0.0D : vec3d.y, vec3d.z));
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setMotion(vec3d);
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
      this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setMotion(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * 57.2957763671875D);
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * 57.2957763671875D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   public void tick() {
      super.tick();
      if (this.throwableShake > 0) {
         --this.throwableShake;
      }

      if (this.inGround) {
         this.inGround = false;
         this.setMotion(this.getMotion().mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
      }

      AxisAlignedBB axisalignedbb = this.getBoundingBox().expand(this.getMotion()).grow(1.0D);
      Iterator var2 = this.world.getEntitiesInAABBexcluding(this, axisalignedbb, (p_lambda$tick$0_0_) -> {
         return !p_lambda$tick$0_0_.isSpectator() && p_lambda$tick$0_0_.canBeCollidedWith();
      }).iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity == this.ignoreEntity) {
            ++this.ignoreTime;
            break;
         }

         if (this.owner != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
            this.ignoreEntity = entity;
            this.ignoreTime = 3;
            break;
         }
      }

      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, axisalignedbb, (p_lambda$tick$1_1_) -> {
         return !p_lambda$tick$1_1_.isSpectator() && p_lambda$tick$1_1_.canBeCollidedWith() && p_lambda$tick$1_1_ != this.ignoreEntity;
      }, RayTraceContext.BlockMode.OUTLINE, true);
      if (this.ignoreEntity != null && this.ignoreTime-- <= 0) {
         this.ignoreEntity = null;
      }

      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && this.world.getBlockState(((BlockRayTraceResult)raytraceresult).getPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.setPortal(((BlockRayTraceResult)raytraceresult).getPos());
         } else if (!ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }
      }

      Vec3d vec3d = this.getMotion();
      double d0 = this.func_226277_ct_() + vec3d.x;
      double d1 = this.func_226278_cu_() + vec3d.y;
      double d2 = this.func_226281_cx_() + vec3d.z;
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);

      for(this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
      float f1;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            float f2 = 0.25F;
            this.world.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
         }

         f1 = 0.8F;
      } else {
         f1 = 0.99F;
      }

      this.setMotion(vec3d.scale((double)f1));
      if (!this.hasNoGravity()) {
         Vec3d vec3d1 = this.getMotion();
         this.setMotion(vec3d1.x, vec3d1.y - (double)this.getGravityVelocity(), vec3d1.z);
      }

      this.setPosition(d0, d1, d2);
   }

   protected float getGravityVelocity() {
      return 0.03F;
   }

   protected abstract void onImpact(RayTraceResult var1);

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("xTile", this.xTile);
      p_213281_1_.putInt("yTile", this.yTile);
      p_213281_1_.putInt("zTile", this.zTile);
      p_213281_1_.putByte("shake", (byte)this.throwableShake);
      p_213281_1_.putBoolean("inGround", this.inGround);
      if (this.ownerId != null) {
         p_213281_1_.put("owner", NBTUtil.writeUniqueId(this.ownerId));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.xTile = p_70037_1_.getInt("xTile");
      this.yTile = p_70037_1_.getInt("yTile");
      this.zTile = p_70037_1_.getInt("zTile");
      this.throwableShake = p_70037_1_.getByte("shake") & 255;
      this.inGround = p_70037_1_.getBoolean("inGround");
      this.owner = null;
      if (p_70037_1_.contains("owner", 10)) {
         this.ownerId = NBTUtil.readUniqueId(p_70037_1_.getCompound("owner"));
      }

   }

   @Nullable
   public LivingEntity getThrower() {
      if ((this.owner == null || this.owner.removed) && this.ownerId != null && this.world instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.ownerId);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         } else {
            this.owner = null;
         }
      }

      return this.owner;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}
