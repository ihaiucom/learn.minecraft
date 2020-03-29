package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteParticle extends SpriteTexturedParticle {
   private NoteParticle(World p_i51018_1_, double p_i51018_2_, double p_i51018_4_, double p_i51018_6_, double p_i51018_8_) {
      super(p_i51018_1_, p_i51018_2_, p_i51018_4_, p_i51018_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= 0.009999999776482582D;
      this.motionY *= 0.009999999776482582D;
      this.motionZ *= 0.009999999776482582D;
      this.motionY += 0.2D;
      this.particleRed = Math.max(0.0F, MathHelper.sin(((float)p_i51018_8_ + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
      this.particleGreen = Math.max(0.0F, MathHelper.sin(((float)p_i51018_8_ + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
      this.particleBlue = Math.max(0.0F, MathHelper.sin(((float)p_i51018_8_ + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
      this.particleScale *= 1.5F;
      this.maxAge = 6;
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
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= 0.6600000262260437D;
         this.motionY *= 0.6600000262260437D;
         this.motionZ *= 0.6600000262260437D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   NoteParticle(World p_i51019_1_, double p_i51019_2_, double p_i51019_4_, double p_i51019_6_, double p_i51019_8_, Object p_i51019_10_) {
      this(p_i51019_1_, p_i51019_2_, p_i51019_4_, p_i51019_6_, p_i51019_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50044_1_) {
         this.spriteSet = p_i50044_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         NoteParticle lvt_15_1_ = new NoteParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
