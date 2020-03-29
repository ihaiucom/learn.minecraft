package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WinGameScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation field_194401_g = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private final boolean poem;
   private final Runnable onFinished;
   private float time;
   private List<String> lines;
   private int totalScrollLength;
   private float scrollSpeed = 0.5F;

   public WinGameScreen(boolean p_i47590_1_, Runnable p_i47590_2_) {
      super(NarratorChatListener.field_216868_a);
      this.poem = p_i47590_1_;
      this.onFinished = p_i47590_2_;
      if (!p_i47590_1_) {
         this.scrollSpeed = 0.75F;
      }

   }

   public void tick() {
      this.minecraft.getMusicTicker().tick();
      this.minecraft.getSoundHandler().tick(false);
      float lvt_1_1_ = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      if (this.time > lvt_1_1_) {
         this.sendRespawnPacket();
      }

   }

   public void onClose() {
      this.sendRespawnPacket();
   }

   private void sendRespawnPacket() {
      this.onFinished.run();
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected void init() {
      if (this.lines == null) {
         this.lines = Lists.newArrayList();
         IResource lvt_1_1_ = null;

         try {
            String lvt_2_1_ = "" + TextFormatting.WHITE + TextFormatting.OBFUSCATED + TextFormatting.GREEN + TextFormatting.AQUA;
            int lvt_3_1_ = true;
            InputStream lvt_4_1_;
            BufferedReader lvt_5_1_;
            if (this.poem) {
               lvt_1_1_ = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
               lvt_4_1_ = lvt_1_1_.getInputStream();
               lvt_5_1_ = new BufferedReader(new InputStreamReader(lvt_4_1_, StandardCharsets.UTF_8));
               Random lvt_6_1_ = new Random(8124371L);

               label113:
               while(true) {
                  String lvt_7_1_;
                  int lvt_8_1_;
                  if ((lvt_7_1_ = lvt_5_1_.readLine()) == null) {
                     lvt_4_1_.close();
                     lvt_8_1_ = 0;

                     while(true) {
                        if (lvt_8_1_ >= 8) {
                           break label113;
                        }

                        this.lines.add("");
                        ++lvt_8_1_;
                     }
                  }

                  String lvt_9_1_;
                  String lvt_10_1_;
                  for(lvt_7_1_ = lvt_7_1_.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername()); lvt_7_1_.contains(lvt_2_1_); lvt_7_1_ = lvt_9_1_ + TextFormatting.WHITE + TextFormatting.OBFUSCATED + "XXXXXXXX".substring(0, lvt_6_1_.nextInt(4) + 3) + lvt_10_1_) {
                     lvt_8_1_ = lvt_7_1_.indexOf(lvt_2_1_);
                     lvt_9_1_ = lvt_7_1_.substring(0, lvt_8_1_);
                     lvt_10_1_ = lvt_7_1_.substring(lvt_8_1_ + lvt_2_1_.length());
                  }

                  this.lines.addAll(this.minecraft.fontRenderer.listFormattedStringToWidth(lvt_7_1_, 274));
                  this.lines.add("");
               }
            }

            lvt_4_1_ = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            lvt_5_1_ = new BufferedReader(new InputStreamReader(lvt_4_1_, StandardCharsets.UTF_8));

            String lvt_6_2_;
            while((lvt_6_2_ = lvt_5_1_.readLine()) != null) {
               lvt_6_2_ = lvt_6_2_.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
               lvt_6_2_ = lvt_6_2_.replaceAll("\t", "    ");
               this.lines.addAll(this.minecraft.fontRenderer.listFormattedStringToWidth(lvt_6_2_, 274));
               this.lines.add("");
            }

            lvt_4_1_.close();
            this.totalScrollLength = this.lines.size() * 12;
         } catch (Exception var14) {
            LOGGER.error("Couldn't load credits", var14);
         } finally {
            IOUtils.closeQuietly(lvt_1_1_);
         }

      }
   }

   private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_) {
      this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
      int lvt_4_1_ = this.width;
      float lvt_5_1_ = -this.time * 0.5F * this.scrollSpeed;
      float lvt_6_1_ = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float lvt_7_1_ = 0.015625F;
      float lvt_8_1_ = this.time * 0.02F;
      float lvt_9_1_ = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float lvt_10_1_ = (lvt_9_1_ - 20.0F - this.time) * 0.005F;
      if (lvt_10_1_ < lvt_8_1_) {
         lvt_8_1_ = lvt_10_1_;
      }

      if (lvt_8_1_ > 1.0F) {
         lvt_8_1_ = 1.0F;
      }

      lvt_8_1_ *= lvt_8_1_;
      lvt_8_1_ = lvt_8_1_ * 96.0F / 255.0F;
      Tessellator lvt_11_1_ = Tessellator.getInstance();
      BufferBuilder lvt_12_1_ = lvt_11_1_.getBuffer();
      lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      lvt_12_1_.func_225582_a_(0.0D, (double)this.height, (double)this.getBlitOffset()).func_225583_a_(0.0F, lvt_5_1_ * 0.015625F).func_227885_a_(lvt_8_1_, lvt_8_1_, lvt_8_1_, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_((double)lvt_4_1_, (double)this.height, (double)this.getBlitOffset()).func_225583_a_((float)lvt_4_1_ * 0.015625F, lvt_5_1_ * 0.015625F).func_227885_a_(lvt_8_1_, lvt_8_1_, lvt_8_1_, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_((double)lvt_4_1_, 0.0D, (double)this.getBlitOffset()).func_225583_a_((float)lvt_4_1_ * 0.015625F, lvt_6_1_ * 0.015625F).func_227885_a_(lvt_8_1_, lvt_8_1_, lvt_8_1_, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_(0.0D, 0.0D, (double)this.getBlitOffset()).func_225583_a_(0.0F, lvt_6_1_ * 0.015625F).func_227885_a_(lvt_8_1_, lvt_8_1_, lvt_8_1_, 1.0F).endVertex();
      lvt_11_1_.draw();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.drawWinGameScreen(p_render_1_, p_render_2_, p_render_3_);
      int lvt_4_1_ = true;
      int lvt_5_1_ = this.width / 2 - 137;
      int lvt_6_1_ = this.height + 50;
      this.time += p_render_3_;
      float lvt_7_1_ = -this.time * this.scrollSpeed;
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, lvt_7_1_, 0.0F);
      this.minecraft.getTextureManager().bindTexture(MINECRAFT_LOGO);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableAlphaTest();
      this.blit(lvt_5_1_, lvt_6_1_, 0, 0, 155, 44);
      this.blit(lvt_5_1_ + 155, lvt_6_1_, 0, 45, 155, 44);
      this.minecraft.getTextureManager().bindTexture(field_194401_g);
      blit(lvt_5_1_ + 88, lvt_6_1_ + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      RenderSystem.disableAlphaTest();
      int lvt_8_1_ = lvt_6_1_ + 100;

      int lvt_9_1_;
      for(lvt_9_1_ = 0; lvt_9_1_ < this.lines.size(); ++lvt_9_1_) {
         if (lvt_9_1_ == this.lines.size() - 1) {
            float lvt_10_1_ = (float)lvt_8_1_ + lvt_7_1_ - (float)(this.height / 2 - 6);
            if (lvt_10_1_ < 0.0F) {
               RenderSystem.translatef(0.0F, -lvt_10_1_, 0.0F);
            }
         }

         if ((float)lvt_8_1_ + lvt_7_1_ + 12.0F + 8.0F > 0.0F && (float)lvt_8_1_ + lvt_7_1_ < (float)this.height) {
            String lvt_10_2_ = (String)this.lines.get(lvt_9_1_);
            if (lvt_10_2_.startsWith("[C]")) {
               this.font.drawStringWithShadow(lvt_10_2_.substring(3), (float)(lvt_5_1_ + (274 - this.font.getStringWidth(lvt_10_2_.substring(3))) / 2), (float)lvt_8_1_, 16777215);
            } else {
               this.font.random.setSeed((long)((float)((long)lvt_9_1_ * 4238972211L) + this.time / 4.0F));
               this.font.drawStringWithShadow(lvt_10_2_, (float)lvt_5_1_, (float)lvt_8_1_, 16777215);
            }
         }

         lvt_8_1_ += 12;
      }

      RenderSystem.popMatrix();
      this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      lvt_9_1_ = this.width;
      int lvt_10_3_ = this.height;
      Tessellator lvt_11_1_ = Tessellator.getInstance();
      BufferBuilder lvt_12_1_ = lvt_11_1_.getBuffer();
      lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      lvt_12_1_.func_225582_a_(0.0D, (double)lvt_10_3_, (double)this.getBlitOffset()).func_225583_a_(0.0F, 1.0F).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_((double)lvt_9_1_, (double)lvt_10_3_, (double)this.getBlitOffset()).func_225583_a_(1.0F, 1.0F).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_((double)lvt_9_1_, 0.0D, (double)this.getBlitOffset()).func_225583_a_(1.0F, 0.0F).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      lvt_12_1_.func_225582_a_(0.0D, 0.0D, (double)this.getBlitOffset()).func_225583_a_(0.0F, 0.0F).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      lvt_11_1_.draw();
      RenderSystem.disableBlend();
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
