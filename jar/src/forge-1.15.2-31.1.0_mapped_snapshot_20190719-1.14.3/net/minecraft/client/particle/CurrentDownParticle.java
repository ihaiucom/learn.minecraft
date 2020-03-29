package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CurrentDownParticle extends SpriteTexturedParticle {
   private float field_203083_a;

   private CurrentDownParticle(World p_i48830_1_, double p_i48830_2_, double p_i48830_4_, double p_i48830_6_) {
      super(p_i48830_1_, p_i48830_2_, p_i48830_4_, p_i48830_6_);
      this.maxAge = (int)(Math.random() * 60.0D) + 30;
      this.canCollide = false;
      this.motionX = 0.0D;
      this.motionY = -0.05D;
      this.motionZ = 0.0D;
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
      this.particleGravity = 0.002F;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         float lvt_1_1_ = 0.6F;
         this.motionX += (double)(0.6F * MathHelper.cos(this.field_203083_a));
         this.motionZ += (double)(0.6F * MathHelper.sin(this.field_203083_a));
         this.motionX *= 0.07D;
         this.motionZ *= 0.07D;
         this.move(this.motionX, this.motionY, this.motionZ);
         if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER) || this.onGround) {
            this.setExpired();
         }

         this.field_203083_a = (float)((double)this.field_203083_a + 0.08D);
      }
   }

   // $FF: synthetic method
   CurrentDownParticle(World p_i50992_1_, double p_i50992_2_, double p_i50992_4_, double p_i50992_6_, Object p_i50992_8_) {
      this(p_i50992_1_, p_i50992_2_, p_i50992_4_, p_i50992_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50972_1_) {
         this.spriteSet = p_i50972_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CurrentDownParticle lvt_15_1_ = new CurrentDownParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
