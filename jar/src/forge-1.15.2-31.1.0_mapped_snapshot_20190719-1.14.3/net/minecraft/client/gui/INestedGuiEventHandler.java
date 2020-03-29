package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface INestedGuiEventHandler extends IGuiEventListener {
   List<? extends IGuiEventListener> children();

   default Optional<IGuiEventListener> func_212930_a(double p_212930_1_, double p_212930_3_) {
      Iterator var5 = this.children().iterator();

      IGuiEventListener lvt_6_1_;
      do {
         if (!var5.hasNext()) {
            return Optional.empty();
         }

         lvt_6_1_ = (IGuiEventListener)var5.next();
      } while(!lvt_6_1_.isMouseOver(p_212930_1_, p_212930_3_));

      return Optional.of(lvt_6_1_);
   }

   default boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      Iterator var6 = this.children().iterator();

      IGuiEventListener lvt_7_1_;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         lvt_7_1_ = (IGuiEventListener)var6.next();
      } while(!lvt_7_1_.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_));

      this.setFocused(lvt_7_1_);
      if (p_mouseClicked_5_ == 0) {
         this.setDragging(true);
      }

      return true;
   }

   default boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      this.setDragging(false);
      return this.func_212930_a(p_mouseReleased_1_, p_mouseReleased_3_).filter((p_212931_5_) -> {
         return p_212931_5_.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }).isPresent();
   }

   default boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.getFocused() != null && this.isDragging() && p_mouseDragged_5_ == 0 ? this.getFocused().mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) : false;
   }

   boolean isDragging();

   void setDragging(boolean var1);

   default boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.func_212930_a(p_mouseScrolled_1_, p_mouseScrolled_3_).filter((p_212929_6_) -> {
         return p_212929_6_.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
      }).isPresent();
   }

   default boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.getFocused() != null && this.getFocused().keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   default boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      return this.getFocused() != null && this.getFocused().keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
   }

   default boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.getFocused() != null && this.getFocused().charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   @Nullable
   IGuiEventListener getFocused();

   void setFocused(@Nullable IGuiEventListener var1);

   default void func_212928_a(@Nullable IGuiEventListener p_212928_1_) {
      this.setFocused(p_212928_1_);
   }

   default void func_212932_b(@Nullable IGuiEventListener p_212932_1_) {
      this.setFocused(p_212932_1_);
   }

   default boolean changeFocus(boolean p_changeFocus_1_) {
      IGuiEventListener lvt_2_1_ = this.getFocused();
      boolean lvt_3_1_ = lvt_2_1_ != null;
      if (lvt_3_1_ && lvt_2_1_.changeFocus(p_changeFocus_1_)) {
         return true;
      } else {
         List<? extends IGuiEventListener> lvt_4_1_ = this.children();
         int lvt_6_1_ = lvt_4_1_.indexOf(lvt_2_1_);
         int lvt_5_3_;
         if (lvt_3_1_ && lvt_6_1_ >= 0) {
            lvt_5_3_ = lvt_6_1_ + (p_changeFocus_1_ ? 1 : 0);
         } else if (p_changeFocus_1_) {
            lvt_5_3_ = 0;
         } else {
            lvt_5_3_ = lvt_4_1_.size();
         }

         ListIterator<? extends IGuiEventListener> lvt_7_1_ = lvt_4_1_.listIterator(lvt_5_3_);
         BooleanSupplier lvt_8_1_ = p_changeFocus_1_ ? lvt_7_1_::hasNext : lvt_7_1_::hasPrevious;
         Supplier lvt_9_1_ = p_changeFocus_1_ ? lvt_7_1_::next : lvt_7_1_::previous;

         IGuiEventListener lvt_10_1_;
         do {
            if (!lvt_8_1_.getAsBoolean()) {
               this.setFocused((IGuiEventListener)null);
               return false;
            }

            lvt_10_1_ = (IGuiEventListener)lvt_9_1_.get();
         } while(!lvt_10_1_.changeFocus(p_changeFocus_1_));

         this.setFocused(lvt_10_1_);
         return true;
      }
   }
}
