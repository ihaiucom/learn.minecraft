package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.AxisAlignedBB;

public interface IMinecartCollisionHandler {
   void onEntityCollision(AbstractMinecartEntity var1, Entity var2);

   AxisAlignedBB getCollisionBox(AbstractMinecartEntity var1, Entity var2);

   AxisAlignedBB getMinecartCollisionBox(AbstractMinecartEntity var1);

   AxisAlignedBB getBoundingBox(AbstractMinecartEntity var1);
}
