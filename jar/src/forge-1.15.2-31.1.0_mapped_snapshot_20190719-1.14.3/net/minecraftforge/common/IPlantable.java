package net.minecraftforge.common;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlantable {
   default PlantType getPlantType(IBlockReader world, BlockPos pos) {
      if (this instanceof CropsBlock) {
         return PlantType.Crop;
      } else if (this instanceof SaplingBlock) {
         return PlantType.Plains;
      } else if (this instanceof FlowerBlock) {
         return PlantType.Plains;
      } else if (this == Blocks.DEAD_BUSH) {
         return PlantType.Desert;
      } else if (this == Blocks.LILY_PAD) {
         return PlantType.Water;
      } else if (this == Blocks.RED_MUSHROOM) {
         return PlantType.Cave;
      } else if (this == Blocks.BROWN_MUSHROOM) {
         return PlantType.Cave;
      } else if (this == Blocks.NETHER_WART) {
         return PlantType.Nether;
      } else {
         return this == Blocks.TALL_GRASS ? PlantType.Plains : PlantType.Plains;
      }
   }

   BlockState getPlant(IBlockReader var1, BlockPos var2);
}
