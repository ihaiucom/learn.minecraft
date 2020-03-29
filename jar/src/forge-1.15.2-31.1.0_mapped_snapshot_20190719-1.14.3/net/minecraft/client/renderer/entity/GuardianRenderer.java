package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianRenderer extends MobRenderer<GuardianEntity, GuardianModel> {
   private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
   private static final RenderType field_229107_h_;

   public GuardianRenderer(EntityRendererManager p_i46171_1_) {
      this(p_i46171_1_, 0.5F);
   }

   protected GuardianRenderer(EntityRendererManager p_i50968_1_, float p_i50968_2_) {
      super(p_i50968_1_, new GuardianModel(), p_i50968_2_);
   }

   public boolean func_225626_a_(GuardianEntity p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (super.func_225626_a_((MobEntity)p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
         return true;
      } else {
         if (p_225626_1_.hasTargetedEntity()) {
            LivingEntity lvt_9_1_ = p_225626_1_.getTargetedEntity();
            if (lvt_9_1_ != null) {
               Vec3d lvt_10_1_ = this.getPosition(lvt_9_1_, (double)lvt_9_1_.getHeight() * 0.5D, 1.0F);
               Vec3d lvt_11_1_ = this.getPosition(p_225626_1_, (double)p_225626_1_.getEyeHeight(), 1.0F);
               return p_225626_2_.func_228957_a_(new AxisAlignedBB(lvt_11_1_.x, lvt_11_1_.y, lvt_11_1_.z, lvt_10_1_.x, lvt_10_1_.y, lvt_10_1_.z));
            }
         }

         return false;
      }
   }

   private Vec3d getPosition(LivingEntity p_177110_1_, double p_177110_2_, float p_177110_4_) {
      double lvt_5_1_ = MathHelper.lerp((double)p_177110_4_, p_177110_1_.lastTickPosX, p_177110_1_.func_226277_ct_());
      double lvt_7_1_ = MathHelper.lerp((double)p_177110_4_, p_177110_1_.lastTickPosY, p_177110_1_.func_226278_cu_()) + p_177110_2_;
      double lvt_9_1_ = MathHelper.lerp((double)p_177110_4_, p_177110_1_.lastTickPosZ, p_177110_1_.func_226281_cx_());
      return new Vec3d(lvt_5_1_, lvt_7_1_, lvt_9_1_);
   }

   public void func_225623_a_(GuardianEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.func_225623_a_((MobEntity)p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      LivingEntity lvt_7_1_ = p_225623_1_.getTargetedEntity();
      if (lvt_7_1_ != null) {
         float lvt_8_1_ = p_225623_1_.getAttackAnimationScale(p_225623_3_);
         float lvt_9_1_ = (float)p_225623_1_.world.getGameTime() + p_225623_3_;
         float lvt_10_1_ = lvt_9_1_ * 0.5F % 1.0F;
         float lvt_11_1_ = p_225623_1_.getEyeHeight();
         p_225623_4_.func_227860_a_();
         p_225623_4_.func_227861_a_(0.0D, (double)lvt_11_1_, 0.0D);
         Vec3d lvt_12_1_ = this.getPosition(lvt_7_1_, (double)lvt_7_1_.getHeight() * 0.5D, p_225623_3_);
         Vec3d lvt_13_1_ = this.getPosition(p_225623_1_, (double)lvt_11_1_, p_225623_3_);
         Vec3d lvt_14_1_ = lvt_12_1_.subtract(lvt_13_1_);
         float lvt_15_1_ = (float)(lvt_14_1_.length() + 1.0D);
         lvt_14_1_ = lvt_14_1_.normalize();
         float lvt_16_1_ = (float)Math.acos(lvt_14_1_.y);
         float lvt_17_1_ = (float)Math.atan2(lvt_14_1_.z, lvt_14_1_.x);
         p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((1.5707964F - lvt_17_1_) * 57.295776F));
         p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_16_1_ * 57.295776F));
         int lvt_18_1_ = true;
         float lvt_19_1_ = lvt_9_1_ * 0.05F * -1.5F;
         float lvt_20_1_ = lvt_8_1_ * lvt_8_1_;
         int lvt_21_1_ = 64 + (int)(lvt_20_1_ * 191.0F);
         int lvt_22_1_ = 32 + (int)(lvt_20_1_ * 191.0F);
         int lvt_23_1_ = 128 - (int)(lvt_20_1_ * 64.0F);
         float lvt_24_1_ = 0.2F;
         float lvt_25_1_ = 0.282F;
         float lvt_26_1_ = MathHelper.cos(lvt_19_1_ + 2.3561945F) * 0.282F;
         float lvt_27_1_ = MathHelper.sin(lvt_19_1_ + 2.3561945F) * 0.282F;
         float lvt_28_1_ = MathHelper.cos(lvt_19_1_ + 0.7853982F) * 0.282F;
         float lvt_29_1_ = MathHelper.sin(lvt_19_1_ + 0.7853982F) * 0.282F;
         float lvt_30_1_ = MathHelper.cos(lvt_19_1_ + 3.926991F) * 0.282F;
         float lvt_31_1_ = MathHelper.sin(lvt_19_1_ + 3.926991F) * 0.282F;
         float lvt_32_1_ = MathHelper.cos(lvt_19_1_ + 5.4977875F) * 0.282F;
         float lvt_33_1_ = MathHelper.sin(lvt_19_1_ + 5.4977875F) * 0.282F;
         float lvt_34_1_ = MathHelper.cos(lvt_19_1_ + 3.1415927F) * 0.2F;
         float lvt_35_1_ = MathHelper.sin(lvt_19_1_ + 3.1415927F) * 0.2F;
         float lvt_36_1_ = MathHelper.cos(lvt_19_1_ + 0.0F) * 0.2F;
         float lvt_37_1_ = MathHelper.sin(lvt_19_1_ + 0.0F) * 0.2F;
         float lvt_38_1_ = MathHelper.cos(lvt_19_1_ + 1.5707964F) * 0.2F;
         float lvt_39_1_ = MathHelper.sin(lvt_19_1_ + 1.5707964F) * 0.2F;
         float lvt_40_1_ = MathHelper.cos(lvt_19_1_ + 4.712389F) * 0.2F;
         float lvt_41_1_ = MathHelper.sin(lvt_19_1_ + 4.712389F) * 0.2F;
         float lvt_43_1_ = 0.0F;
         float lvt_44_1_ = 0.4999F;
         float lvt_45_1_ = -1.0F + lvt_10_1_;
         float lvt_46_1_ = lvt_15_1_ * 2.5F + lvt_45_1_;
         IVertexBuilder lvt_47_1_ = p_225623_5_.getBuffer(field_229107_h_);
         MatrixStack.Entry lvt_48_1_ = p_225623_4_.func_227866_c_();
         Matrix4f lvt_49_1_ = lvt_48_1_.func_227870_a_();
         Matrix3f lvt_50_1_ = lvt_48_1_.func_227872_b_();
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_34_1_, lvt_15_1_, lvt_35_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.4999F, lvt_46_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_34_1_, 0.0F, lvt_35_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.4999F, lvt_45_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_36_1_, 0.0F, lvt_37_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.0F, lvt_45_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_36_1_, lvt_15_1_, lvt_37_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.0F, lvt_46_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_38_1_, lvt_15_1_, lvt_39_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.4999F, lvt_46_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_38_1_, 0.0F, lvt_39_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.4999F, lvt_45_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_40_1_, 0.0F, lvt_41_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.0F, lvt_45_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_40_1_, lvt_15_1_, lvt_41_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.0F, lvt_46_1_);
         float lvt_51_1_ = 0.0F;
         if (p_225623_1_.ticksExisted % 2 == 0) {
            lvt_51_1_ = 0.5F;
         }

         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_26_1_, lvt_15_1_, lvt_27_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.5F, lvt_51_1_ + 0.5F);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_28_1_, lvt_15_1_, lvt_29_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 1.0F, lvt_51_1_ + 0.5F);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_32_1_, lvt_15_1_, lvt_33_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 1.0F, lvt_51_1_);
         func_229108_a_(lvt_47_1_, lvt_49_1_, lvt_50_1_, lvt_30_1_, lvt_15_1_, lvt_31_1_, lvt_21_1_, lvt_22_1_, lvt_23_1_, 0.5F, lvt_51_1_);
         p_225623_4_.func_227865_b_();
      }

   }

   private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
      p_229108_0_.func_227888_a_(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).func_225586_a_(p_229108_6_, p_229108_7_, p_229108_8_, 255).func_225583_a_(p_229108_9_, p_229108_10_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(15728880).func_227887_a_(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getEntityTexture(GuardianEntity p_110775_1_) {
      return GUARDIAN_TEXTURE;
   }

   static {
      field_229107_h_ = RenderType.func_228640_c_(GUARDIAN_BEAM_TEXTURE);
   }
}
