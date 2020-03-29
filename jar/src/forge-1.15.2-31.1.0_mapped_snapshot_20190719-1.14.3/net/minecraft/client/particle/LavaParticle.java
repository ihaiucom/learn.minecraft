package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends SpriteTexturedParticle {
   private LavaParticle(World p_i1215_1_, double p_i1215_2_, double p_i1215_4_, double p_i1215_6_) {
      super(p_i1215_1_, p_i1215_2_, p_i1215_4_, p_i1215_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= 0.800000011920929D;
      this.motionY *= 0.800000011920929D;
      this.motionZ *= 0.800000011920929D;
      this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
      this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
      this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int lvt_2_1_ = super.getBrightnessForRender(p_189214_1_);
      int lvt_3_1_ = true;
      int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
      return 240 | lvt_4_1_ << 16;
   }

   public float getScale(float p_217561_1_) {
      float lvt_2_1_ = ((float)this.age + p_217561_1_) / (float)this.maxAge;
      return this.particleScale * (1.0F - lvt_2_1_ * lvt_2_1_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float lvt_1_1_ = (float)this.age / (float)this.maxAge;
      if (this.rand.nextFloat() > lvt_1_1_) {
         this.world.addParticle(ParticleTypes.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= 0.03D;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9990000128746033D;
         this.motionY *= 0.9990000128746033D;
         this.motionZ *= 0.9990000128746033D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   LavaParticle(World p_i51023_1_, double p_i51023_2_, double p_i51023_4_, double p_i51023_6_, Object p_i51023_8_) {
      this(p_i51023_1_, p_i51023_2_, p_i51023_4_, p_i51023_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50495_1_) {
         this.spriteSet = p_i50495_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         LavaParticle lvt_15_1_ = new LavaParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
