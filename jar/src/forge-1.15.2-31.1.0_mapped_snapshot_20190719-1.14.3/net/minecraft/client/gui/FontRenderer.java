package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
   public final int FONT_HEIGHT = 9;
   public final Random random = new Random();
   private final TextureManager textureManager;
   private final Font font;
   private boolean bidiFlag;

   public FontRenderer(TextureManager p_i49744_1_, Font p_i49744_2_) {
      this.textureManager = p_i49744_1_;
      this.font = p_i49744_2_;
   }

   public void setGlyphProviders(List<IGlyphProvider> p_211568_1_) {
      this.font.setGlyphProviders(p_211568_1_);
   }

   public void close() {
      this.font.close();
   }

   public int drawStringWithShadow(String p_175063_1_, float p_175063_2_, float p_175063_3_, int p_175063_4_) {
      RenderSystem.enableAlphaTest();
      return this.func_228078_a_(p_175063_1_, p_175063_2_, p_175063_3_, p_175063_4_, TransformationMatrix.func_227983_a_().func_227988_c_(), true);
   }

   public int drawString(String p_211126_1_, float p_211126_2_, float p_211126_3_, int p_211126_4_) {
      RenderSystem.enableAlphaTest();
      return this.func_228078_a_(p_211126_1_, p_211126_2_, p_211126_3_, p_211126_4_, TransformationMatrix.func_227983_a_().func_227988_c_(), false);
   }

   public String bidiReorder(String p_147647_1_) {
      try {
         Bidi lvt_2_1_ = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         lvt_2_1_.setReorderingMode(0);
         return lvt_2_1_.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private int func_228078_a_(String p_228078_1_, float p_228078_2_, float p_228078_3_, int p_228078_4_, Matrix4f p_228078_5_, boolean p_228078_6_) {
      if (p_228078_1_ == null) {
         return 0;
      } else {
         IRenderTypeBuffer.Impl lvt_7_1_ = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());
         int lvt_8_1_ = this.func_228079_a_(p_228078_1_, p_228078_2_, p_228078_3_, p_228078_4_, p_228078_6_, p_228078_5_, lvt_7_1_, false, 0, 15728880);
         lvt_7_1_.func_228461_a_();
         return lvt_8_1_;
      }
   }

   public int func_228079_a_(String p_228079_1_, float p_228079_2_, float p_228079_3_, int p_228079_4_, boolean p_228079_5_, Matrix4f p_228079_6_, IRenderTypeBuffer p_228079_7_, boolean p_228079_8_, int p_228079_9_, int p_228079_10_) {
      return this.func_228080_b_(p_228079_1_, p_228079_2_, p_228079_3_, p_228079_4_, p_228079_5_, p_228079_6_, p_228079_7_, p_228079_8_, p_228079_9_, p_228079_10_);
   }

   private int func_228080_b_(String p_228080_1_, float p_228080_2_, float p_228080_3_, int p_228080_4_, boolean p_228080_5_, Matrix4f p_228080_6_, IRenderTypeBuffer p_228080_7_, boolean p_228080_8_, int p_228080_9_, int p_228080_10_) {
      if (this.bidiFlag) {
         p_228080_1_ = this.bidiReorder(p_228080_1_);
      }

      if ((p_228080_4_ & -67108864) == 0) {
         p_228080_4_ |= -16777216;
      }

      if (p_228080_5_) {
         this.func_228081_c_(p_228080_1_, p_228080_2_, p_228080_3_, p_228080_4_, true, p_228080_6_, p_228080_7_, p_228080_8_, p_228080_9_, p_228080_10_);
      }

      Matrix4f lvt_11_1_ = p_228080_6_.func_226601_d_();
      lvt_11_1_.func_226597_a_(new Vector3f(0.0F, 0.0F, 0.001F));
      p_228080_2_ = this.func_228081_c_(p_228080_1_, p_228080_2_, p_228080_3_, p_228080_4_, false, lvt_11_1_, p_228080_7_, p_228080_8_, p_228080_9_, p_228080_10_);
      return (int)p_228080_2_ + (p_228080_5_ ? 1 : 0);
   }

   private float func_228081_c_(String p_228081_1_, float p_228081_2_, float p_228081_3_, int p_228081_4_, boolean p_228081_5_, Matrix4f p_228081_6_, IRenderTypeBuffer p_228081_7_, boolean p_228081_8_, int p_228081_9_, int p_228081_10_) {
      float lvt_11_1_ = p_228081_5_ ? 0.25F : 1.0F;
      float lvt_12_1_ = (float)(p_228081_4_ >> 16 & 255) / 255.0F * lvt_11_1_;
      float lvt_13_1_ = (float)(p_228081_4_ >> 8 & 255) / 255.0F * lvt_11_1_;
      float lvt_14_1_ = (float)(p_228081_4_ & 255) / 255.0F * lvt_11_1_;
      float lvt_15_1_ = p_228081_2_;
      float lvt_16_1_ = lvt_12_1_;
      float lvt_17_1_ = lvt_13_1_;
      float lvt_18_1_ = lvt_14_1_;
      float lvt_19_1_ = (float)(p_228081_4_ >> 24 & 255) / 255.0F;
      boolean lvt_20_1_ = false;
      boolean lvt_21_1_ = false;
      boolean lvt_22_1_ = false;
      boolean lvt_23_1_ = false;
      boolean lvt_24_1_ = false;
      List<TexturedGlyph.Effect> lvt_25_1_ = Lists.newArrayList();

      for(int lvt_26_1_ = 0; lvt_26_1_ < p_228081_1_.length(); ++lvt_26_1_) {
         char lvt_27_1_ = p_228081_1_.charAt(lvt_26_1_);
         if (lvt_27_1_ == 167 && lvt_26_1_ + 1 < p_228081_1_.length()) {
            TextFormatting lvt_28_1_ = TextFormatting.fromFormattingCode(p_228081_1_.charAt(lvt_26_1_ + 1));
            if (lvt_28_1_ != null) {
               if (lvt_28_1_.isNormalStyle()) {
                  lvt_20_1_ = false;
                  lvt_21_1_ = false;
                  lvt_24_1_ = false;
                  lvt_23_1_ = false;
                  lvt_22_1_ = false;
                  lvt_16_1_ = lvt_12_1_;
                  lvt_17_1_ = lvt_13_1_;
                  lvt_18_1_ = lvt_14_1_;
               }

               if (lvt_28_1_.getColor() != null) {
                  int lvt_29_1_ = lvt_28_1_.getColor();
                  lvt_16_1_ = (float)(lvt_29_1_ >> 16 & 255) / 255.0F * lvt_11_1_;
                  lvt_17_1_ = (float)(lvt_29_1_ >> 8 & 255) / 255.0F * lvt_11_1_;
                  lvt_18_1_ = (float)(lvt_29_1_ & 255) / 255.0F * lvt_11_1_;
               } else if (lvt_28_1_ == TextFormatting.OBFUSCATED) {
                  lvt_20_1_ = true;
               } else if (lvt_28_1_ == TextFormatting.BOLD) {
                  lvt_21_1_ = true;
               } else if (lvt_28_1_ == TextFormatting.STRIKETHROUGH) {
                  lvt_24_1_ = true;
               } else if (lvt_28_1_ == TextFormatting.UNDERLINE) {
                  lvt_23_1_ = true;
               } else if (lvt_28_1_ == TextFormatting.ITALIC) {
                  lvt_22_1_ = true;
               }
            }

            ++lvt_26_1_;
         } else {
            IGlyph lvt_28_2_ = this.font.findGlyph(lvt_27_1_);
            TexturedGlyph lvt_29_2_ = lvt_20_1_ && lvt_27_1_ != ' ' ? this.font.obfuscate(lvt_28_2_) : this.font.getGlyph(lvt_27_1_);
            float lvt_30_2_;
            float lvt_31_2_;
            if (!(lvt_29_2_ instanceof EmptyGlyph)) {
               lvt_30_2_ = lvt_21_1_ ? lvt_28_2_.getBoldOffset() : 0.0F;
               lvt_31_2_ = p_228081_5_ ? lvt_28_2_.getShadowOffset() : 0.0F;
               IVertexBuilder lvt_32_1_ = p_228081_7_.getBuffer(lvt_29_2_.func_228163_a_(p_228081_8_));
               this.func_228077_a_(lvt_29_2_, lvt_21_1_, lvt_22_1_, lvt_30_2_, lvt_15_1_ + lvt_31_2_, p_228081_3_ + lvt_31_2_, p_228081_6_, lvt_32_1_, lvt_16_1_, lvt_17_1_, lvt_18_1_, lvt_19_1_, p_228081_10_);
            }

            lvt_30_2_ = lvt_28_2_.getAdvance(lvt_21_1_);
            lvt_31_2_ = p_228081_5_ ? 1.0F : 0.0F;
            if (lvt_24_1_) {
               lvt_25_1_.add(new TexturedGlyph.Effect(lvt_15_1_ + lvt_31_2_ - 1.0F, p_228081_3_ + lvt_31_2_ + 4.5F, lvt_15_1_ + lvt_31_2_ + lvt_30_2_, p_228081_3_ + lvt_31_2_ + 4.5F - 1.0F, -0.01F, lvt_16_1_, lvt_17_1_, lvt_18_1_, lvt_19_1_));
            }

            if (lvt_23_1_) {
               lvt_25_1_.add(new TexturedGlyph.Effect(lvt_15_1_ + lvt_31_2_ - 1.0F, p_228081_3_ + lvt_31_2_ + 9.0F, lvt_15_1_ + lvt_31_2_ + lvt_30_2_, p_228081_3_ + lvt_31_2_ + 9.0F - 1.0F, -0.01F, lvt_16_1_, lvt_17_1_, lvt_18_1_, lvt_19_1_));
            }

            lvt_15_1_ += lvt_30_2_;
         }
      }

      if (p_228081_9_ != 0) {
         float lvt_26_2_ = (float)(p_228081_9_ >> 24 & 255) / 255.0F;
         float lvt_27_2_ = (float)(p_228081_9_ >> 16 & 255) / 255.0F;
         float lvt_28_3_ = (float)(p_228081_9_ >> 8 & 255) / 255.0F;
         float lvt_29_3_ = (float)(p_228081_9_ & 255) / 255.0F;
         lvt_25_1_.add(new TexturedGlyph.Effect(p_228081_2_ - 1.0F, p_228081_3_ + 9.0F, lvt_15_1_ + 1.0F, p_228081_3_ - 1.0F, 0.01F, lvt_27_2_, lvt_28_3_, lvt_29_3_, lvt_26_2_));
      }

      if (!lvt_25_1_.isEmpty()) {
         TexturedGlyph lvt_26_3_ = this.font.func_228157_b_();
         IVertexBuilder lvt_27_3_ = p_228081_7_.getBuffer(lvt_26_3_.func_228163_a_(p_228081_8_));
         Iterator var39 = lvt_25_1_.iterator();

         while(var39.hasNext()) {
            TexturedGlyph.Effect lvt_29_4_ = (TexturedGlyph.Effect)var39.next();
            lvt_26_3_.func_228162_a_(lvt_29_4_, p_228081_6_, lvt_27_3_, p_228081_10_);
         }
      }

      return lvt_15_1_;
   }

   private void func_228077_a_(TexturedGlyph p_228077_1_, boolean p_228077_2_, boolean p_228077_3_, float p_228077_4_, float p_228077_5_, float p_228077_6_, Matrix4f p_228077_7_, IVertexBuilder p_228077_8_, float p_228077_9_, float p_228077_10_, float p_228077_11_, float p_228077_12_, int p_228077_13_) {
      p_228077_1_.func_225595_a_(p_228077_3_, p_228077_5_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      if (p_228077_2_) {
         p_228077_1_.func_225595_a_(p_228077_3_, p_228077_5_ + p_228077_4_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      }

   }

   public int getStringWidth(String p_78256_1_) {
      if (p_78256_1_ == null) {
         return 0;
      } else {
         float lvt_2_1_ = 0.0F;
         boolean lvt_3_1_ = false;

         for(int lvt_4_1_ = 0; lvt_4_1_ < p_78256_1_.length(); ++lvt_4_1_) {
            char lvt_5_1_ = p_78256_1_.charAt(lvt_4_1_);
            if (lvt_5_1_ == 167 && lvt_4_1_ < p_78256_1_.length() - 1) {
               ++lvt_4_1_;
               TextFormatting lvt_6_1_ = TextFormatting.fromFormattingCode(p_78256_1_.charAt(lvt_4_1_));
               if (lvt_6_1_ == TextFormatting.BOLD) {
                  lvt_3_1_ = true;
               } else if (lvt_6_1_ != null && lvt_6_1_.isNormalStyle()) {
                  lvt_3_1_ = false;
               }
            } else {
               lvt_2_1_ += this.font.findGlyph(lvt_5_1_).getAdvance(lvt_3_1_);
            }
         }

         return MathHelper.ceil(lvt_2_1_);
      }
   }

   public float getCharWidth(char p_211125_1_) {
      return p_211125_1_ == 167 ? 0.0F : this.font.findGlyph(p_211125_1_).getAdvance(false);
   }

   public String trimStringToWidth(String p_78269_1_, int p_78269_2_) {
      return this.trimStringToWidth(p_78269_1_, p_78269_2_, false);
   }

   public String trimStringToWidth(String p_78262_1_, int p_78262_2_, boolean p_78262_3_) {
      StringBuilder lvt_4_1_ = new StringBuilder();
      float lvt_5_1_ = 0.0F;
      int lvt_6_1_ = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
      int lvt_7_1_ = p_78262_3_ ? -1 : 1;
      boolean lvt_8_1_ = false;
      boolean lvt_9_1_ = false;

      for(int lvt_10_1_ = lvt_6_1_; lvt_10_1_ >= 0 && lvt_10_1_ < p_78262_1_.length() && lvt_5_1_ < (float)p_78262_2_; lvt_10_1_ += lvt_7_1_) {
         char lvt_11_1_ = p_78262_1_.charAt(lvt_10_1_);
         if (lvt_8_1_) {
            lvt_8_1_ = false;
            TextFormatting lvt_12_1_ = TextFormatting.fromFormattingCode(lvt_11_1_);
            if (lvt_12_1_ == TextFormatting.BOLD) {
               lvt_9_1_ = true;
            } else if (lvt_12_1_ != null && lvt_12_1_.isNormalStyle()) {
               lvt_9_1_ = false;
            }
         } else if (lvt_11_1_ == 167) {
            lvt_8_1_ = true;
         } else {
            lvt_5_1_ += this.getCharWidth(lvt_11_1_);
            if (lvt_9_1_) {
               ++lvt_5_1_;
            }
         }

         if (lvt_5_1_ > (float)p_78262_2_) {
            break;
         }

         if (p_78262_3_) {
            lvt_4_1_.insert(0, lvt_11_1_);
         } else {
            lvt_4_1_.append(lvt_11_1_);
         }
      }

      return lvt_4_1_.toString();
   }

   private String trimStringNewline(String p_78273_1_) {
      while(p_78273_1_ != null && p_78273_1_.endsWith("\n")) {
         p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
      }

      return p_78273_1_;
   }

   public void drawSplitString(String p_78279_1_, int p_78279_2_, int p_78279_3_, int p_78279_4_, int p_78279_5_) {
      p_78279_1_ = this.trimStringNewline(p_78279_1_);
      this.renderSplitString(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, p_78279_5_);
   }

   private void renderSplitString(String p_211124_1_, int p_211124_2_, int p_211124_3_, int p_211124_4_, int p_211124_5_) {
      List<String> lvt_6_1_ = this.listFormattedStringToWidth(p_211124_1_, p_211124_4_);
      Matrix4f lvt_7_1_ = TransformationMatrix.func_227983_a_().func_227988_c_();

      for(Iterator var8 = lvt_6_1_.iterator(); var8.hasNext(); p_211124_3_ += 9) {
         String lvt_9_1_ = (String)var8.next();
         float lvt_10_1_ = (float)p_211124_2_;
         if (this.bidiFlag) {
            int lvt_11_1_ = this.getStringWidth(this.bidiReorder(lvt_9_1_));
            lvt_10_1_ += (float)(p_211124_4_ - lvt_11_1_);
         }

         this.func_228078_a_(lvt_9_1_, lvt_10_1_, (float)p_211124_3_, p_211124_5_, lvt_7_1_, false);
      }

   }

   public int getWordWrappedHeight(String p_78267_1_, int p_78267_2_) {
      return 9 * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
   }

   public void setBidiFlag(boolean p_78275_1_) {
      this.bidiFlag = p_78275_1_;
   }

   public List<String> listFormattedStringToWidth(String p_78271_1_, int p_78271_2_) {
      return Arrays.asList(this.wrapFormattedStringToWidth(p_78271_1_, p_78271_2_).split("\n"));
   }

   public String wrapFormattedStringToWidth(String p_78280_1_, int p_78280_2_) {
      String lvt_3_1_;
      String lvt_5_1_;
      for(lvt_3_1_ = ""; !p_78280_1_.isEmpty(); lvt_3_1_ = lvt_3_1_ + lvt_5_1_ + "\n") {
         int lvt_4_1_ = this.sizeStringToWidth(p_78280_1_, p_78280_2_);
         if (p_78280_1_.length() <= lvt_4_1_) {
            return lvt_3_1_ + p_78280_1_;
         }

         lvt_5_1_ = p_78280_1_.substring(0, lvt_4_1_);
         char lvt_6_1_ = p_78280_1_.charAt(lvt_4_1_);
         boolean lvt_7_1_ = lvt_6_1_ == ' ' || lvt_6_1_ == '\n';
         p_78280_1_ = TextFormatting.getFormatString(lvt_5_1_) + p_78280_1_.substring(lvt_4_1_ + (lvt_7_1_ ? 1 : 0));
      }

      return lvt_3_1_;
   }

   public int sizeStringToWidth(String p_78259_1_, int p_78259_2_) {
      int lvt_3_1_ = Math.max(1, p_78259_2_);
      int lvt_4_1_ = p_78259_1_.length();
      float lvt_5_1_ = 0.0F;
      int lvt_6_1_ = 0;
      int lvt_7_1_ = -1;
      boolean lvt_8_1_ = false;

      for(boolean lvt_9_1_ = true; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_) {
         char lvt_10_1_ = p_78259_1_.charAt(lvt_6_1_);
         switch(lvt_10_1_) {
         case '\n':
            --lvt_6_1_;
            break;
         case ' ':
            lvt_7_1_ = lvt_6_1_;
         default:
            if (lvt_5_1_ != 0.0F) {
               lvt_9_1_ = false;
            }

            lvt_5_1_ += this.getCharWidth(lvt_10_1_);
            if (lvt_8_1_) {
               ++lvt_5_1_;
            }
            break;
         case '§':
            if (lvt_6_1_ < lvt_4_1_ - 1) {
               ++lvt_6_1_;
               TextFormatting lvt_11_1_ = TextFormatting.fromFormattingCode(p_78259_1_.charAt(lvt_6_1_));
               if (lvt_11_1_ == TextFormatting.BOLD) {
                  lvt_8_1_ = true;
               } else if (lvt_11_1_ != null && lvt_11_1_.isNormalStyle()) {
                  lvt_8_1_ = false;
               }
            }
         }

         if (lvt_10_1_ == '\n') {
            ++lvt_6_1_;
            lvt_7_1_ = lvt_6_1_;
            break;
         }

         if (lvt_5_1_ > (float)lvt_3_1_) {
            if (lvt_9_1_) {
               ++lvt_6_1_;
            }
            break;
         }
      }

      return lvt_6_1_ != lvt_4_1_ && lvt_7_1_ != -1 && lvt_7_1_ < lvt_6_1_ ? lvt_7_1_ : lvt_6_1_;
   }

   public int func_216863_a(String p_216863_1_, int p_216863_2_, int p_216863_3_, boolean p_216863_4_) {
      int lvt_5_1_ = p_216863_3_;
      boolean lvt_6_1_ = p_216863_2_ < 0;
      int lvt_7_1_ = Math.abs(p_216863_2_);

      for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_; ++lvt_8_1_) {
         if (lvt_6_1_) {
            while(p_216863_4_ && lvt_5_1_ > 0 && (p_216863_1_.charAt(lvt_5_1_ - 1) == ' ' || p_216863_1_.charAt(lvt_5_1_ - 1) == '\n')) {
               --lvt_5_1_;
            }

            while(lvt_5_1_ > 0 && p_216863_1_.charAt(lvt_5_1_ - 1) != ' ' && p_216863_1_.charAt(lvt_5_1_ - 1) != '\n') {
               --lvt_5_1_;
            }
         } else {
            int lvt_9_1_ = p_216863_1_.length();
            int lvt_10_1_ = p_216863_1_.indexOf(32, lvt_5_1_);
            int lvt_11_1_ = p_216863_1_.indexOf(10, lvt_5_1_);
            if (lvt_10_1_ == -1 && lvt_11_1_ == -1) {
               lvt_5_1_ = -1;
            } else if (lvt_10_1_ != -1 && lvt_11_1_ != -1) {
               lvt_5_1_ = Math.min(lvt_10_1_, lvt_11_1_);
            } else if (lvt_10_1_ != -1) {
               lvt_5_1_ = lvt_10_1_;
            } else {
               lvt_5_1_ = lvt_11_1_;
            }

            if (lvt_5_1_ == -1) {
               lvt_5_1_ = lvt_9_1_;
            } else {
               while(p_216863_4_ && lvt_5_1_ < lvt_9_1_ && (p_216863_1_.charAt(lvt_5_1_) == ' ' || p_216863_1_.charAt(lvt_5_1_) == '\n')) {
                  ++lvt_5_1_;
               }
            }
         }
      }

      return lvt_5_1_;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }
}
