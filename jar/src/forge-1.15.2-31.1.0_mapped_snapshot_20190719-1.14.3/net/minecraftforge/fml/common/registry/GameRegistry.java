package net.minecraftforge.fml.common.registry;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class GameRegistry {
   public static <K extends IForgeRegistryEntry<K>> IForgeRegistry<K> findRegistry(Class<K> registryType) {
      return RegistryManager.ACTIVE.getRegistry(registryType);
   }
}
