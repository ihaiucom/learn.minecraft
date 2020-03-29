package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SweepAttackParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217570_C;

   private SweepAttackParticle(World p_i51054_1_, double p_i51054_2_, double p_i51054_4_, double p_i51054_6_, double p_i51054_8_, IAnimatedSprite p_i51054_10_) {
      super(p_i51054_1_, p_i51054_2_, p_i51054_4_, p_i51054_6_, 0.0D, 0.0D, 0.0D);
      this.field_217570_C = p_i51054_10_;
      this.maxAge = 4;
      float lvt_11_1_ = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = lvt_11_1_;
      this.particleGreen = lvt_11_1_;
      this.particleBlue = lvt_11_1_;
      this.particleScale = 1.0F - (float)p_i51054_8_ * 0.5F;
      this.selectSpriteWithAge(p_i51054_10_);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      return 15728880;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217570_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_LIT;
   }

   // $FF: synthetic method
   SweepAttackParticle(World p_i51055_1_, double p_i51055_2_, double p_i51055_4_, double p_i51055_6_, double p_i51055_8_, IAnimatedSprite p_i51055_10_, Object p_i51055_11_) {
      this(p_i51055_1_, p_i51055_2_, p_i51055_4_, p_i51055_6_, p_i51055_8_, p_i51055_10_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50563_1_) {
         this.spriteSet = p_i50563_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SweepAttackParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, this.spriteSet);
      }
   }
}
