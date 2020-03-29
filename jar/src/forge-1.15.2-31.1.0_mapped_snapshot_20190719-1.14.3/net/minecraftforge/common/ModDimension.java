package net.minecraftforge.common;

import java.util.function.BiFunction;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class ModDimension extends ForgeRegistryEntry<ModDimension> {
   public abstract BiFunction<World, DimensionType, ? extends Dimension> getFactory();

   public void write(PacketBuffer buffer, boolean network) {
   }

   public void read(PacketBuffer buffer, boolean network) {
   }

   public IBiomeMagnifier getMagnifier() {
      return ColumnFuzzedBiomeMagnifier.INSTANCE;
   }

   public static ModDimension withFactory(final BiFunction<World, DimensionType, ? extends Dimension> factory) {
      return new ModDimension() {
         public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
            return factory;
         }
      };
   }
}
