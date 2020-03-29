package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndRodParticle extends SimpleAnimatedParticle {
   private EndRodParticle(World p_i51036_1_, double p_i51036_2_, double p_i51036_4_, double p_i51036_6_, double p_i51036_8_, double p_i51036_10_, double p_i51036_12_, IAnimatedSprite p_i51036_14_) {
      super(p_i51036_1_, p_i51036_2_, p_i51036_4_, p_i51036_6_, p_i51036_14_, -5.0E-4F);
      this.motionX = p_i51036_8_;
      this.motionY = p_i51036_10_;
      this.motionZ = p_i51036_12_;
      this.particleScale *= 0.75F;
      this.maxAge = 60 + this.rand.nextInt(12);
      this.setColorFade(15916745);
      this.selectSpriteWithAge(p_i51036_14_);
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
   }

   // $FF: synthetic method
   EndRodParticle(World p_i51037_1_, double p_i51037_2_, double p_i51037_4_, double p_i51037_6_, double p_i51037_8_, double p_i51037_10_, double p_i51037_12_, IAnimatedSprite p_i51037_14_, Object p_i51037_15_) {
      this(p_i51037_1_, p_i51037_2_, p_i51037_4_, p_i51037_6_, p_i51037_8_, p_i51037_10_, p_i51037_12_, p_i51037_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50058_1_) {
         this.spriteSet = p_i50058_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new EndRodParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
