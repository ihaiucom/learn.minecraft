package net.minecraft.client.particle;

import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RedstoneParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217575_C;

   private RedstoneParticle(World p_i51039_1_, double p_i51039_2_, double p_i51039_4_, double p_i51039_6_, double p_i51039_8_, double p_i51039_10_, double p_i51039_12_, RedstoneParticleData p_i51039_14_, IAnimatedSprite p_i51039_15_) {
      super(p_i51039_1_, p_i51039_2_, p_i51039_4_, p_i51039_6_, p_i51039_8_, p_i51039_10_, p_i51039_12_);
      this.field_217575_C = p_i51039_15_;
      this.motionX *= 0.10000000149011612D;
      this.motionY *= 0.10000000149011612D;
      this.motionZ *= 0.10000000149011612D;
      float lvt_16_1_ = (float)Math.random() * 0.4F + 0.6F;
      this.particleRed = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i51039_14_.getRed() * lvt_16_1_;
      this.particleGreen = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i51039_14_.getGreen() * lvt_16_1_;
      this.particleBlue = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i51039_14_.getBlue() * lvt_16_1_;
      this.particleScale *= 0.75F * p_i51039_14_.getAlpha();
      int lvt_17_1_ = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.maxAge = (int)Math.max((float)lvt_17_1_ * p_i51039_14_.getAlpha(), 1.0F);
      this.selectSpriteWithAge(p_i51039_15_);
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
         this.selectSpriteWithAge(this.field_217575_C);
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= 0.9599999785423279D;
         this.motionY *= 0.9599999785423279D;
         this.motionZ *= 0.9599999785423279D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   RedstoneParticle(World p_i51040_1_, double p_i51040_2_, double p_i51040_4_, double p_i51040_6_, double p_i51040_8_, double p_i51040_10_, double p_i51040_12_, RedstoneParticleData p_i51040_14_, IAnimatedSprite p_i51040_15_, Object p_i51040_16_) {
      this(p_i51040_1_, p_i51040_2_, p_i51040_4_, p_i51040_6_, p_i51040_8_, p_i51040_10_, p_i51040_12_, p_i51040_14_, p_i51040_15_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<RedstoneParticleData> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50477_1_) {
         this.spriteSet = p_i50477_1_;
      }

      public Particle makeParticle(RedstoneParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new RedstoneParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, p_199234_1_, this.spriteSet);
      }
   }
}
