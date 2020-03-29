package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BedrockBlock extends Block {
   public BedrockBlock(Block.Properties p_i49994_1_) {
      super(p_i49994_1_);
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return false;
   }
}
