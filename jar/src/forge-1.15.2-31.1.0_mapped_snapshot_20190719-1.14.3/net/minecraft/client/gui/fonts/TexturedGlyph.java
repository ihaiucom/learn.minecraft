package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedGlyph {
   private final RenderType field_228160_a_;
   private final RenderType field_228161_b_;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float minX;
   private final float maxX;
   private final float minY;
   private final float maxY;

   public TexturedGlyph(RenderType p_i225922_1_, RenderType p_i225922_2_, float p_i225922_3_, float p_i225922_4_, float p_i225922_5_, float p_i225922_6_, float p_i225922_7_, float p_i225922_8_, float p_i225922_9_, float p_i225922_10_) {
      this.field_228160_a_ = p_i225922_1_;
      this.field_228161_b_ = p_i225922_2_;
      this.u0 = p_i225922_3_;
      this.u1 = p_i225922_4_;
      this.v0 = p_i225922_5_;
      this.v1 = p_i225922_6_;
      this.minX = p_i225922_7_;
      this.maxX = p_i225922_8_;
      this.minY = p_i225922_9_;
      this.maxY = p_i225922_10_;
   }

   public void func_225595_a_(boolean p_225595_1_, float p_225595_2_, float p_225595_3_, Matrix4f p_225595_4_, IVertexBuilder p_225595_5_, float p_225595_6_, float p_225595_7_, float p_225595_8_, float p_225595_9_, int p_225595_10_) {
      int lvt_11_1_ = true;
      float lvt_12_1_ = p_225595_2_ + this.minX;
      float lvt_13_1_ = p_225595_2_ + this.maxX;
      float lvt_14_1_ = this.minY - 3.0F;
      float lvt_15_1_ = this.maxY - 3.0F;
      float lvt_16_1_ = p_225595_3_ + lvt_14_1_;
      float lvt_17_1_ = p_225595_3_ + lvt_15_1_;
      float lvt_18_1_ = p_225595_1_ ? 1.0F - 0.25F * lvt_14_1_ : 0.0F;
      float lvt_19_1_ = p_225595_1_ ? 1.0F - 0.25F * lvt_15_1_ : 0.0F;
      p_225595_5_.func_227888_a_(p_225595_4_, lvt_12_1_ + lvt_18_1_, lvt_16_1_, 0.0F).func_227885_a_(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).func_225583_a_(this.u0, this.v0).func_227886_a_(p_225595_10_).endVertex();
      p_225595_5_.func_227888_a_(p_225595_4_, lvt_12_1_ + lvt_19_1_, lvt_17_1_, 0.0F).func_227885_a_(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).func_225583_a_(this.u0, this.v1).func_227886_a_(p_225595_10_).endVertex();
      p_225595_5_.func_227888_a_(p_225595_4_, lvt_13_1_ + lvt_19_1_, lvt_17_1_, 0.0F).func_227885_a_(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).func_225583_a_(this.u1, this.v1).func_227886_a_(p_225595_10_).endVertex();
      p_225595_5_.func_227888_a_(p_225595_4_, lvt_13_1_ + lvt_18_1_, lvt_16_1_, 0.0F).func_227885_a_(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).func_225583_a_(this.u1, this.v0).func_227886_a_(p_225595_10_).endVertex();
   }

   public void func_228162_a_(TexturedGlyph.Effect p_228162_1_, Matrix4f p_228162_2_, IVertexBuilder p_228162_3_, int p_228162_4_) {
      p_228162_3_.func_227888_a_(p_228162_2_, p_228162_1_.field_228164_a_, p_228162_1_.field_228165_b_, p_228162_1_.field_228168_e_).func_227885_a_(p_228162_1_.field_228169_f_, p_228162_1_.g, p_228162_1_.field_228170_h_, p_228162_1_.field_228171_i_).func_225583_a_(this.u0, this.v0).func_227886_a_(p_228162_4_).endVertex();
      p_228162_3_.func_227888_a_(p_228162_2_, p_228162_1_.field_228166_c_, p_228162_1_.field_228165_b_, p_228162_1_.field_228168_e_).func_227885_a_(p_228162_1_.field_228169_f_, p_228162_1_.g, p_228162_1_.field_228170_h_, p_228162_1_.field_228171_i_).func_225583_a_(this.u0, this.v1).func_227886_a_(p_228162_4_).endVertex();
      p_228162_3_.func_227888_a_(p_228162_2_, p_228162_1_.field_228166_c_, p_228162_1_.field_228167_d_, p_228162_1_.field_228168_e_).func_227885_a_(p_228162_1_.field_228169_f_, p_228162_1_.g, p_228162_1_.field_228170_h_, p_228162_1_.field_228171_i_).func_225583_a_(this.u1, this.v1).func_227886_a_(p_228162_4_).endVertex();
      p_228162_3_.func_227888_a_(p_228162_2_, p_228162_1_.field_228164_a_, p_228162_1_.field_228167_d_, p_228162_1_.field_228168_e_).func_227885_a_(p_228162_1_.field_228169_f_, p_228162_1_.g, p_228162_1_.field_228170_h_, p_228162_1_.field_228171_i_).func_225583_a_(this.u1, this.v0).func_227886_a_(p_228162_4_).endVertex();
   }

   public RenderType func_228163_a_(boolean p_228163_1_) {
      return p_228163_1_ ? this.field_228161_b_ : this.field_228160_a_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Effect {
      protected final float field_228164_a_;
      protected final float field_228165_b_;
      protected final float field_228166_c_;
      protected final float field_228167_d_;
      protected final float field_228168_e_;
      protected final float field_228169_f_;
      protected final float g;
      protected final float field_228170_h_;
      protected final float field_228171_i_;

      public Effect(float p_i225923_1_, float p_i225923_2_, float p_i225923_3_, float p_i225923_4_, float p_i225923_5_, float p_i225923_6_, float p_i225923_7_, float p_i225923_8_, float p_i225923_9_) {
         this.field_228164_a_ = p_i225923_1_;
         this.field_228165_b_ = p_i225923_2_;
         this.field_228166_c_ = p_i225923_3_;
         this.field_228167_d_ = p_i225923_4_;
         this.field_228168_e_ = p_i225923_5_;
         this.field_228169_f_ = p_i225923_6_;
         this.g = p_i225923_7_;
         this.field_228170_h_ = p_i225923_8_;
         this.field_228171_i_ = p_i225923_9_;
      }
   }
}
