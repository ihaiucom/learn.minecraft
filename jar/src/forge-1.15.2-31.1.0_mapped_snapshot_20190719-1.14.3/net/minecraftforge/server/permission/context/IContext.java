package net.minecraftforge.server.permission.context;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IContext {
   @Nullable
   World getWorld();

   @Nullable
   PlayerEntity getPlayer();

   @Nullable
   <T> T get(ContextKey<T> var1);

   boolean has(ContextKey<?> var1);
}
