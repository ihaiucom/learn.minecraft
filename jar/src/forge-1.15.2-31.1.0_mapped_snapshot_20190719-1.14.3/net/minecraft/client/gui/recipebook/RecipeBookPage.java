package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookPage {
   private final List<RecipeWidget> buttons = Lists.newArrayListWithCapacity(20);
   private RecipeWidget hoveredButton;
   private final RecipeOverlayGui overlay = new RecipeOverlayGui();
   private Minecraft minecraft;
   private final List<IRecipeUpdateListener> listeners = Lists.newArrayList();
   private List<RecipeList> recipeLists;
   private ToggleWidget forwardButton;
   private ToggleWidget backButton;
   private int totalPages;
   private int currentPage;
   private RecipeBook recipeBook;
   private IRecipe<?> lastClickedRecipe;
   private RecipeList lastClickedRecipeList;

   public RecipeBookPage() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 20; ++lvt_1_1_) {
         this.buttons.add(new RecipeWidget());
      }

   }

   public void init(Minecraft p_194194_1_, int p_194194_2_, int p_194194_3_) {
      this.minecraft = p_194194_1_;
      this.recipeBook = p_194194_1_.player.getRecipeBook();

      for(int lvt_4_1_ = 0; lvt_4_1_ < this.buttons.size(); ++lvt_4_1_) {
         ((RecipeWidget)this.buttons.get(lvt_4_1_)).setPosition(p_194194_2_ + 11 + 25 * (lvt_4_1_ % 5), p_194194_3_ + 31 + 25 * (lvt_4_1_ / 5));
      }

      this.forwardButton = new ToggleWidget(p_194194_2_ + 93, p_194194_3_ + 137, 12, 17, false);
      this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookGui.RECIPE_BOOK);
      this.backButton = new ToggleWidget(p_194194_2_ + 38, p_194194_3_ + 137, 12, 17, true);
      this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookGui.RECIPE_BOOK);
   }

   public void addListener(RecipeBookGui p_193732_1_) {
      this.listeners.remove(p_193732_1_);
      this.listeners.add(p_193732_1_);
   }

   public void updateLists(List<RecipeList> p_194192_1_, boolean p_194192_2_) {
      this.recipeLists = p_194192_1_;
      this.totalPages = (int)Math.ceil((double)p_194192_1_.size() / 20.0D);
      if (this.totalPages <= this.currentPage || p_194192_2_) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int lvt_1_1_ = 20 * this.currentPage;

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.buttons.size(); ++lvt_2_1_) {
         RecipeWidget lvt_3_1_ = (RecipeWidget)this.buttons.get(lvt_2_1_);
         if (lvt_1_1_ + lvt_2_1_ < this.recipeLists.size()) {
            RecipeList lvt_4_1_ = (RecipeList)this.recipeLists.get(lvt_1_1_ + lvt_2_1_);
            lvt_3_1_.func_203400_a(lvt_4_1_, this);
            lvt_3_1_.visible = true;
         } else {
            lvt_3_1_.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
      this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
   }

   public void render(int p_194191_1_, int p_194191_2_, int p_194191_3_, int p_194191_4_, float p_194191_5_) {
      if (this.totalPages > 1) {
         String lvt_6_1_ = this.currentPage + 1 + "/" + this.totalPages;
         int lvt_7_1_ = this.minecraft.fontRenderer.getStringWidth(lvt_6_1_);
         this.minecraft.fontRenderer.drawString(lvt_6_1_, (float)(p_194191_1_ - lvt_7_1_ / 2 + 73), (float)(p_194191_2_ + 141), -1);
      }

      this.hoveredButton = null;
      Iterator var8 = this.buttons.iterator();

      while(var8.hasNext()) {
         RecipeWidget lvt_7_2_ = (RecipeWidget)var8.next();
         lvt_7_2_.render(p_194191_3_, p_194191_4_, p_194191_5_);
         if (lvt_7_2_.visible && lvt_7_2_.isHovered()) {
            this.hoveredButton = lvt_7_2_;
         }
      }

      this.backButton.render(p_194191_3_, p_194191_4_, p_194191_5_);
      this.forwardButton.render(p_194191_3_, p_194191_4_, p_194191_5_);
      this.overlay.render(p_194191_3_, p_194191_4_, p_194191_5_);
   }

   public void renderTooltip(int p_193721_1_, int p_193721_2_) {
      if (this.minecraft.currentScreen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         this.minecraft.currentScreen.renderTooltip(this.hoveredButton.getToolTipText(this.minecraft.currentScreen), p_193721_1_, p_193721_2_);
      }

   }

   @Nullable
   public IRecipe<?> getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeList getLastClickedRecipeList() {
      return this.lastClickedRecipeList;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean func_198955_a(double p_198955_1_, double p_198955_3_, int p_198955_5_, int p_198955_6_, int p_198955_7_, int p_198955_8_, int p_198955_9_) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeList = null;
      if (this.overlay.isVisible()) {
         if (this.overlay.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeList = this.overlay.getRecipeList();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if (this.forwardButton.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         Iterator var10 = this.buttons.iterator();

         RecipeWidget lvt_11_1_;
         do {
            if (!var10.hasNext()) {
               return false;
            }

            lvt_11_1_ = (RecipeWidget)var10.next();
         } while(!lvt_11_1_.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_));

         if (p_198955_5_ == 0) {
            this.lastClickedRecipe = lvt_11_1_.getRecipe();
            this.lastClickedRecipeList = lvt_11_1_.getList();
         } else if (p_198955_5_ == 1 && !this.overlay.isVisible() && !lvt_11_1_.isOnlyOption()) {
            this.overlay.func_201703_a(this.minecraft, lvt_11_1_.getList(), lvt_11_1_.x, lvt_11_1_.y, p_198955_6_ + p_198955_8_ / 2, p_198955_7_ + 13 + p_198955_9_ / 2, (float)lvt_11_1_.getWidth());
         }

         return true;
      }
   }

   public void recipesShown(List<IRecipe<?>> p_194195_1_) {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         IRecipeUpdateListener lvt_3_1_ = (IRecipeUpdateListener)var2.next();
         lvt_3_1_.recipesShown(p_194195_1_);
      }

   }

   public Minecraft func_203411_d() {
      return this.minecraft;
   }

   public RecipeBook func_203412_e() {
      return this.recipeBook;
   }
}
