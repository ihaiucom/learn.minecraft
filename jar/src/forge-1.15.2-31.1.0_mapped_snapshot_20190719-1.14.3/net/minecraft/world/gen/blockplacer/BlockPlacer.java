package net.minecraft.world.gen.blockplacer;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class BlockPlacer implements IDynamicSerializable {
   protected final BlockPlacerType<?> field_227258_a_;

   protected BlockPlacer(BlockPlacerType<?> p_i225824_1_) {
      this.field_227258_a_ = p_i225824_1_;
   }

   public abstract void func_225567_a_(IWorld var1, BlockPos var2, BlockState var3, Random var4);
}
