package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class AreaContext extends PlayerContext {
   private final AxisAlignedBB area;

   public AreaContext(PlayerEntity ep, AxisAlignedBB aabb) {
      super(ep);
      this.area = (AxisAlignedBB)Preconditions.checkNotNull(aabb, "AxisAlignedBB can't be null in AreaContext!");
   }

   @Nullable
   public <T> T get(ContextKey<T> key) {
      return key.equals(ContextKeys.AREA) ? this.area : super.get(key);
   }

   protected boolean covers(ContextKey<?> key) {
      return key.equals(ContextKeys.AREA);
   }
}
