package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeartParticle extends SpriteTexturedParticle {
   private HeartParticle(World p_i51030_1_, double p_i51030_2_, double p_i51030_4_, double p_i51030_6_) {
      super(p_i51030_1_, p_i51030_2_, p_i51030_4_, p_i51030_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= 0.009999999776482582D;
      this.motionY *= 0.009999999776482582D;
      this.motionZ *= 0.009999999776482582D;
      this.motionY += 0.1D;
      this.particleScale *= 1.5F;
      this.maxAge = 16;
      this.canCollide = false;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getScale(float p_217561_1_) {
      return this.particleScale * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= 0.8600000143051147D;
         this.motionY *= 0.8600000143051147D;
         this.motionZ *= 0.8600000143051147D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   HeartParticle(World p_i51031_1_, double p_i51031_2_, double p_i51031_4_, double p_i51031_6_, Object p_i51031_8_) {
      this(p_i51031_1_, p_i51031_2_, p_i51031_4_, p_i51031_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class AngryVillagerFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public AngryVillagerFactory(IAnimatedSprite p_i50748_1_) {
         this.spriteSet = p_i50748_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         HeartParticle lvt_15_1_ = new HeartParticle(p_199234_2_, p_199234_3_, p_199234_5_ + 0.5D, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         lvt_15_1_.setColor(1.0F, 1.0F, 1.0F);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50747_1_) {
         this.spriteSet = p_i50747_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         HeartParticle lvt_15_1_ = new HeartParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
