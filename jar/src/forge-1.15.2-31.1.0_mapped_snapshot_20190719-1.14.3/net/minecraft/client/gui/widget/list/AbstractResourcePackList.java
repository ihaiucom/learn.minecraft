package net.minecraft.client.gui.widget.list;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractResourcePackList extends ExtendedList<AbstractResourcePackList.ResourcePackEntry> {
   private static final ResourceLocation field_214367_b = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent field_214368_c = new TranslationTextComponent("resourcePack.incompatible", new Object[0]);
   private static final ITextComponent field_214369_d = new TranslationTextComponent("resourcePack.incompatible.confirm.title", new Object[0]);
   protected final Minecraft mc;
   private final ITextComponent field_214370_e;

   public AbstractResourcePackList(Minecraft p_i51074_1_, int p_i51074_2_, int p_i51074_3_, ITextComponent p_i51074_4_) {
      super(p_i51074_1_, p_i51074_2_, p_i51074_3_, 32, p_i51074_3_ - 55 + 4, 36);
      this.mc = p_i51074_1_;
      this.centerListVertically = false;
      p_i51074_1_.fontRenderer.getClass();
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
      this.field_214370_e = p_i51074_4_;
   }

   protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
      ITextComponent lvt_4_1_ = (new StringTextComponent("")).appendSibling(this.field_214370_e).applyTextStyles(TextFormatting.UNDERLINE, TextFormatting.BOLD);
      this.mc.fontRenderer.drawString(lvt_4_1_.getFormattedText(), (float)(p_renderHeader_1_ + this.width / 2 - this.mc.fontRenderer.getStringWidth(lvt_4_1_.getFormattedText()) / 2), (float)Math.min(this.y0 + 3, p_renderHeader_2_), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   public void func_214365_a(AbstractResourcePackList.ResourcePackEntry p_214365_1_) {
      this.addEntry(p_214365_1_);
      p_214365_1_.field_214430_c = this;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResourcePackEntry extends ExtendedList.AbstractListEntry<AbstractResourcePackList.ResourcePackEntry> {
      private AbstractResourcePackList field_214430_c;
      protected final Minecraft field_214428_a;
      protected final ResourcePacksScreen field_214429_b;
      private final ClientResourcePackInfo field_214431_d;

      public ResourcePackEntry(AbstractResourcePackList p_i50749_1_, ResourcePacksScreen p_i50749_2_, ClientResourcePackInfo p_i50749_3_) {
         this.field_214429_b = p_i50749_2_;
         this.field_214428_a = Minecraft.getInstance();
         this.field_214431_d = p_i50749_3_;
         this.field_214430_c = p_i50749_1_;
      }

      public void func_214422_a(SelectedResourcePackList p_214422_1_) {
         this.func_214418_e().getPriority().func_198993_a(p_214422_1_.children(), this, AbstractResourcePackList.ResourcePackEntry::func_214418_e, true);
         this.func_230009_b_(p_214422_1_);
      }

      public void func_230009_b_(SelectedResourcePackList p_230009_1_) {
         this.field_214430_c = p_230009_1_;
      }

      protected void func_214419_a() {
         this.field_214431_d.func_195808_a(this.field_214428_a.getTextureManager());
      }

      protected PackCompatibility func_214423_b() {
         return this.field_214431_d.getCompatibility();
      }

      protected String func_214420_c() {
         return this.field_214431_d.getDescription().getFormattedText();
      }

      protected String func_214416_d() {
         return this.field_214431_d.func_195789_b().getFormattedText();
      }

      public ClientResourcePackInfo func_214418_e() {
         return this.field_214431_d;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         PackCompatibility lvt_10_1_ = this.func_214423_b();
         if (!lvt_10_1_.func_198968_a()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.fill(p_render_3_ - 1, p_render_2_ - 1, p_render_3_ + p_render_4_ - 9, p_render_2_ + p_render_5_ + 1, -8978432);
         }

         this.func_214419_a();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         String lvt_11_1_ = this.func_214416_d();
         String lvt_12_1_ = this.func_214420_c();
         int lvt_13_1_;
         if (this.func_214424_f() && (this.field_214428_a.gameSettings.touchscreen || p_render_8_)) {
            this.field_214428_a.getTextureManager().bindTexture(AbstractResourcePackList.field_214367_b);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            lvt_13_1_ = p_render_6_ - p_render_3_;
            int lvt_14_1_ = p_render_7_ - p_render_2_;
            if (!lvt_10_1_.func_198968_a()) {
               lvt_11_1_ = AbstractResourcePackList.field_214368_c.getFormattedText();
               lvt_12_1_ = lvt_10_1_.func_198967_b().getFormattedText();
            }

            if (this.func_214425_g()) {
               if (lvt_13_1_ < 32) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.func_214426_h()) {
                  if (lvt_13_1_ < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214414_i()) {
                  if (lvt_13_1_ < 32 && lvt_13_1_ > 16 && lvt_14_1_ < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214427_j()) {
                  if (lvt_13_1_ < 32 && lvt_13_1_ > 16 && lvt_14_1_ > 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         lvt_13_1_ = this.field_214428_a.fontRenderer.getStringWidth(lvt_11_1_);
         if (lvt_13_1_ > 157) {
            lvt_11_1_ = this.field_214428_a.fontRenderer.trimStringToWidth(lvt_11_1_, 157 - this.field_214428_a.fontRenderer.getStringWidth("...")) + "...";
         }

         this.field_214428_a.fontRenderer.drawStringWithShadow(lvt_11_1_, (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 1), 16777215);
         List<String> lvt_14_2_ = this.field_214428_a.fontRenderer.listFormattedStringToWidth(lvt_12_1_, 157);

         for(int lvt_15_1_ = 0; lvt_15_1_ < 2 && lvt_15_1_ < lvt_14_2_.size(); ++lvt_15_1_) {
            this.field_214428_a.fontRenderer.drawStringWithShadow((String)lvt_14_2_.get(lvt_15_1_), (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 12 + 10 * lvt_15_1_), 8421504);
         }

      }

      protected boolean func_214424_f() {
         return !this.field_214431_d.isOrderLocked() || !this.field_214431_d.isAlwaysEnabled();
      }

      protected boolean func_214425_g() {
         return !this.field_214429_b.func_214299_c(this);
      }

      protected boolean func_214426_h() {
         return this.field_214429_b.func_214299_c(this) && !this.field_214431_d.isAlwaysEnabled();
      }

      protected boolean func_214414_i() {
         List<AbstractResourcePackList.ResourcePackEntry> lvt_1_1_ = this.field_214430_c.children();
         int lvt_2_1_ = lvt_1_1_.indexOf(this);
         return lvt_2_1_ > 0 && !((AbstractResourcePackList.ResourcePackEntry)lvt_1_1_.get(lvt_2_1_ - 1)).field_214431_d.isOrderLocked();
      }

      protected boolean func_214427_j() {
         List<AbstractResourcePackList.ResourcePackEntry> lvt_1_1_ = this.field_214430_c.children();
         int lvt_2_1_ = lvt_1_1_.indexOf(this);
         return lvt_2_1_ >= 0 && lvt_2_1_ < lvt_1_1_.size() - 1 && !((AbstractResourcePackList.ResourcePackEntry)lvt_1_1_.get(lvt_2_1_ + 1)).field_214431_d.isOrderLocked();
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         double lvt_6_1_ = p_mouseClicked_1_ - (double)this.field_214430_c.getRowLeft();
         double lvt_8_1_ = p_mouseClicked_3_ - (double)this.field_214430_c.getRowTop(this.field_214430_c.children().indexOf(this));
         if (this.func_214424_f() && lvt_6_1_ <= 32.0D) {
            if (this.func_214425_g()) {
               this.func_214415_k().markChanged();
               PackCompatibility lvt_10_1_ = this.func_214423_b();
               if (lvt_10_1_.func_198968_a()) {
                  this.func_214415_k().func_214300_a(this);
               } else {
                  ITextComponent lvt_11_1_ = lvt_10_1_.func_198971_c();
                  this.field_214428_a.displayGuiScreen(new ConfirmScreen((p_214417_1_) -> {
                     this.field_214428_a.displayGuiScreen(this.func_214415_k());
                     if (p_214417_1_) {
                        this.func_214415_k().func_214300_a(this);
                     }

                  }, AbstractResourcePackList.field_214369_d, lvt_11_1_));
               }

               return true;
            }

            if (lvt_6_1_ < 16.0D && this.func_214426_h()) {
               this.func_214415_k().func_214297_b(this);
               return true;
            }

            List lvt_10_3_;
            int lvt_11_3_;
            if (lvt_6_1_ > 16.0D && lvt_8_1_ < 16.0D && this.func_214414_i()) {
               lvt_10_3_ = this.field_214430_c.children();
               lvt_11_3_ = lvt_10_3_.indexOf(this);
               lvt_10_3_.remove(lvt_11_3_);
               lvt_10_3_.add(lvt_11_3_ - 1, this);
               this.func_214415_k().markChanged();
               return true;
            }

            if (lvt_6_1_ > 16.0D && lvt_8_1_ > 16.0D && this.func_214427_j()) {
               lvt_10_3_ = this.field_214430_c.children();
               lvt_11_3_ = lvt_10_3_.indexOf(this);
               lvt_10_3_.remove(lvt_11_3_);
               lvt_10_3_.add(lvt_11_3_ + 1, this);
               this.func_214415_k().markChanged();
               return true;
            }
         }

         return false;
      }

      public ResourcePacksScreen func_214415_k() {
         return this.field_214429_b;
      }
   }
}
