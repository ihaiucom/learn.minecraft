package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingDustParticle extends SpriteTexturedParticle {
   private final float rotSpeed;
   private final IAnimatedSprite field_217580_F;

   private FallingDustParticle(World p_i51033_1_, double p_i51033_2_, double p_i51033_4_, double p_i51033_6_, float p_i51033_8_, float p_i51033_9_, float p_i51033_10_, IAnimatedSprite p_i51033_11_) {
      super(p_i51033_1_, p_i51033_2_, p_i51033_4_, p_i51033_6_);
      this.field_217580_F = p_i51033_11_;
      this.particleRed = p_i51033_8_;
      this.particleGreen = p_i51033_9_;
      this.particleBlue = p_i51033_10_;
      float lvt_12_1_ = 0.9F;
      this.particleScale *= 0.67499995F;
      int lvt_13_1_ = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.maxAge = (int)Math.max((float)lvt_13_1_ * 0.9F, 1.0F);
      this.selectSpriteWithAge(p_i51033_11_);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.particleAngle = (float)Math.random() * 6.2831855F;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
         this.selectSpriteWithAge(this.field_217580_F);
         this.prevParticleAngle = this.particleAngle;
         this.particleAngle += 3.1415927F * this.rotSpeed * 2.0F;
         if (this.onGround) {
            this.prevParticleAngle = this.particleAngle = 0.0F;
         }

         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionY -= 0.003000000026077032D;
         this.motionY = Math.max(this.motionY, -0.14000000059604645D);
      }
   }

   // $FF: synthetic method
   FallingDustParticle(World p_i51034_1_, double p_i51034_2_, double p_i51034_4_, double p_i51034_6_, float p_i51034_8_, float p_i51034_9_, float p_i51034_10_, IAnimatedSprite p_i51034_11_, Object p_i51034_12_) {
      this(p_i51034_1_, p_i51034_2_, p_i51034_4_, p_i51034_6_, p_i51034_8_, p_i51034_9_, p_i51034_10_, p_i51034_11_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i51109_1_) {
         this.spriteSet = p_i51109_1_;
      }

      @Nullable
      public Particle makeParticle(BlockParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         BlockState lvt_15_1_ = p_199234_1_.getBlockState();
         if (!lvt_15_1_.isAir() && lvt_15_1_.getRenderType() == BlockRenderType.INVISIBLE) {
            return null;
         } else {
            int lvt_16_1_ = Minecraft.getInstance().getBlockColors().getColorOrMaterialColor(lvt_15_1_, p_199234_2_, new BlockPos(p_199234_3_, p_199234_5_, p_199234_7_));
            if (lvt_15_1_.getBlock() instanceof FallingBlock) {
               lvt_16_1_ = ((FallingBlock)lvt_15_1_.getBlock()).getDustColor(lvt_15_1_);
            }

            float lvt_17_1_ = (float)(lvt_16_1_ >> 16 & 255) / 255.0F;
            float lvt_18_1_ = (float)(lvt_16_1_ >> 8 & 255) / 255.0F;
            float lvt_19_1_ = (float)(lvt_16_1_ & 255) / 255.0F;
            return new FallingDustParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, lvt_17_1_, lvt_18_1_, lvt_19_1_, this.spriteSet);
         }
      }
   }
}
