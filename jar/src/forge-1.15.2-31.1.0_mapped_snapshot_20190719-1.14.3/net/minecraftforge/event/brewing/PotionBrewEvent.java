package net.minecraftforge.event.brewing;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class PotionBrewEvent extends Event {
   private NonNullList<ItemStack> stacks;

   protected PotionBrewEvent(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   @Nonnull
   public ItemStack getItem(int index) {
      return index >= 0 && index < this.stacks.size() ? (ItemStack)this.stacks.get(index) : ItemStack.EMPTY;
   }

   public void setItem(int index, @Nonnull ItemStack stack) {
      if (index < this.stacks.size()) {
         this.stacks.set(index, stack);
      }

   }

   public int getLength() {
      return this.stacks.size();
   }

   public static class Post extends PotionBrewEvent {
      public Post(NonNullList<ItemStack> stacks) {
         super(stacks);
      }
   }

   @Cancelable
   public static class Pre extends PotionBrewEvent {
      public Pre(NonNullList<ItemStack> stacks) {
         super(stacks);
      }
   }
}
