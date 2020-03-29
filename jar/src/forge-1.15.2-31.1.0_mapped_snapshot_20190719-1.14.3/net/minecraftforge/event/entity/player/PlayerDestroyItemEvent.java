package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PlayerDestroyItemEvent extends PlayerEvent {
   @Nonnull
   private final ItemStack original;
   @Nullable
   private final Hand hand;

   public PlayerDestroyItemEvent(PlayerEntity player, @Nonnull ItemStack original, @Nullable Hand hand) {
      super(player);
      this.original = original;
      this.hand = hand;
   }

   @Nonnull
   public ItemStack getOriginal() {
      return this.original;
   }

   @Nullable
   public Hand getHand() {
      return this.hand;
   }
}
