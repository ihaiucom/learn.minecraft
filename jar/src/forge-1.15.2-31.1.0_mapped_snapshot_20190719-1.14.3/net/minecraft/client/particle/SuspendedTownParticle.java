package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SuspendedTownParticle extends SpriteTexturedParticle {
   private SuspendedTownParticle(World p_i1232_1_, double p_i1232_2_, double p_i1232_4_, double p_i1232_6_, double p_i1232_8_, double p_i1232_10_, double p_i1232_12_) {
      super(p_i1232_1_, p_i1232_2_, p_i1232_4_, p_i1232_6_, p_i1232_8_, p_i1232_10_, p_i1232_12_);
      float lvt_14_1_ = this.rand.nextFloat() * 0.1F + 0.2F;
      this.particleRed = lvt_14_1_;
      this.particleGreen = lvt_14_1_;
      this.particleBlue = lvt_14_1_;
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
      this.motionX *= 0.019999999552965164D;
      this.motionY *= 0.019999999552965164D;
      this.motionZ *= 0.019999999552965164D;
      this.maxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.99D;
         this.motionY *= 0.99D;
         this.motionZ *= 0.99D;
      }
   }

   // $FF: synthetic method
   SuspendedTownParticle(World p_i51000_1_, double p_i51000_2_, double p_i51000_4_, double p_i51000_6_, double p_i51000_8_, double p_i51000_10_, double p_i51000_12_, Object p_i51000_14_) {
      this(p_i51000_1_, p_i51000_2_, p_i51000_4_, p_i51000_6_, p_i51000_8_, p_i51000_10_, p_i51000_12_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class DolphinSpeedFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public DolphinSpeedFactory(IAnimatedSprite p_i50523_1_) {
         this.spriteSet = p_i50523_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SuspendedTownParticle lvt_15_1_ = new SuspendedTownParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.setColor(0.3F, 0.5F, 1.0F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         lvt_15_1_.setAlphaF(1.0F - p_199234_2_.rand.nextFloat() * 0.7F);
         lvt_15_1_.setMaxAge(lvt_15_1_.getMaxAge() / 2);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ComposterFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public ComposterFactory(IAnimatedSprite p_i50524_1_) {
         this.spriteSet = p_i50524_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SuspendedTownParticle lvt_15_1_ = new SuspendedTownParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         lvt_15_1_.setColor(1.0F, 1.0F, 1.0F);
         lvt_15_1_.setMaxAge(3 + p_199234_2_.getRandom().nextInt(5));
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HappyVillagerFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public HappyVillagerFactory(IAnimatedSprite p_i50522_1_) {
         this.spriteSet = p_i50522_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SuspendedTownParticle lvt_15_1_ = new SuspendedTownParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         lvt_15_1_.setColor(1.0F, 1.0F, 1.0F);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50521_1_) {
         this.spriteSet = p_i50521_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SuspendedTownParticle lvt_15_1_ = new SuspendedTownParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
