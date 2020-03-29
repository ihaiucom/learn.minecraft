package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class WitherSkullEntity extends DamagingProjectileEntity {
   private static final DataParameter<Boolean> INVULNERABLE;

   public WitherSkullEntity(EntityType<? extends WitherSkullEntity> p_i50147_1_, World p_i50147_2_) {
      super(p_i50147_1_, p_i50147_2_);
   }

   public WitherSkullEntity(World p_i1794_1_, LivingEntity p_i1794_2_, double p_i1794_3_, double p_i1794_5_, double p_i1794_7_) {
      super(EntityType.WITHER_SKULL, p_i1794_2_, p_i1794_3_, p_i1794_5_, p_i1794_7_, p_i1794_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public WitherSkullEntity(World p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
      super(EntityType.WITHER_SKULL, p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
   }

   protected float getMotionFactor() {
      return this.isSkullInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public boolean isBurning() {
      return false;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return this.isSkullInvulnerable() && p_180428_4_.canEntityDestroy(p_180428_2_, p_180428_3_, this) ? Math.min(0.8F, p_180428_6_) : p_180428_6_;
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      super.onImpact(p_70227_1_);
      if (!this.world.isRemote) {
         if (p_70227_1_.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)p_70227_1_).getEntity();
            if (this.shootingEntity != null) {
               if (entity.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F)) {
                  if (entity.isAlive()) {
                     this.applyEnchantments(this.shootingEntity, entity);
                  } else {
                     this.shootingEntity.heal(5.0F);
                  }
               }
            } else {
               entity.attackEntityFrom(DamageSource.MAGIC, 5.0F);
            }

            if (entity instanceof LivingEntity) {
               int i = 0;
               if (this.world.getDifficulty() == Difficulty.NORMAL) {
                  i = 10;
               } else if (this.world.getDifficulty() == Difficulty.HARD) {
                  i = 40;
               }

               if (i > 0) {
                  ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.WITHER, 20 * i, 1));
               }
            }
         }

         Explosion.Mode explosion$mode = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
         this.world.createExplosion(this, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 1.0F, false, explosion$mode);
         this.remove();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(INVULNERABLE, false);
   }

   public boolean isSkullInvulnerable() {
      return (Boolean)this.dataManager.get(INVULNERABLE);
   }

   public void setSkullInvulnerable(boolean p_82343_1_) {
      this.dataManager.set(INVULNERABLE, p_82343_1_);
   }

   protected boolean isFireballFiery() {
      return false;
   }

   static {
      INVULNERABLE = EntityDataManager.createKey(WitherSkullEntity.class, DataSerializers.BOOLEAN);
   }
}
