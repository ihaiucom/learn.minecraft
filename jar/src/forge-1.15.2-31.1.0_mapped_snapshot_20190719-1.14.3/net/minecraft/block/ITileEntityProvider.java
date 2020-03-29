package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

/** @deprecated */
@Deprecated
public interface ITileEntityProvider {
   @Nullable
   TileEntity createNewTileEntity(IBlockReader var1);
}
