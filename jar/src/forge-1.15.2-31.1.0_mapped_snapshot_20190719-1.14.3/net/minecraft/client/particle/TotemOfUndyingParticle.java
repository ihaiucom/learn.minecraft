package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TotemOfUndyingParticle extends SimpleAnimatedParticle {
   private TotemOfUndyingParticle(World p_i50996_1_, double p_i50996_2_, double p_i50996_4_, double p_i50996_6_, double p_i50996_8_, double p_i50996_10_, double p_i50996_12_, IAnimatedSprite p_i50996_14_) {
      super(p_i50996_1_, p_i50996_2_, p_i50996_4_, p_i50996_6_, p_i50996_14_, -0.05F);
      this.motionX = p_i50996_8_;
      this.motionY = p_i50996_10_;
      this.motionZ = p_i50996_12_;
      this.particleScale *= 0.75F;
      this.maxAge = 60 + this.rand.nextInt(12);
      this.selectSpriteWithAge(p_i50996_14_);
      if (this.rand.nextInt(4) == 0) {
         this.setColor(0.6F + this.rand.nextFloat() * 0.2F, 0.6F + this.rand.nextFloat() * 0.3F, this.rand.nextFloat() * 0.2F);
      } else {
         this.setColor(0.1F + this.rand.nextFloat() * 0.2F, 0.4F + this.rand.nextFloat() * 0.3F, this.rand.nextFloat() * 0.2F);
      }

      this.setBaseAirFriction(0.6F);
   }

   // $FF: synthetic method
   TotemOfUndyingParticle(World p_i50997_1_, double p_i50997_2_, double p_i50997_4_, double p_i50997_6_, double p_i50997_8_, double p_i50997_10_, double p_i50997_12_, IAnimatedSprite p_i50997_14_, Object p_i50997_15_) {
      this(p_i50997_1_, p_i50997_2_, p_i50997_4_, p_i50997_6_, p_i50997_8_, p_i50997_10_, p_i50997_12_, p_i50997_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50316_1_) {
         this.spriteSet = p_i50316_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new TotemOfUndyingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
