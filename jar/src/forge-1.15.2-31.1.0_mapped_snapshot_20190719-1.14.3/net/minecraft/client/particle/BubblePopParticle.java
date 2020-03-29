package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubblePopParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217573_C;

   private BubblePopParticle(World p_i51048_1_, double p_i51048_2_, double p_i51048_4_, double p_i51048_6_, double p_i51048_8_, double p_i51048_10_, double p_i51048_12_, IAnimatedSprite p_i51048_14_) {
      super(p_i51048_1_, p_i51048_2_, p_i51048_4_, p_i51048_6_);
      this.field_217573_C = p_i51048_14_;
      this.maxAge = 4;
      this.particleGravity = 0.008F;
      this.motionX = p_i51048_8_;
      this.motionY = p_i51048_10_;
      this.motionZ = p_i51048_12_;
      this.selectSpriteWithAge(p_i51048_14_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.selectSpriteWithAge(this.field_217573_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   // $FF: synthetic method
   BubblePopParticle(World p_i51049_1_, double p_i51049_2_, double p_i51049_4_, double p_i51049_6_, double p_i51049_8_, double p_i51049_10_, double p_i51049_12_, IAnimatedSprite p_i51049_14_, Object p_i51049_15_) {
      this(p_i51049_1_, p_i51049_2_, p_i51049_4_, p_i51049_6_, p_i51049_8_, p_i51049_10_, p_i51049_12_, p_i51049_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i49967_1_) {
         this.spriteSet = p_i49967_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BubblePopParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
