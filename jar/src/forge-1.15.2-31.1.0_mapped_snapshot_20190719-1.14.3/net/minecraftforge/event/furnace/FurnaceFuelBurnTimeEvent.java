package net.minecraftforge.event.furnace;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class FurnaceFuelBurnTimeEvent extends Event {
   @Nonnull
   private final ItemStack itemStack;
   private int burnTime;

   public FurnaceFuelBurnTimeEvent(@Nonnull ItemStack itemStack, int burnTime) {
      this.itemStack = itemStack;
      this.burnTime = burnTime;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public void setBurnTime(int burnTime) {
      if (burnTime >= 0) {
         this.burnTime = burnTime;
         this.setCanceled(true);
      }

   }

   public int getBurnTime() {
      return this.burnTime;
   }
}
