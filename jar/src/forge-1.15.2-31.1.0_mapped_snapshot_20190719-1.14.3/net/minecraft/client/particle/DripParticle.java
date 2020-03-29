package net.minecraft.client.particle;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DripParticle extends SpriteTexturedParticle {
   private final Fluid fluid;

   private DripParticle(World p_i49197_1_, double p_i49197_2_, double p_i49197_4_, double p_i49197_6_, Fluid p_i49197_8_) {
      super(p_i49197_1_, p_i49197_2_, p_i49197_4_, p_i49197_6_);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.fluid = p_i49197_8_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getBrightnessForRender(float p_189214_1_) {
      return this.fluid.isIn(FluidTags.LAVA) ? 240 : super.getBrightnessForRender(p_189214_1_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.func_217576_g();
      if (!this.isExpired) {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.func_217577_h();
         if (!this.isExpired) {
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
            BlockPos lvt_1_1_ = new BlockPos(this.posX, this.posY, this.posZ);
            IFluidState lvt_2_1_ = this.world.getFluidState(lvt_1_1_);
            if (lvt_2_1_.getFluid() == this.fluid && this.posY < (double)((float)lvt_1_1_.getY() + lvt_2_1_.func_215679_a(this.world, lvt_1_1_))) {
               this.setExpired();
            }

         }
      }
   }

   protected void func_217576_g() {
      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

   }

   protected void func_217577_h() {
   }

   // $FF: synthetic method
   DripParticle(World p_i51041_1_, double p_i51041_2_, double p_i51041_4_, double p_i51041_6_, Fluid p_i51041_8_, Object p_i51041_9_) {
      this(p_i51041_1_, p_i51041_2_, p_i51041_4_, p_i51041_6_, p_i51041_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingNectarFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228339_a_;

      public FallingNectarFactory(IAnimatedSprite p_i225962_1_) {
         this.field_228339_a_ = p_i225962_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.FallingNectarParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY);
         lvt_15_1_.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
         lvt_15_1_.particleGravity = 0.007F;
         lvt_15_1_.setColor(0.92F, 0.782F, 0.72F);
         lvt_15_1_.selectSpriteRandomly(this.field_228339_a_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228338_a_;

      public LandingHoneyFactory(IAnimatedSprite p_i225961_1_) {
         this.field_228338_a_ = p_i225961_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.Landing(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY);
         lvt_15_1_.maxAge = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
         lvt_15_1_.setColor(0.522F, 0.408F, 0.082F);
         lvt_15_1_.selectSpriteRandomly(this.field_228338_a_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228336_a_;

      public FallingHoneyFactory(IAnimatedSprite p_i225959_1_) {
         this.field_228336_a_ = p_i225959_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.FallingHoneyParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.field_229429_ai_);
         lvt_15_1_.particleGravity = 0.01F;
         lvt_15_1_.setColor(0.582F, 0.448F, 0.082F);
         lvt_15_1_.selectSpriteRandomly(this.field_228336_a_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228337_a_;

      public DrippingHoneyFactory(IAnimatedSprite p_i225960_1_) {
         this.field_228337_a_ = p_i225960_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle.Dripping lvt_15_1_ = new DripParticle.Dripping(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.field_229428_ah_);
         lvt_15_1_.particleGravity *= 0.01F;
         lvt_15_1_.maxAge = 100;
         lvt_15_1_.setColor(0.622F, 0.508F, 0.082F);
         lvt_15_1_.selectSpriteRandomly(this.field_228337_a_);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public LandingLavaFactory(IAnimatedSprite p_i50504_1_) {
         this.spriteSet = p_i50504_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.Landing(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA);
         lvt_15_1_.setColor(1.0F, 0.2857143F, 0.083333336F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public FallingLavaFactory(IAnimatedSprite p_i50506_1_) {
         this.spriteSet = p_i50506_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.FallingLiquidParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
         lvt_15_1_.setColor(1.0F, 0.2857143F, 0.083333336F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public DrippingLavaFactory(IAnimatedSprite p_i50505_1_) {
         this.spriteSet = p_i50505_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle.DrippingLava lvt_15_1_ = new DripParticle.DrippingLava(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public FallingWaterFactory(IAnimatedSprite p_i50503_1_) {
         this.spriteSet = p_i50503_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.FallingLiquidParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.WATER, ParticleTypes.SPLASH);
         lvt_15_1_.setColor(0.2F, 0.3F, 1.0F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public DrippingWaterFactory(IAnimatedSprite p_i50502_1_) {
         this.spriteSet = p_i50502_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle lvt_15_1_ = new DripParticle.Dripping(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.WATER, ParticleTypes.FALLING_WATER);
         lvt_15_1_.setColor(0.2F, 0.3F, 1.0F);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Landing extends DripParticle {
      private Landing(World p_i50507_1_, double p_i50507_2_, double p_i50507_4_, double p_i50507_6_, Fluid p_i50507_8_) {
         super(p_i50507_1_, p_i50507_2_, p_i50507_4_, p_i50507_6_, p_i50507_8_, null);
         this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }

      // $FF: synthetic method
      Landing(World p_i50508_1_, double p_i50508_2_, double p_i50508_4_, double p_i50508_6_, Fluid p_i50508_8_, Object p_i50508_9_) {
         this(p_i50508_1_, p_i50508_2_, p_i50508_4_, p_i50508_6_, p_i50508_8_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingNectarParticle extends DripParticle {
      private FallingNectarParticle(World p_i225955_1_, double p_i225955_2_, double p_i225955_4_, double p_i225955_6_, Fluid p_i225955_8_) {
         super(p_i225955_1_, p_i225955_2_, p_i225955_4_, p_i225955_6_, p_i225955_8_, null);
         this.maxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
         }

      }

      // $FF: synthetic method
      FallingNectarParticle(World p_i225956_1_, double p_i225956_2_, double p_i225956_4_, double p_i225956_6_, Fluid p_i225956_8_, Object p_i225956_9_) {
         this(p_i225956_1_, p_i225956_2_, p_i225956_4_, p_i225956_6_, p_i225956_8_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingHoneyParticle extends DripParticle.FallingLiquidParticle {
      private FallingHoneyParticle(World p_i225957_1_, double p_i225957_2_, double p_i225957_4_, double p_i225957_6_, Fluid p_i225957_8_, IParticleData p_i225957_9_) {
         super(p_i225957_1_, p_i225957_2_, p_i225957_4_, p_i225957_6_, p_i225957_8_, p_i225957_9_, null);
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
            this.world.addParticle(this.field_228335_a_, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            this.world.playSound(this.posX + 0.5D, this.posY, this.posZ + 0.5D, SoundEvents.field_226130_ae_, SoundCategory.BLOCKS, 0.3F + this.world.rand.nextFloat() * 2.0F / 3.0F, 1.0F, false);
         }

      }

      // $FF: synthetic method
      FallingHoneyParticle(World p_i225958_1_, double p_i225958_2_, double p_i225958_4_, double p_i225958_6_, Fluid p_i225958_8_, IParticleData p_i225958_9_, Object p_i225958_10_) {
         this(p_i225958_1_, p_i225958_2_, p_i225958_4_, p_i225958_6_, p_i225958_8_, p_i225958_9_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingLiquidParticle extends DripParticle.FallingNectarParticle {
      protected final IParticleData field_228335_a_;

      private FallingLiquidParticle(World p_i225953_1_, double p_i225953_2_, double p_i225953_4_, double p_i225953_6_, Fluid p_i225953_8_, IParticleData p_i225953_9_) {
         super(p_i225953_1_, p_i225953_2_, p_i225953_4_, p_i225953_6_, p_i225953_8_, null);
         this.field_228335_a_ = p_i225953_9_;
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
            this.world.addParticle(this.field_228335_a_, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         }

      }

      // $FF: synthetic method
      FallingLiquidParticle(World p_i225954_1_, double p_i225954_2_, double p_i225954_4_, double p_i225954_6_, Fluid p_i225954_8_, IParticleData p_i225954_9_, Object p_i225954_10_) {
         this(p_i225954_1_, p_i225954_2_, p_i225954_4_, p_i225954_6_, p_i225954_8_, p_i225954_9_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DrippingLava extends DripParticle.Dripping {
      private DrippingLava(World p_i50513_1_, double p_i50513_2_, double p_i50513_4_, double p_i50513_6_, Fluid p_i50513_8_, IParticleData p_i50513_9_) {
         super(p_i50513_1_, p_i50513_2_, p_i50513_4_, p_i50513_6_, p_i50513_8_, p_i50513_9_, null);
      }

      protected void func_217576_g() {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.maxAge + 16);
         this.particleBlue = 4.0F / (float)(40 - this.maxAge + 8);
         super.func_217576_g();
      }

      // $FF: synthetic method
      DrippingLava(World p_i50514_1_, double p_i50514_2_, double p_i50514_4_, double p_i50514_6_, Fluid p_i50514_8_, IParticleData p_i50514_9_, Object p_i50514_10_) {
         this(p_i50514_1_, p_i50514_2_, p_i50514_4_, p_i50514_6_, p_i50514_8_, p_i50514_9_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Dripping extends DripParticle {
      private final IParticleData field_217579_C;

      private Dripping(World p_i50509_1_, double p_i50509_2_, double p_i50509_4_, double p_i50509_6_, Fluid p_i50509_8_, IParticleData p_i50509_9_) {
         super(p_i50509_1_, p_i50509_2_, p_i50509_4_, p_i50509_6_, p_i50509_8_, null);
         this.field_217579_C = p_i50509_9_;
         this.particleGravity *= 0.02F;
         this.maxAge = 40;
      }

      protected void func_217576_g() {
         if (this.maxAge-- <= 0) {
            this.setExpired();
            this.world.addParticle(this.field_217579_C, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
         }

      }

      protected void func_217577_h() {
         this.motionX *= 0.02D;
         this.motionY *= 0.02D;
         this.motionZ *= 0.02D;
      }

      // $FF: synthetic method
      Dripping(World p_i50510_1_, double p_i50510_2_, double p_i50510_4_, double p_i50510_6_, Fluid p_i50510_8_, IParticleData p_i50510_9_, Object p_i50510_10_) {
         this(p_i50510_1_, p_i50510_2_, p_i50510_4_, p_i50510_6_, p_i50510_8_, p_i50510_9_);
      }
   }
}
