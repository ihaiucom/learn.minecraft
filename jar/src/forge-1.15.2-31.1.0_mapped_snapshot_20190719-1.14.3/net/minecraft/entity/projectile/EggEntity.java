package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EggEntity extends ProjectileItemEntity {
   public EggEntity(EntityType<? extends EggEntity> p_i50154_1_, World p_i50154_2_) {
      super(p_i50154_1_, p_i50154_2_);
   }

   public EggEntity(World p_i1780_1_, LivingEntity p_i1780_2_) {
      super(EntityType.EGG, p_i1780_2_, p_i1780_1_);
   }

   public EggEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
      super(EntityType.EGG, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 3) {
         double lvt_2_1_ = 0.08D;

         for(int lvt_4_1_ = 0; lvt_4_1_ < 8; ++lvt_4_1_) {
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (p_70184_1_.getType() == RayTraceResult.Type.ENTITY) {
         ((EntityRayTraceResult)p_70184_1_).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.world.isRemote) {
         if (this.rand.nextInt(8) == 0) {
            int lvt_2_1_ = 1;
            if (this.rand.nextInt(32) == 0) {
               lvt_2_1_ = 4;
            }

            for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
               ChickenEntity lvt_4_1_ = (ChickenEntity)EntityType.CHICKEN.create(this.world);
               lvt_4_1_.setGrowingAge(-24000);
               lvt_4_1_.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, 0.0F);
               this.world.addEntity(lvt_4_1_);
            }
         }

         this.world.setEntityState(this, (byte)3);
         this.remove();
      }

   }

   protected Item func_213885_i() {
      return Items.EGG;
   }
}
