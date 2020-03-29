package net.minecraftforge.common.util;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

public interface ITeleporter {
   default Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
      return (Entity)repositionEntity.apply(true);
   }
}
