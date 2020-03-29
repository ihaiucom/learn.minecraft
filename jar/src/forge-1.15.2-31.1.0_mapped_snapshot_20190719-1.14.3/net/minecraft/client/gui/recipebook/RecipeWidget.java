package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeWidget extends Widget {
   private static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/gui/recipe_book.png");
   private RecipeBookContainer<?> field_203401_p;
   private RecipeBook book;
   private RecipeList list;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeWidget() {
      super(0, 0, 25, 25, "");
   }

   public void func_203400_a(RecipeList p_203400_1_, RecipeBookPage p_203400_2_) {
      this.list = p_203400_1_;
      this.field_203401_p = (RecipeBookContainer)p_203400_2_.func_203411_d().player.openContainer;
      this.book = p_203400_2_.func_203412_e();
      List<IRecipe<?>> lvt_3_1_ = p_203400_1_.getRecipes(this.book.isFilteringCraftable(this.field_203401_p));
      Iterator var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         IRecipe<?> lvt_5_1_ = (IRecipe)var4.next();
         if (this.book.isNew(lvt_5_1_)) {
            p_203400_2_.recipesShown(lvt_3_1_);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeList getList() {
      return this.list;
   }

   public void setPosition(int p_191770_1_, int p_191770_2_) {
      this.x = p_191770_1_;
      this.y = p_191770_2_;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (!Screen.hasControlDown()) {
         this.time += p_renderButton_3_;
      }

      Minecraft lvt_4_1_ = Minecraft.getInstance();
      lvt_4_1_.getTextureManager().bindTexture(RECIPE_BOOK);
      int lvt_5_1_ = 29;
      if (!this.list.containsCraftableRecipes()) {
         lvt_5_1_ += 25;
      }

      int lvt_6_1_ = 206;
      if (this.list.getRecipes(this.book.isFilteringCraftable(this.field_203401_p)).size() > 1) {
         lvt_6_1_ += 25;
      }

      boolean lvt_7_1_ = this.animationTime > 0.0F;
      if (lvt_7_1_) {
         float lvt_8_1_ = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         RenderSystem.scalef(lvt_8_1_, lvt_8_1_, 1.0F);
         RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         this.animationTime -= p_renderButton_3_;
      }

      this.blit(this.x, this.y, lvt_5_1_, lvt_6_1_, this.width, this.height);
      List<IRecipe<?>> lvt_8_2_ = this.getOrderedRecipes();
      this.currentIndex = MathHelper.floor(this.time / 30.0F) % lvt_8_2_.size();
      ItemStack lvt_9_1_ = ((IRecipe)lvt_8_2_.get(this.currentIndex)).getRecipeOutput();
      int lvt_10_1_ = 4;
      if (this.list.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         lvt_4_1_.getItemRenderer().renderItemAndEffectIntoGUI(lvt_9_1_, this.x + lvt_10_1_ + 1, this.y + lvt_10_1_ + 1);
         --lvt_10_1_;
      }

      lvt_4_1_.getItemRenderer().renderItemAndEffectIntoGUI(lvt_9_1_, this.x + lvt_10_1_, this.y + lvt_10_1_);
      if (lvt_7_1_) {
         RenderSystem.popMatrix();
      }

   }

   private List<IRecipe<?>> getOrderedRecipes() {
      List<IRecipe<?>> lvt_1_1_ = this.list.getDisplayRecipes(true);
      if (!this.book.isFilteringCraftable(this.field_203401_p)) {
         lvt_1_1_.addAll(this.list.getDisplayRecipes(false));
      }

      return lvt_1_1_;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public IRecipe<?> getRecipe() {
      List<IRecipe<?>> lvt_1_1_ = this.getOrderedRecipes();
      return (IRecipe)lvt_1_1_.get(this.currentIndex);
   }

   public List<String> getToolTipText(Screen p_191772_1_) {
      ItemStack lvt_2_1_ = ((IRecipe)this.getOrderedRecipes().get(this.currentIndex)).getRecipeOutput();
      List<String> lvt_3_1_ = p_191772_1_.getTooltipFromItem(lvt_2_1_);
      if (this.list.getRecipes(this.book.isFilteringCraftable(this.field_203401_p)).size() > 1) {
         lvt_3_1_.add(I18n.format("gui.recipebook.moreRecipes"));
      }

      return lvt_3_1_;
   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return p_isValidClickButton_1_ == 0 || p_isValidClickButton_1_ == 1;
   }
}
