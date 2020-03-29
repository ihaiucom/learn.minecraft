package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianModel extends SegmentedModel<GuardianEntity> {
   private static final float[] field_217136_a = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] field_217137_b = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] field_217138_f = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] field_217139_g = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] field_217140_h = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] field_217141_i = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private final ModelRenderer guardianBody;
   private final ModelRenderer guardianEye;
   private final ModelRenderer[] guardianSpines;
   private final ModelRenderer[] guardianTail;

   public GuardianModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.guardianSpines = new ModelRenderer[12];
      this.guardianBody = new ModelRenderer(this);
      this.guardianBody.setTextureOffset(0, 0).func_228300_a_(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F);
      this.guardianBody.setTextureOffset(0, 28).func_228300_a_(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F);
      this.guardianBody.setTextureOffset(0, 28).func_228304_a_(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true);
      this.guardianBody.setTextureOffset(16, 40).func_228300_a_(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F);
      this.guardianBody.setTextureOffset(16, 40).func_228300_a_(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F);

      for(int lvt_1_1_ = 0; lvt_1_1_ < this.guardianSpines.length; ++lvt_1_1_) {
         this.guardianSpines[lvt_1_1_] = new ModelRenderer(this, 0, 0);
         this.guardianSpines[lvt_1_1_].func_228300_a_(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);
         this.guardianBody.addChild(this.guardianSpines[lvt_1_1_]);
      }

      this.guardianEye = new ModelRenderer(this, 8, 0);
      this.guardianEye.func_228300_a_(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F);
      this.guardianBody.addChild(this.guardianEye);
      this.guardianTail = new ModelRenderer[3];
      this.guardianTail[0] = new ModelRenderer(this, 40, 0);
      this.guardianTail[0].func_228300_a_(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F);
      this.guardianTail[1] = new ModelRenderer(this, 0, 54);
      this.guardianTail[1].func_228300_a_(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F);
      this.guardianTail[2] = new ModelRenderer(this);
      this.guardianTail[2].setTextureOffset(41, 32).func_228300_a_(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F);
      this.guardianTail[2].setTextureOffset(25, 19).func_228300_a_(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F);
      this.guardianBody.addChild(this.guardianTail[0]);
      this.guardianTail[0].addChild(this.guardianTail[1]);
      this.guardianTail[1].addChild(this.guardianTail[2]);
      this.func_228261_a_(0.0F, 0.0F);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.guardianBody);
   }

   public void func_225597_a_(GuardianEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = p_225597_4_ - (float)p_225597_1_.ticksExisted;
      this.guardianBody.rotateAngleY = p_225597_5_ * 0.017453292F;
      this.guardianBody.rotateAngleX = p_225597_6_ * 0.017453292F;
      float lvt_8_1_ = (1.0F - p_225597_1_.getSpikesAnimation(lvt_7_1_)) * 0.55F;
      this.func_228261_a_(p_225597_4_, lvt_8_1_);
      this.guardianEye.rotationPointZ = -8.25F;
      Entity lvt_9_1_ = Minecraft.getInstance().getRenderViewEntity();
      if (p_225597_1_.hasTargetedEntity()) {
         lvt_9_1_ = p_225597_1_.getTargetedEntity();
      }

      if (lvt_9_1_ != null) {
         Vec3d lvt_10_1_ = ((Entity)lvt_9_1_).getEyePosition(0.0F);
         Vec3d lvt_11_1_ = p_225597_1_.getEyePosition(0.0F);
         double lvt_12_1_ = lvt_10_1_.y - lvt_11_1_.y;
         if (lvt_12_1_ > 0.0D) {
            this.guardianEye.rotationPointY = 0.0F;
         } else {
            this.guardianEye.rotationPointY = 1.0F;
         }

         Vec3d lvt_14_1_ = p_225597_1_.getLook(0.0F);
         lvt_14_1_ = new Vec3d(lvt_14_1_.x, 0.0D, lvt_14_1_.z);
         Vec3d lvt_15_1_ = (new Vec3d(lvt_11_1_.x - lvt_10_1_.x, 0.0D, lvt_11_1_.z - lvt_10_1_.z)).normalize().rotateYaw(1.5707964F);
         double lvt_16_1_ = lvt_14_1_.dotProduct(lvt_15_1_);
         this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(lvt_16_1_)) * 2.0F * (float)Math.signum(lvt_16_1_);
      }

      this.guardianEye.showModel = true;
      float lvt_10_2_ = p_225597_1_.getTailAnimation(lvt_7_1_);
      this.guardianTail[0].rotateAngleY = MathHelper.sin(lvt_10_2_) * 3.1415927F * 0.05F;
      this.guardianTail[1].rotateAngleY = MathHelper.sin(lvt_10_2_) * 3.1415927F * 0.1F;
      this.guardianTail[1].rotationPointX = -1.5F;
      this.guardianTail[1].rotationPointY = 0.5F;
      this.guardianTail[1].rotationPointZ = 14.0F;
      this.guardianTail[2].rotateAngleY = MathHelper.sin(lvt_10_2_) * 3.1415927F * 0.15F;
      this.guardianTail[2].rotationPointX = 0.5F;
      this.guardianTail[2].rotationPointY = 0.5F;
      this.guardianTail[2].rotationPointZ = 6.0F;
   }

   private void func_228261_a_(float p_228261_1_, float p_228261_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < 12; ++lvt_3_1_) {
         this.guardianSpines[lvt_3_1_].rotateAngleX = 3.1415927F * field_217136_a[lvt_3_1_];
         this.guardianSpines[lvt_3_1_].rotateAngleY = 3.1415927F * field_217137_b[lvt_3_1_];
         this.guardianSpines[lvt_3_1_].rotateAngleZ = 3.1415927F * field_217138_f[lvt_3_1_];
         this.guardianSpines[lvt_3_1_].rotationPointX = field_217139_g[lvt_3_1_] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)lvt_3_1_) * 0.01F - p_228261_2_);
         this.guardianSpines[lvt_3_1_].rotationPointY = 16.0F + field_217140_h[lvt_3_1_] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)lvt_3_1_) * 0.01F - p_228261_2_);
         this.guardianSpines[lvt_3_1_].rotationPointZ = field_217141_i[lvt_3_1_] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)lvt_3_1_) * 0.01F - p_228261_2_);
      }

   }
}
