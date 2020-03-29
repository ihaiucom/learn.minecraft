package net.minecraftforge.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider<B extends CapabilityProvider<B>> implements ICapabilityProvider {
   @Nonnull
   private final Class<B> baseClass;
   @Nullable
   private CapabilityDispatcher capabilities;
   private boolean valid = true;

   protected CapabilityProvider(Class<B> baseClass) {
      this.baseClass = baseClass;
   }

   protected final void gatherCapabilities() {
      this.gatherCapabilities((ICapabilityProvider)null);
   }

   protected final void gatherCapabilities(@Nullable ICapabilityProvider parent) {
      this.capabilities = ForgeEventFactory.gatherCapabilities(this.baseClass, this, parent);
   }

   @Nullable
   protected final CapabilityDispatcher getCapabilities() {
      return this.capabilities;
   }

   public final boolean areCapsCompatible(CapabilityProvider<B> other) {
      return this.areCapsCompatible(other.getCapabilities());
   }

   public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
      CapabilityDispatcher disp = this.getCapabilities();
      if (disp == null) {
         return other == null ? true : other.areCompatible((CapabilityDispatcher)null);
      } else {
         return disp.areCompatible(other);
      }
   }

   @Nullable
   protected final CompoundNBT serializeCaps() {
      CapabilityDispatcher disp = this.getCapabilities();
      return disp != null ? disp.serializeNBT() : null;
   }

   protected final void deserializeCaps(CompoundNBT tag) {
      CapabilityDispatcher disp = this.getCapabilities();
      if (disp != null) {
         disp.deserializeNBT(tag);
      }

   }

   protected void invalidateCaps() {
      this.valid = false;
      CapabilityDispatcher disp = this.getCapabilities();
      if (disp != null) {
         disp.invalidate();
      }

   }

   protected void reviveCaps() {
      this.valid = true;
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      CapabilityDispatcher disp = this.getCapabilities();
      return this.valid && disp != null ? disp.getCapability(cap, side) : LazyOptional.empty();
   }
}
