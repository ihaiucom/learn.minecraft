package net.minecraftforge.common.ticket;

import javax.annotation.Nonnull;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class AABBTicket extends SimpleTicket<Vec3d> {
   @Nonnull
   public final AxisAlignedBB axisAlignedBB;

   public AABBTicket(@Nonnull AxisAlignedBB axisAlignedBB) {
      this.axisAlignedBB = axisAlignedBB;
   }

   public boolean matches(Vec3d toMatch) {
      return this.axisAlignedBB.contains(toMatch);
   }
}
