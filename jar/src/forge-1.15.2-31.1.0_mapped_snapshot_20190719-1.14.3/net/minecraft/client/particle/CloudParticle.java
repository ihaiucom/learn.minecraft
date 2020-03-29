package net.minecraft.client.particle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloudParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217583_C;

   private CloudParticle(World p_i51015_1_, double p_i51015_2_, double p_i51015_4_, double p_i51015_6_, double p_i51015_8_, double p_i51015_10_, double p_i51015_12_, IAnimatedSprite p_i51015_14_) {
      super(p_i51015_1_, p_i51015_2_, p_i51015_4_, p_i51015_6_, 0.0D, 0.0D, 0.0D);
      this.field_217583_C = p_i51015_14_;
      float lvt_15_1_ = 2.5F;
      this.motionX *= 0.10000000149011612D;
      this.motionY *= 0.10000000149011612D;
      this.motionZ *= 0.10000000149011612D;
      this.motionX += p_i51015_8_;
      this.motionY += p_i51015_10_;
      this.motionZ += p_i51015_12_;
      float lvt_16_1_ = 1.0F - (float)(Math.random() * 0.30000001192092896D);
      this.particleRed = lvt_16_1_;
      this.particleGreen = lvt_16_1_;
      this.particleBlue = lvt_16_1_;
      this.particleScale *= 1.875F;
      int lvt_17_1_ = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.maxAge = (int)Math.max((float)lvt_17_1_ * 2.5F, 1.0F);
      this.canCollide = false;
      this.selectSpriteWithAge(p_i51015_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
         this.selectSpriteWithAge(this.field_217583_C);
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9599999785423279D;
         this.motionY *= 0.9599999785423279D;
         this.motionZ *= 0.9599999785423279D;
         PlayerEntity lvt_1_1_ = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0D, false);
         if (lvt_1_1_ != null) {
            double lvt_2_1_ = lvt_1_1_.func_226278_cu_();
            if (this.posY > lvt_2_1_) {
               this.posY += (lvt_2_1_ - this.posY) * 0.2D;
               this.motionY += (lvt_1_1_.getMotion().y - this.motionY) * 0.2D;
               this.setPosition(this.posX, this.posY, this.posZ);
            }
         }

         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   CloudParticle(World p_i51016_1_, double p_i51016_2_, double p_i51016_4_, double p_i51016_6_, double p_i51016_8_, double p_i51016_10_, double p_i51016_12_, IAnimatedSprite p_i51016_14_, Object p_i51016_15_) {
      this(p_i51016_1_, p_i51016_2_, p_i51016_4_, p_i51016_6_, p_i51016_8_, p_i51016_10_, p_i51016_12_, p_i51016_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class SneezeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public SneezeFactory(IAnimatedSprite p_i50629_1_) {
         this.spriteSet = p_i50629_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle lvt_15_1_ = new CloudParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
         lvt_15_1_.setColor(200.0F, 50.0F, 120.0F);
         lvt_15_1_.setAlphaF(0.4F);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50630_1_) {
         this.spriteSet = p_i50630_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new CloudParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
