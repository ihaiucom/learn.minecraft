package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
   private float textureWidth;
   private float textureHeight;
   private int textureOffsetX;
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   public boolean mirror;
   public boolean showModel;
   private final ObjectList<ModelRenderer.ModelBox> cubeList;
   private final ObjectList<ModelRenderer> childModels;

   public ModelRenderer(Model p_i1173_1_) {
      this.textureWidth = 64.0F;
      this.textureHeight = 32.0F;
      this.showModel = true;
      this.cubeList = new ObjectArrayList();
      this.childModels = new ObjectArrayList();
      p_i1173_1_.accept(this);
      this.setTextureSize(p_i1173_1_.textureWidth, p_i1173_1_.textureHeight);
   }

   public ModelRenderer(Model p_i46358_1_, int p_i46358_2_, int p_i46358_3_) {
      this(p_i46358_1_.textureWidth, p_i46358_1_.textureHeight, p_i46358_2_, p_i46358_3_);
      p_i46358_1_.accept(this);
   }

   public ModelRenderer(int p_i225949_1_, int p_i225949_2_, int p_i225949_3_, int p_i225949_4_) {
      this.textureWidth = 64.0F;
      this.textureHeight = 32.0F;
      this.showModel = true;
      this.cubeList = new ObjectArrayList();
      this.childModels = new ObjectArrayList();
      this.setTextureSize(p_i225949_1_, p_i225949_2_);
      this.setTextureOffset(p_i225949_3_, p_i225949_4_);
   }

   public void copyModelAngles(ModelRenderer p_217177_1_) {
      this.rotateAngleX = p_217177_1_.rotateAngleX;
      this.rotateAngleY = p_217177_1_.rotateAngleY;
      this.rotateAngleZ = p_217177_1_.rotateAngleZ;
      this.rotationPointX = p_217177_1_.rotationPointX;
      this.rotationPointY = p_217177_1_.rotationPointY;
      this.rotationPointZ = p_217177_1_.rotationPointZ;
   }

   public void addChild(ModelRenderer p_78792_1_) {
      this.childModels.add(p_78792_1_);
   }

   public ModelRenderer setTextureOffset(int p_78784_1_, int p_78784_2_) {
      this.textureOffsetX = p_78784_1_;
      this.textureOffsetY = p_78784_2_;
      return this;
   }

   public ModelRenderer func_217178_a(String p_217178_1_, float p_217178_2_, float p_217178_3_, float p_217178_4_, int p_217178_5_, int p_217178_6_, int p_217178_7_, float p_217178_8_, int p_217178_9_, int p_217178_10_) {
      this.setTextureOffset(p_217178_9_, p_217178_10_);
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_217178_2_, p_217178_3_, p_217178_4_, (float)p_217178_5_, (float)p_217178_6_, (float)p_217178_7_, p_217178_8_, p_217178_8_, p_217178_8_, this.mirror, false);
      return this;
   }

   public ModelRenderer func_228300_a_(float p_228300_1_, float p_228300_2_, float p_228300_3_, float p_228300_4_, float p_228300_5_, float p_228300_6_) {
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_228300_1_, p_228300_2_, p_228300_3_, p_228300_4_, p_228300_5_, p_228300_6_, 0.0F, 0.0F, 0.0F, this.mirror, false);
      return this;
   }

   public ModelRenderer func_228304_a_(float p_228304_1_, float p_228304_2_, float p_228304_3_, float p_228304_4_, float p_228304_5_, float p_228304_6_, boolean p_228304_7_) {
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_228304_1_, p_228304_2_, p_228304_3_, p_228304_4_, p_228304_5_, p_228304_6_, 0.0F, 0.0F, 0.0F, p_228304_7_, false);
      return this;
   }

   public void func_228301_a_(float p_228301_1_, float p_228301_2_, float p_228301_3_, float p_228301_4_, float p_228301_5_, float p_228301_6_, float p_228301_7_) {
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_228301_1_, p_228301_2_, p_228301_3_, p_228301_4_, p_228301_5_, p_228301_6_, p_228301_7_, p_228301_7_, p_228301_7_, this.mirror, false);
   }

   public void func_228302_a_(float p_228302_1_, float p_228302_2_, float p_228302_3_, float p_228302_4_, float p_228302_5_, float p_228302_6_, float p_228302_7_, float p_228302_8_, float p_228302_9_) {
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_228302_1_, p_228302_2_, p_228302_3_, p_228302_4_, p_228302_5_, p_228302_6_, p_228302_7_, p_228302_8_, p_228302_9_, this.mirror, false);
   }

   public void func_228303_a_(float p_228303_1_, float p_228303_2_, float p_228303_3_, float p_228303_4_, float p_228303_5_, float p_228303_6_, float p_228303_7_, boolean p_228303_8_) {
      this.func_228305_a_(this.textureOffsetX, this.textureOffsetY, p_228303_1_, p_228303_2_, p_228303_3_, p_228303_4_, p_228303_5_, p_228303_6_, p_228303_7_, p_228303_7_, p_228303_7_, p_228303_8_, false);
   }

   private void func_228305_a_(int p_228305_1_, int p_228305_2_, float p_228305_3_, float p_228305_4_, float p_228305_5_, float p_228305_6_, float p_228305_7_, float p_228305_8_, float p_228305_9_, float p_228305_10_, float p_228305_11_, boolean p_228305_12_, boolean p_228305_13_) {
      this.cubeList.add(new ModelRenderer.ModelBox(p_228305_1_, p_228305_2_, p_228305_3_, p_228305_4_, p_228305_5_, p_228305_6_, p_228305_7_, p_228305_8_, p_228305_9_, p_228305_10_, p_228305_11_, p_228305_12_, this.textureWidth, this.textureHeight));
   }

   public void setRotationPoint(float p_78793_1_, float p_78793_2_, float p_78793_3_) {
      this.rotationPointX = p_78793_1_;
      this.rotationPointY = p_78793_2_;
      this.rotationPointZ = p_78793_3_;
   }

   public void func_228308_a_(MatrixStack p_228308_1_, IVertexBuilder p_228308_2_, int p_228308_3_, int p_228308_4_) {
      this.func_228309_a_(p_228308_1_, p_228308_2_, p_228308_3_, p_228308_4_, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_228309_a_(MatrixStack p_228309_1_, IVertexBuilder p_228309_2_, int p_228309_3_, int p_228309_4_, float p_228309_5_, float p_228309_6_, float p_228309_7_, float p_228309_8_) {
      if (this.showModel) {
         if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
            p_228309_1_.func_227860_a_();
            this.func_228307_a_(p_228309_1_);
            this.func_228306_a_(p_228309_1_.func_227866_c_(), p_228309_2_, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
            ObjectListIterator var9 = this.childModels.iterator();

            while(var9.hasNext()) {
               ModelRenderer lvt_10_1_ = (ModelRenderer)var9.next();
               lvt_10_1_.func_228309_a_(p_228309_1_, p_228309_2_, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
            }

            p_228309_1_.func_227865_b_();
         }
      }
   }

   public void func_228307_a_(MatrixStack p_228307_1_) {
      p_228307_1_.func_227861_a_((double)(this.rotationPointX / 16.0F), (double)(this.rotationPointY / 16.0F), (double)(this.rotationPointZ / 16.0F));
      if (this.rotateAngleZ != 0.0F) {
         p_228307_1_.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(this.rotateAngleZ));
      }

      if (this.rotateAngleY != 0.0F) {
         p_228307_1_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(this.rotateAngleY));
      }

      if (this.rotateAngleX != 0.0F) {
         p_228307_1_.func_227863_a_(Vector3f.field_229179_b_.func_229193_c_(this.rotateAngleX));
      }

   }

   private void func_228306_a_(MatrixStack.Entry p_228306_1_, IVertexBuilder p_228306_2_, int p_228306_3_, int p_228306_4_, float p_228306_5_, float p_228306_6_, float p_228306_7_, float p_228306_8_) {
      Matrix4f lvt_9_1_ = p_228306_1_.func_227870_a_();
      Matrix3f lvt_10_1_ = p_228306_1_.func_227872_b_();
      ObjectListIterator var11 = this.cubeList.iterator();

      while(var11.hasNext()) {
         ModelRenderer.ModelBox lvt_12_1_ = (ModelRenderer.ModelBox)var11.next();
         ModelRenderer.TexturedQuad[] var13 = lvt_12_1_.quads;
         int var14 = var13.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            ModelRenderer.TexturedQuad lvt_16_1_ = var13[var15];
            Vector3f lvt_17_1_ = lvt_16_1_.field_228312_b_.func_229195_e_();
            lvt_17_1_.func_229188_a_(lvt_10_1_);
            float lvt_18_1_ = lvt_17_1_.getX();
            float lvt_19_1_ = lvt_17_1_.getY();
            float lvt_20_1_ = lvt_17_1_.getZ();

            for(int lvt_21_1_ = 0; lvt_21_1_ < 4; ++lvt_21_1_) {
               ModelRenderer.PositionTextureVertex lvt_22_1_ = lvt_16_1_.field_78239_a[lvt_21_1_];
               float lvt_23_1_ = lvt_22_1_.vector3D.getX() / 16.0F;
               float lvt_24_1_ = lvt_22_1_.vector3D.getY() / 16.0F;
               float lvt_25_1_ = lvt_22_1_.vector3D.getZ() / 16.0F;
               Vector4f lvt_26_1_ = new Vector4f(lvt_23_1_, lvt_24_1_, lvt_25_1_, 1.0F);
               lvt_26_1_.func_229372_a_(lvt_9_1_);
               p_228306_2_.func_225588_a_(lvt_26_1_.getX(), lvt_26_1_.getY(), lvt_26_1_.getZ(), p_228306_5_, p_228306_6_, p_228306_7_, p_228306_8_, lvt_22_1_.texturePositionX, lvt_22_1_.texturePositionY, p_228306_4_, p_228306_3_, lvt_18_1_, lvt_19_1_, lvt_20_1_);
            }
         }
      }

   }

   public ModelRenderer setTextureSize(int p_78787_1_, int p_78787_2_) {
      this.textureWidth = (float)p_78787_1_;
      this.textureHeight = (float)p_78787_2_;
      return this;
   }

   public ModelRenderer.ModelBox func_228310_a_(Random p_228310_1_) {
      return (ModelRenderer.ModelBox)this.cubeList.get(p_228310_1_.nextInt(this.cubeList.size()));
   }

   @OnlyIn(Dist.CLIENT)
   static class PositionTextureVertex {
      public final Vector3f vector3D;
      public final float texturePositionX;
      public final float texturePositionY;

      public PositionTextureVertex(float p_i1158_1_, float p_i1158_2_, float p_i1158_3_, float p_i1158_4_, float p_i1158_5_) {
         this(new Vector3f(p_i1158_1_, p_i1158_2_, p_i1158_3_), p_i1158_4_, p_i1158_5_);
      }

      public ModelRenderer.PositionTextureVertex setTexturePosition(float p_78240_1_, float p_78240_2_) {
         return new ModelRenderer.PositionTextureVertex(this.vector3D, p_78240_1_, p_78240_2_);
      }

      public PositionTextureVertex(Vector3f p_i225952_1_, float p_i225952_2_, float p_i225952_3_) {
         this.vector3D = p_i225952_1_;
         this.texturePositionX = p_i225952_2_;
         this.texturePositionY = p_i225952_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexturedQuad {
      public final ModelRenderer.PositionTextureVertex[] field_78239_a;
      public final Vector3f field_228312_b_;

      public TexturedQuad(ModelRenderer.PositionTextureVertex[] p_i225951_1_, float p_i225951_2_, float p_i225951_3_, float p_i225951_4_, float p_i225951_5_, float p_i225951_6_, float p_i225951_7_, boolean p_i225951_8_, Direction p_i225951_9_) {
         this.field_78239_a = p_i225951_1_;
         float lvt_10_1_ = 0.0F / p_i225951_6_;
         float lvt_11_1_ = 0.0F / p_i225951_7_;
         p_i225951_1_[0] = p_i225951_1_[0].setTexturePosition(p_i225951_4_ / p_i225951_6_ - lvt_10_1_, p_i225951_3_ / p_i225951_7_ + lvt_11_1_);
         p_i225951_1_[1] = p_i225951_1_[1].setTexturePosition(p_i225951_2_ / p_i225951_6_ + lvt_10_1_, p_i225951_3_ / p_i225951_7_ + lvt_11_1_);
         p_i225951_1_[2] = p_i225951_1_[2].setTexturePosition(p_i225951_2_ / p_i225951_6_ + lvt_10_1_, p_i225951_5_ / p_i225951_7_ - lvt_11_1_);
         p_i225951_1_[3] = p_i225951_1_[3].setTexturePosition(p_i225951_4_ / p_i225951_6_ - lvt_10_1_, p_i225951_5_ / p_i225951_7_ - lvt_11_1_);
         if (p_i225951_8_) {
            int lvt_12_1_ = p_i225951_1_.length;

            for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_12_1_ / 2; ++lvt_13_1_) {
               ModelRenderer.PositionTextureVertex lvt_14_1_ = p_i225951_1_[lvt_13_1_];
               p_i225951_1_[lvt_13_1_] = p_i225951_1_[lvt_12_1_ - 1 - lvt_13_1_];
               p_i225951_1_[lvt_12_1_ - 1 - lvt_13_1_] = lvt_14_1_;
            }
         }

         this.field_228312_b_ = p_i225951_9_.func_229386_k_();
         if (p_i225951_8_) {
            this.field_228312_b_.func_229192_b_(-1.0F, 1.0F, 1.0F);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ModelBox {
      private final ModelRenderer.TexturedQuad[] quads;
      public final float posX1;
      public final float posY1;
      public final float posZ1;
      public final float posX2;
      public final float posY2;
      public final float posZ2;

      public ModelBox(int p_i225950_1_, int p_i225950_2_, float p_i225950_3_, float p_i225950_4_, float p_i225950_5_, float p_i225950_6_, float p_i225950_7_, float p_i225950_8_, float p_i225950_9_, float p_i225950_10_, float p_i225950_11_, boolean p_i225950_12_, float p_i225950_13_, float p_i225950_14_) {
         this.posX1 = p_i225950_3_;
         this.posY1 = p_i225950_4_;
         this.posZ1 = p_i225950_5_;
         this.posX2 = p_i225950_3_ + p_i225950_6_;
         this.posY2 = p_i225950_4_ + p_i225950_7_;
         this.posZ2 = p_i225950_5_ + p_i225950_8_;
         this.quads = new ModelRenderer.TexturedQuad[6];
         float lvt_15_1_ = p_i225950_3_ + p_i225950_6_;
         float lvt_16_1_ = p_i225950_4_ + p_i225950_7_;
         float lvt_17_1_ = p_i225950_5_ + p_i225950_8_;
         p_i225950_3_ -= p_i225950_9_;
         p_i225950_4_ -= p_i225950_10_;
         p_i225950_5_ -= p_i225950_11_;
         lvt_15_1_ += p_i225950_9_;
         lvt_16_1_ += p_i225950_10_;
         lvt_17_1_ += p_i225950_11_;
         if (p_i225950_12_) {
            float lvt_18_1_ = lvt_15_1_;
            lvt_15_1_ = p_i225950_3_;
            p_i225950_3_ = lvt_18_1_;
         }

         ModelRenderer.PositionTextureVertex lvt_18_2_ = new ModelRenderer.PositionTextureVertex(p_i225950_3_, p_i225950_4_, p_i225950_5_, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex lvt_19_1_ = new ModelRenderer.PositionTextureVertex(lvt_15_1_, p_i225950_4_, p_i225950_5_, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex lvt_20_1_ = new ModelRenderer.PositionTextureVertex(lvt_15_1_, lvt_16_1_, p_i225950_5_, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex lvt_21_1_ = new ModelRenderer.PositionTextureVertex(p_i225950_3_, lvt_16_1_, p_i225950_5_, 8.0F, 0.0F);
         ModelRenderer.PositionTextureVertex lvt_22_1_ = new ModelRenderer.PositionTextureVertex(p_i225950_3_, p_i225950_4_, lvt_17_1_, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex lvt_23_1_ = new ModelRenderer.PositionTextureVertex(lvt_15_1_, p_i225950_4_, lvt_17_1_, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex lvt_24_1_ = new ModelRenderer.PositionTextureVertex(lvt_15_1_, lvt_16_1_, lvt_17_1_, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex lvt_25_1_ = new ModelRenderer.PositionTextureVertex(p_i225950_3_, lvt_16_1_, lvt_17_1_, 8.0F, 0.0F);
         float lvt_26_1_ = (float)p_i225950_1_;
         float lvt_27_1_ = (float)p_i225950_1_ + p_i225950_8_;
         float lvt_28_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_;
         float lvt_29_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_6_;
         float lvt_30_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_;
         float lvt_31_1_ = (float)p_i225950_1_ + p_i225950_8_ + p_i225950_6_ + p_i225950_8_ + p_i225950_6_;
         float lvt_32_1_ = (float)p_i225950_2_;
         float lvt_33_1_ = (float)p_i225950_2_ + p_i225950_8_;
         float lvt_34_1_ = (float)p_i225950_2_ + p_i225950_8_ + p_i225950_7_;
         this.quads[2] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_23_1_, lvt_22_1_, lvt_18_2_, lvt_19_1_}, lvt_27_1_, lvt_32_1_, lvt_28_1_, lvt_33_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.DOWN);
         this.quads[3] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_20_1_, lvt_21_1_, lvt_25_1_, lvt_24_1_}, lvt_28_1_, lvt_33_1_, lvt_29_1_, lvt_32_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.UP);
         this.quads[1] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_18_2_, lvt_22_1_, lvt_25_1_, lvt_21_1_}, lvt_26_1_, lvt_33_1_, lvt_27_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.WEST);
         this.quads[4] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_19_1_, lvt_18_2_, lvt_21_1_, lvt_20_1_}, lvt_27_1_, lvt_33_1_, lvt_28_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.NORTH);
         this.quads[0] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_23_1_, lvt_19_1_, lvt_20_1_, lvt_24_1_}, lvt_28_1_, lvt_33_1_, lvt_30_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.EAST);
         this.quads[5] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{lvt_22_1_, lvt_23_1_, lvt_24_1_, lvt_25_1_}, lvt_30_1_, lvt_33_1_, lvt_31_1_, lvt_34_1_, p_i225950_13_, p_i225950_14_, p_i225950_12_, Direction.SOUTH);
      }
   }
}
