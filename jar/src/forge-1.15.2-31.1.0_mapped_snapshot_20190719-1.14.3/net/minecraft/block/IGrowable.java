package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IGrowable {
   boolean canGrow(IBlockReader var1, BlockPos var2, BlockState var3, boolean var4);

   boolean canUseBonemeal(World var1, Random var2, BlockPos var3, BlockState var4);

   void func_225535_a_(ServerWorld var1, Random var2, BlockPos var3, BlockState var4);
}
