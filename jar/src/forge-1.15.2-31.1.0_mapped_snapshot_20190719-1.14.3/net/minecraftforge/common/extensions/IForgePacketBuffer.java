package net.minecraftforge.common.extensions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public interface IForgePacketBuffer {
   default PacketBuffer getBuffer() {
      return (PacketBuffer)this;
   }

   default <T extends IForgeRegistryEntry<T>> void writeRegistryIdUnsafe(@Nonnull IForgeRegistry<T> registry, @Nonnull T entry) {
      ForgeRegistry<T> forgeRegistry = (ForgeRegistry)registry;
      int id = forgeRegistry.getID(entry);
      this.getBuffer().writeVarInt(id);
   }

   default void writeRegistryIdUnsafe(@Nonnull IForgeRegistry<?> registry, @Nonnull ResourceLocation entryKey) {
      ForgeRegistry<?> forgeRegistry = (ForgeRegistry)registry;
      int id = forgeRegistry.getID(entryKey);
      this.getBuffer().writeVarInt(id);
   }

   default <T extends IForgeRegistryEntry<T>> T readRegistryIdUnsafe(@Nonnull IForgeRegistry<T> registry) {
      ForgeRegistry<T> forgeRegistry = (ForgeRegistry)registry;
      int id = this.getBuffer().readVarInt();
      return forgeRegistry.getValue(id);
   }

   default <T extends IForgeRegistryEntry<T>> void writeRegistryId(@Nonnull T entry) {
      Class<T> regType = ((IForgeRegistryEntry)Objects.requireNonNull(entry, "Cannot write a null registry entry!")).getRegistryType();
      IForgeRegistry<T> retrievedRegistry = RegistryManager.ACTIVE.getRegistry(regType);
      Preconditions.checkArgument(retrievedRegistry != null, "Cannot write registry id for an unknown registry type: %s", regType.getName());
      ResourceLocation name = retrievedRegistry.getRegistryName();
      Preconditions.checkArgument(retrievedRegistry.containsValue(entry), "Cannot find %s in %s", entry.getRegistryName() != null ? entry.getRegistryName() : entry, name);
      ForgeRegistry<T> reg = (ForgeRegistry)retrievedRegistry;
      this.getBuffer().writeResourceLocation(name);
      this.getBuffer().writeVarInt(reg.getID(entry));
   }

   default <T extends IForgeRegistryEntry<T>> T readRegistryId() {
      ResourceLocation location = this.getBuffer().readResourceLocation();
      ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(location);
      return registry.getValue(this.getBuffer().readVarInt());
   }

   default <T extends IForgeRegistryEntry<T>> T readRegistryIdSafe(Class<? super T> registrySuperType) {
      T value = this.readRegistryId();
      if (!value.getRegistryType().equals(registrySuperType)) {
         throw new IllegalArgumentException("Attempted to read an registryValue of the wrong type from the Buffer!");
      } else {
         return value;
      }
   }

   default void writeFluidStack(FluidStack stack) {
      if (stack.isEmpty()) {
         this.getBuffer().writeBoolean(false);
      } else {
         this.getBuffer().writeBoolean(true);
         stack.writeToPacket(this.getBuffer());
      }

   }

   default FluidStack readFluidStack() {
      return !this.getBuffer().readBoolean() ? FluidStack.EMPTY : FluidStack.readFromPacket(this.getBuffer());
   }
}
