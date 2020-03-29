package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeOverlayGui extends AbstractGui implements IRenderable, IGuiEventListener {
   private static final ResourceLocation RECIPE_BOOK_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
   private final List<RecipeOverlayGui.RecipeButtonWidget> buttonList = Lists.newArrayList();
   private boolean visible;
   private int x;
   private int y;
   private Minecraft mc;
   private RecipeList recipeList;
   private IRecipe<?> lastRecipeClicked;
   private float time;
   private boolean field_201704_n;

   public void func_201703_a(Minecraft p_201703_1_, RecipeList p_201703_2_, int p_201703_3_, int p_201703_4_, int p_201703_5_, int p_201703_6_, float p_201703_7_) {
      this.mc = p_201703_1_;
      this.recipeList = p_201703_2_;
      if (p_201703_1_.player.openContainer instanceof AbstractFurnaceContainer) {
         this.field_201704_n = true;
      }

      boolean lvt_8_1_ = p_201703_1_.player.getRecipeBook().isFilteringCraftable((RecipeBookContainer)p_201703_1_.player.openContainer);
      List<IRecipe<?>> lvt_9_1_ = p_201703_2_.getDisplayRecipes(true);
      List<IRecipe<?>> lvt_10_1_ = lvt_8_1_ ? Collections.emptyList() : p_201703_2_.getDisplayRecipes(false);
      int lvt_11_1_ = lvt_9_1_.size();
      int lvt_12_1_ = lvt_11_1_ + lvt_10_1_.size();
      int lvt_13_1_ = lvt_12_1_ <= 16 ? 4 : 5;
      int lvt_14_1_ = (int)Math.ceil((double)((float)lvt_12_1_ / (float)lvt_13_1_));
      this.x = p_201703_3_;
      this.y = p_201703_4_;
      int lvt_15_1_ = true;
      float lvt_16_1_ = (float)(this.x + Math.min(lvt_12_1_, lvt_13_1_) * 25);
      float lvt_17_1_ = (float)(p_201703_5_ + 50);
      if (lvt_16_1_ > lvt_17_1_) {
         this.x = (int)((float)this.x - p_201703_7_ * (float)((int)((lvt_16_1_ - lvt_17_1_) / p_201703_7_)));
      }

      float lvt_18_1_ = (float)(this.y + lvt_14_1_ * 25);
      float lvt_19_1_ = (float)(p_201703_6_ + 50);
      if (lvt_18_1_ > lvt_19_1_) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((lvt_18_1_ - lvt_19_1_) / p_201703_7_));
      }

      float lvt_20_1_ = (float)this.y;
      float lvt_21_1_ = (float)(p_201703_6_ - 100);
      if (lvt_20_1_ < lvt_21_1_) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((lvt_20_1_ - lvt_21_1_) / p_201703_7_));
      }

      this.visible = true;
      this.buttonList.clear();

      for(int lvt_22_1_ = 0; lvt_22_1_ < lvt_12_1_; ++lvt_22_1_) {
         boolean lvt_23_1_ = lvt_22_1_ < lvt_11_1_;
         IRecipe<?> lvt_24_1_ = lvt_23_1_ ? (IRecipe)lvt_9_1_.get(lvt_22_1_) : (IRecipe)lvt_10_1_.get(lvt_22_1_ - lvt_11_1_);
         int lvt_25_1_ = this.x + 4 + 25 * (lvt_22_1_ % lvt_13_1_);
         int lvt_26_1_ = this.y + 5 + 25 * (lvt_22_1_ / lvt_13_1_);
         if (this.field_201704_n) {
            this.buttonList.add(new RecipeOverlayGui.FurnaceRecipeButtonWidget(lvt_25_1_, lvt_26_1_, lvt_24_1_, lvt_23_1_));
         } else {
            this.buttonList.add(new RecipeOverlayGui.RecipeButtonWidget(lvt_25_1_, lvt_26_1_, lvt_24_1_, lvt_23_1_));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return false;
   }

   public RecipeList getRecipeList() {
      return this.recipeList;
   }

   public IRecipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ != 0) {
         return false;
      } else {
         Iterator var6 = this.buttonList.iterator();

         RecipeOverlayGui.RecipeButtonWidget lvt_7_1_;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            lvt_7_1_ = (RecipeOverlayGui.RecipeButtonWidget)var6.next();
         } while(!lvt_7_1_.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_));

         this.lastRecipeClicked = lvt_7_1_.recipe;
         return true;
      }
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return false;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.visible) {
         this.time += p_render_3_;
         RenderSystem.enableBlend();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(RECIPE_BOOK_TEXTURE);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 170.0F);
         int lvt_4_1_ = this.buttonList.size() <= 16 ? 4 : 5;
         int lvt_5_1_ = Math.min(this.buttonList.size(), lvt_4_1_);
         int lvt_6_1_ = MathHelper.ceil((float)this.buttonList.size() / (float)lvt_4_1_);
         int lvt_7_1_ = true;
         int lvt_8_1_ = true;
         int lvt_9_1_ = true;
         int lvt_10_1_ = true;
         this.nineInchSprite(lvt_5_1_, lvt_6_1_, 24, 4, 82, 208);
         RenderSystem.disableBlend();
         Iterator var11 = this.buttonList.iterator();

         while(var11.hasNext()) {
            RecipeOverlayGui.RecipeButtonWidget lvt_12_1_ = (RecipeOverlayGui.RecipeButtonWidget)var11.next();
            lvt_12_1_.render(p_render_1_, p_render_2_, p_render_3_);
         }

         RenderSystem.popMatrix();
      }
   }

   private void nineInchSprite(int p_191846_1_, int p_191846_2_, int p_191846_3_, int p_191846_4_, int p_191846_5_, int p_191846_6_) {
      this.blit(this.x, this.y, p_191846_5_, p_191846_6_, p_191846_4_, p_191846_4_);
      this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_, p_191846_4_, p_191846_4_);
      this.blit(this.x, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);
      this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);

      for(int lvt_7_1_ = 0; lvt_7_1_ < p_191846_1_; ++lvt_7_1_) {
         this.blit(this.x + p_191846_4_ + lvt_7_1_ * p_191846_3_, this.y, p_191846_5_ + p_191846_4_, p_191846_6_, p_191846_3_, p_191846_4_);
         this.blit(this.x + p_191846_4_ + (lvt_7_1_ + 1) * p_191846_3_, this.y, p_191846_5_ + p_191846_4_, p_191846_6_, p_191846_4_, p_191846_4_);

         for(int lvt_8_1_ = 0; lvt_8_1_ < p_191846_2_; ++lvt_8_1_) {
            if (lvt_7_1_ == 0) {
               this.blit(this.x, this.y + p_191846_4_ + lvt_8_1_ * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
               this.blit(this.x, this.y + p_191846_4_ + (lvt_8_1_ + 1) * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_4_);
            }

            this.blit(this.x + p_191846_4_ + lvt_7_1_ * p_191846_3_, this.y + p_191846_4_ + lvt_8_1_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_3_, p_191846_3_);
            this.blit(this.x + p_191846_4_ + (lvt_7_1_ + 1) * p_191846_3_, this.y + p_191846_4_ + lvt_8_1_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
            this.blit(this.x + p_191846_4_ + lvt_7_1_ * p_191846_3_, this.y + p_191846_4_ + (lvt_8_1_ + 1) * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_3_, p_191846_4_);
            this.blit(this.x + p_191846_4_ + (lvt_7_1_ + 1) * p_191846_3_ - 1, this.y + p_191846_4_ + (lvt_8_1_ + 1) * p_191846_3_ - 1, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_ + 1, p_191846_4_ + 1);
            if (lvt_7_1_ == p_191846_1_ - 1) {
               this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ + lvt_8_1_ * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
               this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ + (lvt_8_1_ + 1) * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_4_);
            }
         }

         this.blit(this.x + p_191846_4_ + lvt_7_1_ * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_3_, p_191846_4_);
         this.blit(this.x + p_191846_4_ + (lvt_7_1_ + 1) * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);
      }

   }

   public void setVisible(boolean p_192999_1_) {
      this.visible = p_192999_1_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   @OnlyIn(Dist.CLIENT)
   class RecipeButtonWidget extends Widget implements IRecipePlacer<Ingredient> {
      private final IRecipe<?> recipe;
      private final boolean isCraftable;
      protected final List<RecipeOverlayGui.RecipeButtonWidget.Child> field_201506_o = Lists.newArrayList();

      public RecipeButtonWidget(int p_i47594_2_, int p_i47594_3_, IRecipe<?> p_i47594_4_, boolean p_i47594_5_) {
         super(p_i47594_2_, p_i47594_3_, 200, 20, "");
         this.width = 24;
         this.height = 24;
         this.recipe = p_i47594_4_;
         this.isCraftable = p_i47594_5_;
         this.func_201505_a(p_i47594_4_);
      }

      protected void func_201505_a(IRecipe<?> p_201505_1_) {
         this.placeRecipe(3, 3, -1, p_201505_1_, p_201505_1_.getIngredients().iterator(), 0);
      }

      public void setSlotContents(Iterator<Ingredient> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_) {
         ItemStack[] lvt_6_1_ = ((Ingredient)p_201500_1_.next()).getMatchingStacks();
         if (lvt_6_1_.length != 0) {
            this.field_201506_o.add(new RecipeOverlayGui.RecipeButtonWidget.Child(3 + p_201500_5_ * 7, 3 + p_201500_4_ * 7, lvt_6_1_));
         }

      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RenderSystem.enableAlphaTest();
         RecipeOverlayGui.this.mc.getTextureManager().bindTexture(RecipeOverlayGui.RECIPE_BOOK_TEXTURE);
         int lvt_4_1_ = 152;
         if (!this.isCraftable) {
            lvt_4_1_ += 26;
         }

         int lvt_5_1_ = RecipeOverlayGui.this.field_201704_n ? 130 : 78;
         if (this.isHovered()) {
            lvt_5_1_ += 26;
         }

         this.blit(this.x, this.y, lvt_4_1_, lvt_5_1_, this.width, this.height);
         Iterator var6 = this.field_201506_o.iterator();

         while(var6.hasNext()) {
            RecipeOverlayGui.RecipeButtonWidget.Child lvt_7_1_ = (RecipeOverlayGui.RecipeButtonWidget.Child)var6.next();
            RenderSystem.pushMatrix();
            float lvt_8_1_ = 0.42F;
            int lvt_9_1_ = (int)((float)(this.x + lvt_7_1_.field_201706_b) / 0.42F - 3.0F);
            int lvt_10_1_ = (int)((float)(this.y + lvt_7_1_.field_201707_c) / 0.42F - 3.0F);
            RenderSystem.scalef(0.42F, 0.42F, 1.0F);
            RecipeOverlayGui.this.mc.getItemRenderer().renderItemAndEffectIntoGUI(lvt_7_1_.field_201705_a[MathHelper.floor(RecipeOverlayGui.this.time / 30.0F) % lvt_7_1_.field_201705_a.length], lvt_9_1_, lvt_10_1_);
            RenderSystem.popMatrix();
         }

         RenderSystem.disableAlphaTest();
      }

      @OnlyIn(Dist.CLIENT)
      public class Child {
         public final ItemStack[] field_201705_a;
         public final int field_201706_b;
         public final int field_201707_c;

         public Child(int p_i48748_2_, int p_i48748_3_, ItemStack[] p_i48748_4_) {
            this.field_201706_b = p_i48748_2_;
            this.field_201707_c = p_i48748_3_;
            this.field_201705_a = p_i48748_4_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class FurnaceRecipeButtonWidget extends RecipeOverlayGui.RecipeButtonWidget {
      public FurnaceRecipeButtonWidget(int p_i48747_2_, int p_i48747_3_, IRecipe<?> p_i48747_4_, boolean p_i48747_5_) {
         super(p_i48747_2_, p_i48747_3_, p_i48747_4_, p_i48747_5_);
      }

      protected void func_201505_a(IRecipe<?> p_201505_1_) {
         ItemStack[] lvt_2_1_ = ((Ingredient)p_201505_1_.getIngredients().get(0)).getMatchingStacks();
         this.field_201506_o.add(new RecipeOverlayGui.RecipeButtonWidget.Child(10, 10, lvt_2_1_));
      }
   }
}
