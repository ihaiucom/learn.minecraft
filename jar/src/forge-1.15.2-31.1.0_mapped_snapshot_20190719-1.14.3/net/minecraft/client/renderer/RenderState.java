package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderState {
   protected final String field_228509_a_;
   private final Runnable field_228507_Q_;
   private final Runnable field_228508_R_;
   protected static final RenderState.TransparencyState field_228510_b_ = new RenderState.TransparencyState("no_transparency", () -> {
      RenderSystem.disableBlend();
   }, () -> {
   });
   protected static final RenderState.TransparencyState field_228511_c_ = new RenderState.TransparencyState("additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState field_228512_d_ = new RenderState.TransparencyState("lightning_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState field_228513_e_ = new RenderState.TransparencyState("glint_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState field_228514_f_ = new RenderState.TransparencyState("crumbling_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState field_228515_g_ = new RenderState.TransparencyState("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
   }, () -> {
      RenderSystem.disableBlend();
   });
   protected static final RenderState.AlphaState field_228516_h_ = new RenderState.AlphaState(0.0F);
   protected static final RenderState.AlphaState field_228517_i_ = new RenderState.AlphaState(0.003921569F);
   protected static final RenderState.AlphaState field_228518_j_ = new RenderState.AlphaState(0.5F);
   protected static final RenderState.ShadeModelState field_228519_k_ = new RenderState.ShadeModelState(false);
   protected static final RenderState.ShadeModelState field_228520_l_ = new RenderState.ShadeModelState(true);
   protected static final RenderState.TextureState field_228521_m_;
   protected static final RenderState.TextureState field_228522_n_;
   protected static final RenderState.TextureState field_228523_o_;
   protected static final RenderState.TexturingState field_228524_p_;
   protected static final RenderState.TexturingState field_228525_q_;
   protected static final RenderState.TexturingState field_228526_r_;
   protected static final RenderState.TexturingState field_228527_s_;
   protected static final RenderState.LightmapState field_228528_t_;
   protected static final RenderState.LightmapState field_228529_u_;
   protected static final RenderState.OverlayState field_228530_v_;
   protected static final RenderState.OverlayState field_228531_w_;
   protected static final RenderState.DiffuseLightingState field_228532_x_;
   protected static final RenderState.DiffuseLightingState field_228533_y_;
   protected static final RenderState.CullState field_228534_z_;
   protected static final RenderState.CullState field_228491_A_;
   protected static final RenderState.DepthTestState field_228492_B_;
   protected static final RenderState.DepthTestState field_228493_C_;
   protected static final RenderState.DepthTestState field_228494_D_;
   protected static final RenderState.WriteMaskState field_228495_E_;
   protected static final RenderState.WriteMaskState field_228496_F_;
   protected static final RenderState.WriteMaskState field_228497_G_;
   protected static final RenderState.LayerState field_228498_H_;
   protected static final RenderState.LayerState field_228499_I_;
   protected static final RenderState.LayerState field_228500_J_;
   protected static final RenderState.FogState field_228501_K_;
   protected static final RenderState.FogState field_228502_L_;
   protected static final RenderState.FogState field_228503_M_;
   protected static final RenderState.TargetState field_228504_N_;
   protected static final RenderState.TargetState field_228505_O_;
   protected static final RenderState.LineState field_228506_P_;

   public RenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
      this.field_228509_a_ = p_i225973_1_;
      this.field_228507_Q_ = p_i225973_2_;
      this.field_228508_R_ = p_i225973_3_;
   }

   public void func_228547_a_() {
      this.field_228507_Q_.run();
   }

   public void func_228549_b_() {
      this.field_228508_R_.run();
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         RenderState lvt_2_1_ = (RenderState)p_equals_1_;
         return this.field_228509_a_.equals(lvt_2_1_.field_228509_a_);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_228509_a_.hashCode();
   }

   private static void func_228548_a_(float p_228548_0_) {
      RenderSystem.matrixMode(5890);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      long lvt_1_1_ = Util.milliTime() * 8L;
      float lvt_3_1_ = (float)(lvt_1_1_ % 110000L) / 110000.0F;
      float lvt_4_1_ = (float)(lvt_1_1_ % 30000L) / 30000.0F;
      RenderSystem.translatef(-lvt_3_1_, lvt_4_1_, 0.0F);
      RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(p_228548_0_, p_228548_0_, p_228548_0_);
      RenderSystem.matrixMode(5888);
   }

   static {
      field_228521_m_ = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, true);
      field_228522_n_ = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, false);
      field_228523_o_ = new RenderState.TextureState();
      field_228524_p_ = new RenderState.TexturingState("default_texturing", () -> {
      }, () -> {
      });
      field_228525_q_ = new RenderState.TexturingState("outline_texturing", () -> {
         RenderSystem.setupOutline();
      }, () -> {
         RenderSystem.teardownOutline();
      });
      field_228526_r_ = new RenderState.TexturingState("glint_texturing", () -> {
         func_228548_a_(8.0F);
      }, () -> {
         RenderSystem.matrixMode(5890);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      field_228527_s_ = new RenderState.TexturingState("entity_glint_texturing", () -> {
         func_228548_a_(0.16F);
      }, () -> {
         RenderSystem.matrixMode(5890);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      field_228528_t_ = new RenderState.LightmapState(true);
      field_228529_u_ = new RenderState.LightmapState(false);
      field_228530_v_ = new RenderState.OverlayState(true);
      field_228531_w_ = new RenderState.OverlayState(false);
      field_228532_x_ = new RenderState.DiffuseLightingState(true);
      field_228533_y_ = new RenderState.DiffuseLightingState(false);
      field_228534_z_ = new RenderState.CullState(true);
      field_228491_A_ = new RenderState.CullState(false);
      field_228492_B_ = new RenderState.DepthTestState(519);
      field_228493_C_ = new RenderState.DepthTestState(514);
      field_228494_D_ = new RenderState.DepthTestState(515);
      field_228495_E_ = new RenderState.WriteMaskState(true, true);
      field_228496_F_ = new RenderState.WriteMaskState(true, false);
      field_228497_G_ = new RenderState.WriteMaskState(false, true);
      field_228498_H_ = new RenderState.LayerState("no_layering", () -> {
      }, () -> {
      });
      field_228499_I_ = new RenderState.LayerState("polygon_offset_layering", () -> {
         RenderSystem.polygonOffset(-1.0F, -10.0F);
         RenderSystem.enablePolygonOffset();
      }, () -> {
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
      });
      field_228500_J_ = new RenderState.LayerState("projection_layering", () -> {
         RenderSystem.matrixMode(5889);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(1.0F, 1.0F, 0.999F);
         RenderSystem.matrixMode(5888);
      }, () -> {
         RenderSystem.matrixMode(5889);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      field_228501_K_ = new RenderState.FogState("no_fog", () -> {
      }, () -> {
      });
      field_228502_L_ = new RenderState.FogState("fog", () -> {
         FogRenderer.func_228373_b_();
         RenderSystem.enableFog();
      }, () -> {
         RenderSystem.disableFog();
      });
      field_228503_M_ = new RenderState.FogState("black_fog", () -> {
         RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
         RenderSystem.enableFog();
      }, () -> {
         FogRenderer.func_228373_b_();
         RenderSystem.disableFog();
      });
      field_228504_N_ = new RenderState.TargetState("main_target", () -> {
      }, () -> {
      });
      field_228505_O_ = new RenderState.TargetState("outline_target", () -> {
         Minecraft.getInstance().worldRenderer.func_228448_p_().bindFramebuffer(false);
      }, () -> {
         Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
      });
      field_228506_P_ = new RenderState.LineState(OptionalDouble.of(1.0D));
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineState extends RenderState {
      private final OptionalDouble field_228587_Q_;

      public LineState(OptionalDouble p_i225982_1_) {
         super("alpha", () -> {
            if (!Objects.equals(p_i225982_1_, OptionalDouble.of(1.0D))) {
               if (p_i225982_1_.isPresent()) {
                  RenderSystem.lineWidth((float)p_i225982_1_.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().func_228018_at_().getFramebufferWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(p_i225982_1_, OptionalDouble.of(1.0D))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.field_228587_Q_ = p_i225982_1_;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            return !super.equals(p_equals_1_) ? false : Objects.equals(this.field_228587_Q_, ((RenderState.LineState)p_equals_1_).field_228587_Q_);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.field_228587_Q_});
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TargetState extends RenderState {
      public TargetState(String p_i225984_1_, Runnable p_i225984_2_, Runnable p_i225984_3_) {
         super(p_i225984_1_, p_i225984_2_, p_i225984_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FogState extends RenderState {
      public FogState(String p_i225979_1_, Runnable p_i225979_2_, Runnable p_i225979_3_) {
         super(p_i225979_1_, p_i225979_2_, p_i225979_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LayerState extends RenderState {
      public LayerState(String p_i225980_1_, Runnable p_i225980_2_, Runnable p_i225980_3_) {
         super(p_i225980_1_, p_i225980_2_, p_i225980_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WriteMaskState extends RenderState {
      private final boolean field_228610_Q_;
      private final boolean field_228611_R_;

      public WriteMaskState(boolean p_i225991_1_, boolean p_i225991_2_) {
         super("write_mask_state", () -> {
            if (!p_i225991_2_) {
               RenderSystem.depthMask(p_i225991_2_);
            }

            if (!p_i225991_1_) {
               RenderSystem.colorMask(p_i225991_1_, p_i225991_1_, p_i225991_1_, p_i225991_1_);
            }

         }, () -> {
            if (!p_i225991_2_) {
               RenderSystem.depthMask(true);
            }

            if (!p_i225991_1_) {
               RenderSystem.colorMask(true, true, true, true);
            }

         });
         this.field_228610_Q_ = p_i225991_1_;
         this.field_228611_R_ = p_i225991_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.WriteMaskState lvt_2_1_ = (RenderState.WriteMaskState)p_equals_1_;
            return this.field_228610_Q_ == lvt_2_1_.field_228610_Q_ && this.field_228611_R_ == lvt_2_1_.field_228611_R_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_228610_Q_, this.field_228611_R_});
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DepthTestState extends RenderState {
      private final int field_228580_Q_;

      public DepthTestState(int p_i225977_1_) {
         super("depth_test", () -> {
            if (p_i225977_1_ != 519) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(p_i225977_1_);
            }

         }, () -> {
            if (p_i225977_1_ != 519) {
               RenderSystem.disableDepthTest();
               RenderSystem.depthFunc(515);
            }

         });
         this.field_228580_Q_ = p_i225977_1_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.DepthTestState lvt_2_1_ = (RenderState.DepthTestState)p_equals_1_;
            return this.field_228580_Q_ == lvt_2_1_.field_228580_Q_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.field_228580_Q_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CullState extends RenderState.BooleanState {
      public CullState(boolean p_i225976_1_) {
         super("cull", () -> {
            if (p_i225976_1_) {
               RenderSystem.enableCull();
            }

         }, () -> {
            if (p_i225976_1_) {
               RenderSystem.disableCull();
            }

         }, p_i225976_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DiffuseLightingState extends RenderState.BooleanState {
      public DiffuseLightingState(boolean p_i225978_1_) {
         super("diffuse_lighting", () -> {
            if (p_i225978_1_) {
               RenderHelper.func_227780_a_();
            }

         }, () -> {
            if (p_i225978_1_) {
               RenderHelper.disableStandardItemLighting();
            }

         }, p_i225978_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayState extends RenderState.BooleanState {
      public OverlayState(boolean p_i225985_1_) {
         super("overlay", () -> {
            if (p_i225985_1_) {
               Minecraft.getInstance().gameRenderer.func_228385_m_().func_229198_a_();
            }

         }, () -> {
            if (p_i225985_1_) {
               Minecraft.getInstance().gameRenderer.func_228385_m_().func_229203_b_();
            }

         }, p_i225985_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LightmapState extends RenderState.BooleanState {
      public LightmapState(boolean p_i225981_1_) {
         super("lightmap", () -> {
            if (p_i225981_1_) {
               Minecraft.getInstance().gameRenderer.func_228384_l_().enableLightmap();
            }

         }, () -> {
            if (p_i225981_1_) {
               Minecraft.getInstance().gameRenderer.func_228384_l_().disableLightmap();
            }

         }, p_i225981_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState extends RenderState {
      private final boolean field_228577_Q_;

      public BooleanState(String p_i225975_1_, Runnable p_i225975_2_, Runnable p_i225975_3_, boolean p_i225975_4_) {
         super(p_i225975_1_, p_i225975_2_, p_i225975_3_);
         this.field_228577_Q_ = p_i225975_4_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.BooleanState lvt_2_1_ = (RenderState.BooleanState)p_equals_1_;
            return this.field_228577_Q_ == lvt_2_1_.field_228577_Q_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.field_228577_Q_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class PortalTexturingState extends RenderState.TexturingState {
      private final int field_228596_Q_;

      public PortalTexturingState(int p_i225986_1_) {
         super("portal_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.scalef(0.5F, 0.5F, 1.0F);
            RenderSystem.translatef(17.0F / (float)p_i225986_1_, (2.0F + (float)p_i225986_1_ / 1.5F) * ((float)(Util.milliTime() % 800000L) / 800000.0F), 0.0F);
            RenderSystem.rotatef(((float)(p_i225986_1_ * p_i225986_1_) * 4321.0F + (float)p_i225986_1_ * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.scalef(4.5F - (float)p_i225986_1_ / 4.0F, 4.5F - (float)p_i225986_1_ / 4.0F, 1.0F);
            RenderSystem.mulTextureByProjModelView();
            RenderSystem.matrixMode(5888);
            RenderSystem.setupEndPortalTexGen();
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.clearTexGen();
         });
         this.field_228596_Q_ = p_i225986_1_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.PortalTexturingState lvt_2_1_ = (RenderState.PortalTexturingState)p_equals_1_;
            return this.field_228596_Q_ == lvt_2_1_.field_228596_Q_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.field_228596_Q_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class OffsetTexturingState extends RenderState.TexturingState {
      private final float field_228590_Q_;
      private final float field_228591_R_;

      public OffsetTexturingState(float p_i225983_1_, float p_i225983_2_) {
         super("offset_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(p_i225983_1_, p_i225983_2_, 0.0F);
            RenderSystem.matrixMode(5888);
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
         });
         this.field_228590_Q_ = p_i225983_1_;
         this.field_228591_R_ = p_i225983_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.OffsetTexturingState lvt_2_1_ = (RenderState.OffsetTexturingState)p_equals_1_;
            return Float.compare(lvt_2_1_.field_228590_Q_, this.field_228590_Q_) == 0 && Float.compare(lvt_2_1_.field_228591_R_, this.field_228591_R_) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_228590_Q_, this.field_228591_R_});
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TexturingState extends RenderState {
      public TexturingState(String p_i225989_1_, Runnable p_i225989_2_, Runnable p_i225989_3_) {
         super(p_i225989_1_, p_i225989_2_, p_i225989_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureState extends RenderState {
      private final Optional<ResourceLocation> field_228602_Q_;
      private final boolean field_228603_R_;
      private final boolean field_228604_S_;

      public TextureState(ResourceLocation p_i225988_1_, boolean p_i225988_2_, boolean p_i225988_3_) {
         super("texture", () -> {
            RenderSystem.enableTexture();
            TextureManager lvt_3_1_ = Minecraft.getInstance().getTextureManager();
            lvt_3_1_.bindTexture(p_i225988_1_);
            lvt_3_1_.func_229267_b_(p_i225988_1_).setBlurMipmapDirect(p_i225988_2_, p_i225988_3_);
         }, () -> {
         });
         this.field_228602_Q_ = Optional.of(p_i225988_1_);
         this.field_228603_R_ = p_i225988_2_;
         this.field_228604_S_ = p_i225988_3_;
      }

      public TextureState() {
         super("texture", () -> {
            RenderSystem.disableTexture();
         }, () -> {
            RenderSystem.enableTexture();
         });
         this.field_228602_Q_ = Optional.empty();
         this.field_228603_R_ = false;
         this.field_228604_S_ = false;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.TextureState lvt_2_1_ = (RenderState.TextureState)p_equals_1_;
            return this.field_228602_Q_.equals(lvt_2_1_.field_228602_Q_) && this.field_228603_R_ == lvt_2_1_.field_228603_R_ && this.field_228604_S_ == lvt_2_1_.field_228604_S_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.field_228602_Q_.hashCode();
      }

      protected Optional<ResourceLocation> func_228606_c_() {
         return this.field_228602_Q_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ShadeModelState extends RenderState {
      private final boolean field_228599_Q_;

      public ShadeModelState(boolean p_i225987_1_) {
         super("shade_model", () -> {
            RenderSystem.shadeModel(p_i225987_1_ ? 7425 : 7424);
         }, () -> {
            RenderSystem.shadeModel(7424);
         });
         this.field_228599_Q_ = p_i225987_1_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.ShadeModelState lvt_2_1_ = (RenderState.ShadeModelState)p_equals_1_;
            return this.field_228599_Q_ == lvt_2_1_.field_228599_Q_;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.field_228599_Q_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class AlphaState extends RenderState {
      private final float field_228574_Q_;

      public AlphaState(float p_i225974_1_) {
         super("alpha", () -> {
            if (p_i225974_1_ > 0.0F) {
               RenderSystem.enableAlphaTest();
               RenderSystem.alphaFunc(516, p_i225974_1_);
            } else {
               RenderSystem.disableAlphaTest();
            }

         }, () -> {
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultAlphaFunc();
         });
         this.field_228574_Q_ = p_i225974_1_;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            if (!super.equals(p_equals_1_)) {
               return false;
            } else {
               return this.field_228574_Q_ == ((RenderState.AlphaState)p_equals_1_).field_228574_Q_;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.field_228574_Q_});
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TransparencyState extends RenderState {
      public TransparencyState(String p_i225990_1_, Runnable p_i225990_2_, Runnable p_i225990_3_) {
         super(p_i225990_1_, p_i225990_2_, p_i225990_3_);
      }
   }
}
