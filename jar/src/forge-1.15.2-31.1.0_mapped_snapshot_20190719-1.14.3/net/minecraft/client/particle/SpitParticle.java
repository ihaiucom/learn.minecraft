package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpitParticle extends PoofParticle {
   private SpitParticle(World p_i51006_1_, double p_i51006_2_, double p_i51006_4_, double p_i51006_6_, double p_i51006_8_, double p_i51006_10_, double p_i51006_12_, IAnimatedSprite p_i51006_14_) {
      super(p_i51006_1_, p_i51006_2_, p_i51006_4_, p_i51006_6_, p_i51006_8_, p_i51006_10_, p_i51006_12_, p_i51006_14_);
      this.particleGravity = 0.5F;
   }

   public void tick() {
      super.tick();
      this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;
   }

   // $FF: synthetic method
   SpitParticle(World p_i51007_1_, double p_i51007_2_, double p_i51007_4_, double p_i51007_6_, double p_i51007_8_, double p_i51007_10_, double p_i51007_12_, IAnimatedSprite p_i51007_14_, Object p_i51007_15_) {
      this(p_i51007_1_, p_i51007_2_, p_i51007_4_, p_i51007_6_, p_i51007_8_, p_i51007_10_, p_i51007_12_, p_i51007_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50812_1_) {
         this.spriteSet = p_i50812_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SpitParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
