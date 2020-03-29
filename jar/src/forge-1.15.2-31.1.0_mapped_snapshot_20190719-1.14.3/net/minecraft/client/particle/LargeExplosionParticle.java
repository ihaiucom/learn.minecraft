package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeExplosionParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217582_C;

   private LargeExplosionParticle(World p_i51028_1_, double p_i51028_2_, double p_i51028_4_, double p_i51028_6_, double p_i51028_8_, IAnimatedSprite p_i51028_10_) {
      super(p_i51028_1_, p_i51028_2_, p_i51028_4_, p_i51028_6_, 0.0D, 0.0D, 0.0D);
      this.maxAge = 6 + this.rand.nextInt(4);
      float lvt_11_1_ = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = lvt_11_1_;
      this.particleGreen = lvt_11_1_;
      this.particleBlue = lvt_11_1_;
      this.particleScale = 2.0F * (1.0F - (float)p_i51028_8_ * 0.5F);
      this.field_217582_C = p_i51028_10_;
      this.selectSpriteWithAge(p_i51028_10_);
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
         this.selectSpriteWithAge(this.field_217582_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_LIT;
   }

   // $FF: synthetic method
   LargeExplosionParticle(World p_i51029_1_, double p_i51029_2_, double p_i51029_4_, double p_i51029_6_, double p_i51029_8_, IAnimatedSprite p_i51029_10_, Object p_i51029_11_) {
      this(p_i51029_1_, p_i51029_2_, p_i51029_4_, p_i51029_6_, p_i51029_8_, p_i51029_10_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50634_1_) {
         this.spriteSet = p_i50634_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new LargeExplosionParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, this.spriteSet);
      }
   }
}
