package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableParticle extends SpriteTexturedParticle {
   private final double coordX;
   private final double coordY;
   private final double coordZ;

   private EnchantmentTableParticle(World p_i1204_1_, double p_i1204_2_, double p_i1204_4_, double p_i1204_6_, double p_i1204_8_, double p_i1204_10_, double p_i1204_12_) {
      super(p_i1204_1_, p_i1204_2_, p_i1204_4_, p_i1204_6_);
      this.motionX = p_i1204_8_;
      this.motionY = p_i1204_10_;
      this.motionZ = p_i1204_12_;
      this.coordX = p_i1204_2_;
      this.coordY = p_i1204_4_;
      this.coordZ = p_i1204_6_;
      this.prevPosX = p_i1204_2_ + p_i1204_8_;
      this.prevPosY = p_i1204_4_ + p_i1204_10_;
      this.prevPosZ = p_i1204_6_ + p_i1204_12_;
      this.posX = this.prevPosX;
      this.posY = this.prevPosY;
      this.posZ = this.prevPosZ;
      this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
      float lvt_14_1_ = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = 0.9F * lvt_14_1_;
      this.particleGreen = 0.9F * lvt_14_1_;
      this.particleBlue = lvt_14_1_;
      this.canCollide = false;
      this.maxAge = (int)(Math.random() * 10.0D) + 30;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int lvt_2_1_ = super.getBrightnessForRender(p_189214_1_);
      float lvt_3_1_ = (float)this.age / (float)this.maxAge;
      lvt_3_1_ *= lvt_3_1_;
      lvt_3_1_ *= lvt_3_1_;
      int lvt_4_1_ = lvt_2_1_ & 255;
      int lvt_5_1_ = lvt_2_1_ >> 16 & 255;
      lvt_5_1_ += (int)(lvt_3_1_ * 15.0F * 16.0F);
      if (lvt_5_1_ > 240) {
         lvt_5_1_ = 240;
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
         float lvt_1_1_ = (float)this.age / (float)this.maxAge;
         lvt_1_1_ = 1.0F - lvt_1_1_;
         float lvt_2_1_ = 1.0F - lvt_1_1_;
         lvt_2_1_ *= lvt_2_1_;
         lvt_2_1_ *= lvt_2_1_;
         this.posX = this.coordX + this.motionX * (double)lvt_1_1_;
         this.posY = this.coordY + this.motionY * (double)lvt_1_1_ - (double)(lvt_2_1_ * 1.2F);
         this.posZ = this.coordZ + this.motionZ * (double)lvt_1_1_;
      }
   }

   // $FF: synthetic method
   EnchantmentTableParticle(World p_i51038_1_, double p_i51038_2_, double p_i51038_4_, double p_i51038_6_, double p_i51038_8_, double p_i51038_10_, double p_i51038_12_, Object p_i51038_14_) {
      this(p_i51038_1_, p_i51038_2_, p_i51038_4_, p_i51038_6_, p_i51038_8_, p_i51038_10_, p_i51038_12_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class NautilusFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public NautilusFactory(IAnimatedSprite p_i50442_1_) {
         this.spriteSet = p_i50442_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         EnchantmentTableParticle lvt_15_1_ = new EnchantmentTableParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class EnchantmentTable implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public EnchantmentTable(IAnimatedSprite p_i50441_1_) {
         this.spriteSet = p_i50441_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         EnchantmentTableParticle lvt_15_1_ = new EnchantmentTableParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
