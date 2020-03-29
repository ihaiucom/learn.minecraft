package net.minecraftforge.client;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;

public class FluidContainerColorer implements IItemColor {
   public int getColor(@Nonnull ItemStack stack, int tintIndex) {
      return tintIndex != 1 ? -1 : (Integer)FluidUtil.getFluidContained(stack).map((fstack) -> {
         return fstack.getFluid().getAttributes().getColor(fstack);
      }).orElse(-1);
   }
}
