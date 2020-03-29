package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellParticle extends SpriteTexturedParticle {
   private static final Random RANDOM = new Random();
   private final IAnimatedSprite field_217586_F;

   private SpellParticle(World p_i51008_1_, double p_i51008_2_, double p_i51008_4_, double p_i51008_6_, double p_i51008_8_, double p_i51008_10_, double p_i51008_12_, IAnimatedSprite p_i51008_14_) {
      super(p_i51008_1_, p_i51008_2_, p_i51008_4_, p_i51008_6_, 0.5D - RANDOM.nextDouble(), p_i51008_10_, 0.5D - RANDOM.nextDouble());
      this.field_217586_F = p_i51008_14_;
      this.motionY *= 0.20000000298023224D;
      if (p_i51008_8_ == 0.0D && p_i51008_12_ == 0.0D) {
         this.motionX *= 0.10000000149011612D;
         this.motionZ *= 0.10000000149011612D;
      }

      this.particleScale *= 0.75F;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.canCollide = false;
      this.selectSpriteWithAge(p_i51008_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217586_F);
         this.motionY += 0.004D;
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
   SpellParticle(World p_i51009_1_, double p_i51009_2_, double p_i51009_4_, double p_i51009_6_, double p_i51009_8_, double p_i51009_10_, double p_i51009_12_, IAnimatedSprite p_i51009_14_, Object p_i51009_15_) {
      this(p_i51009_1_, p_i51009_2_, p_i51009_4_, p_i51009_6_, p_i51009_8_, p_i51009_10_, p_i51009_12_, p_i51009_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class InstantFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public InstantFactory(IAnimatedSprite p_i50845_1_) {
         this.spriteSet = p_i50845_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WitchFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public WitchFactory(IAnimatedSprite p_i50842_1_) {
         this.spriteSet = p_i50842_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SpellParticle lvt_15_1_ = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
         float lvt_16_1_ = p_199234_2_.rand.nextFloat() * 0.5F + 0.35F;
         lvt_15_1_.setColor(1.0F * lvt_16_1_, 0.0F * lvt_16_1_, 1.0F * lvt_16_1_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class AmbientMobFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public AmbientMobFactory(IAnimatedSprite p_i50846_1_) {
         this.spriteSet = p_i50846_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle lvt_15_1_ = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
         lvt_15_1_.setAlphaF(0.15F);
         lvt_15_1_.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MobFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public MobFactory(IAnimatedSprite p_i50844_1_) {
         this.spriteSet = p_i50844_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle lvt_15_1_ = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
         lvt_15_1_.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50843_1_) {
         this.spriteSet = p_i50843_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet);
      }
   }
}
