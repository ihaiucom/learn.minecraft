package net.minecraftforge.registries;

import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public interface IRegistryDelegate<T> extends Supplier<T> {
   T get();

   ResourceLocation name();

   Class<T> type();
}
