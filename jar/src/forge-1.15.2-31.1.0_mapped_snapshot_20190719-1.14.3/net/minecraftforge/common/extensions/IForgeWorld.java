package net.minecraftforge.common.extensions;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IForgeWorld extends ICapabilityProvider {
   double getMaxEntityRadius();

   double increaseMaxEntityRadius(double var1);
}
