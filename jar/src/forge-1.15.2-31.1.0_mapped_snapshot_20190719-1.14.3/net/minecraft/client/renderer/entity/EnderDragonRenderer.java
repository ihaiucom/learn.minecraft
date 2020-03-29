package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderDragonRenderer extends EntityRenderer<EnderDragonEntity> {
   public static final ResourceLocation ENDERCRYSTAL_BEAM_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation field_229052_g_ = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType field_229053_h_;
   private static final RenderType field_229054_i_;
   private static final RenderType field_229055_j_;
   private static final RenderType field_229056_k_;
   private static final float field_229057_l_;
   private final EnderDragonRenderer.EnderDragonModel field_229058_m_ = new EnderDragonRenderer.EnderDragonModel();

   public EnderDragonRenderer(EntityRendererManager p_i46183_1_) {
      super(p_i46183_1_);
      this.shadowSize = 0.5F;
   }

   public void func_225623_a_(EnderDragonEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      float lvt_7_1_ = (float)p_225623_1_.getMovementOffsets(7, p_225623_3_)[0];
      float lvt_8_1_ = (float)(p_225623_1_.getMovementOffsets(5, p_225623_3_)[1] - p_225623_1_.getMovementOffsets(10, p_225623_3_)[1]);
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-lvt_7_1_));
      p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_8_1_ * 10.0F));
      p_225623_4_.func_227861_a_(0.0D, 0.0D, 1.0D);
      p_225623_4_.func_227862_a_(-1.0F, -1.0F, 1.0F);
      p_225623_4_.func_227861_a_(0.0D, -1.5010000467300415D, 0.0D);
      boolean lvt_9_1_ = p_225623_1_.hurtTime > 0;
      this.field_229058_m_.setLivingAnimations(p_225623_1_, 0.0F, 0.0F, p_225623_3_);
      IVertexBuilder lvt_10_3_;
      if (p_225623_1_.deathTicks > 0) {
         float lvt_10_1_ = (float)p_225623_1_.deathTicks / 200.0F;
         IVertexBuilder lvt_11_1_ = p_225623_5_.getBuffer(RenderType.func_228635_a_(DRAGON_EXPLODING_TEXTURES, lvt_10_1_));
         this.field_229058_m_.func_225598_a_(p_225623_4_, lvt_11_1_, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
         IVertexBuilder lvt_12_1_ = p_225623_5_.getBuffer(field_229054_i_);
         this.field_229058_m_.func_225598_a_(p_225623_4_, lvt_12_1_, p_225623_6_, OverlayTexture.func_229200_a_(0.0F, lvt_9_1_), 1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         lvt_10_3_ = p_225623_5_.getBuffer(field_229053_h_);
         this.field_229058_m_.func_225598_a_(p_225623_4_, lvt_10_3_, p_225623_6_, OverlayTexture.func_229200_a_(0.0F, lvt_9_1_), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      lvt_10_3_ = p_225623_5_.getBuffer(field_229055_j_);
      this.field_229058_m_.func_225598_a_(p_225623_4_, lvt_10_3_, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
      float lvt_11_2_;
      float lvt_12_2_;
      if (p_225623_1_.deathTicks > 0) {
         lvt_11_2_ = ((float)p_225623_1_.deathTicks + p_225623_3_) / 200.0F;
         lvt_12_2_ = 0.0F;
         if (lvt_11_2_ > 0.8F) {
            lvt_12_2_ = (lvt_11_2_ - 0.8F) / 0.2F;
         }

         Random lvt_13_1_ = new Random(432L);
         IVertexBuilder lvt_14_1_ = p_225623_5_.getBuffer(RenderType.func_228657_l_());
         p_225623_4_.func_227860_a_();
         p_225623_4_.func_227861_a_(0.0D, -1.0D, -2.0D);

         for(int lvt_15_1_ = 0; (float)lvt_15_1_ < (lvt_11_2_ + lvt_11_2_ * lvt_11_2_) / 2.0F * 60.0F; ++lvt_15_1_) {
            p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F));
            p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F));
            p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F));
            p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F));
            p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F));
            p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(lvt_13_1_.nextFloat() * 360.0F + lvt_11_2_ * 90.0F));
            float lvt_16_1_ = lvt_13_1_.nextFloat() * 20.0F + 5.0F + lvt_12_2_ * 10.0F;
            float lvt_17_1_ = lvt_13_1_.nextFloat() * 2.0F + 1.0F + lvt_12_2_ * 2.0F;
            Matrix4f lvt_18_1_ = p_225623_4_.func_227866_c_().func_227870_a_();
            int lvt_19_1_ = (int)(255.0F * (1.0F - lvt_12_2_));
            func_229061_a_(lvt_14_1_, lvt_18_1_, lvt_19_1_);
            func_229060_a_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
            func_229062_b_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
            func_229061_a_(lvt_14_1_, lvt_18_1_, lvt_19_1_);
            func_229062_b_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
            func_229063_c_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
            func_229061_a_(lvt_14_1_, lvt_18_1_, lvt_19_1_);
            func_229063_c_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
            func_229060_a_(lvt_14_1_, lvt_18_1_, lvt_16_1_, lvt_17_1_);
         }

         p_225623_4_.func_227865_b_();
      }

      p_225623_4_.func_227865_b_();
      if (p_225623_1_.field_70992_bH != null) {
         p_225623_4_.func_227860_a_();
         lvt_11_2_ = (float)(p_225623_1_.field_70992_bH.func_226277_ct_() - MathHelper.lerp((double)p_225623_3_, p_225623_1_.prevPosX, p_225623_1_.func_226277_ct_()));
         lvt_12_2_ = (float)(p_225623_1_.field_70992_bH.func_226278_cu_() - MathHelper.lerp((double)p_225623_3_, p_225623_1_.prevPosY, p_225623_1_.func_226278_cu_()));
         float lvt_13_2_ = (float)(p_225623_1_.field_70992_bH.func_226281_cx_() - MathHelper.lerp((double)p_225623_3_, p_225623_1_.prevPosZ, p_225623_1_.func_226281_cx_()));
         func_229059_a_(lvt_11_2_, lvt_12_2_ + EnderCrystalRenderer.func_229051_a_(p_225623_1_.field_70992_bH, p_225623_3_), lvt_13_2_, p_225623_3_, p_225623_1_.ticksExisted, p_225623_4_, p_225623_5_, p_225623_6_);
         p_225623_4_.func_227865_b_();
      }

      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   private static void func_229061_a_(IVertexBuilder p_229061_0_, Matrix4f p_229061_1_, int p_229061_2_) {
      p_229061_0_.func_227888_a_(p_229061_1_, 0.0F, 0.0F, 0.0F).func_225586_a_(255, 255, 255, p_229061_2_).endVertex();
      p_229061_0_.func_227888_a_(p_229061_1_, 0.0F, 0.0F, 0.0F).func_225586_a_(255, 255, 255, p_229061_2_).endVertex();
   }

   private static void func_229060_a_(IVertexBuilder p_229060_0_, Matrix4f p_229060_1_, float p_229060_2_, float p_229060_3_) {
      p_229060_0_.func_227888_a_(p_229060_1_, -field_229057_l_ * p_229060_3_, p_229060_2_, -0.5F * p_229060_3_).func_225586_a_(255, 0, 255, 0).endVertex();
   }

   private static void func_229062_b_(IVertexBuilder p_229062_0_, Matrix4f p_229062_1_, float p_229062_2_, float p_229062_3_) {
      p_229062_0_.func_227888_a_(p_229062_1_, field_229057_l_ * p_229062_3_, p_229062_2_, -0.5F * p_229062_3_).func_225586_a_(255, 0, 255, 0).endVertex();
   }

   private static void func_229063_c_(IVertexBuilder p_229063_0_, Matrix4f p_229063_1_, float p_229063_2_, float p_229063_3_) {
      p_229063_0_.func_227888_a_(p_229063_1_, 0.0F, p_229063_2_, 1.0F * p_229063_3_).func_225586_a_(255, 0, 255, 0).endVertex();
   }

   public static void func_229059_a_(float p_229059_0_, float p_229059_1_, float p_229059_2_, float p_229059_3_, int p_229059_4_, MatrixStack p_229059_5_, IRenderTypeBuffer p_229059_6_, int p_229059_7_) {
      float lvt_8_1_ = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_2_ * p_229059_2_);
      float lvt_9_1_ = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_1_ * p_229059_1_ + p_229059_2_ * p_229059_2_);
      p_229059_5_.func_227860_a_();
      p_229059_5_.func_227861_a_(0.0D, 2.0D, 0.0D);
      p_229059_5_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_((float)(-Math.atan2((double)p_229059_2_, (double)p_229059_0_)) - 1.5707964F));
      p_229059_5_.func_227863_a_(Vector3f.field_229179_b_.func_229193_c_((float)(-Math.atan2((double)lvt_8_1_, (double)p_229059_1_)) - 1.5707964F));
      IVertexBuilder lvt_10_1_ = p_229059_6_.getBuffer(field_229056_k_);
      float lvt_11_1_ = 0.0F - ((float)p_229059_4_ + p_229059_3_) * 0.01F;
      float lvt_12_1_ = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_1_ * p_229059_1_ + p_229059_2_ * p_229059_2_) / 32.0F - ((float)p_229059_4_ + p_229059_3_) * 0.01F;
      int lvt_13_1_ = true;
      float lvt_14_1_ = 0.0F;
      float lvt_15_1_ = 0.75F;
      float lvt_16_1_ = 0.0F;
      MatrixStack.Entry lvt_17_1_ = p_229059_5_.func_227866_c_();
      Matrix4f lvt_18_1_ = lvt_17_1_.func_227870_a_();
      Matrix3f lvt_19_1_ = lvt_17_1_.func_227872_b_();

      for(int lvt_20_1_ = 1; lvt_20_1_ <= 8; ++lvt_20_1_) {
         float lvt_21_1_ = MathHelper.sin((float)lvt_20_1_ * 6.2831855F / 8.0F) * 0.75F;
         float lvt_22_1_ = MathHelper.cos((float)lvt_20_1_ * 6.2831855F / 8.0F) * 0.75F;
         float lvt_23_1_ = (float)lvt_20_1_ / 8.0F;
         lvt_10_1_.func_227888_a_(lvt_18_1_, lvt_14_1_ * 0.2F, lvt_15_1_ * 0.2F, 0.0F).func_225586_a_(0, 0, 0, 255).func_225583_a_(lvt_16_1_, lvt_11_1_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229059_7_).func_227887_a_(lvt_19_1_, 0.0F, -1.0F, 0.0F).endVertex();
         lvt_10_1_.func_227888_a_(lvt_18_1_, lvt_14_1_, lvt_15_1_, lvt_9_1_).func_225586_a_(255, 255, 255, 255).func_225583_a_(lvt_16_1_, lvt_12_1_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229059_7_).func_227887_a_(lvt_19_1_, 0.0F, -1.0F, 0.0F).endVertex();
         lvt_10_1_.func_227888_a_(lvt_18_1_, lvt_21_1_, lvt_22_1_, lvt_9_1_).func_225586_a_(255, 255, 255, 255).func_225583_a_(lvt_23_1_, lvt_12_1_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229059_7_).func_227887_a_(lvt_19_1_, 0.0F, -1.0F, 0.0F).endVertex();
         lvt_10_1_.func_227888_a_(lvt_18_1_, lvt_21_1_ * 0.2F, lvt_22_1_ * 0.2F, 0.0F).func_225586_a_(0, 0, 0, 255).func_225583_a_(lvt_23_1_, lvt_11_1_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229059_7_).func_227887_a_(lvt_19_1_, 0.0F, -1.0F, 0.0F).endVertex();
         lvt_14_1_ = lvt_21_1_;
         lvt_15_1_ = lvt_22_1_;
         lvt_16_1_ = lvt_23_1_;
      }

      p_229059_5_.func_227865_b_();
   }

   public ResourceLocation getEntityTexture(EnderDragonEntity p_110775_1_) {
      return DRAGON_TEXTURES;
   }

   static {
      field_229053_h_ = RenderType.func_228640_c_(DRAGON_TEXTURES);
      field_229054_i_ = RenderType.func_228648_g_(DRAGON_TEXTURES);
      field_229055_j_ = RenderType.func_228652_i_(field_229052_g_);
      field_229056_k_ = RenderType.func_228646_f_(ENDERCRYSTAL_BEAM_TEXTURES);
      field_229057_l_ = (float)(Math.sqrt(3.0D) / 2.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public static class EnderDragonModel extends EntityModel<EnderDragonEntity> {
      private final ModelRenderer field_78221_a;
      private final ModelRenderer spine;
      private final ModelRenderer field_78220_c;
      private final ModelRenderer field_78217_d;
      private ModelRenderer field_229065_h_;
      private ModelRenderer field_229066_i_;
      private ModelRenderer field_229067_j_;
      private ModelRenderer field_229068_k_;
      private ModelRenderer field_229069_l_;
      private ModelRenderer field_229070_m_;
      private ModelRenderer field_229071_n_;
      private ModelRenderer field_229072_o_;
      private ModelRenderer field_229073_p_;
      private ModelRenderer field_229074_t_;
      private ModelRenderer field_229075_u_;
      private ModelRenderer field_229076_v_;
      private ModelRenderer field_229077_w_;
      private ModelRenderer field_229078_x_;
      private ModelRenderer field_229079_y_;
      private ModelRenderer field_229080_z_;
      @Nullable
      private EnderDragonEntity field_229064_A_;
      private float partialTicks;

      public EnderDragonModel() {
         this.textureWidth = 256;
         this.textureHeight = 256;
         float lvt_1_1_ = -16.0F;
         this.field_78221_a = new ModelRenderer(this);
         this.field_78221_a.func_217178_a("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
         this.field_78221_a.func_217178_a("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
         this.field_78221_a.mirror = true;
         this.field_78221_a.func_217178_a("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
         this.field_78221_a.func_217178_a("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
         this.field_78221_a.mirror = false;
         this.field_78221_a.func_217178_a("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
         this.field_78221_a.func_217178_a("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
         this.field_78220_c = new ModelRenderer(this);
         this.field_78220_c.setRotationPoint(0.0F, 4.0F, -8.0F);
         this.field_78220_c.func_217178_a("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
         this.field_78221_a.addChild(this.field_78220_c);
         this.spine = new ModelRenderer(this);
         this.spine.func_217178_a("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
         this.spine.func_217178_a("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
         this.field_78217_d = new ModelRenderer(this);
         this.field_78217_d.setRotationPoint(0.0F, 4.0F, 8.0F);
         this.field_78217_d.func_217178_a("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
         this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
         this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
         this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
         this.field_229065_h_ = new ModelRenderer(this);
         this.field_229065_h_.mirror = true;
         this.field_229065_h_.setRotationPoint(12.0F, 5.0F, 2.0F);
         this.field_229065_h_.func_217178_a("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
         this.field_229065_h_.func_217178_a("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
         this.field_229066_i_ = new ModelRenderer(this);
         this.field_229066_i_.mirror = true;
         this.field_229066_i_.setRotationPoint(56.0F, 0.0F, 0.0F);
         this.field_229066_i_.func_217178_a("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
         this.field_229066_i_.func_217178_a("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
         this.field_229065_h_.addChild(this.field_229066_i_);
         this.field_229067_j_ = new ModelRenderer(this);
         this.field_229067_j_.setRotationPoint(12.0F, 20.0F, 2.0F);
         this.field_229067_j_.func_217178_a("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
         this.field_229068_k_ = new ModelRenderer(this);
         this.field_229068_k_.setRotationPoint(0.0F, 20.0F, -1.0F);
         this.field_229068_k_.func_217178_a("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
         this.field_229067_j_.addChild(this.field_229068_k_);
         this.field_229069_l_ = new ModelRenderer(this);
         this.field_229069_l_.setRotationPoint(0.0F, 23.0F, 0.0F);
         this.field_229069_l_.func_217178_a("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
         this.field_229068_k_.addChild(this.field_229069_l_);
         this.field_229070_m_ = new ModelRenderer(this);
         this.field_229070_m_.setRotationPoint(16.0F, 16.0F, 42.0F);
         this.field_229070_m_.func_217178_a("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
         this.field_229071_n_ = new ModelRenderer(this);
         this.field_229071_n_.setRotationPoint(0.0F, 32.0F, -4.0F);
         this.field_229071_n_.func_217178_a("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
         this.field_229070_m_.addChild(this.field_229071_n_);
         this.field_229072_o_ = new ModelRenderer(this);
         this.field_229072_o_.setRotationPoint(0.0F, 31.0F, 4.0F);
         this.field_229072_o_.func_217178_a("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
         this.field_229071_n_.addChild(this.field_229072_o_);
         this.field_229073_p_ = new ModelRenderer(this);
         this.field_229073_p_.setRotationPoint(-12.0F, 5.0F, 2.0F);
         this.field_229073_p_.func_217178_a("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
         this.field_229073_p_.func_217178_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
         this.field_229074_t_ = new ModelRenderer(this);
         this.field_229074_t_.setRotationPoint(-56.0F, 0.0F, 0.0F);
         this.field_229074_t_.func_217178_a("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
         this.field_229074_t_.func_217178_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
         this.field_229073_p_.addChild(this.field_229074_t_);
         this.field_229075_u_ = new ModelRenderer(this);
         this.field_229075_u_.setRotationPoint(-12.0F, 20.0F, 2.0F);
         this.field_229075_u_.func_217178_a("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
         this.field_229076_v_ = new ModelRenderer(this);
         this.field_229076_v_.setRotationPoint(0.0F, 20.0F, -1.0F);
         this.field_229076_v_.func_217178_a("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
         this.field_229075_u_.addChild(this.field_229076_v_);
         this.field_229077_w_ = new ModelRenderer(this);
         this.field_229077_w_.setRotationPoint(0.0F, 23.0F, 0.0F);
         this.field_229077_w_.func_217178_a("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
         this.field_229076_v_.addChild(this.field_229077_w_);
         this.field_229078_x_ = new ModelRenderer(this);
         this.field_229078_x_.setRotationPoint(-16.0F, 16.0F, 42.0F);
         this.field_229078_x_.func_217178_a("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
         this.field_229079_y_ = new ModelRenderer(this);
         this.field_229079_y_.setRotationPoint(0.0F, 32.0F, -4.0F);
         this.field_229079_y_.func_217178_a("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
         this.field_229078_x_.addChild(this.field_229079_y_);
         this.field_229080_z_ = new ModelRenderer(this);
         this.field_229080_z_.setRotationPoint(0.0F, 31.0F, 4.0F);
         this.field_229080_z_.func_217178_a("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
         this.field_229079_y_.addChild(this.field_229080_z_);
      }

      public void setLivingAnimations(EnderDragonEntity p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
         this.field_229064_A_ = p_212843_1_;
         this.partialTicks = p_212843_4_;
      }

      public void func_225597_a_(EnderDragonEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      }

      public void func_225598_a_(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
         p_225598_1_.func_227860_a_();
         float lvt_9_1_ = MathHelper.lerp(this.partialTicks, this.field_229064_A_.prevAnimTime, this.field_229064_A_.animTime);
         this.field_78220_c.rotateAngleX = (float)(Math.sin((double)(lvt_9_1_ * 6.2831855F)) + 1.0D) * 0.2F;
         float lvt_10_1_ = (float)(Math.sin((double)(lvt_9_1_ * 6.2831855F - 1.0F)) + 1.0D);
         lvt_10_1_ = (lvt_10_1_ * lvt_10_1_ + lvt_10_1_ * 2.0F) * 0.05F;
         p_225598_1_.func_227861_a_(0.0D, (double)(lvt_10_1_ - 2.0F), -3.0D);
         p_225598_1_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_10_1_ * 2.0F));
         float lvt_11_1_ = 0.0F;
         float lvt_12_1_ = 20.0F;
         float lvt_13_1_ = -12.0F;
         float lvt_14_1_ = 1.5F;
         double[] lvt_15_1_ = this.field_229064_A_.getMovementOffsets(6, this.partialTicks);
         float lvt_16_1_ = MathHelper.func_226168_l_(this.field_229064_A_.getMovementOffsets(5, this.partialTicks)[0] - this.field_229064_A_.getMovementOffsets(10, this.partialTicks)[0]);
         float lvt_17_1_ = MathHelper.func_226168_l_(this.field_229064_A_.getMovementOffsets(5, this.partialTicks)[0] + (double)(lvt_16_1_ / 2.0F));
         float lvt_18_1_ = lvt_9_1_ * 6.2831855F;

         float lvt_21_2_;
         for(int lvt_19_1_ = 0; lvt_19_1_ < 5; ++lvt_19_1_) {
            double[] lvt_20_1_ = this.field_229064_A_.getMovementOffsets(5 - lvt_19_1_, this.partialTicks);
            lvt_21_2_ = (float)Math.cos((double)((float)lvt_19_1_ * 0.45F + lvt_18_1_)) * 0.15F;
            this.spine.rotateAngleY = MathHelper.func_226168_l_(lvt_20_1_[0] - lvt_15_1_[0]) * 0.017453292F * 1.5F;
            this.spine.rotateAngleX = lvt_21_2_ + this.field_229064_A_.getHeadPartYOffset(lvt_19_1_, lvt_15_1_, lvt_20_1_) * 0.017453292F * 1.5F * 5.0F;
            this.spine.rotateAngleZ = -MathHelper.func_226168_l_(lvt_20_1_[0] - (double)lvt_17_1_) * 0.017453292F * 1.5F;
            this.spine.rotationPointY = lvt_12_1_;
            this.spine.rotationPointZ = lvt_13_1_;
            this.spine.rotationPointX = lvt_11_1_;
            lvt_12_1_ = (float)((double)lvt_12_1_ + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            lvt_13_1_ = (float)((double)lvt_13_1_ - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            lvt_11_1_ = (float)((double)lvt_11_1_ - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.func_228308_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_);
         }

         this.field_78221_a.rotationPointY = lvt_12_1_;
         this.field_78221_a.rotationPointZ = lvt_13_1_;
         this.field_78221_a.rotationPointX = lvt_11_1_;
         double[] lvt_19_2_ = this.field_229064_A_.getMovementOffsets(0, this.partialTicks);
         this.field_78221_a.rotateAngleY = MathHelper.func_226168_l_(lvt_19_2_[0] - lvt_15_1_[0]) * 0.017453292F;
         this.field_78221_a.rotateAngleX = MathHelper.func_226168_l_((double)this.field_229064_A_.getHeadPartYOffset(6, lvt_15_1_, lvt_19_2_)) * 0.017453292F * 1.5F * 5.0F;
         this.field_78221_a.rotateAngleZ = -MathHelper.func_226168_l_(lvt_19_2_[0] - (double)lvt_17_1_) * 0.017453292F;
         this.field_78221_a.func_228308_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_);
         p_225598_1_.func_227860_a_();
         p_225598_1_.func_227861_a_(0.0D, 1.0D, 0.0D);
         p_225598_1_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(-lvt_16_1_ * 1.5F));
         p_225598_1_.func_227861_a_(0.0D, -1.0D, 0.0D);
         this.field_78217_d.rotateAngleZ = 0.0F;
         this.field_78217_d.func_228308_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_);
         float lvt_20_2_ = lvt_9_1_ * 6.2831855F;
         this.field_229065_h_.rotateAngleX = 0.125F - (float)Math.cos((double)lvt_20_2_) * 0.2F;
         this.field_229065_h_.rotateAngleY = -0.25F;
         this.field_229065_h_.rotateAngleZ = -((float)(Math.sin((double)lvt_20_2_) + 0.125D)) * 0.8F;
         this.field_229066_i_.rotateAngleZ = (float)(Math.sin((double)(lvt_20_2_ + 2.0F)) + 0.5D) * 0.75F;
         this.field_229073_p_.rotateAngleX = this.field_229065_h_.rotateAngleX;
         this.field_229073_p_.rotateAngleY = -this.field_229065_h_.rotateAngleY;
         this.field_229073_p_.rotateAngleZ = -this.field_229065_h_.rotateAngleZ;
         this.field_229074_t_.rotateAngleZ = -this.field_229066_i_.rotateAngleZ;
         this.func_229081_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, lvt_10_1_, this.field_229065_h_, this.field_229067_j_, this.field_229068_k_, this.field_229069_l_, this.field_229070_m_, this.field_229071_n_, this.field_229072_o_);
         this.func_229081_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, lvt_10_1_, this.field_229073_p_, this.field_229075_u_, this.field_229076_v_, this.field_229077_w_, this.field_229078_x_, this.field_229079_y_, this.field_229080_z_);
         p_225598_1_.func_227865_b_();
         lvt_21_2_ = -((float)Math.sin((double)(lvt_9_1_ * 6.2831855F))) * 0.0F;
         lvt_18_1_ = lvt_9_1_ * 6.2831855F;
         lvt_12_1_ = 10.0F;
         lvt_13_1_ = 60.0F;
         lvt_11_1_ = 0.0F;
         lvt_15_1_ = this.field_229064_A_.getMovementOffsets(11, this.partialTicks);

         for(int lvt_22_1_ = 0; lvt_22_1_ < 12; ++lvt_22_1_) {
            lvt_19_2_ = this.field_229064_A_.getMovementOffsets(12 + lvt_22_1_, this.partialTicks);
            lvt_21_2_ = (float)((double)lvt_21_2_ + Math.sin((double)((float)lvt_22_1_ * 0.45F + lvt_18_1_)) * 0.05000000074505806D);
            this.spine.rotateAngleY = (MathHelper.func_226168_l_(lvt_19_2_[0] - lvt_15_1_[0]) * 1.5F + 180.0F) * 0.017453292F;
            this.spine.rotateAngleX = lvt_21_2_ + (float)(lvt_19_2_[1] - lvt_15_1_[1]) * 0.017453292F * 1.5F * 5.0F;
            this.spine.rotateAngleZ = MathHelper.func_226168_l_(lvt_19_2_[0] - (double)lvt_17_1_) * 0.017453292F * 1.5F;
            this.spine.rotationPointY = lvt_12_1_;
            this.spine.rotationPointZ = lvt_13_1_;
            this.spine.rotationPointX = lvt_11_1_;
            lvt_12_1_ = (float)((double)lvt_12_1_ + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            lvt_13_1_ = (float)((double)lvt_13_1_ - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            lvt_11_1_ = (float)((double)lvt_11_1_ - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.func_228308_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_);
         }

         p_225598_1_.func_227865_b_();
      }

      private void func_229081_a_(MatrixStack p_229081_1_, IVertexBuilder p_229081_2_, int p_229081_3_, int p_229081_4_, float p_229081_5_, ModelRenderer p_229081_6_, ModelRenderer p_229081_7_, ModelRenderer p_229081_8_, ModelRenderer p_229081_9_, ModelRenderer p_229081_10_, ModelRenderer p_229081_11_, ModelRenderer p_229081_12_) {
         p_229081_10_.rotateAngleX = 1.0F + p_229081_5_ * 0.1F;
         p_229081_11_.rotateAngleX = 0.5F + p_229081_5_ * 0.1F;
         p_229081_12_.rotateAngleX = 0.75F + p_229081_5_ * 0.1F;
         p_229081_7_.rotateAngleX = 1.3F + p_229081_5_ * 0.1F;
         p_229081_8_.rotateAngleX = -0.5F - p_229081_5_ * 0.1F;
         p_229081_9_.rotateAngleX = 0.75F + p_229081_5_ * 0.1F;
         p_229081_6_.func_228308_a_(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
         p_229081_7_.func_228308_a_(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
         p_229081_10_.func_228308_a_(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
      }
   }
}
