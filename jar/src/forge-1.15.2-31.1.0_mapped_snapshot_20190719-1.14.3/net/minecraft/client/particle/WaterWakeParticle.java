package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterWakeParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217589_C;

   private WaterWakeParticle(World p_i50993_1_, double p_i50993_2_, double p_i50993_4_, double p_i50993_6_, double p_i50993_8_, double p_i50993_10_, double p_i50993_12_, IAnimatedSprite p_i50993_14_) {
      super(p_i50993_1_, p_i50993_2_, p_i50993_4_, p_i50993_6_, 0.0D, 0.0D, 0.0D);
      this.field_217589_C = p_i50993_14_;
      this.motionX *= 0.30000001192092896D;
      this.motionY = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.motionZ *= 0.30000001192092896D;
      this.setSize(0.01F, 0.01F);
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.selectSpriteWithAge(p_i50993_14_);
      this.particleGravity = 0.0F;
      this.motionX = p_i50993_8_;
      this.motionY = p_i50993_10_;
      this.motionZ = p_i50993_12_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      int lvt_1_1_ = 60 - this.maxAge;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      } else {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9800000190734863D;
         this.motionY *= 0.9800000190734863D;
         this.motionZ *= 0.9800000190734863D;
         float lvt_2_1_ = (float)lvt_1_1_ * 0.001F;
         this.setSize(lvt_2_1_, lvt_2_1_);
         this.setSprite(this.field_217589_C.get(lvt_1_1_ % 4, 4));
      }
   }

   // $FF: synthetic method
   WaterWakeParticle(World p_i50994_1_, double p_i50994_2_, double p_i50994_4_, double p_i50994_6_, double p_i50994_8_, double p_i50994_10_, double p_i50994_12_, IAnimatedSprite p_i50994_14_, Object p_i50994_15_) {
      this(p_i50994_1_, p_i50994_2_, p_i50994_4_, p_i50994_6_, p_i50994_8_, p_i50994_10_, p_i50994_12_, p_i50994_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i51267_1_) {
         this.spriteSet = p_i51267_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new WaterWakeParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
