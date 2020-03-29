package net.minecraftforge.common.util;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonNullPredicate<T> {
   boolean test(@Nonnull T var1);
}
