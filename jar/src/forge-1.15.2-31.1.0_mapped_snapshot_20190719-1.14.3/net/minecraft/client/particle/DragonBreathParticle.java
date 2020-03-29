package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DragonBreathParticle extends SpriteTexturedParticle {
   private boolean hasHitGround;
   private final IAnimatedSprite field_217574_F;

   private DragonBreathParticle(World p_i51042_1_, double p_i51042_2_, double p_i51042_4_, double p_i51042_6_, double p_i51042_8_, double p_i51042_10_, double p_i51042_12_, IAnimatedSprite p_i51042_14_) {
      super(p_i51042_1_, p_i51042_2_, p_i51042_4_, p_i51042_6_);
      this.motionX = p_i51042_8_;
      this.motionY = p_i51042_10_;
      this.motionZ = p_i51042_12_;
      this.particleRed = MathHelper.nextFloat(this.rand, 0.7176471F, 0.8745098F);
      this.particleGreen = MathHelper.nextFloat(this.rand, 0.0F, 0.0F);
      this.particleBlue = MathHelper.nextFloat(this.rand, 0.8235294F, 0.9764706F);
      this.particleScale *= 0.75F;
      this.maxAge = (int)(20.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D));
      this.hasHitGround = false;
      this.canCollide = false;
      this.field_217574_F = p_i51042_14_;
      this.selectSpriteWithAge(p_i51042_14_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217574_F);
         if (this.onGround) {
            this.motionY = 0.0D;
            this.hasHitGround = true;
         }

         if (this.hasHitGround) {
            this.motionY += 0.002D;
         }

         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= 0.9599999785423279D;
         this.motionZ *= 0.9599999785423279D;
         if (this.hasHitGround) {
            this.motionY *= 0.9599999785423279D;
         }

      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getScale(float p_217561_1_) {
      return this.particleScale * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
   }

   // $FF: synthetic method
   DragonBreathParticle(World p_i51043_1_, double p_i51043_2_, double p_i51043_4_, double p_i51043_6_, double p_i51043_8_, double p_i51043_10_, double p_i51043_12_, IAnimatedSprite p_i51043_14_, Object p_i51043_15_) {
      this(p_i51043_1_, p_i51043_2_, p_i51043_4_, p_i51043_6_, p_i51043_8_, p_i51043_10_, p_i51043_12_, p_i51043_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50559_1_) {
         this.spriteSet = p_i50559_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new DragonBreathParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
