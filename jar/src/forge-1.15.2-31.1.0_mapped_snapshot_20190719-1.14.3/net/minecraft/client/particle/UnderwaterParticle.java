package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterParticle extends SpriteTexturedParticle {
   private UnderwaterParticle(World p_i51001_1_, double p_i51001_2_, double p_i51001_4_, double p_i51001_6_) {
      super(p_i51001_1_, p_i51001_2_, p_i51001_4_ - 0.125D, p_i51001_6_);
      this.particleRed = 0.4F;
      this.particleGreen = 0.4F;
      this.particleBlue = 0.7F;
      this.setSize(0.01F, 0.01F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
      this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
         if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER)) {
            this.setExpired();
         }

      }
   }

   // $FF: synthetic method
   UnderwaterParticle(World p_i51002_1_, double p_i51002_2_, double p_i51002_4_, double p_i51002_6_, Object p_i51002_8_) {
      this(p_i51002_1_, p_i51002_2_, p_i51002_4_, p_i51002_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50567_1_) {
         this.spriteSet = p_i50567_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         UnderwaterParticle lvt_15_1_ = new UnderwaterParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
