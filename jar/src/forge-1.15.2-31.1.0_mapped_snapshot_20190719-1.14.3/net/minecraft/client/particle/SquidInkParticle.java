package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidInkParticle extends SimpleAnimatedParticle {
   private SquidInkParticle(World p_i51003_1_, double p_i51003_2_, double p_i51003_4_, double p_i51003_6_, double p_i51003_8_, double p_i51003_10_, double p_i51003_12_, IAnimatedSprite p_i51003_14_) {
      super(p_i51003_1_, p_i51003_2_, p_i51003_4_, p_i51003_6_, p_i51003_14_, 0.0F);
      this.particleScale = 0.5F;
      this.setAlphaF(1.0F);
      this.setColor(0.0F, 0.0F, 0.0F);
      this.maxAge = (int)((double)(this.particleScale * 12.0F) / (Math.random() * 0.800000011920929D + 0.20000000298023224D));
      this.selectSpriteWithAge(p_i51003_14_);
      this.canCollide = false;
      this.motionX = p_i51003_8_;
      this.motionY = p_i51003_10_;
      this.motionZ = p_i51003_12_;
      this.setBaseAirFriction(0.0F);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217584_C);
         if (this.age > this.maxAge / 2) {
            this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
         }

         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).isAir()) {
            this.motionY -= 0.00800000037997961D;
         }

         this.motionX *= 0.9200000166893005D;
         this.motionY *= 0.9200000166893005D;
         this.motionZ *= 0.9200000166893005D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   SquidInkParticle(World p_i51004_1_, double p_i51004_2_, double p_i51004_4_, double p_i51004_6_, double p_i51004_8_, double p_i51004_10_, double p_i51004_12_, IAnimatedSprite p_i51004_14_, Object p_i51004_15_) {
      this(p_i51004_1_, p_i51004_2_, p_i51004_4_, p_i51004_6_, p_i51004_8_, p_i51004_10_, p_i51004_12_, p_i51004_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50599_1_) {
         this.spriteSet = p_i50599_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SquidInkParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
