package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class FluidActionResult {
   public static final FluidActionResult FAILURE;
   public final boolean success;
   @Nonnull
   public final ItemStack result;

   public FluidActionResult(@Nonnull ItemStack result) {
      this(true, result);
   }

   private FluidActionResult(boolean success, @Nonnull ItemStack result) {
      this.success = success;
      this.result = result;
   }

   public boolean isSuccess() {
      return this.success;
   }

   @Nonnull
   public ItemStack getResult() {
      return this.result;
   }

   static {
      FAILURE = new FluidActionResult(false, ItemStack.EMPTY);
   }
}
