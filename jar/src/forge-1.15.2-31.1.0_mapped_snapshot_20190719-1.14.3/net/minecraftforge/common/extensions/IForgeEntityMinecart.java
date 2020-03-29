package net.minecraftforge.common.extensions;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.IMinecartCollisionHandler;

public interface IForgeEntityMinecart {
   float DEFAULT_MAX_SPEED_AIR_LATERAL = 0.4F;
   float DEFAULT_MAX_SPEED_AIR_VERTICAL = -1.0F;
   double DEFAULT_AIR_DRAG = 0.949999988079071D;
   IMinecartCollisionHandler COLLISIONS = null;

   default AbstractMinecartEntity getMinecart() {
      return (AbstractMinecartEntity)this;
   }

   default IMinecartCollisionHandler getCollisionHandler() {
      return COLLISIONS;
   }

   default BlockPos getCurrentRailPosition() {
      int x = MathHelper.floor(this.getMinecart().func_226277_ct_());
      int y = MathHelper.floor(this.getMinecart().func_226278_cu_());
      int z = MathHelper.floor(this.getMinecart().func_226281_cx_());
      BlockPos pos = new BlockPos(x, y - 1, z);
      if (this.getMinecart().world.getBlockState(pos).isIn(BlockTags.RAILS)) {
         pos = pos.down();
      }

      return pos;
   }

   double getMaxSpeedWithRail();

   void moveMinecartOnRail(BlockPos var1);

   default ItemStack getCartItem() {
      switch(this.getMinecart().getMinecartType()) {
      case FURNACE:
         return new ItemStack(Items.FURNACE_MINECART);
      case CHEST:
         return new ItemStack(Items.CHEST_MINECART);
      case TNT:
         return new ItemStack(Items.TNT_MINECART);
      case HOPPER:
         return new ItemStack(Items.HOPPER_MINECART);
      case COMMAND_BLOCK:
         return new ItemStack(Items.COMMAND_BLOCK_MINECART);
      default:
         return new ItemStack(Items.MINECART);
      }
   }

   boolean canUseRail();

   void setCanUseRail(boolean var1);

   default boolean shouldDoRailFunctions() {
      return true;
   }

   default boolean isPoweredCart() {
      return this.getMinecart().getMinecartType() == AbstractMinecartEntity.Type.FURNACE;
   }

   default boolean canBeRidden() {
      return this.getMinecart().getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE;
   }

   default float getMaxCartSpeedOnRail() {
      return 1.2F;
   }

   float getCurrentCartSpeedCapOnRail();

   void setCurrentCartSpeedCapOnRail(float var1);

   float getMaxSpeedAirLateral();

   void setMaxSpeedAirLateral(float var1);

   float getMaxSpeedAirVertical();

   void setMaxSpeedAirVertical(float var1);

   double getDragAir();

   void setDragAir(double var1);

   default double getSlopeAdjustment() {
      return 0.0078125D;
   }

   default int getComparatorLevel() {
      return -1;
   }
}
