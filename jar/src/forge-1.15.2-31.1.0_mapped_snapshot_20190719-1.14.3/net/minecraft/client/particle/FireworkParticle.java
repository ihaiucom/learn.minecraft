package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkParticle {
   @OnlyIn(Dist.CLIENT)
   public static class SparkFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public SparkFactory(IAnimatedSprite p_i50883_1_) {
         this.spriteSet = p_i50883_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         FireworkParticle.Spark lvt_15_1_ = new FireworkParticle.Spark(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, Minecraft.getInstance().particles, this.spriteSet);
         lvt_15_1_.setAlphaF(0.99F);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public OverlayFactory(IAnimatedSprite p_i50889_1_) {
         this.spriteSet = p_i50889_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         FireworkParticle.Overlay lvt_15_1_ = new FireworkParticle.Overlay(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Overlay extends SpriteTexturedParticle {
      private Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
         super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
         this.maxAge = 4;
      }

      public IParticleRenderType getRenderType() {
         return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
      }

      public void func_225606_a_(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
         this.setAlphaF(0.6F - ((float)this.age + p_225606_3_ - 1.0F) * 0.25F * 0.5F);
         super.func_225606_a_(p_225606_1_, p_225606_2_, p_225606_3_);
      }

      public float getScale(float p_217561_1_) {
         return 7.1F * MathHelper.sin(((float)this.age + p_217561_1_ - 1.0F) * 0.25F * 3.1415927F);
      }

      // $FF: synthetic method
      Overlay(World p_i50887_1_, double p_i50887_2_, double p_i50887_4_, double p_i50887_6_, Object p_i50887_8_) {
         this(p_i50887_1_, p_i50887_2_, p_i50887_4_, p_i50887_6_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Spark extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean twinkle;
      private final ParticleManager effectRenderer;
      private float fadeColourRed;
      private float fadeColourGreen;
      private float fadeColourBlue;
      private boolean hasFadeColour;

      private Spark(World p_i50884_1_, double p_i50884_2_, double p_i50884_4_, double p_i50884_6_, double p_i50884_8_, double p_i50884_10_, double p_i50884_12_, ParticleManager p_i50884_14_, IAnimatedSprite p_i50884_15_) {
         super(p_i50884_1_, p_i50884_2_, p_i50884_4_, p_i50884_6_, p_i50884_15_, -0.004F);
         this.motionX = p_i50884_8_;
         this.motionY = p_i50884_10_;
         this.motionZ = p_i50884_12_;
         this.effectRenderer = p_i50884_14_;
         this.particleScale *= 0.75F;
         this.maxAge = 48 + this.rand.nextInt(12);
         this.selectSpriteWithAge(p_i50884_15_);
      }

      public void setTrail(boolean p_92045_1_) {
         this.trail = p_92045_1_;
      }

      public void setTwinkle(boolean p_92043_1_) {
         this.twinkle = p_92043_1_;
      }

      public void func_225606_a_(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
         if (!this.twinkle || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.func_225606_a_(p_225606_1_, p_225606_2_, p_225606_3_);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
            FireworkParticle.Spark lvt_1_1_ = new FireworkParticle.Spark(this.world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.effectRenderer, this.field_217584_C);
            lvt_1_1_.setAlphaF(0.99F);
            lvt_1_1_.setColor(this.particleRed, this.particleGreen, this.particleBlue);
            lvt_1_1_.age = lvt_1_1_.maxAge / 2;
            if (this.hasFadeColour) {
               lvt_1_1_.hasFadeColour = true;
               lvt_1_1_.fadeColourRed = this.fadeColourRed;
               lvt_1_1_.fadeColourGreen = this.fadeColourGreen;
               lvt_1_1_.fadeColourBlue = this.fadeColourBlue;
            }

            lvt_1_1_.twinkle = this.twinkle;
            this.effectRenderer.addEffect(lvt_1_1_);
         }

      }

      // $FF: synthetic method
      Spark(World p_i50885_1_, double p_i50885_2_, double p_i50885_4_, double p_i50885_6_, double p_i50885_8_, double p_i50885_10_, double p_i50885_12_, ParticleManager p_i50885_14_, IAnimatedSprite p_i50885_15_, Object p_i50885_16_) {
         this(p_i50885_1_, p_i50885_2_, p_i50885_4_, p_i50885_6_, p_i50885_8_, p_i50885_10_, p_i50885_12_, p_i50885_14_, p_i50885_15_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Starter extends MetaParticle {
      private int fireworkAge;
      private final ParticleManager manager;
      private ListNBT fireworkExplosions;
      private boolean twinkle;

      public Starter(World p_i46464_1_, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double p_i46464_8_, double p_i46464_10_, double p_i46464_12_, ParticleManager p_i46464_14_, @Nullable CompoundNBT p_i46464_15_) {
         super(p_i46464_1_, p_i46464_2_, p_i46464_4_, p_i46464_6_);
         this.motionX = p_i46464_8_;
         this.motionY = p_i46464_10_;
         this.motionZ = p_i46464_12_;
         this.manager = p_i46464_14_;
         this.maxAge = 8;
         if (p_i46464_15_ != null) {
            this.fireworkExplosions = p_i46464_15_.getList("Explosions", 10);
            if (this.fireworkExplosions.isEmpty()) {
               this.fireworkExplosions = null;
            } else {
               this.maxAge = this.fireworkExplosions.size() * 2 - 1;

               for(int lvt_16_1_ = 0; lvt_16_1_ < this.fireworkExplosions.size(); ++lvt_16_1_) {
                  CompoundNBT lvt_17_1_ = this.fireworkExplosions.getCompound(lvt_16_1_);
                  if (lvt_17_1_.getBoolean("Flicker")) {
                     this.twinkle = true;
                     this.maxAge += 15;
                     break;
                  }
               }
            }
         }

      }

      public void tick() {
         boolean lvt_1_3_;
         if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            lvt_1_3_ = this.isFarFromCamera();
            boolean lvt_2_1_ = false;
            if (this.fireworkExplosions.size() >= 3) {
               lvt_2_1_ = true;
            } else {
               for(int lvt_3_1_ = 0; lvt_3_1_ < this.fireworkExplosions.size(); ++lvt_3_1_) {
                  CompoundNBT lvt_4_1_ = this.fireworkExplosions.getCompound(lvt_3_1_);
                  if (FireworkRocketItem.Shape.func_196070_a(lvt_4_1_.getByte("Type")) == FireworkRocketItem.Shape.LARGE_BALL) {
                     lvt_2_1_ = true;
                     break;
                  }
               }
            }

            SoundEvent lvt_3_3_;
            if (lvt_2_1_) {
               lvt_3_3_ = lvt_1_3_ ? SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               lvt_3_3_ = lvt_1_3_ ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
            }

            this.world.playSound(this.posX, this.posY, this.posZ, lvt_3_3_, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
         }

         if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.size()) {
            int lvt_1_2_ = this.fireworkAge / 2;
            CompoundNBT lvt_2_2_ = this.fireworkExplosions.getCompound(lvt_1_2_);
            FireworkRocketItem.Shape lvt_3_4_ = FireworkRocketItem.Shape.func_196070_a(lvt_2_2_.getByte("Type"));
            boolean lvt_4_2_ = lvt_2_2_.getBoolean("Trail");
            boolean lvt_5_1_ = lvt_2_2_.getBoolean("Flicker");
            int[] lvt_6_1_ = lvt_2_2_.getIntArray("Colors");
            int[] lvt_7_1_ = lvt_2_2_.getIntArray("FadeColors");
            if (lvt_6_1_.length == 0) {
               lvt_6_1_ = new int[]{DyeColor.BLACK.getFireworkColor()};
            }

            switch(lvt_3_4_) {
            case SMALL_BALL:
            default:
               this.createBall(0.25D, 2, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
               break;
            case LARGE_BALL:
               this.createBall(0.5D, 4, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
               break;
            case STAR:
               this.createShaped(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_, false);
               break;
            case CREEPER:
               this.createShaped(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_, true);
               break;
            case BURST:
               this.createBurst(lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
            }

            int lvt_8_1_ = lvt_6_1_[0];
            float lvt_9_1_ = (float)((lvt_8_1_ & 16711680) >> 16) / 255.0F;
            float lvt_10_1_ = (float)((lvt_8_1_ & '\uff00') >> 8) / 255.0F;
            float lvt_11_1_ = (float)((lvt_8_1_ & 255) >> 0) / 255.0F;
            Particle lvt_12_1_ = this.manager.addParticle(ParticleTypes.FLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            lvt_12_1_.setColor(lvt_9_1_, lvt_10_1_, lvt_11_1_);
         }

         ++this.fireworkAge;
         if (this.fireworkAge > this.maxAge) {
            if (this.twinkle) {
               lvt_1_3_ = this.isFarFromCamera();
               SoundEvent lvt_2_3_ = lvt_1_3_ ? SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE;
               this.world.playSound(this.posX, this.posY, this.posZ, lvt_2_3_, SoundCategory.AMBIENT, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setExpired();
         }

      }

      private boolean isFarFromCamera() {
         Minecraft lvt_1_1_ = Minecraft.getInstance();
         return lvt_1_1_.gameRenderer.getActiveRenderInfo().getProjectedView().squareDistanceTo(this.posX, this.posY, this.posZ) >= 256.0D;
      }

      private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
         FireworkParticle.Spark lvt_17_1_ = (FireworkParticle.Spark)this.manager.addParticle(ParticleTypes.FIREWORK, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_);
         lvt_17_1_.setTrail(p_92034_15_);
         lvt_17_1_.setTwinkle(p_92034_16_);
         lvt_17_1_.setAlphaF(0.99F);
         int lvt_18_1_ = this.rand.nextInt(p_92034_13_.length);
         lvt_17_1_.setColor(p_92034_13_[lvt_18_1_]);
         if (p_92034_14_.length > 0) {
            lvt_17_1_.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
         }

      }

      private void createBall(double p_92035_1_, int p_92035_3_, int[] p_92035_4_, int[] p_92035_5_, boolean p_92035_6_, boolean p_92035_7_) {
         double lvt_8_1_ = this.posX;
         double lvt_10_1_ = this.posY;
         double lvt_12_1_ = this.posZ;

         for(int lvt_14_1_ = -p_92035_3_; lvt_14_1_ <= p_92035_3_; ++lvt_14_1_) {
            for(int lvt_15_1_ = -p_92035_3_; lvt_15_1_ <= p_92035_3_; ++lvt_15_1_) {
               for(int lvt_16_1_ = -p_92035_3_; lvt_16_1_ <= p_92035_3_; ++lvt_16_1_) {
                  double lvt_17_1_ = (double)lvt_15_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double lvt_19_1_ = (double)lvt_14_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double lvt_21_1_ = (double)lvt_16_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double lvt_23_1_ = (double)MathHelper.sqrt(lvt_17_1_ * lvt_17_1_ + lvt_19_1_ * lvt_19_1_ + lvt_21_1_ * lvt_21_1_) / p_92035_1_ + this.rand.nextGaussian() * 0.05D;
                  this.createParticle(lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_17_1_ / lvt_23_1_, lvt_19_1_ / lvt_23_1_, lvt_21_1_ / lvt_23_1_, p_92035_4_, p_92035_5_, p_92035_6_, p_92035_7_);
                  if (lvt_14_1_ != -p_92035_3_ && lvt_14_1_ != p_92035_3_ && lvt_15_1_ != -p_92035_3_ && lvt_15_1_ != p_92035_3_) {
                     lvt_16_1_ += p_92035_3_ * 2 - 1;
                  }
               }
            }
         }

      }

      private void createShaped(double p_92038_1_, double[][] p_92038_3_, int[] p_92038_4_, int[] p_92038_5_, boolean p_92038_6_, boolean p_92038_7_, boolean p_92038_8_) {
         double lvt_9_1_ = p_92038_3_[0][0];
         double lvt_11_1_ = p_92038_3_[0][1];
         this.createParticle(this.posX, this.posY, this.posZ, lvt_9_1_ * p_92038_1_, lvt_11_1_ * p_92038_1_, 0.0D, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
         float lvt_13_1_ = this.rand.nextFloat() * 3.1415927F;
         double lvt_14_1_ = p_92038_8_ ? 0.034D : 0.34D;

         for(int lvt_16_1_ = 0; lvt_16_1_ < 3; ++lvt_16_1_) {
            double lvt_17_1_ = (double)lvt_13_1_ + (double)((float)lvt_16_1_ * 3.1415927F) * lvt_14_1_;
            double lvt_19_1_ = lvt_9_1_;
            double lvt_21_1_ = lvt_11_1_;

            for(int lvt_23_1_ = 1; lvt_23_1_ < p_92038_3_.length; ++lvt_23_1_) {
               double lvt_24_1_ = p_92038_3_[lvt_23_1_][0];
               double lvt_26_1_ = p_92038_3_[lvt_23_1_][1];

               for(double lvt_28_1_ = 0.25D; lvt_28_1_ <= 1.0D; lvt_28_1_ += 0.25D) {
                  double lvt_30_1_ = MathHelper.lerp(lvt_28_1_, lvt_19_1_, lvt_24_1_) * p_92038_1_;
                  double lvt_32_1_ = MathHelper.lerp(lvt_28_1_, lvt_21_1_, lvt_26_1_) * p_92038_1_;
                  double lvt_34_1_ = lvt_30_1_ * Math.sin(lvt_17_1_);
                  lvt_30_1_ *= Math.cos(lvt_17_1_);

                  for(double lvt_36_1_ = -1.0D; lvt_36_1_ <= 1.0D; lvt_36_1_ += 2.0D) {
                     this.createParticle(this.posX, this.posY, this.posZ, lvt_30_1_ * lvt_36_1_, lvt_32_1_, lvt_34_1_ * lvt_36_1_, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
                  }
               }

               lvt_19_1_ = lvt_24_1_;
               lvt_21_1_ = lvt_26_1_;
            }
         }

      }

      private void createBurst(int[] p_92036_1_, int[] p_92036_2_, boolean p_92036_3_, boolean p_92036_4_) {
         double lvt_5_1_ = this.rand.nextGaussian() * 0.05D;
         double lvt_7_1_ = this.rand.nextGaussian() * 0.05D;

         for(int lvt_9_1_ = 0; lvt_9_1_ < 70; ++lvt_9_1_) {
            double lvt_10_1_ = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + lvt_5_1_;
            double lvt_12_1_ = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + lvt_7_1_;
            double lvt_14_1_ = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
            this.createParticle(this.posX, this.posY, this.posZ, lvt_10_1_, lvt_14_1_, lvt_12_1_, p_92036_1_, p_92036_2_, p_92036_3_, p_92036_4_);
         }

      }
   }
}
