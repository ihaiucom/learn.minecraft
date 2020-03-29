package net.minecraft.client.particle;

import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleAnimatedParticle extends SpriteTexturedParticle {
   protected final IAnimatedSprite field_217584_C;
   private final float yAccel;
   private float baseAirFriction = 0.91F;
   private float fadeTargetRed;
   private float fadeTargetGreen;
   private float fadeTargetBlue;
   private boolean fadingColor;

   protected SimpleAnimatedParticle(World p_i51013_1_, double p_i51013_2_, double p_i51013_4_, double p_i51013_6_, IAnimatedSprite p_i51013_8_, float p_i51013_9_) {
      super(p_i51013_1_, p_i51013_2_, p_i51013_4_, p_i51013_6_);
      this.field_217584_C = p_i51013_8_;
      this.yAccel = p_i51013_9_;
   }

   public void setColor(int p_187146_1_) {
      float lvt_2_1_ = (float)((p_187146_1_ & 16711680) >> 16) / 255.0F;
      float lvt_3_1_ = (float)((p_187146_1_ & '\uff00') >> 8) / 255.0F;
      float lvt_4_1_ = (float)((p_187146_1_ & 255) >> 0) / 255.0F;
      float lvt_5_1_ = 1.0F;
      this.setColor(lvt_2_1_ * 1.0F, lvt_3_1_ * 1.0F, lvt_4_1_ * 1.0F);
   }

   public void setColorFade(int p_187145_1_) {
      this.fadeTargetRed = (float)((p_187145_1_ & 16711680) >> 16) / 255.0F;
      this.fadeTargetGreen = (float)((p_187145_1_ & '\uff00') >> 8) / 255.0F;
      this.fadeTargetBlue = (float)((p_187145_1_ & 255) >> 0) / 255.0F;
      this.fadingColor = true;
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
         this.selectSpriteWithAge(this.field_217584_C);
         if (this.age > this.maxAge / 2) {
            this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            if (this.fadingColor) {
               this.particleRed += (this.fadeTargetRed - this.particleRed) * 0.2F;
               this.particleGreen += (this.fadeTargetGreen - this.particleGreen) * 0.2F;
               this.particleBlue += (this.fadeTargetBlue - this.particleBlue) * 0.2F;
            }
         }

         this.motionY += (double)this.yAccel;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)this.baseAirFriction;
         this.motionY *= (double)this.baseAirFriction;
         this.motionZ *= (double)this.baseAirFriction;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   public int getBrightnessForRender(float p_189214_1_) {
      return 15728880;
   }

   protected void setBaseAirFriction(float p_191238_1_) {
      this.baseAirFriction = p_191238_1_;
   }
}
