package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener {
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   private float oldMouseX;
   private float oldMouseY;
   private final RecipeBookGui recipeBookGui = new RecipeBookGui();
   private boolean field_212353_B;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(PlayerEntity p_i1094_1_) {
      super(p_i1094_1_.container, p_i1094_1_.inventory, new TranslationTextComponent("container.crafting", new Object[0]));
      this.passEvents = true;
   }

   public void tick() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
      } else {
         this.recipeBookGui.tick();
      }
   }

   protected void init() {
      if (this.minecraft.playerController.isInCreativeMode()) {
         this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookGui.func_201520_a(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookContainer)this.container);
         this.field_212353_B = true;
         this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
         this.children.add(this.recipeBookGui);
         this.func_212928_a(this.recipeBookGui);
         this.addButton(new ImageButton(this.guiLeft + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (p_214086_1_) -> {
            this.recipeBookGui.func_201518_a(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton)p_214086_1_).setPosition(this.guiLeft + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
      }
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.font.drawString(this.title.getFormattedText(), 97.0F, 8.0F, 4210752);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.hasActivePotionEffects = !this.recipeBookGui.isVisible();
      if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
         this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
         this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
      } else {
         this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
         super.render(p_render_1_, p_render_2_, p_render_3_);
         this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, p_render_3_);
      }

      this.renderHoveredToolTip(p_render_1_, p_render_2_);
      this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
      this.oldMouseX = (float)p_render_1_;
      this.oldMouseY = (float)p_render_2_;
      this.func_212932_b(this.recipeBookGui);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      int lvt_4_1_ = this.guiLeft;
      int lvt_5_1_ = this.guiTop;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      func_228187_a_(lvt_4_1_ + 51, lvt_5_1_ + 75, 30, (float)(lvt_4_1_ + 51) - this.oldMouseX, (float)(lvt_5_1_ + 75 - 50) - this.oldMouseY, this.minecraft.player);
   }

   public static void func_228187_a_(int p_228187_0_, int p_228187_1_, int p_228187_2_, float p_228187_3_, float p_228187_4_, LivingEntity p_228187_5_) {
      float lvt_6_1_ = (float)Math.atan((double)(p_228187_3_ / 40.0F));
      float lvt_7_1_ = (float)Math.atan((double)(p_228187_4_ / 40.0F));
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)p_228187_0_, (float)p_228187_1_, 1050.0F);
      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
      MatrixStack lvt_8_1_ = new MatrixStack();
      lvt_8_1_.func_227861_a_(0.0D, 0.0D, 1000.0D);
      lvt_8_1_.func_227862_a_((float)p_228187_2_, (float)p_228187_2_, (float)p_228187_2_);
      Quaternion lvt_9_1_ = Vector3f.field_229183_f_.func_229187_a_(180.0F);
      Quaternion lvt_10_1_ = Vector3f.field_229179_b_.func_229187_a_(lvt_7_1_ * 20.0F);
      lvt_9_1_.multiply(lvt_10_1_);
      lvt_8_1_.func_227863_a_(lvt_9_1_);
      float lvt_11_1_ = p_228187_5_.renderYawOffset;
      float lvt_12_1_ = p_228187_5_.rotationYaw;
      float lvt_13_1_ = p_228187_5_.rotationPitch;
      float lvt_14_1_ = p_228187_5_.prevRotationYawHead;
      float lvt_15_1_ = p_228187_5_.rotationYawHead;
      p_228187_5_.renderYawOffset = 180.0F + lvt_6_1_ * 20.0F;
      p_228187_5_.rotationYaw = 180.0F + lvt_6_1_ * 40.0F;
      p_228187_5_.rotationPitch = -lvt_7_1_ * 20.0F;
      p_228187_5_.rotationYawHead = p_228187_5_.rotationYaw;
      p_228187_5_.prevRotationYawHead = p_228187_5_.rotationYaw;
      EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getRenderManager();
      lvt_10_1_.conjugate();
      lvt_16_1_.func_229089_a_(lvt_10_1_);
      lvt_16_1_.setRenderShadow(false);
      IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().func_228019_au_().func_228487_b_();
      lvt_16_1_.func_229084_a_(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, lvt_8_1_, lvt_17_1_, 15728880);
      lvt_17_1_.func_228461_a_();
      lvt_16_1_.setRenderShadow(true);
      p_228187_5_.renderYawOffset = lvt_11_1_;
      p_228187_5_.rotationYaw = lvt_12_1_;
      p_228187_5_.rotationPitch = lvt_13_1_;
      p_228187_5_.prevRotationYawHead = lvt_14_1_;
      p_228187_5_.rotationYawHead = lvt_15_1_;
      RenderSystem.popMatrix();
   }

   protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookGui.isVisible() ? false : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean lvt_8_1_ = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.recipeBookGui.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && lvt_8_1_;
   }

   protected void handleMouseClick(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.handleMouseClick(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.recipeBookGui.slotClicked(p_184098_1_);
   }

   public void recipesUpdated() {
      this.recipeBookGui.recipesUpdated();
   }

   public void removed() {
      if (this.field_212353_B) {
         this.recipeBookGui.removed();
      }

      super.removed();
   }

   public RecipeBookGui func_194310_f() {
      return this.recipeBookGui;
   }
}
