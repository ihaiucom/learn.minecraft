package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeTabToggleWidget extends ToggleWidget {
   private final RecipeBookCategories category;
   private float animationTime;

   public RecipeTabToggleWidget(RecipeBookCategories p_i51075_1_) {
      super(0, 0, 35, 27, false);
      this.category = p_i51075_1_;
      this.initTextureValues(153, 2, 35, 0, RecipeBookGui.RECIPE_BOOK);
   }

   public void startAnimation(Minecraft p_193918_1_) {
      ClientRecipeBook lvt_2_1_ = p_193918_1_.player.getRecipeBook();
      List<RecipeList> lvt_3_1_ = lvt_2_1_.getRecipes(this.category);
      if (p_193918_1_.player.openContainer instanceof RecipeBookContainer) {
         Iterator var4 = lvt_3_1_.iterator();

         while(var4.hasNext()) {
            RecipeList lvt_5_1_ = (RecipeList)var4.next();
            Iterator var6 = lvt_5_1_.getRecipes(lvt_2_1_.isFilteringCraftable((RecipeBookContainer)p_193918_1_.player.openContainer)).iterator();

            while(var6.hasNext()) {
               IRecipe<?> lvt_7_1_ = (IRecipe)var6.next();
               if (lvt_2_1_.isNew(lvt_7_1_)) {
                  this.animationTime = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.animationTime > 0.0F) {
         float lvt_4_1_ = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         RenderSystem.scalef(1.0F, lvt_4_1_, 1.0F);
         RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
      }

      Minecraft lvt_4_2_ = Minecraft.getInstance();
      lvt_4_2_.getTextureManager().bindTexture(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int lvt_5_1_ = this.xTexStart;
      int lvt_6_1_ = this.yTexStart;
      if (this.stateTriggered) {
         lvt_5_1_ += this.xDiffTex;
      }

      if (this.isHovered()) {
         lvt_6_1_ += this.yDiffTex;
      }

      int lvt_7_1_ = this.x;
      if (this.stateTriggered) {
         lvt_7_1_ -= 2;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.blit(lvt_7_1_, this.y, lvt_5_1_, lvt_6_1_, this.width, this.height);
      RenderSystem.enableDepthTest();
      this.renderIcon(lvt_4_2_.getItemRenderer());
      if (this.animationTime > 0.0F) {
         RenderSystem.popMatrix();
         this.animationTime -= p_renderButton_3_;
      }

   }

   private void renderIcon(ItemRenderer p_193920_1_) {
      List<ItemStack> lvt_2_1_ = this.category.getIcons();
      int lvt_3_1_ = this.stateTriggered ? -2 : 0;
      if (lvt_2_1_.size() == 1) {
         p_193920_1_.renderItemAndEffectIntoGUI((ItemStack)lvt_2_1_.get(0), this.x + 9 + lvt_3_1_, this.y + 5);
      } else if (lvt_2_1_.size() == 2) {
         p_193920_1_.renderItemAndEffectIntoGUI((ItemStack)lvt_2_1_.get(0), this.x + 3 + lvt_3_1_, this.y + 5);
         p_193920_1_.renderItemAndEffectIntoGUI((ItemStack)lvt_2_1_.get(1), this.x + 14 + lvt_3_1_, this.y + 5);
      }

   }

   public RecipeBookCategories func_201503_d() {
      return this.category;
   }

   public boolean func_199500_a(ClientRecipeBook p_199500_1_) {
      List<RecipeList> lvt_2_1_ = p_199500_1_.getRecipes(this.category);
      this.visible = false;
      if (lvt_2_1_ != null) {
         Iterator var3 = lvt_2_1_.iterator();

         while(var3.hasNext()) {
            RecipeList lvt_4_1_ = (RecipeList)var3.next();
            if (lvt_4_1_.isNotEmpty() && lvt_4_1_.containsValidRecipes()) {
               this.visible = true;
               break;
            }
         }
      }

      return this.visible;
   }
}
