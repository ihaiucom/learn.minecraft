package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBlockColor {
   int getColor(BlockState var1, @Nullable ILightReader var2, @Nullable BlockPos var3, int var4);
}
