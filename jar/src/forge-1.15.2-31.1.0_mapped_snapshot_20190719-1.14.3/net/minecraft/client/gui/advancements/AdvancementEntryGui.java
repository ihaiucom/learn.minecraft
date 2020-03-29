package net.minecraft.client.gui.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementEntryGui extends AbstractGui {
   private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final Pattern PATTERN = Pattern.compile("(.+) \\S+");
   private final AdvancementTabGui guiAdvancementTab;
   private final Advancement advancement;
   private final DisplayInfo displayInfo;
   private final String title;
   private final int width;
   private final List<String> description;
   private final Minecraft minecraft;
   private AdvancementEntryGui parent;
   private final List<AdvancementEntryGui> children = Lists.newArrayList();
   private AdvancementProgress advancementProgress;
   private final int x;
   private final int y;

   public AdvancementEntryGui(AdvancementTabGui p_i47385_1_, Minecraft p_i47385_2_, Advancement p_i47385_3_, DisplayInfo p_i47385_4_) {
      this.guiAdvancementTab = p_i47385_1_;
      this.advancement = p_i47385_3_;
      this.displayInfo = p_i47385_4_;
      this.minecraft = p_i47385_2_;
      this.title = p_i47385_2_.fontRenderer.trimStringToWidth(p_i47385_4_.getTitle().getFormattedText(), 163);
      this.x = MathHelper.floor(p_i47385_4_.getX() * 28.0F);
      this.y = MathHelper.floor(p_i47385_4_.getY() * 27.0F);
      int lvt_5_1_ = p_i47385_3_.getRequirementCount();
      int lvt_6_1_ = String.valueOf(lvt_5_1_).length();
      int lvt_7_1_ = lvt_5_1_ > 1 ? p_i47385_2_.fontRenderer.getStringWidth("  ") + p_i47385_2_.fontRenderer.getStringWidth("0") * lvt_6_1_ * 2 + p_i47385_2_.fontRenderer.getStringWidth("/") : 0;
      int lvt_8_1_ = 29 + p_i47385_2_.fontRenderer.getStringWidth(this.title) + lvt_7_1_;
      String lvt_9_1_ = p_i47385_4_.getDescription().getFormattedText();
      this.description = this.findOptimalLines(lvt_9_1_, lvt_8_1_);

      String lvt_11_1_;
      for(Iterator var10 = this.description.iterator(); var10.hasNext(); lvt_8_1_ = Math.max(lvt_8_1_, p_i47385_2_.fontRenderer.getStringWidth(lvt_11_1_))) {
         lvt_11_1_ = (String)var10.next();
      }

      this.width = lvt_8_1_ + 3 + 5;
   }

   private List<String> findOptimalLines(String p_192995_1_, int p_192995_2_) {
      if (p_192995_1_.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> lvt_3_1_ = this.minecraft.fontRenderer.listFormattedStringToWidth(p_192995_1_, p_192995_2_);
         if (lvt_3_1_.size() < 2) {
            return lvt_3_1_;
         } else {
            String lvt_4_1_ = (String)lvt_3_1_.get(0);
            String lvt_5_1_ = (String)lvt_3_1_.get(1);
            int lvt_6_1_ = this.minecraft.fontRenderer.getStringWidth(lvt_4_1_ + ' ' + lvt_5_1_.split(" ")[0]);
            if (lvt_6_1_ - p_192995_2_ <= 10) {
               return this.minecraft.fontRenderer.listFormattedStringToWidth(p_192995_1_, lvt_6_1_);
            } else {
               Matcher lvt_7_1_ = PATTERN.matcher(lvt_4_1_);
               if (lvt_7_1_.matches()) {
                  int lvt_8_1_ = this.minecraft.fontRenderer.getStringWidth(lvt_7_1_.group(1));
                  if (p_192995_2_ - lvt_8_1_ <= 10) {
                     return this.minecraft.fontRenderer.listFormattedStringToWidth(p_192995_1_, lvt_8_1_);
                  }
               }

               return lvt_3_1_;
            }
         }
      }
   }

   @Nullable
   private AdvancementEntryGui getFirstVisibleParent(Advancement p_191818_1_) {
      do {
         p_191818_1_ = p_191818_1_.getParent();
      } while(p_191818_1_ != null && p_191818_1_.getDisplay() == null);

      if (p_191818_1_ != null && p_191818_1_.getDisplay() != null) {
         return this.guiAdvancementTab.getAdvancementGui(p_191818_1_);
      } else {
         return null;
      }
   }

   public void drawConnectivity(int p_191819_1_, int p_191819_2_, boolean p_191819_3_) {
      if (this.parent != null) {
         int lvt_4_1_ = p_191819_1_ + this.parent.x + 13;
         int lvt_5_1_ = p_191819_1_ + this.parent.x + 26 + 4;
         int lvt_6_1_ = p_191819_2_ + this.parent.y + 13;
         int lvt_7_1_ = p_191819_1_ + this.x + 13;
         int lvt_8_1_ = p_191819_2_ + this.y + 13;
         int lvt_9_1_ = p_191819_3_ ? -16777216 : -1;
         if (p_191819_3_) {
            this.hLine(lvt_5_1_, lvt_4_1_, lvt_6_1_ - 1, lvt_9_1_);
            this.hLine(lvt_5_1_ + 1, lvt_4_1_, lvt_6_1_, lvt_9_1_);
            this.hLine(lvt_5_1_, lvt_4_1_, lvt_6_1_ + 1, lvt_9_1_);
            this.hLine(lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_ - 1, lvt_9_1_);
            this.hLine(lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_, lvt_9_1_);
            this.hLine(lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_ + 1, lvt_9_1_);
            this.vLine(lvt_5_1_ - 1, lvt_8_1_, lvt_6_1_, lvt_9_1_);
            this.vLine(lvt_5_1_ + 1, lvt_8_1_, lvt_6_1_, lvt_9_1_);
         } else {
            this.hLine(lvt_5_1_, lvt_4_1_, lvt_6_1_, lvt_9_1_);
            this.hLine(lvt_7_1_, lvt_5_1_, lvt_8_1_, lvt_9_1_);
            this.vLine(lvt_5_1_, lvt_8_1_, lvt_6_1_, lvt_9_1_);
         }
      }

      Iterator var10 = this.children.iterator();

      while(var10.hasNext()) {
         AdvancementEntryGui lvt_5_2_ = (AdvancementEntryGui)var10.next();
         lvt_5_2_.drawConnectivity(p_191819_1_, p_191819_2_, p_191819_3_);
      }

   }

   public void draw(int p_191817_1_, int p_191817_2_) {
      if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
         float lvt_3_1_ = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
         AdvancementState lvt_4_2_;
         if (lvt_3_1_ >= 1.0F) {
            lvt_4_2_ = AdvancementState.OBTAINED;
         } else {
            lvt_4_2_ = AdvancementState.UNOBTAINED;
         }

         this.minecraft.getTextureManager().bindTexture(WIDGETS);
         this.blit(p_191817_1_ + this.x + 3, p_191817_2_ + this.y, this.displayInfo.getFrame().getIcon(), 128 + lvt_4_2_.getId() * 26, 26, 26);
         this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity)null, this.displayInfo.getIcon(), p_191817_1_ + this.x + 8, p_191817_2_ + this.y + 5);
      }

      Iterator var5 = this.children.iterator();

      while(var5.hasNext()) {
         AdvancementEntryGui lvt_4_3_ = (AdvancementEntryGui)var5.next();
         lvt_4_3_.draw(p_191817_1_, p_191817_2_);
      }

   }

   public void setAdvancementProgress(AdvancementProgress p_191824_1_) {
      this.advancementProgress = p_191824_1_;
   }

   public void addGuiAdvancement(AdvancementEntryGui p_191822_1_) {
      this.children.add(p_191822_1_);
   }

   public void drawHover(int p_191821_1_, int p_191821_2_, float p_191821_3_, int p_191821_4_, int p_191821_5_) {
      boolean lvt_6_1_ = p_191821_4_ + p_191821_1_ + this.x + this.width + 26 >= this.guiAdvancementTab.getScreen().width;
      String lvt_7_1_ = this.advancementProgress == null ? null : this.advancementProgress.getProgressText();
      int lvt_8_1_ = lvt_7_1_ == null ? 0 : this.minecraft.fontRenderer.getStringWidth(lvt_7_1_);
      int var10000 = 113 - p_191821_2_ - this.y - 26;
      int var10002 = this.description.size();
      this.minecraft.fontRenderer.getClass();
      boolean lvt_9_1_ = var10000 <= 6 + var10002 * 9;
      float lvt_10_1_ = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
      int lvt_14_1_ = MathHelper.floor(lvt_10_1_ * (float)this.width);
      AdvancementState lvt_11_4_;
      AdvancementState lvt_12_4_;
      AdvancementState lvt_13_4_;
      if (lvt_10_1_ >= 1.0F) {
         lvt_14_1_ = this.width / 2;
         lvt_11_4_ = AdvancementState.OBTAINED;
         lvt_12_4_ = AdvancementState.OBTAINED;
         lvt_13_4_ = AdvancementState.OBTAINED;
      } else if (lvt_14_1_ < 2) {
         lvt_14_1_ = this.width / 2;
         lvt_11_4_ = AdvancementState.UNOBTAINED;
         lvt_12_4_ = AdvancementState.UNOBTAINED;
         lvt_13_4_ = AdvancementState.UNOBTAINED;
      } else if (lvt_14_1_ > this.width - 2) {
         lvt_14_1_ = this.width / 2;
         lvt_11_4_ = AdvancementState.OBTAINED;
         lvt_12_4_ = AdvancementState.OBTAINED;
         lvt_13_4_ = AdvancementState.UNOBTAINED;
      } else {
         lvt_11_4_ = AdvancementState.OBTAINED;
         lvt_12_4_ = AdvancementState.UNOBTAINED;
         lvt_13_4_ = AdvancementState.UNOBTAINED;
      }

      int lvt_15_1_ = this.width - lvt_14_1_;
      this.minecraft.getTextureManager().bindTexture(WIDGETS);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      int lvt_16_1_ = p_191821_2_ + this.y;
      int lvt_17_2_;
      if (lvt_6_1_) {
         lvt_17_2_ = p_191821_1_ + this.x - this.width + 26 + 6;
      } else {
         lvt_17_2_ = p_191821_1_ + this.x;
      }

      int var10001 = this.description.size();
      this.minecraft.fontRenderer.getClass();
      int lvt_18_1_ = 32 + var10001 * 9;
      if (!this.description.isEmpty()) {
         if (lvt_9_1_) {
            this.render9Sprite(lvt_17_2_, lvt_16_1_ + 26 - lvt_18_1_, this.width, lvt_18_1_, 10, 200, 26, 0, 52);
         } else {
            this.render9Sprite(lvt_17_2_, lvt_16_1_, this.width, lvt_18_1_, 10, 200, 26, 0, 52);
         }
      }

      this.blit(lvt_17_2_, lvt_16_1_, 0, lvt_11_4_.getId() * 26, lvt_14_1_, 26);
      this.blit(lvt_17_2_ + lvt_14_1_, lvt_16_1_, 200 - lvt_15_1_, lvt_12_4_.getId() * 26, lvt_15_1_, 26);
      this.blit(p_191821_1_ + this.x + 3, p_191821_2_ + this.y, this.displayInfo.getFrame().getIcon(), 128 + lvt_13_4_.getId() * 26, 26, 26);
      if (lvt_6_1_) {
         this.minecraft.fontRenderer.drawStringWithShadow(this.title, (float)(lvt_17_2_ + 5), (float)(p_191821_2_ + this.y + 9), -1);
         if (lvt_7_1_ != null) {
            this.minecraft.fontRenderer.drawStringWithShadow(lvt_7_1_, (float)(p_191821_1_ + this.x - lvt_8_1_), (float)(p_191821_2_ + this.y + 9), -1);
         }
      } else {
         this.minecraft.fontRenderer.drawStringWithShadow(this.title, (float)(p_191821_1_ + this.x + 32), (float)(p_191821_2_ + this.y + 9), -1);
         if (lvt_7_1_ != null) {
            this.minecraft.fontRenderer.drawStringWithShadow(lvt_7_1_, (float)(p_191821_1_ + this.x + this.width - lvt_8_1_ - 5), (float)(p_191821_2_ + this.y + 9), -1);
         }
      }

      int lvt_19_1_;
      int var10003;
      FontRenderer var20;
      String var21;
      float var22;
      if (lvt_9_1_) {
         for(lvt_19_1_ = 0; lvt_19_1_ < this.description.size(); ++lvt_19_1_) {
            var20 = this.minecraft.fontRenderer;
            var21 = (String)this.description.get(lvt_19_1_);
            var22 = (float)(lvt_17_2_ + 5);
            var10003 = lvt_16_1_ + 26 - lvt_18_1_ + 7;
            this.minecraft.fontRenderer.getClass();
            var20.drawString(var21, var22, (float)(var10003 + lvt_19_1_ * 9), -5592406);
         }
      } else {
         for(lvt_19_1_ = 0; lvt_19_1_ < this.description.size(); ++lvt_19_1_) {
            var20 = this.minecraft.fontRenderer;
            var21 = (String)this.description.get(lvt_19_1_);
            var22 = (float)(lvt_17_2_ + 5);
            var10003 = p_191821_2_ + this.y + 9 + 17;
            this.minecraft.fontRenderer.getClass();
            var20.drawString(var21, var22, (float)(var10003 + lvt_19_1_ * 9), -5592406);
         }
      }

      this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity)null, this.displayInfo.getIcon(), p_191821_1_ + this.x + 8, p_191821_2_ + this.y + 5);
   }

   protected void render9Sprite(int p_192994_1_, int p_192994_2_, int p_192994_3_, int p_192994_4_, int p_192994_5_, int p_192994_6_, int p_192994_7_, int p_192994_8_, int p_192994_9_) {
      this.blit(p_192994_1_, p_192994_2_, p_192994_8_, p_192994_9_, p_192994_5_, p_192994_5_);
      this.renderRepeating(p_192994_1_ + p_192994_5_, p_192994_2_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
      this.blit(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_, p_192994_5_, p_192994_5_);
      this.blit(p_192994_1_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
      this.renderRepeating(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
      this.blit(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
      this.renderRepeating(p_192994_1_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
      this.renderRepeating(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_ - p_192994_5_ - p_192994_5_);
      this.renderRepeating(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
   }

   protected void renderRepeating(int p_192993_1_, int p_192993_2_, int p_192993_3_, int p_192993_4_, int p_192993_5_, int p_192993_6_, int p_192993_7_, int p_192993_8_) {
      for(int lvt_9_1_ = 0; lvt_9_1_ < p_192993_3_; lvt_9_1_ += p_192993_7_) {
         int lvt_10_1_ = p_192993_1_ + lvt_9_1_;
         int lvt_11_1_ = Math.min(p_192993_7_, p_192993_3_ - lvt_9_1_);

         for(int lvt_12_1_ = 0; lvt_12_1_ < p_192993_4_; lvt_12_1_ += p_192993_8_) {
            int lvt_13_1_ = p_192993_2_ + lvt_12_1_;
            int lvt_14_1_ = Math.min(p_192993_8_, p_192993_4_ - lvt_12_1_);
            this.blit(lvt_10_1_, lvt_13_1_, p_192993_5_, p_192993_6_, lvt_11_1_, lvt_14_1_);
         }
      }

   }

   public boolean isMouseOver(int p_191816_1_, int p_191816_2_, int p_191816_3_, int p_191816_4_) {
      if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
         int lvt_5_1_ = p_191816_1_ + this.x;
         int lvt_6_1_ = lvt_5_1_ + 26;
         int lvt_7_1_ = p_191816_2_ + this.y;
         int lvt_8_1_ = lvt_7_1_ + 26;
         return p_191816_3_ >= lvt_5_1_ && p_191816_3_ <= lvt_6_1_ && p_191816_4_ >= lvt_7_1_ && p_191816_4_ <= lvt_8_1_;
      } else {
         return false;
      }
   }

   public void attachToParent() {
      if (this.parent == null && this.advancement.getParent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancement);
         if (this.parent != null) {
            this.parent.addGuiAdvancement(this);
         }
      }

   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }
}
