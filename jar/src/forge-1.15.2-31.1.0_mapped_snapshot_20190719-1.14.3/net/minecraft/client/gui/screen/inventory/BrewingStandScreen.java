package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrewingStandScreen extends ContainerScreen<BrewingStandContainer> {
   private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

   public BrewingStandScreen(BrewingStandContainer p_i51097_1_, PlayerInventory p_i51097_2_, ITextComponent p_i51097_3_) {
      super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.font.drawString(this.title.getFormattedText(), (float)(this.xSize / 2 - this.font.getStringWidth(this.title.getFormattedText()) / 2), 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
      int lvt_4_1_ = (this.width - this.xSize) / 2;
      int lvt_5_1_ = (this.height - this.ySize) / 2;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      int lvt_6_1_ = ((BrewingStandContainer)this.container).func_216982_e();
      int lvt_7_1_ = MathHelper.clamp((18 * lvt_6_1_ + 20 - 1) / 20, 0, 18);
      if (lvt_7_1_ > 0) {
         this.blit(lvt_4_1_ + 60, lvt_5_1_ + 44, 176, 29, lvt_7_1_, 4);
      }

      int lvt_8_1_ = ((BrewingStandContainer)this.container).func_216981_f();
      if (lvt_8_1_ > 0) {
         int lvt_9_1_ = (int)(28.0F * (1.0F - (float)lvt_8_1_ / 400.0F));
         if (lvt_9_1_ > 0) {
            this.blit(lvt_4_1_ + 97, lvt_5_1_ + 16, 176, 0, 9, lvt_9_1_);
         }

         lvt_9_1_ = BUBBLELENGTHS[lvt_8_1_ / 2 % 7];
         if (lvt_9_1_ > 0) {
            this.blit(lvt_4_1_ + 63, lvt_5_1_ + 14 + 29 - lvt_9_1_, 185, 29 - lvt_9_1_, 12, lvt_9_1_);
         }
      }

   }
}
