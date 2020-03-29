package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MerchantScreen extends ContainerScreen<MerchantContainer> {
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");
   private int selectedMerchantRecipe;
   private final MerchantScreen.TradeButton[] field_214138_m = new MerchantScreen.TradeButton[7];
   private int field_214139_n;
   private boolean field_214140_o;

   public MerchantScreen(MerchantContainer p_i51080_1_, PlayerInventory p_i51080_2_, ITextComponent p_i51080_3_) {
      super(p_i51080_1_, p_i51080_2_, p_i51080_3_);
      this.xSize = 276;
   }

   private void func_195391_j() {
      ((MerchantContainer)this.container).setCurrentRecipeIndex(this.selectedMerchantRecipe);
      ((MerchantContainer)this.container).func_217046_g(this.selectedMerchantRecipe);
      this.minecraft.getConnection().sendPacket(new CSelectTradePacket(this.selectedMerchantRecipe));
   }

   protected void init() {
      super.init();
      int lvt_1_1_ = (this.width - this.xSize) / 2;
      int lvt_2_1_ = (this.height - this.ySize) / 2;
      int lvt_3_1_ = lvt_2_1_ + 16 + 2;

      for(int lvt_4_1_ = 0; lvt_4_1_ < 7; ++lvt_4_1_) {
         this.field_214138_m[lvt_4_1_] = (MerchantScreen.TradeButton)this.addButton(new MerchantScreen.TradeButton(lvt_1_1_ + 5, lvt_3_1_, lvt_4_1_, (p_214132_1_) -> {
            if (p_214132_1_ instanceof MerchantScreen.TradeButton) {
               this.selectedMerchantRecipe = ((MerchantScreen.TradeButton)p_214132_1_).func_212937_a() + this.field_214139_n;
               this.func_195391_j();
            }

         }));
         lvt_3_1_ += 20;
      }

   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      int lvt_3_1_ = ((MerchantContainer)this.container).func_217049_g();
      int lvt_4_1_ = this.ySize - 94;
      String lvt_5_3_;
      if (lvt_3_1_ > 0 && lvt_3_1_ <= 5 && ((MerchantContainer)this.container).func_217042_i()) {
         lvt_5_3_ = this.title.getFormattedText();
         String lvt_6_1_ = "- " + I18n.format("merchant.level." + lvt_3_1_);
         int lvt_7_1_ = this.font.getStringWidth(lvt_5_3_);
         int lvt_8_1_ = this.font.getStringWidth(lvt_6_1_);
         int lvt_9_1_ = lvt_7_1_ + lvt_8_1_ + 3;
         int lvt_10_1_ = 49 + this.xSize / 2 - lvt_9_1_ / 2;
         this.font.drawString(lvt_5_3_, (float)lvt_10_1_, 6.0F, 4210752);
         this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)lvt_4_1_, 4210752);
         this.font.drawString(lvt_6_1_, (float)(lvt_10_1_ + lvt_7_1_ + 3), 6.0F, 4210752);
      } else {
         lvt_5_3_ = this.title.getFormattedText();
         this.font.drawString(lvt_5_3_, (float)(49 + this.xSize / 2 - this.font.getStringWidth(lvt_5_3_) / 2), 6.0F, 4210752);
         this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 107.0F, (float)lvt_4_1_, 4210752);
      }

      lvt_5_3_ = I18n.format("merchant.trades");
      int lvt_6_2_ = this.font.getStringWidth(lvt_5_3_);
      this.font.drawString(lvt_5_3_, (float)(5 - lvt_6_2_ / 2 + 48), 6.0F, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int lvt_4_1_ = (this.width - this.xSize) / 2;
      int lvt_5_1_ = (this.height - this.ySize) / 2;
      blit(lvt_4_1_, lvt_5_1_, this.getBlitOffset(), 0.0F, 0.0F, this.xSize, this.ySize, 256, 512);
      MerchantOffers lvt_6_1_ = ((MerchantContainer)this.container).func_217051_h();
      if (!lvt_6_1_.isEmpty()) {
         int lvt_7_1_ = this.selectedMerchantRecipe;
         if (lvt_7_1_ < 0 || lvt_7_1_ >= lvt_6_1_.size()) {
            return;
         }

         MerchantOffer lvt_8_1_ = (MerchantOffer)lvt_6_1_.get(lvt_7_1_);
         if (lvt_8_1_.func_222217_o()) {
            this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(this.guiLeft + 83 + 99, this.guiTop + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
         }
      }

   }

   private void func_214130_a(int p_214130_1_, int p_214130_2_, MerchantOffer p_214130_3_) {
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int lvt_4_1_ = ((MerchantContainer)this.container).func_217049_g();
      int lvt_5_1_ = ((MerchantContainer)this.container).func_217048_e();
      if (lvt_4_1_ < 5) {
         blit(p_214130_1_ + 136, p_214130_2_ + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
         int lvt_6_1_ = VillagerData.func_221133_b(lvt_4_1_);
         if (lvt_5_1_ >= lvt_6_1_ && VillagerData.func_221128_d(lvt_4_1_)) {
            int lvt_7_1_ = true;
            float lvt_8_1_ = (float)(100 / (VillagerData.func_221127_c(lvt_4_1_) - lvt_6_1_));
            int lvt_9_1_ = Math.min(MathHelper.floor(lvt_8_1_ * (float)(lvt_5_1_ - lvt_6_1_)), 100);
            blit(p_214130_1_ + 136, p_214130_2_ + 16, this.getBlitOffset(), 0.0F, 191.0F, lvt_9_1_ + 1, 5, 256, 512);
            int lvt_10_1_ = ((MerchantContainer)this.container).func_217047_f();
            if (lvt_10_1_ > 0) {
               int lvt_11_1_ = Math.min(MathHelper.floor((float)lvt_10_1_ * lvt_8_1_), 100 - lvt_9_1_);
               blit(p_214130_1_ + 136 + lvt_9_1_ + 1, p_214130_2_ + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, lvt_11_1_, 3, 256, 512);
            }

         }
      }
   }

   private void func_214129_a(int p_214129_1_, int p_214129_2_, MerchantOffers p_214129_3_) {
      int lvt_4_1_ = p_214129_3_.size() + 1 - 7;
      if (lvt_4_1_ > 1) {
         int lvt_5_1_ = 139 - (27 + (lvt_4_1_ - 1) * 139 / lvt_4_1_);
         int lvt_6_1_ = 1 + lvt_5_1_ / lvt_4_1_ + 139 / lvt_4_1_;
         int lvt_7_1_ = true;
         int lvt_8_1_ = Math.min(113, this.field_214139_n * lvt_6_1_);
         if (this.field_214139_n == lvt_4_1_ - 1) {
            lvt_8_1_ = 113;
         }

         blit(p_214129_1_ + 94, p_214129_2_ + 18 + lvt_8_1_, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
      } else {
         blit(p_214129_1_ + 94, p_214129_2_ + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      MerchantOffers lvt_4_1_ = ((MerchantContainer)this.container).func_217051_h();
      if (!lvt_4_1_.isEmpty()) {
         int lvt_5_1_ = (this.width - this.xSize) / 2;
         int lvt_6_1_ = (this.height - this.ySize) / 2;
         int lvt_7_1_ = lvt_6_1_ + 16 + 1;
         int lvt_8_1_ = lvt_5_1_ + 5 + 5;
         RenderSystem.pushMatrix();
         RenderSystem.enableRescaleNormal();
         this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
         this.func_214129_a(lvt_5_1_, lvt_6_1_, lvt_4_1_);
         int lvt_9_1_ = 0;
         Iterator var10 = lvt_4_1_.iterator();

         while(true) {
            MerchantOffer lvt_11_1_;
            while(var10.hasNext()) {
               lvt_11_1_ = (MerchantOffer)var10.next();
               if (this.func_214135_a(lvt_4_1_.size()) && (lvt_9_1_ < this.field_214139_n || lvt_9_1_ >= 7 + this.field_214139_n)) {
                  ++lvt_9_1_;
               } else {
                  ItemStack lvt_12_1_ = lvt_11_1_.func_222218_a();
                  ItemStack lvt_13_1_ = lvt_11_1_.func_222205_b();
                  ItemStack lvt_14_1_ = lvt_11_1_.func_222202_c();
                  ItemStack lvt_15_1_ = lvt_11_1_.func_222200_d();
                  this.itemRenderer.zLevel = 100.0F;
                  int lvt_16_1_ = lvt_7_1_ + 2;
                  this.func_214137_a(lvt_13_1_, lvt_12_1_, lvt_8_1_, lvt_16_1_);
                  if (!lvt_14_1_.isEmpty()) {
                     this.itemRenderer.renderItemAndEffectIntoGUI(lvt_14_1_, lvt_5_1_ + 5 + 35, lvt_16_1_);
                     this.itemRenderer.renderItemOverlays(this.font, lvt_14_1_, lvt_5_1_ + 5 + 35, lvt_16_1_);
                  }

                  this.func_214134_a(lvt_11_1_, lvt_5_1_, lvt_16_1_);
                  this.itemRenderer.renderItemAndEffectIntoGUI(lvt_15_1_, lvt_5_1_ + 5 + 68, lvt_16_1_);
                  this.itemRenderer.renderItemOverlays(this.font, lvt_15_1_, lvt_5_1_ + 5 + 68, lvt_16_1_);
                  this.itemRenderer.zLevel = 0.0F;
                  lvt_7_1_ += 20;
                  ++lvt_9_1_;
               }
            }

            int lvt_10_1_ = this.selectedMerchantRecipe;
            lvt_11_1_ = (MerchantOffer)lvt_4_1_.get(lvt_10_1_);
            if (((MerchantContainer)this.container).func_217042_i()) {
               this.func_214130_a(lvt_5_1_, lvt_6_1_, lvt_11_1_);
            }

            if (lvt_11_1_.func_222217_o() && this.isPointInRegion(186, 35, 22, 21, (double)p_render_1_, (double)p_render_2_) && ((MerchantContainer)this.container).func_223432_h()) {
               this.renderTooltip(I18n.format("merchant.deprecated"), p_render_1_, p_render_2_);
            }

            MerchantScreen.TradeButton[] var18 = this.field_214138_m;
            int var19 = var18.length;

            for(int var20 = 0; var20 < var19; ++var20) {
               MerchantScreen.TradeButton lvt_15_2_ = var18[var20];
               if (lvt_15_2_.isHovered()) {
                  lvt_15_2_.renderToolTip(p_render_1_, p_render_2_);
               }

               lvt_15_2_.visible = lvt_15_2_.field_212938_a < ((MerchantContainer)this.container).func_217051_h().size();
            }

            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
            break;
         }
      }

      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   private void func_214134_a(MerchantOffer p_214134_1_, int p_214134_2_, int p_214134_3_) {
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      if (p_214134_1_.func_222217_o()) {
         blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
      } else {
         blit(p_214134_2_ + 5 + 35 + 20, p_214134_3_ + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
      }

   }

   private void func_214137_a(ItemStack p_214137_1_, ItemStack p_214137_2_, int p_214137_3_, int p_214137_4_) {
      this.itemRenderer.renderItemAndEffectIntoGUI(p_214137_1_, p_214137_3_, p_214137_4_);
      if (p_214137_2_.getCount() == p_214137_1_.getCount()) {
         this.itemRenderer.renderItemOverlays(this.font, p_214137_1_, p_214137_3_, p_214137_4_);
      } else {
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_2_, p_214137_3_, p_214137_4_, p_214137_2_.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_214137_1_, p_214137_3_ + 14, p_214137_4_, p_214137_1_.getCount() == 1 ? "1" : null);
         this.minecraft.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(p_214137_3_ + 7, p_214137_4_ + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }

   }

   private boolean func_214135_a(int p_214135_1_) {
      return p_214135_1_ > 7;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      int lvt_7_1_ = ((MerchantContainer)this.container).func_217051_h().size();
      if (this.func_214135_a(lvt_7_1_)) {
         int lvt_8_1_ = lvt_7_1_ - 7;
         this.field_214139_n = (int)((double)this.field_214139_n - p_mouseScrolled_5_);
         this.field_214139_n = MathHelper.clamp(this.field_214139_n, 0, lvt_8_1_);
      }

      return true;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      int lvt_10_1_ = ((MerchantContainer)this.container).func_217051_h().size();
      if (this.field_214140_o) {
         int lvt_11_1_ = this.guiTop + 18;
         int lvt_12_1_ = lvt_11_1_ + 139;
         int lvt_13_1_ = lvt_10_1_ - 7;
         float lvt_14_1_ = ((float)p_mouseDragged_3_ - (float)lvt_11_1_ - 13.5F) / ((float)(lvt_12_1_ - lvt_11_1_) - 27.0F);
         lvt_14_1_ = lvt_14_1_ * (float)lvt_13_1_ + 0.5F;
         this.field_214139_n = MathHelper.clamp((int)lvt_14_1_, 0, lvt_13_1_);
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.field_214140_o = false;
      int lvt_6_1_ = (this.width - this.xSize) / 2;
      int lvt_7_1_ = (this.height - this.ySize) / 2;
      if (this.func_214135_a(((MerchantContainer)this.container).func_217051_h().size()) && p_mouseClicked_1_ > (double)(lvt_6_1_ + 94) && p_mouseClicked_1_ < (double)(lvt_6_1_ + 94 + 6) && p_mouseClicked_3_ > (double)(lvt_7_1_ + 18) && p_mouseClicked_3_ <= (double)(lvt_7_1_ + 18 + 139 + 1)) {
         this.field_214140_o = true;
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   @OnlyIn(Dist.CLIENT)
   class TradeButton extends Button {
      final int field_212938_a;

      public TradeButton(int p_i50601_2_, int p_i50601_3_, int p_i50601_4_, Button.IPressable p_i50601_5_) {
         super(p_i50601_2_, p_i50601_3_, 89, 20, "", p_i50601_5_);
         this.field_212938_a = p_i50601_4_;
         this.visible = false;
      }

      public int func_212937_a() {
         return this.field_212938_a;
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         if (this.isHovered && ((MerchantContainer)MerchantScreen.this.container).func_217051_h().size() > this.field_212938_a + MerchantScreen.this.field_214139_n) {
            ItemStack lvt_3_3_;
            if (p_renderToolTip_1_ < this.x + 20) {
               lvt_3_3_ = ((MerchantOffer)((MerchantContainer)MerchantScreen.this.container).func_217051_h().get(this.field_212938_a + MerchantScreen.this.field_214139_n)).func_222205_b();
               MerchantScreen.this.renderTooltip(lvt_3_3_, p_renderToolTip_1_, p_renderToolTip_2_);
            } else if (p_renderToolTip_1_ < this.x + 50 && p_renderToolTip_1_ > this.x + 30) {
               lvt_3_3_ = ((MerchantOffer)((MerchantContainer)MerchantScreen.this.container).func_217051_h().get(this.field_212938_a + MerchantScreen.this.field_214139_n)).func_222202_c();
               if (!lvt_3_3_.isEmpty()) {
                  MerchantScreen.this.renderTooltip(lvt_3_3_, p_renderToolTip_1_, p_renderToolTip_2_);
               }
            } else if (p_renderToolTip_1_ > this.x + 65) {
               lvt_3_3_ = ((MerchantOffer)((MerchantContainer)MerchantScreen.this.container).func_217051_h().get(this.field_212938_a + MerchantScreen.this.field_214139_n)).func_222200_d();
               MerchantScreen.this.renderTooltip(lvt_3_3_, p_renderToolTip_1_, p_renderToolTip_2_);
            }
         }

      }
   }
}
