package net.minecraftforge.event;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class AnvilUpdateEvent extends Event {
   @Nonnull
   private final ItemStack left;
   @Nonnull
   private final ItemStack right;
   private final String name;
   @Nonnull
   private ItemStack output;
   private int cost;
   private int materialCost;

   public AnvilUpdateEvent(@Nonnull ItemStack left, @Nonnull ItemStack right, String name, int cost) {
      this.left = left;
      this.right = right;
      this.output = ItemStack.EMPTY;
      this.name = name;
      this.setCost(cost);
      this.setMaterialCost(0);
   }

   @Nonnull
   public ItemStack getLeft() {
      return this.left;
   }

   @Nonnull
   public ItemStack getRight() {
      return this.right;
   }

   public String getName() {
      return this.name;
   }

   @Nonnull
   public ItemStack getOutput() {
      return this.output;
   }

   public void setOutput(@Nonnull ItemStack output) {
      this.output = output;
   }

   public int getCost() {
      return this.cost;
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public int getMaterialCost() {
      return this.materialCost;
   }

   public void setMaterialCost(int materialCost) {
      this.materialCost = materialCost;
   }
}
