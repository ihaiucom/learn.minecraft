package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AnvilRepairEvent extends PlayerEvent {
   @Nonnull
   private final ItemStack left;
   @Nonnull
   private final ItemStack right;
   @Nonnull
   private final ItemStack output;
   private float breakChance;

   public AnvilRepairEvent(PlayerEntity player, @Nonnull ItemStack left, @Nonnull ItemStack right, @Nonnull ItemStack output) {
      super(player);
      this.output = output;
      this.left = left;
      this.right = right;
      this.setBreakChance(0.12F);
   }

   @Nonnull
   public ItemStack getItemResult() {
      return this.output;
   }

   @Nonnull
   public ItemStack getItemInput() {
      return this.left;
   }

   @Nonnull
   public ItemStack getIngredientInput() {
      return this.right;
   }

   public float getBreakChance() {
      return this.breakChance;
   }

   public void setBreakChance(float breakChance) {
      this.breakChance = breakChance;
   }
}
