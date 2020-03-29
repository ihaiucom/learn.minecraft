package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EvokerFangsEntity extends Entity {
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks;
   private boolean clientSideAttackStarted;
   private LivingEntity caster;
   private UUID casterUuid;

   public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> p_i50170_1_, World p_i50170_2_) {
      super(p_i50170_1_, p_i50170_2_);
      this.lifeTicks = 22;
   }

   public EvokerFangsEntity(World p_i47276_1_, double p_i47276_2_, double p_i47276_4_, double p_i47276_6_, float p_i47276_8_, int p_i47276_9_, LivingEntity p_i47276_10_) {
      this(EntityType.EVOKER_FANGS, p_i47276_1_);
      this.warmupDelayTicks = p_i47276_9_;
      this.setCaster(p_i47276_10_);
      this.rotationYaw = p_i47276_8_ * 57.295776F;
      this.setPosition(p_i47276_2_, p_i47276_4_, p_i47276_6_);
   }

   protected void registerData() {
   }

   public void setCaster(@Nullable LivingEntity p_190549_1_) {
      this.caster = p_190549_1_;
      this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
   }

   @Nullable
   public LivingEntity getCaster() {
      if (this.caster == null && this.casterUuid != null && this.world instanceof ServerWorld) {
         Entity lvt_1_1_ = ((ServerWorld)this.world).getEntityByUuid(this.casterUuid);
         if (lvt_1_1_ instanceof LivingEntity) {
            this.caster = (LivingEntity)lvt_1_1_;
         }
      }

      return this.caster;
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      this.warmupDelayTicks = p_70037_1_.getInt("Warmup");
      if (p_70037_1_.hasUniqueId("OwnerUUID")) {
         this.casterUuid = p_70037_1_.getUniqueId("OwnerUUID");
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("Warmup", this.warmupDelayTicks);
      if (this.casterUuid != null) {
         p_213281_1_.putUniqueId("OwnerUUID", this.casterUuid);
      }

   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int lvt_1_1_ = 0; lvt_1_1_ < 12; ++lvt_1_1_) {
                  double lvt_2_1_ = this.func_226277_ct_() + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.getWidth() * 0.5D;
                  double lvt_4_1_ = this.func_226278_cu_() + 0.05D + this.rand.nextDouble();
                  double lvt_6_1_ = this.func_226281_cx_() + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.getWidth() * 0.5D;
                  double lvt_8_1_ = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double lvt_10_1_ = 0.3D + this.rand.nextDouble() * 0.3D;
                  double lvt_12_1_ = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.world.addParticle(ParticleTypes.CRIT, lvt_2_1_, lvt_4_1_ + 1.0D, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            List<LivingEntity> lvt_1_2_ = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D));
            Iterator var15 = lvt_1_2_.iterator();

            while(var15.hasNext()) {
               LivingEntity lvt_3_1_ = (LivingEntity)var15.next();
               this.damage(lvt_3_1_);
            }
         }

         if (!this.sentSpikeEvent) {
            this.world.setEntityState(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.remove();
         }
      }

   }

   private void damage(LivingEntity p_190551_1_) {
      LivingEntity lvt_2_1_ = this.getCaster();
      if (p_190551_1_.isAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != lvt_2_1_) {
         if (lvt_2_1_ == null) {
            p_190551_1_.attackEntityFrom(DamageSource.MAGIC, 6.0F);
         } else {
            if (lvt_2_1_.isOnSameTeam(p_190551_1_)) {
               return;
            }

            p_190551_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, lvt_2_1_), 6.0F);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      super.handleStatusUpdate(p_70103_1_);
      if (p_70103_1_ == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float p_190550_1_) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int lvt_2_1_ = this.lifeTicks - 2;
         return lvt_2_1_ <= 0 ? 1.0F : 1.0F - ((float)lvt_2_1_ - p_190550_1_) / 20.0F;
      }
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}
