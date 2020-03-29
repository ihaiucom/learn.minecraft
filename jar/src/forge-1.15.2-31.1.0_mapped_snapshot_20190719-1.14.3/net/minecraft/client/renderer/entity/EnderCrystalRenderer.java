package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderCrystalRenderer extends EntityRenderer<EnderCrystalEntity> {
   private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType field_229046_e_;
   private static final float field_229047_f_;
   private final ModelRenderer field_229048_g_;
   private final ModelRenderer field_229049_h_;
   private final ModelRenderer field_229050_i_;

   public EnderCrystalRenderer(EntityRendererManager p_i46184_1_) {
      super(p_i46184_1_);
      this.shadowSize = 0.5F;
      this.field_229049_h_ = new ModelRenderer(64, 32, 0, 0);
      this.field_229049_h_.func_228300_a_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.field_229048_g_ = new ModelRenderer(64, 32, 32, 0);
      this.field_229048_g_.func_228300_a_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.field_229050_i_ = new ModelRenderer(64, 32, 0, 16);
      this.field_229050_i_.func_228300_a_(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
   }

   public void func_225623_a_(EnderCrystalEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      float lvt_7_1_ = func_229051_a_(p_225623_1_, p_225623_3_);
      float lvt_8_1_ = ((float)p_225623_1_.innerRotation + p_225623_3_) * 3.0F;
      IVertexBuilder lvt_9_1_ = p_225623_5_.getBuffer(field_229046_e_);
      p_225623_4_.func_227860_a_();
      p_225623_4_.func_227862_a_(2.0F, 2.0F, 2.0F);
      p_225623_4_.func_227861_a_(0.0D, -0.5D, 0.0D);
      int lvt_10_1_ = OverlayTexture.field_229196_a_;
      if (p_225623_1_.shouldShowBottom()) {
         this.field_229050_i_.func_228308_a_(p_225623_4_, lvt_9_1_, p_225623_6_, lvt_10_1_);
      }

      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_8_1_));
      p_225623_4_.func_227861_a_(0.0D, (double)(1.5F + lvt_7_1_ / 2.0F), 0.0D);
      p_225623_4_.func_227863_a_(new Quaternion(new Vector3f(field_229047_f_, 0.0F, field_229047_f_), 60.0F, true));
      this.field_229049_h_.func_228308_a_(p_225623_4_, lvt_9_1_, p_225623_6_, lvt_10_1_);
      float lvt_11_1_ = 0.875F;
      p_225623_4_.func_227862_a_(0.875F, 0.875F, 0.875F);
      p_225623_4_.func_227863_a_(new Quaternion(new Vector3f(field_229047_f_, 0.0F, field_229047_f_), 60.0F, true));
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_8_1_));
      this.field_229049_h_.func_228308_a_(p_225623_4_, lvt_9_1_, p_225623_6_, lvt_10_1_);
      p_225623_4_.func_227862_a_(0.875F, 0.875F, 0.875F);
      p_225623_4_.func_227863_a_(new Quaternion(new Vector3f(field_229047_f_, 0.0F, field_229047_f_), 60.0F, true));
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_8_1_));
      this.field_229048_g_.func_228308_a_(p_225623_4_, lvt_9_1_, p_225623_6_, lvt_10_1_);
      p_225623_4_.func_227865_b_();
      p_225623_4_.func_227865_b_();
      BlockPos lvt_12_1_ = p_225623_1_.getBeamTarget();
      if (lvt_12_1_ != null) {
         float lvt_13_1_ = (float)lvt_12_1_.getX() + 0.5F;
         float lvt_14_1_ = (float)lvt_12_1_.getY() + 0.5F;
         float lvt_15_1_ = (float)lvt_12_1_.getZ() + 0.5F;
         float lvt_16_1_ = (float)((double)lvt_13_1_ - p_225623_1_.func_226277_ct_());
         float lvt_17_1_ = (float)((double)lvt_14_1_ - p_225623_1_.func_226278_cu_());
         float lvt_18_1_ = (float)((double)lvt_15_1_ - p_225623_1_.func_226281_cx_());
         p_225623_4_.func_227861_a_((double)lvt_16_1_, (double)lvt_17_1_, (double)lvt_18_1_);
         EnderDragonRenderer.func_229059_a_(-lvt_16_1_, -lvt_17_1_ + lvt_7_1_, -lvt_18_1_, p_225623_3_, p_225623_1_.innerRotation, p_225623_4_, p_225623_5_, p_225623_6_);
      }

      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public static float func_229051_a_(EnderCrystalEntity p_229051_0_, float p_229051_1_) {
      float lvt_2_1_ = (float)p_229051_0_.innerRotation + p_229051_1_;
      float lvt_3_1_ = MathHelper.sin(lvt_2_1_ * 0.2F) / 2.0F + 0.5F;
      lvt_3_1_ = (lvt_3_1_ * lvt_3_1_ + lvt_3_1_) * 0.4F;
      return lvt_3_1_ - 1.4F;
   }

   public ResourceLocation getEntityTexture(EnderCrystalEntity p_110775_1_) {
      return ENDER_CRYSTAL_TEXTURES;
   }

   public boolean func_225626_a_(EnderCrystalEntity p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return super.func_225626_a_(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_) || p_225626_1_.getBeamTarget() != null;
   }

   static {
      field_229046_e_ = RenderType.func_228640_c_(ENDER_CRYSTAL_TEXTURES);
      field_229047_f_ = (float)Math.sin(0.7853981633974483D);
   }
}
