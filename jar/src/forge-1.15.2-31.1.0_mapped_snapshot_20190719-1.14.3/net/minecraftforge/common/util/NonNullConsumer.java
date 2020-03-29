package net.minecraftforge.common.util;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonNullConsumer<T> {
   void accept(@Nonnull T var1);
}
