package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CritParticle extends SpriteTexturedParticle {
   private CritParticle(World p_i46284_1_, double p_i46284_2_, double p_i46284_4_, double p_i46284_6_, double p_i46284_8_, double p_i46284_10_, double p_i46284_12_) {
      super(p_i46284_1_, p_i46284_2_, p_i46284_4_, p_i46284_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= 0.10000000149011612D;
      this.motionY *= 0.10000000149011612D;
      this.motionZ *= 0.10000000149011612D;
      this.motionX += p_i46284_8_ * 0.4D;
      this.motionY += p_i46284_10_ * 0.4D;
      this.motionZ += p_i46284_12_ * 0.4D;
      float lvt_14_1_ = (float)(Math.random() * 0.30000001192092896D + 0.6000000238418579D);
      this.particleRed = lvt_14_1_;
      this.particleGreen = lvt_14_1_;
      this.particleBlue = lvt_14_1_;
      this.particleScale *= 0.75F;
      this.maxAge = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
      this.canCollide = false;
      this.tick();
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
         this.particleGreen = (float)((double)this.particleGreen * 0.96D);
         this.particleBlue = (float)((double)this.particleBlue * 0.9D);
         this.motionX *= 0.699999988079071D;
         this.motionY *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
         this.motionY -= 0.019999999552965164D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   // $FF: synthetic method
   CritParticle(World p_i51044_1_, double p_i51044_2_, double p_i51044_4_, double p_i51044_6_, double p_i51044_8_, double p_i51044_10_, double p_i51044_12_, Object p_i51044_14_) {
      this(p_i51044_1_, p_i51044_2_, p_i51044_4_, p_i51044_6_, p_i51044_8_, p_i51044_10_, p_i51044_12_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class DamageIndicatorFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public DamageIndicatorFactory(IAnimatedSprite p_i50589_1_) {
         this.spriteSet = p_i50589_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CritParticle lvt_15_1_ = new CritParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_ + 1.0D, p_199234_13_);
         lvt_15_1_.setMaxAge(20);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MagicFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public MagicFactory(IAnimatedSprite p_i50588_1_) {
         this.spriteSet = p_i50588_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CritParticle lvt_15_1_ = new CritParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.particleRed *= 0.3F;
         lvt_15_1_.particleGreen *= 0.8F;
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50587_1_) {
         this.spriteSet = p_i50587_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CritParticle lvt_15_1_ = new CritParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
