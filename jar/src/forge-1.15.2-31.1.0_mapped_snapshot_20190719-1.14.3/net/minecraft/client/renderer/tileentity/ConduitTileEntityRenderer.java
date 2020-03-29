package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConduitTileEntityRenderer extends TileEntityRenderer<ConduitTileEntity> {
   public static final Material BASE_TEXTURE;
   public static final Material CAGE_TEXTURE;
   public static final Material WIND_TEXTURE;
   public static final Material VERTICAL_WIND_TEXTURE;
   public static final Material OPEN_EYE_TEXTURE;
   public static final Material CLOSED_EYE_TEXTURE;
   private final ModelRenderer field_228872_h_ = new ModelRenderer(16, 16, 0, 0);
   private final ModelRenderer field_228873_i_;
   private final ModelRenderer field_228874_j_;
   private final ModelRenderer field_228875_k_;

   public ConduitTileEntityRenderer(TileEntityRendererDispatcher p_i226009_1_) {
      super(p_i226009_1_);
      this.field_228872_h_.func_228301_a_(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
      this.field_228873_i_ = new ModelRenderer(64, 32, 0, 0);
      this.field_228873_i_.func_228300_a_(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      this.field_228874_j_ = new ModelRenderer(32, 16, 0, 0);
      this.field_228874_j_.func_228300_a_(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.field_228875_k_ = new ModelRenderer(32, 16, 0, 0);
      this.field_228875_k_.func_228300_a_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
   }

   public void func_225616_a_(ConduitTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      float lvt_7_1_ = (float)p_225616_1_.ticksExisted + p_225616_2_;
      float lvt_8_2_;
      if (!p_225616_1_.isActive()) {
         lvt_8_2_ = p_225616_1_.getActiveRotation(0.0F);
         IVertexBuilder lvt_9_1_ = BASE_TEXTURE.func_229311_a_(p_225616_4_, RenderType::func_228634_a_);
         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
         p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_8_2_));
         this.field_228874_j_.func_228308_a_(p_225616_3_, lvt_9_1_, p_225616_5_, p_225616_6_);
         p_225616_3_.func_227865_b_();
      } else {
         lvt_8_2_ = p_225616_1_.getActiveRotation(p_225616_2_) * 57.295776F;
         float lvt_9_2_ = MathHelper.sin(lvt_7_1_ * 0.1F) / 2.0F + 0.5F;
         lvt_9_2_ += lvt_9_2_ * lvt_9_2_;
         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227861_a_(0.5D, (double)(0.3F + lvt_9_2_ * 0.2F), 0.5D);
         Vector3f lvt_10_1_ = new Vector3f(0.5F, 1.0F, 0.5F);
         lvt_10_1_.func_229194_d_();
         p_225616_3_.func_227863_a_(new Quaternion(lvt_10_1_, lvt_8_2_, true));
         this.field_228875_k_.func_228308_a_(p_225616_3_, CAGE_TEXTURE.func_229311_a_(p_225616_4_, RenderType::func_228640_c_), p_225616_5_, p_225616_6_);
         p_225616_3_.func_227865_b_();
         int lvt_11_1_ = p_225616_1_.ticksExisted / 66 % 3;
         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
         if (lvt_11_1_ == 1) {
            p_225616_3_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90.0F));
         } else if (lvt_11_1_ == 2) {
            p_225616_3_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(90.0F));
         }

         IVertexBuilder lvt_12_1_ = (lvt_11_1_ == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).func_229311_a_(p_225616_4_, RenderType::func_228640_c_);
         this.field_228873_i_.func_228308_a_(p_225616_3_, lvt_12_1_, p_225616_5_, p_225616_6_);
         p_225616_3_.func_227865_b_();
         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
         p_225616_3_.func_227862_a_(0.875F, 0.875F, 0.875F);
         p_225616_3_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
         p_225616_3_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         this.field_228873_i_.func_228308_a_(p_225616_3_, lvt_12_1_, p_225616_5_, p_225616_6_);
         p_225616_3_.func_227865_b_();
         ActiveRenderInfo lvt_13_1_ = this.field_228858_b_.renderInfo;
         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227861_a_(0.5D, (double)(0.3F + lvt_9_2_ * 0.2F), 0.5D);
         p_225616_3_.func_227862_a_(0.5F, 0.5F, 0.5F);
         float lvt_14_1_ = -lvt_13_1_.getYaw();
         p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_14_1_));
         p_225616_3_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_13_1_.getPitch()));
         p_225616_3_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         float lvt_15_1_ = 1.3333334F;
         p_225616_3_.func_227862_a_(1.3333334F, 1.3333334F, 1.3333334F);
         this.field_228872_h_.func_228308_a_(p_225616_3_, (p_225616_1_.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).func_229311_a_(p_225616_4_, RenderType::func_228640_c_), p_225616_5_, p_225616_6_);
         p_225616_3_.func_227865_b_();
      }
   }

   static {
      BASE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/base"));
      CAGE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/cage"));
      WIND_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind"));
      VERTICAL_WIND_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind_vertical"));
      OPEN_EYE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/open_eye"));
      CLOSED_EYE_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/closed_eye"));
   }
}
