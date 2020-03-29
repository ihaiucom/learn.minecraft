package net.minecraftforge.fluids.capability;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IFluidHandlerItem extends IFluidHandler {
   @Nonnull
   ItemStack getContainer();
}
