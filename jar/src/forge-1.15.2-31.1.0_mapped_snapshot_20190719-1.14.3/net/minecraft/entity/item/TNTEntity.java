package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTEntity extends Entity {
   private static final DataParameter<Integer> FUSE;
   @Nullable
   private LivingEntity tntPlacedBy;
   private int fuse;

   public TNTEntity(EntityType<? extends TNTEntity> p_i50216_1_, World p_i50216_2_) {
      super(p_i50216_1_, p_i50216_2_);
      this.fuse = 80;
      this.preventEntitySpawning = true;
   }

   public TNTEntity(World p_i1730_1_, double p_i1730_2_, double p_i1730_4_, double p_i1730_6_, @Nullable LivingEntity p_i1730_8_) {
      this(EntityType.TNT, p_i1730_1_);
      this.setPosition(p_i1730_2_, p_i1730_4_, p_i1730_6_);
      double lvt_9_1_ = p_i1730_1_.rand.nextDouble() * 6.2831854820251465D;
      this.setMotion(-Math.sin(lvt_9_1_) * 0.02D, 0.20000000298023224D, -Math.cos(lvt_9_1_) * 0.02D);
      this.setFuse(80);
      this.prevPosX = p_i1730_2_;
      this.prevPosY = p_i1730_4_;
      this.prevPosZ = p_i1730_6_;
      this.tntPlacedBy = p_i1730_8_;
   }

   protected void registerData() {
      this.dataManager.register(FUSE, 80);
   }

   protected boolean func_225502_at_() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   public void tick() {
      if (!this.hasNoGravity()) {
         this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
      }

      this.move(MoverType.SELF, this.getMotion());
      this.setMotion(this.getMotion().scale(0.98D));
      if (this.onGround) {
         this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
      }

      --this.fuse;
      if (this.fuse <= 0) {
         this.remove();
         if (!this.world.isRemote) {
            this.explode();
         }
      } else {
         this.handleWaterMovement();
         if (this.world.isRemote) {
            this.world.addParticle(ParticleTypes.SMOKE, this.func_226277_ct_(), this.func_226278_cu_() + 0.5D, this.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void explode() {
      float lvt_1_1_ = 4.0F;
      this.world.createExplosion(this, this.func_226277_ct_(), this.func_226283_e_(0.0625D), this.func_226281_cx_(), 4.0F, Explosion.Mode.BREAK);
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Fuse", (short)this.getFuse());
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      this.setFuse(p_70037_1_.getShort("Fuse"));
   }

   @Nullable
   public LivingEntity getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   public void setFuse(int p_184534_1_) {
      this.dataManager.set(FUSE, p_184534_1_);
      this.fuse = p_184534_1_;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (FUSE.equals(p_184206_1_)) {
         this.fuse = this.getFuseDataManager();
      }

   }

   public int getFuseDataManager() {
      return (Integer)this.dataManager.get(FUSE);
   }

   public int getFuse() {
      return this.fuse;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   static {
      FUSE = EntityDataManager.createKey(TNTEntity.class, DataSerializers.VARINT);
   }
}
