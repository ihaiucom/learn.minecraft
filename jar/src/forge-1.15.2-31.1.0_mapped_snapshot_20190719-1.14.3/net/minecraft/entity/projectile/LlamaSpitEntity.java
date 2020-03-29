package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class LlamaSpitEntity extends Entity implements IProjectile {
   public LlamaEntity owner;
   private CompoundNBT ownerNbt;

   public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> p_i50162_1_, World p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public LlamaSpitEntity(World p_i47273_1_, LlamaEntity p_i47273_2_) {
      this(EntityType.LLAMA_SPIT, p_i47273_1_);
      this.owner = p_i47273_2_;
      this.setPosition(p_i47273_2_.func_226277_ct_() - (double)(p_i47273_2_.getWidth() + 1.0F) * 0.5D * (double)MathHelper.sin(p_i47273_2_.renderYawOffset * 0.017453292F), p_i47273_2_.func_226280_cw_() - 0.10000000149011612D, p_i47273_2_.func_226281_cx_() + (double)(p_i47273_2_.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(p_i47273_2_.renderYawOffset * 0.017453292F));
   }

   @OnlyIn(Dist.CLIENT)
   public LlamaSpitEntity(World p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(EntityType.LLAMA_SPIT, p_i47274_1_);
      this.setPosition(p_i47274_2_, p_i47274_4_, p_i47274_6_);

      for(int i = 0; i < 7; ++i) {
         double d0 = 0.4D + 0.1D * (double)i;
         p_i47274_1_.addParticle(ParticleTypes.SPIT, p_i47274_2_, p_i47274_4_, p_i47274_6_, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
      }

      this.setMotion(p_i47274_8_, p_i47274_10_, p_i47274_12_);
   }

   public void tick() {
      super.tick();
      if (this.ownerNbt != null) {
         this.restoreOwnerFromSave();
      }

      Vec3d vec3d = this.getMotion();
      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, this.getBoundingBox().expand(vec3d).grow(1.0D), (p_lambda$tick$0_1_) -> {
         return !p_lambda$tick$0_1_.isSpectator() && p_lambda$tick$0_1_ != this.owner;
      }, RayTraceContext.BlockMode.OUTLINE, true);
      if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact((Entity)this, raytraceresult)) {
         this.onHit(raytraceresult);
      }

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
      float f1 = 0.99F;
      float f2 = 0.06F;
      if (!this.world.isMaterialInBB(this.getBoundingBox(), Material.AIR)) {
         this.remove();
      } else if (this.isInWaterOrBubbleColumn()) {
         this.remove();
      } else {
         this.setMotion(vec3d.scale(0.9900000095367432D));
         if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.05999999865889549D, 0.0D));
         }

         this.setPosition(d0, d1, d2);
      }

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
      }

   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setMotion(vec3d);
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, p_70186_5_) * 57.2957763671875D);
      this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public void onHit(RayTraceResult p_190536_1_) {
      RayTraceResult.Type raytraceresult$type = p_190536_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.ENTITY && this.owner != null) {
         ((EntityRayTraceResult)p_190536_1_).getEntity().attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 1.0F);
      } else if (raytraceresult$type == RayTraceResult.Type.BLOCK && !this.world.isRemote) {
         this.remove();
      }

   }

   protected void registerData() {
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("Owner", 10)) {
         this.ownerNbt = p_70037_1_.getCompound("Owner");
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      if (this.owner != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         UUID uuid = this.owner.getUniqueID();
         compoundnbt.putUniqueId("OwnerUUID", uuid);
         p_213281_1_.put("Owner", compoundnbt);
      }

   }

   private void restoreOwnerFromSave() {
      if (this.ownerNbt != null && this.ownerNbt.hasUniqueId("OwnerUUID")) {
         UUID uuid = this.ownerNbt.getUniqueId("OwnerUUID");
         Iterator var2 = this.world.getEntitiesWithinAABB(LlamaEntity.class, this.getBoundingBox().grow(15.0D)).iterator();

         while(var2.hasNext()) {
            LlamaEntity llamaentity = (LlamaEntity)var2.next();
            if (llamaentity.getUniqueID().equals(uuid)) {
               this.owner = llamaentity;
               break;
            }
         }
      }

      this.ownerNbt = null;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}
