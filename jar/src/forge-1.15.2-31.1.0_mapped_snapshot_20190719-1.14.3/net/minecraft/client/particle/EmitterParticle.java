package net.minecraft.client.particle;

import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmitterParticle extends MetaParticle {
   private final Entity attachedEntity;
   private int age;
   private final int lifetime;
   private final IParticleData particleTypes;

   public EmitterParticle(World p_i47638_1_, Entity p_i47638_2_, IParticleData p_i47638_3_) {
      this(p_i47638_1_, p_i47638_2_, p_i47638_3_, 3);
   }

   public EmitterParticle(World p_i47639_1_, Entity p_i47639_2_, IParticleData p_i47639_3_, int p_i47639_4_) {
      this(p_i47639_1_, p_i47639_2_, p_i47639_3_, p_i47639_4_, p_i47639_2_.getMotion());
   }

   private EmitterParticle(World p_i50995_1_, Entity p_i50995_2_, IParticleData p_i50995_3_, int p_i50995_4_, Vec3d p_i50995_5_) {
      super(p_i50995_1_, p_i50995_2_.func_226277_ct_(), p_i50995_2_.func_226283_e_(0.5D), p_i50995_2_.func_226281_cx_(), p_i50995_5_.x, p_i50995_5_.y, p_i50995_5_.z);
      this.attachedEntity = p_i50995_2_;
      this.lifetime = p_i50995_4_;
      this.particleTypes = p_i50995_3_;
      this.tick();
   }

   public void tick() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 16; ++lvt_1_1_) {
         double lvt_2_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double lvt_4_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double lvt_6_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         if (lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ <= 1.0D) {
            double lvt_8_1_ = this.attachedEntity.func_226275_c_(lvt_2_1_ / 4.0D);
            double lvt_10_1_ = this.attachedEntity.func_226283_e_(0.5D + lvt_4_1_ / 4.0D);
            double lvt_12_1_ = this.attachedEntity.func_226285_f_(lvt_6_1_ / 4.0D);
            this.world.addParticle(this.particleTypes, false, lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_2_1_, lvt_4_1_ + 0.2D, lvt_6_1_);
         }
      }

      ++this.age;
      if (this.age >= this.lifetime) {
         this.setExpired();
      }

   }
}
