package net.minecraftforge.event.brewing;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerBrewedPotionEvent extends PlayerEvent {
   private final ItemStack stack;

   public PlayerBrewedPotionEvent(PlayerEntity player, @Nonnull ItemStack stack) {
      super(player);
      this.stack = stack;
   }

   @Nonnull
   public ItemStack getStack() {
      return this.stack;
   }
}
