package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrindstoneScreen extends ContainerScreen<GrindstoneContainer> {
   private static final ResourceLocation field_214110_k = new ResourceLocation("textures/gui/container/grindstone.png");

   public GrindstoneScreen(GrindstoneContainer p_i51086_1_, PlayerInventory p_i51086_2_, ITextComponent p_i51086_3_) {
      super(p_i51086_1_, p_i51086_2_, p_i51086_3_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(field_214110_k);
      int lvt_4_1_ = (this.width - this.xSize) / 2;
      int lvt_5_1_ = (this.height - this.ySize) / 2;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      if ((((GrindstoneContainer)this.container).getSlot(0).getHasStack() || ((GrindstoneContainer)this.container).getSlot(1).getHasStack()) && !((GrindstoneContainer)this.container).getSlot(2).getHasStack()) {
         this.blit(lvt_4_1_ + 92, lvt_5_1_ + 31, this.xSize, 0, 28, 21);
      }

   }
}
