package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EnderPearlEntity extends ProjectileItemEntity {
   private LivingEntity perlThrower;

   public EnderPearlEntity(EntityType<? extends EnderPearlEntity> p_i50153_1_, World p_i50153_2_) {
      super(p_i50153_1_, p_i50153_2_);
   }

   public EnderPearlEntity(World p_i1783_1_, LivingEntity p_i1783_2_) {
      super(EntityType.ENDER_PEARL, p_i1783_2_, p_i1783_1_);
      this.perlThrower = p_i1783_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public EnderPearlEntity(World p_i1784_1_, double p_i1784_2_, double p_i1784_4_, double p_i1784_6_) {
      super(EntityType.ENDER_PEARL, p_i1784_2_, p_i1784_4_, p_i1784_6_, p_i1784_1_);
   }

   protected Item func_213885_i() {
      return Items.ENDER_PEARL;
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      LivingEntity livingentity = this.getThrower();
      if (p_70184_1_.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)p_70184_1_).getEntity();
         if (entity == this.perlThrower) {
            return;
         }

         entity.attackEntityFrom(DamageSource.causeThrownDamage(this, livingentity), 0.0F);
      }

      if (p_70184_1_.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)p_70184_1_).getPos();
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity instanceof EndGatewayTileEntity) {
            EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
            if (livingentity != null) {
               if (livingentity instanceof ServerPlayerEntity) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayerEntity)livingentity, this.world.getBlockState(blockpos));
               }

               endgatewaytileentity.teleportEntity(livingentity);
               this.remove();
               return;
            }

            endgatewaytileentity.teleportEntity(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.world.addParticle(ParticleTypes.PORTAL, this.func_226277_ct_(), this.func_226278_cu_() + this.rand.nextDouble() * 2.0D, this.func_226281_cx_(), this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
      }

      if (!this.world.isRemote) {
         if (livingentity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
            if (serverplayerentity.connection.getNetworkManager().isChannelOpen() && serverplayerentity.world == this.world && !serverplayerentity.isSleeping()) {
               EnderTeleportEvent event = new EnderTeleportEvent(serverplayerentity, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 5.0F);
               if (!MinecraftForge.EVENT_BUS.post(event)) {
                  if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                     EndermiteEntity endermiteentity = (EndermiteEntity)EntityType.ENDERMITE.create(this.world);
                     endermiteentity.setSpawnedByPlayer(true);
                     endermiteentity.setLocationAndAngles(livingentity.func_226277_ct_(), livingentity.func_226278_cu_(), livingentity.func_226281_cx_(), livingentity.rotationYaw, livingentity.rotationPitch);
                     this.world.addEntity(endermiteentity);
                  }

                  if (livingentity.isPassenger()) {
                     livingentity.stopRiding();
                  }

                  livingentity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                  livingentity.fallDistance = 0.0F;
                  livingentity.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
               }
            }
         } else if (livingentity != null) {
            livingentity.setPositionAndUpdate(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
            livingentity.fallDistance = 0.0F;
         }

         this.remove();
      }

   }

   public void tick() {
      LivingEntity livingentity = this.getThrower();
      if (livingentity != null && livingentity instanceof PlayerEntity && !livingentity.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      if (this.owner.dimension != p_changeDimension_1_) {
         this.owner = null;
      }

      return super.changeDimension(p_changeDimension_1_, p_changeDimension_2_);
   }
}
