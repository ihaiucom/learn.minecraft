package net.minecraftforge.common;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

/** @deprecated */
@Deprecated
public interface IShearable {
   default boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
      return true;
   }

   @Nonnull
   default List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune) {
      return NonNullList.create();
   }
}
