package net.minecraftforge.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public interface ICapabilityProvider {
   @Nonnull
   <T> LazyOptional<T> getCapability(@Nonnull Capability<T> var1, @Nullable Direction var2);

   @Nonnull
   default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
      return this.getCapability(cap, (Direction)null);
   }
}
