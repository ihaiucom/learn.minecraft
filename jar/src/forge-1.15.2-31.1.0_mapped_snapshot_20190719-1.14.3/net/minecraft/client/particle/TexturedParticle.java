package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedParticle extends Particle {
   protected float particleScale;

   protected TexturedParticle(World p_i51011_1_, double p_i51011_2_, double p_i51011_4_, double p_i51011_6_) {
      super(p_i51011_1_, p_i51011_2_, p_i51011_4_, p_i51011_6_);
      this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected TexturedParticle(World p_i51012_1_, double p_i51012_2_, double p_i51012_4_, double p_i51012_6_, double p_i51012_8_, double p_i51012_10_, double p_i51012_12_) {
      super(p_i51012_1_, p_i51012_2_, p_i51012_4_, p_i51012_6_, p_i51012_8_, p_i51012_10_, p_i51012_12_);
      this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public void func_225606_a_(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      Vec3d lvt_4_1_ = p_225606_2_.getProjectedView();
      float lvt_5_1_ = (float)(MathHelper.lerp((double)p_225606_3_, this.prevPosX, this.posX) - lvt_4_1_.getX());
      float lvt_6_1_ = (float)(MathHelper.lerp((double)p_225606_3_, this.prevPosY, this.posY) - lvt_4_1_.getY());
      float lvt_7_1_ = (float)(MathHelper.lerp((double)p_225606_3_, this.prevPosZ, this.posZ) - lvt_4_1_.getZ());
      Quaternion lvt_8_2_;
      if (this.particleAngle == 0.0F) {
         lvt_8_2_ = p_225606_2_.func_227995_f_();
      } else {
         lvt_8_2_ = new Quaternion(p_225606_2_.func_227995_f_());
         float lvt_9_1_ = MathHelper.lerp(p_225606_3_, this.prevParticleAngle, this.particleAngle);
         lvt_8_2_.multiply(Vector3f.field_229183_f_.func_229193_c_(lvt_9_1_));
      }

      Vector3f lvt_9_2_ = new Vector3f(-1.0F, -1.0F, 0.0F);
      lvt_9_2_.func_214905_a(lvt_8_2_);
      Vector3f[] lvt_10_1_ = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float lvt_11_1_ = this.getScale(p_225606_3_);

      for(int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_) {
         Vector3f lvt_13_1_ = lvt_10_1_[lvt_12_1_];
         lvt_13_1_.func_214905_a(lvt_8_2_);
         lvt_13_1_.mul(lvt_11_1_);
         lvt_13_1_.add(lvt_5_1_, lvt_6_1_, lvt_7_1_);
      }

      float lvt_12_2_ = this.getMinU();
      float lvt_13_2_ = this.getMaxU();
      float lvt_14_1_ = this.getMinV();
      float lvt_15_1_ = this.getMaxV();
      int lvt_16_1_ = this.getBrightnessForRender(p_225606_3_);
      p_225606_1_.func_225582_a_((double)lvt_10_1_[0].getX(), (double)lvt_10_1_[0].getY(), (double)lvt_10_1_[0].getZ()).func_225583_a_(lvt_13_2_, lvt_15_1_).func_227885_a_(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_227886_a_(lvt_16_1_).endVertex();
      p_225606_1_.func_225582_a_((double)lvt_10_1_[1].getX(), (double)lvt_10_1_[1].getY(), (double)lvt_10_1_[1].getZ()).func_225583_a_(lvt_13_2_, lvt_14_1_).func_227885_a_(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_227886_a_(lvt_16_1_).endVertex();
      p_225606_1_.func_225582_a_((double)lvt_10_1_[2].getX(), (double)lvt_10_1_[2].getY(), (double)lvt_10_1_[2].getZ()).func_225583_a_(lvt_12_2_, lvt_14_1_).func_227885_a_(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_227886_a_(lvt_16_1_).endVertex();
      p_225606_1_.func_225582_a_((double)lvt_10_1_[3].getX(), (double)lvt_10_1_[3].getY(), (double)lvt_10_1_[3].getZ()).func_225583_a_(lvt_12_2_, lvt_15_1_).func_227885_a_(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).func_227886_a_(lvt_16_1_).endVertex();
   }

   public float getScale(float p_217561_1_) {
      return this.particleScale;
   }

   public Particle multipleParticleScaleBy(float p_70541_1_) {
      this.particleScale *= p_70541_1_;
      return super.multipleParticleScaleBy(p_70541_1_);
   }

   protected abstract float getMinU();

   protected abstract float getMaxU();

   protected abstract float getMinV();

   protected abstract float getMaxV();
}
