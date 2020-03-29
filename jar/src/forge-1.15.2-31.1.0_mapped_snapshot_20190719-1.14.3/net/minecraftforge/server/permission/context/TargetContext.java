package net.minecraftforge.server.permission.context;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class TargetContext extends PlayerContext {
   private final Entity target;

   public TargetContext(PlayerEntity ep, @Nullable Entity entity) {
      super(ep);
      this.target = entity;
   }

   @Nullable
   public <T> T get(ContextKey<T> key) {
      return key.equals(ContextKeys.TARGET) ? this.target : super.get(key);
   }

   protected boolean covers(ContextKey<?> key) {
      return this.target != null && key.equals(ContextKeys.TARGET);
   }
}
