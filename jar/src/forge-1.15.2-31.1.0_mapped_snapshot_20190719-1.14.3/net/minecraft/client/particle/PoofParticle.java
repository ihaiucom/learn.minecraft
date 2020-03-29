package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoofParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217581_C;

   protected PoofParticle(World p_i51035_1_, double p_i51035_2_, double p_i51035_4_, double p_i51035_6_, double p_i51035_8_, double p_i51035_10_, double p_i51035_12_, IAnimatedSprite p_i51035_14_) {
      super(p_i51035_1_, p_i51035_2_, p_i51035_4_, p_i51035_6_);
      this.field_217581_C = p_i51035_14_;
      this.motionX = p_i51035_8_ + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.motionY = p_i51035_10_ + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.motionZ = p_i51035_12_ + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      float lvt_15_1_ = this.rand.nextFloat() * 0.3F + 0.7F;
      this.particleRed = lvt_15_1_;
      this.particleGreen = lvt_15_1_;
      this.particleBlue = lvt_15_1_;
      this.particleScale = 0.1F * (this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F);
      this.maxAge = (int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
      this.selectSpriteWithAge(p_i51035_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217581_C);
         this.motionY += 0.004D;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.8999999761581421D;
         this.motionY *= 0.8999999761581421D;
         this.motionZ *= 0.8999999761581421D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i49913_1_) {
         this.spriteSet = p_i49913_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new PoofParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
