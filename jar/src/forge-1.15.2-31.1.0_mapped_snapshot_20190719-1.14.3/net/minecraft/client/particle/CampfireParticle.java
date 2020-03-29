package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireParticle extends SpriteTexturedParticle {
   private CampfireParticle(World p_i51046_1_, double p_i51046_2_, double p_i51046_4_, double p_i51046_6_, double p_i51046_8_, double p_i51046_10_, double p_i51046_12_, boolean p_i51046_14_) {
      super(p_i51046_1_, p_i51046_2_, p_i51046_4_, p_i51046_6_);
      this.multipleParticleScaleBy(3.0F);
      this.setSize(0.25F, 0.25F);
      if (p_i51046_14_) {
         this.maxAge = this.rand.nextInt(50) + 280;
      } else {
         this.maxAge = this.rand.nextInt(50) + 80;
      }

      this.particleGravity = 3.0E-6F;
      this.motionX = p_i51046_8_;
      this.motionY = p_i51046_10_ + (double)(this.rand.nextFloat() / 500.0F);
      this.motionZ = p_i51046_12_;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ < this.maxAge && this.particleAlpha > 0.0F) {
         this.motionX += (double)(this.rand.nextFloat() / 5000.0F * (float)(this.rand.nextBoolean() ? 1 : -1));
         this.motionZ += (double)(this.rand.nextFloat() / 5000.0F * (float)(this.rand.nextBoolean() ? 1 : -1));
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.age >= this.maxAge - 60 && this.particleAlpha > 0.01F) {
            this.particleAlpha -= 0.015F;
         }

      } else {
         this.setExpired();
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   // $FF: synthetic method
   CampfireParticle(World p_i51047_1_, double p_i51047_2_, double p_i51047_4_, double p_i51047_6_, double p_i51047_8_, double p_i51047_10_, double p_i51047_12_, boolean p_i51047_14_, Object p_i51047_15_) {
      this(p_i51047_1_, p_i51047_2_, p_i51047_4_, p_i51047_6_, p_i51047_8_, p_i51047_10_, p_i51047_12_, p_i51047_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class SignalSmokeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public SignalSmokeFactory(IAnimatedSprite p_i51179_1_) {
         this.spriteSet = p_i51179_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CampfireParticle lvt_15_1_ = new CampfireParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, true);
         lvt_15_1_.setAlphaF(0.95F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CozySmokeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public CozySmokeFactory(IAnimatedSprite p_i51180_1_) {
         this.spriteSet = p_i51180_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CampfireParticle lvt_15_1_ = new CampfireParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, false);
         lvt_15_1_.setAlphaF(0.9F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
