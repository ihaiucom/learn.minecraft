package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorMenu {
   private static final ISpectatorMenuObject CLOSE_ITEM = new SpectatorMenu.EndSpectatorObject();
   private static final ISpectatorMenuObject SCROLL_LEFT = new SpectatorMenu.MoveMenuObject(-1, true);
   private static final ISpectatorMenuObject SCROLL_RIGHT_ENABLED = new SpectatorMenu.MoveMenuObject(1, true);
   private static final ISpectatorMenuObject SCROLL_RIGHT_DISABLED = new SpectatorMenu.MoveMenuObject(1, false);
   public static final ISpectatorMenuObject EMPTY_SLOT = new ISpectatorMenuObject() {
      public void selectItem(SpectatorMenu p_178661_1_) {
      }

      public ITextComponent getSpectatorName() {
         return new StringTextComponent("");
      }

      public void renderIcon(float p_178663_1_, int p_178663_2_) {
      }

      public boolean isEnabled() {
         return false;
      }
   };
   private final ISpectatorMenuRecipient listener;
   private final List<SpectatorDetails> previousCategories = Lists.newArrayList();
   private ISpectatorMenuView category = new BaseSpectatorGroup();
   private int selectedSlot = -1;
   private int page;

   public SpectatorMenu(ISpectatorMenuRecipient p_i45497_1_) {
      this.listener = p_i45497_1_;
   }

   public ISpectatorMenuObject getItem(int p_178643_1_) {
      int lvt_2_1_ = p_178643_1_ + this.page * 6;
      if (this.page > 0 && p_178643_1_ == 0) {
         return SCROLL_LEFT;
      } else if (p_178643_1_ == 7) {
         return lvt_2_1_ < this.category.getItems().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED;
      } else if (p_178643_1_ == 8) {
         return CLOSE_ITEM;
      } else {
         return lvt_2_1_ >= 0 && lvt_2_1_ < this.category.getItems().size() ? (ISpectatorMenuObject)MoreObjects.firstNonNull(this.category.getItems().get(lvt_2_1_), EMPTY_SLOT) : EMPTY_SLOT;
      }
   }

   public List<ISpectatorMenuObject> getItems() {
      List<ISpectatorMenuObject> lvt_1_1_ = Lists.newArrayList();

      for(int lvt_2_1_ = 0; lvt_2_1_ <= 8; ++lvt_2_1_) {
         lvt_1_1_.add(this.getItem(lvt_2_1_));
      }

      return lvt_1_1_;
   }

   public ISpectatorMenuObject getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public ISpectatorMenuView getSelectedCategory() {
      return this.category;
   }

   public void selectSlot(int p_178644_1_) {
      ISpectatorMenuObject lvt_2_1_ = this.getItem(p_178644_1_);
      if (lvt_2_1_ != EMPTY_SLOT) {
         if (this.selectedSlot == p_178644_1_ && lvt_2_1_.isEnabled()) {
            lvt_2_1_.selectItem(this);
         } else {
            this.selectedSlot = p_178644_1_;
         }
      }

   }

   public void exit() {
      this.listener.onSpectatorMenuClosed(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void selectCategory(ISpectatorMenuView p_178647_1_) {
      this.previousCategories.add(this.getCurrentPage());
      this.category = p_178647_1_;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorDetails getCurrentPage() {
      return new SpectatorDetails(this.category, this.getItems(), this.selectedSlot);
   }

   @OnlyIn(Dist.CLIENT)
   static class MoveMenuObject implements ISpectatorMenuObject {
      private final int direction;
      private final boolean enabled;

      public MoveMenuObject(int p_i45495_1_, boolean p_i45495_2_) {
         this.direction = p_i45495_1_;
         this.enabled = p_i45495_2_;
      }

      public void selectItem(SpectatorMenu p_178661_1_) {
         p_178661_1_.page = p_178661_1_.page + this.direction;
      }

      public ITextComponent getSpectatorName() {
         return this.direction < 0 ? new TranslationTextComponent("spectatorMenu.previous_page", new Object[0]) : new TranslationTextComponent("spectatorMenu.next_page", new Object[0]);
      }

      public void renderIcon(float p_178663_1_, int p_178663_2_) {
         Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
         if (this.direction < 0) {
            AbstractGui.blit(0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
         } else {
            AbstractGui.blit(0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
         }

      }

      public boolean isEnabled() {
         return this.enabled;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class EndSpectatorObject implements ISpectatorMenuObject {
      private EndSpectatorObject() {
      }

      public void selectItem(SpectatorMenu p_178661_1_) {
         p_178661_1_.exit();
      }

      public ITextComponent getSpectatorName() {
         return new TranslationTextComponent("spectatorMenu.close", new Object[0]);
      }

      public void renderIcon(float p_178663_1_, int p_178663_2_) {
         Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
         AbstractGui.blit(0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
      }

      public boolean isEnabled() {
         return true;
      }

      // $FF: synthetic method
      EndSpectatorObject(Object p_i45496_1_) {
         this();
      }
   }
}
