package net.minecraftforge.server.permission.context;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Context implements IContext {
   private Map<ContextKey<?>, Object> map;

   @Nullable
   public World getWorld() {
      return null;
   }

   @Nullable
   public PlayerEntity getPlayer() {
      return null;
   }

   @Nullable
   public <T> T get(ContextKey<T> key) {
      return this.map != null && !this.map.isEmpty() ? this.map.get(key) : null;
   }

   public boolean has(ContextKey<?> key) {
      return this.covers(key) || this.map != null && !this.map.isEmpty() && this.map.containsKey(key);
   }

   public <T> Context set(ContextKey<T> key, @Nullable T obj) {
      if (this.covers(key)) {
         return this;
      } else {
         if (this.map == null) {
            this.map = new HashMap();
         }

         this.map.put(key, obj);
         return this;
      }
   }

   protected boolean covers(ContextKey<?> key) {
      return false;
   }
}
