package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameParticle extends SpriteTexturedParticle {
   private FlameParticle(World p_i1209_1_, double p_i1209_2_, double p_i1209_4_, double p_i1209_6_, double p_i1209_8_, double p_i1209_10_, double p_i1209_12_) {
      super(p_i1209_1_, p_i1209_2_, p_i1209_4_, p_i1209_6_, p_i1209_8_, p_i1209_10_, p_i1209_12_);
      this.motionX = this.motionX * 0.009999999776482582D + p_i1209_8_;
      this.motionY = this.motionY * 0.009999999776482582D + p_i1209_10_;
      this.motionZ = this.motionZ * 0.009999999776482582D + p_i1209_12_;
      this.posX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
   }

   public float getScale(float p_217561_1_) {
      float lvt_2_1_ = ((float)this.age + p_217561_1_) / (float)this.maxAge;
      return this.particleScale * (1.0F - lvt_2_1_ * lvt_2_1_ * 0.5F);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      float lvt_2_1_ = ((float)this.age + p_189214_1_) / (float)this.maxAge;
      lvt_2_1_ = MathHelper.clamp(lvt_2_1_, 0.0F, 1.0F);
      int lvt_3_1_ = super.getBrightnessForRender(p_189214_1_);
      int lvt_4_1_ = lvt_3_1_ & 255;
      int lvt_5_1_ = lvt_3_1_ >> 16 & 255;
      lvt_4_1_ += (int)(lvt_2_1_ * 15.0F * 16.0F);
      if (lvt_4_1_ > 240) {
         lvt_4_1_ = 240;
      }

      return lvt_4_1_ | lvt_5_1_ << 16;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
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
   FlameParticle(World p_i51032_1_, double p_i51032_2_, double p_i51032_4_, double p_i51032_6_, double p_i51032_8_, double p_i51032_10_, double p_i51032_12_, Object p_i51032_14_) {
      this(p_i51032_1_, p_i51032_2_, p_i51032_4_, p_i51032_6_, p_i51032_8_, p_i51032_10_, p_i51032_12_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50823_1_) {
         this.spriteSet = p_i50823_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         FlameParticle lvt_15_1_ = new FlameParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
