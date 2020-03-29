package net.minecraftforge.common.util;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonNullFunction<T, R> {
   @Nonnull
   R apply(@Nonnull T var1);
}
