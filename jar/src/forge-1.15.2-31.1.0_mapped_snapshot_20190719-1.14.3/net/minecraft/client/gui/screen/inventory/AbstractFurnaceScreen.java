package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends ContainerScreen<T> implements IRecipeShownListener {
   private static final ResourceLocation field_214089_l = new ResourceLocation("textures/gui/recipe_button.png");
   public final AbstractRecipeBookGui field_214088_k;
   private boolean field_214090_m;
   private final ResourceLocation field_214091_n;

   public AbstractFurnaceScreen(T p_i51104_1_, AbstractRecipeBookGui p_i51104_2_, PlayerInventory p_i51104_3_, ITextComponent p_i51104_4_, ResourceLocation p_i51104_5_) {
      super(p_i51104_1_, p_i51104_3_, p_i51104_4_);
      this.field_214088_k = p_i51104_2_;
      this.field_214091_n = p_i51104_5_;
   }

   public void init() {
      super.init();
      this.field_214090_m = this.width < 379;
      this.field_214088_k.func_201520_a(this.width, this.height, this.minecraft, this.field_214090_m, (RecipeBookContainer)this.container);
      this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
      this.addButton(new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l, (p_214087_1_) -> {
         this.field_214088_k.func_201518_a(this.field_214090_m);
         this.field_214088_k.toggleVisibility();
         this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
         ((ImageButton)p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
      }));
   }

   public void tick() {
      super.tick();
      this.field_214088_k.tick();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (this.field_214088_k.isVisible() && this.field_214090_m) {
         this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
         this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
      } else {
         this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
         super.render(p_render_1_, p_render_2_, p_render_3_);
         this.field_214088_k.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_render_3_);
      }

      this.renderHoveredToolTip(p_render_1_, p_render_2_);
      this.field_214088_k.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      String lvt_3_1_ = this.title.getFormattedText();
      this.font.drawString(lvt_3_1_, (float)(this.xSize / 2 - this.font.getStringWidth(lvt_3_1_) / 2), 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(this.field_214091_n);
      int lvt_4_1_ = this.guiLeft;
      int lvt_5_1_ = this.guiTop;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      int lvt_6_2_;
      if (((AbstractFurnaceContainer)this.container).func_217061_l()) {
         lvt_6_2_ = ((AbstractFurnaceContainer)this.container).getBurnLeftScaled();
         this.blit(lvt_4_1_ + 56, lvt_5_1_ + 36 + 12 - lvt_6_2_, 176, 12 - lvt_6_2_, 14, lvt_6_2_ + 1);
      }

      lvt_6_2_ = ((AbstractFurnaceContainer)this.container).getCookProgressionScaled();
      this.blit(lvt_4_1_ + 79, lvt_5_1_ + 34, 176, 14, lvt_6_2_ + 1, 16);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.field_214088_k.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.field_214090_m && this.field_214088_k.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void handleMouseClick(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.handleMouseClick(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.field_214088_k.slotClicked(p_184098_1_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.field_214088_k.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean lvt_8_1_ = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.field_214088_k.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && lvt_8_1_;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.field_214088_k.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void recipesUpdated() {
      this.field_214088_k.recipesUpdated();
   }

   public RecipeBookGui func_194310_f() {
      return this.field_214088_k;
   }

   public void removed() {
      this.field_214088_k.removed();
      super.removed();
   }
}
