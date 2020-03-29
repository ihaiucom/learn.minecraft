package net.minecraftforge.event.world;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class GetCollisionBoxesEvent extends WorldEvent {
   private final Entity entity;
   private final AxisAlignedBB aabb;
   private final List<AxisAlignedBB> collisionBoxesList;

   public GetCollisionBoxesEvent(World world, @Nullable Entity entity, AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxesList) {
      super(world);
      this.entity = entity;
      this.aabb = aabb;
      this.collisionBoxesList = collisionBoxesList;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public AxisAlignedBB getAabb() {
      return this.aabb;
   }

   public List<AxisAlignedBB> getCollisionBoxesList() {
      return this.collisionBoxesList;
   }
}
